package ch.uzh.ifi.hase.soprafs22;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// requiremnts need to specifs which roles are required and then provide requiremnts for those
// maybe post roles and then post reuirements as dictionary role:N

public class Optimizer {

    public User[] users;
    public Schedule[] schedules;

    public Day[] days;


    public int[][][] base;  // user - day - slot
    public int[][][] special; // user - day - slot
    public IloNumVar[][][][] x; // user - day - slot - role// initialize
    public int[][][] requirements; // day - slot - role

    public Long[] capacityOverall; // for eaxh user
    public Long[] capacityDaily; // now specific for each user, could be fixed or specific for user and day

    public HashMap<Long, List<Long>> roles; // for each user list of roles he can do stored as id
    public String[] rolesAll;
    // this should be stored in the team instamce

    private IloCplex cplex = new IloCplex(); // check

    public Optimizer(TeamCalendar calendar ) throws IloException {
        // Initialize the model. It is important to initialize the variables first!
        // add instantiation of fields

        //this.base = createBaseArray(calendar);
        //this.special = createSpecialArray(calendar);
       // this.requirements = createArray(calendar);

        addVariables();
        addSpecialRequirementConstraint();
        addCapacityOverallConstraint();
        capacityDailyConstraint();
        requirementConstraint();
        addObjective();
    }

    public void solve() throws IloException {
        cplex.solve();
    }


    // 4-d array of variables
    private void addVariables() throws IloException {
        for (int i = 0; i < base.length; i++) {
            for (int j = 0; j < base[i].length; j++) {
                for (int k = 0; k < base[i][j].length; k++) {
                    for (int r = 0; r<rolesAll.length; r++) {
                        IloNumVar var = cplex.boolVar();
                        x[i][j][k][r] = var;
                    }
                }
            }
        }
    }

    /**
     * Checks whether the current solution to the model is feasible
     *
     * @return the feasibility of the model
     * @throws IloException if something is wrong with CPLEX
     */
    public boolean isFeasible() throws IloException {
        return cplex.isPrimalFeasible();
    }



    private void addObjective() throws IloException {
        // Initialize the objective sum to 0
        IloNumExpr obj = cplex.constant(0);
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                for (int k = 0; k < x[i][j].length; k++) {
                    // for each day, slot, user:
                    IloNumExpr expr = cplex.constant(0);
                    for (int r = 0; r< x[i][j][k].length; r ++){
                        IloNumExpr term = x[i][j][k][r];
                        expr = cplex.sum(expr, term);
                    }
                    // Take the product of the decision variable and the base preferences
                    IloNumExpr term = cplex.prod(expr, base[i][j][k]);
                    // Add the term to the current sum
                    obj = cplex.sum(obj, term);
                }
            }
        }
        // Add the obj expression as a maximization objective
        cplex.addMaximize(obj);
    }

    // constraint: special req are satisfied
    private void addSpecialRequirementConstraint() throws IloException {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                for (int k = 0; k < x[i][j].length; k++) {
                    IloNumExpr lhs = cplex.constant(0);
                    for (int r = 0; r < x[i][j][k].length; r++) {
                        IloNumExpr term = x[i][j][k][r];
                        lhs = cplex.sum(lhs,term);
                    }
                    cplex.addEq(lhs, special[i][j][k]);
                }
            }
        }
    }


    private void addCapacityOverallConstraint() throws IloException {
        for (int i = 0; i < x.length; i++) {
            // for each person compute sum of hours working and specify that it is not more than some contant
            IloNumExpr lhs = cplex.constant(0);
            for (int j = 0; j < x[i].length; j++) {
                for (int k = 0; k < x[i][j].length; k++) {
                    for (int r = 0; k < x[i][j][k].length; r++) {
                        IloNumExpr term = x[i][j][k][r];
                        lhs = cplex.sum(lhs, term);
                    }
                }
            }
            cplex.addLe(lhs, capacityOverall[i]);
        }
    }


    private void capacityDailyConstraint() throws IloException {
        // sum across users and roles
        int n_days = x[0].length;
        int n_slottd = x[0][0].length;

        for (int i = 0; i < x.length; i++) {
            // for each person compute sum of hours working and specify that it is not more than some contant
            for (int j = 0; j < x[i].length; j++) {
                IloNumExpr lhs = cplex.constant(0);
                for (int k = 0; k < x[i][j].length; k++) {
                    for (int r = 0; k < x[i][j][k].length; r++) {
                        IloNumExpr term = x[i][j][k][r];
                        lhs = cplex.sum(lhs,  term);
                    }
                }
                cplex.addLe(lhs, capacityOverall[i]);
            }
        }
    }

    private void requirementConstraint() throws IloException {
        // for each day each slot sum of users x which <= requirements for that day, slot
            // for each day
            for (int j = 0; j < requirements.length; j++) {
                // for each slot
                for (int k = 0; k < requirements[j].length; k++) {
                    // for each role
                    for (int r = 0; r < requirements[j][k].length; r++) {
                        IloNumExpr lhs = cplex.constant(0);
                        for (int i= 0; i< x.length; i++){
                            IloNumExpr term = x[i][j][k][r];
                            lhs = cplex.sum(lhs,  term);
                    }
                        cplex.addLe(lhs, requirements[j][k][r]);

                    }
            }
        }
    }

    private void noCollisionConstraint() throws IloException {
        // sum across roles should not be larger than

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[i].length; j++) {
                for (int k = 0; k < x[i][j].length; k++) {
                    IloNumExpr lhs = cplex.constant(0);
                    for (int r = 0; r < x[i][j][k].length; r++) {
                        IloNumExpr term = x[i][j][k][r];
                        lhs = cplex.sum(lhs, term);
                    }
                    cplex.addLe(lhs, 1);
                }
            }
        }
    }




    public void createBaseArray(TeamCalendar calendar){
        // change iterating to for i ....
        int nDays = 0;
        int nSlots = 0;
        int nUsers = 0;
        // first loop, count n, have a hashmap of slots, and a hashmap of users
        // where key is id/ from - to / 0-30 for days  - value is the number
        // hashmaps need to be stored as well to unfold that afterwards - it is like look up table

        for (Map.Entry<Weekday, Day> entry:calendar.getBasePlan().entrySet()){
            for (Event event:entry.getValue().getEvents()){
                int to = event.getTimeTo();
                int from = event.getTimeFrom();

                // fill out the requirement hourly by role
                for (int i = to; i <from; i++){
                    for (int k = 0; k< rolesAll.length; k++){
                        this.requirements[nDays][i][k] = event.getRequirements().get(rolesAll[k]);
                    }
                }
                // get the list of users
                for(Schedule schedule: event.getSchedules()){
                 this.users[nUsers] = schedule.getUser();
                    this.schedules[nUsers] = schedule;
                }
            }
        }

    }


}
