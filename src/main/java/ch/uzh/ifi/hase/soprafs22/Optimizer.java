package ch.uzh.ifi.hase.soprafs22;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import ch.uzh.ifi.hase.soprafs22.entity.*;

import java.util.*;


public class Optimizer {

    int nCols;
    TeamCalendar teamCalendar;
    ArrayList<Schedule> result;
    LpSolve solver;

    // TODO: add overlapping constraint

    public Optimizer(TeamCalendar teamCalendar) throws LpSolveException {
        this.teamCalendar = teamCalendar;
        computeN();
        // intialize arrays

        this.result = new ArrayList<>();
        this.solver = LpSolve.makeLp(0, nCols);
        defineObjective();
        addRequirementConsraint();
        addSpecialPreferenceConstraint();

        // solve the problem
        this.solver.setMaxim();
        int sol = this.solver.solve();

        // TODO: if the solution returned is optimal, store it else  relax some constraints
        readSolution();
        this.solver.deleteLp();
    }


    private void defineObjective() throws LpSolveException {
        double[] obj = new double[nCols+1];
        obj[0] = 0;
        int i = 1;
        // 1) create array obj and define obj function, should be done before constraints for better performance
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    this.result.add(schedule);
                    obj[i] = schedule.getBase();
                    this.solver.setBinary(i, true); // if this does not work change to i, because I dont know yet exactly at which point 0 column is reserved for rhs
                    i++;
                }
            }
        }

        // set objective function
        this.solver.setObjFn(obj);
        this.solver.setMaxim();
    }

    private void addRequirementConsraint() throws LpSolveException {
        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                double[] req = new double[nCols+1];
                req[0]=0;
                for (Schedule schedule : slot.getSchedules()) {
                    req[i] = 1;
                    i += 1;
                }

                // add constraint that for each slot requirements should be satisfied
                this.solver.addConstraint(req, LpSolve.EQ, slot.getRequirement());
            }
        }
    }

    private void addSpecialPreferenceConstraint() throws LpSolveException {

        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    // if the user has special req, create constraints
                    if (schedule.getSpecial() != -1) {
                        double[] special = new double[nCols+1];
                        special[0] = 0;
                        special[i] = 1;
                        this.solver.addConstraint(special, LpSolve.EQ, schedule.getSpecial());
                    }
                    i += 1;
                }
            }
        }
    }
    private void addHourLimitConstraint() throws LpSolveException {
        HashMap<Long, ArrayList<Integer>> usersWeek1 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> usersWeek2 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> usersWeek3 = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> usersWeek4 = new HashMap<>();

        // TODO:creat similar per day

        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    //store schedules for each user
                    // TODO:add similar for 2-3-4 weeks and check this
                    if (day.getId() < 7) {
                        if (!usersWeek1.containsKey(schedule.getUser())) {
                            ArrayList<Integer> tmp = new ArrayList<>();
                            tmp.add(i);
                            usersWeek1.put(schedule.getUser().getId(), tmp);
                        }
                    }
                    i += 1;
                }
            }
        }


        // number of hours should not exceed 40 h for each week
        for (Long key : usersWeek1.keySet()) {
            double[] req = new double[nCols];
            for (Integer value : usersWeek1.get(key)) {
                int hours = result.get(value).getSlot().getTimeTo() - result.get(value).getSlot().getTimeFrom(); // TODO: change this
                req[value] = hours;
            }
            this.solver.addConstraint(req, LpSolve.EQ, 40);
        }
    }

    private void readSolution() throws LpSolveException {
        double[] var = solver.getPtrVariables();
        for (int j = 0; j < var.length; j++) {
            this.result.get(j).setAssigned((int) var[j]);
        }
    }



    private void computeN(){
        int i = 0;
        for (Day day:this.teamCalendar.getBasePlan()){
            for (Slot slot: day.getSlots()){
                for (Schedule schedule: slot.getSchedules()){
                    i+=1;
                }
            }
        }
        this.nCols =i;
    }

}

