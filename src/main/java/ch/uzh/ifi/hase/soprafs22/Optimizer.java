package ch.uzh.ifi.hase.soprafs22;


import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;


public class Optimizer {
    private final Logger log = LoggerFactory.getLogger(Optimizer.class);

    int nCols;
    TeamCalendar teamCalendar;
    ArrayList<Schedule> result;
    LpSolve solver;
    int weeklyLimit = 40;
    int dailyLimit = 12;



    public Optimizer(TeamCalendar teamCalendar) throws LpSolveException, ArithmeticException {
        this.teamCalendar = teamCalendar;
        computeN();
        this.result = new ArrayList<>();
        this.solver = LpSolve.makeLp(0, nCols);

        defineObjective();

        addRequirementConstraint();
        addSpecialPreferenceConstraint();

        addInternalCollisionsConstraint();
        addExternalCollisionsConstraint();

        addHourLimitConstraintWeekly();
        addHourLimitConstraintDaily();

        // solve the problem
        this.solver.setMaxim();
        int sol = this.solver.solve();

     // if constraints could be satisfied and all worked
        if(sol == LpSolve.OPTIMAL){
            readSolution();
            this.solver.deleteLp();

        }
        else{ // if not try relaxing constraints
            this.solver.deleteLp();
            solveReducedProblemIgnoreSpecial();
        }
    }

    // always need to account external collisions otherwise cant depict overlapping slots
    /*
    private void solveReducedProblemIgnoreExternalCollisions() throws LpSolveException, ArithmeticException {
        this.result = new ArrayList<>();
        this.solver = LpSolve.makeLp(0, nCols);
        defineObjective();

        addRequirementConstraint();
        addSpecialPreferenceConstraint();

        addInternalCollisionsConstraint();

        addHourLimitConstraintWeekly();
        addHourLimitConstraintDaily();

        // solve the problem
        this.solver.setMaxim();
        int sol = this.solver.solve();

        if(sol == LpSolve.OPTIMAL){
            readSolution();
            this.solver.deleteLp();

        }
        else{ // if not try relaxing constraints further
            this.solver.deleteLp();
            solveReducedProblemIgnoreSpecial();
        }
    }
*/

    private void  solveReducedProblemIgnoreSpecial() throws LpSolveException, ArithmeticException {
        this.result = new ArrayList<>();
        this.solver = LpSolve.makeLp(0, nCols);
        defineObjective();

        addRequirementConstraint();


        addInternalCollisionsConstraint();
        addExternalCollisionsConstraint();


        addHourLimitConstraintWeekly();
        addHourLimitConstraintDaily();

        // solve the problem
        this.solver.setMaxim();
        int sol = this.solver.solve();

        if(sol == LpSolve.OPTIMAL){
            readSolution();
            this.solver.deleteLp();

        }
        else{ // if not try relaxing constraints further
            this.solver.deleteLp();

            throw new ArithmeticException("Not possible to optimize, ask the admin to change his requirements");
        }
    }



    private void defineObjective() throws LpSolveException {
        double[] obj = new double[nCols+1]; // should be 1 longer because 0 element is reserved for the rhs
        int i = 1;
        // 1) create array obj and define obj function, should be done before constraints for better performance
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    this.result.add(schedule);
                    obj[i] = schedule.getBase();
                    this.solver.setBinary(i, true);
                    i++;
                }
            }
        }

        // set objective function
        this.solver.setObjFn(obj);
        this.solver.setMaxim();
    }

    private void addRequirementConstraint() throws LpSolveException {
        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                double[] req = new double[nCols+1];
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
                        special[i] = 1;
                        this.solver.addConstraint(special, LpSolve.EQ, schedule.getSpecial()); // exactly equal to value in special
                    }
                    i += 1;
                }
            }
        }
    }

    private void addHourLimitConstraintWeekly() throws LpSolveException {
        HashMap<Long, ArrayList<Integer>> usersWeek1 = new HashMap<>(); // key: id of the user, value: indices of columns related to him in this particular week
        HashMap<Long, ArrayList<Integer>> usersWeek2 = new HashMap<>();
        HashMap<Long, ArrayList<Integer>> usersWeek3 = new HashMap<>();
        HashMap<Long, ArrayList<Integer>> usersWeek4 = new HashMap<>();

        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {

                    // if week one
                    if (day.getWeekday() < 7) {
                        addToUserHashmap( usersWeek1, schedule, i);
                    }

                    // if week two
                    else if (day.getWeekday() < 14) {
                        addToUserHashmap( usersWeek2, schedule, i);
                    }

                    // if week three
                    else if (day.getWeekday() < 21) {
                        addToUserHashmap( usersWeek3, schedule, i);
                    }

                    // if week four
                    else if (day.getWeekday() < 28) {
                        addToUserHashmap( usersWeek4, schedule, i);
                    }
                    // event if there is some more for week 5, it will be taken into account in daily constraint. 12 by 3 is not exceeding 40
                    i += 1;
                }
            }
        }

        addHourlyConstraint(usersWeek1, this.weeklyLimit);
        addHourlyConstraint(usersWeek2, this.weeklyLimit);
        addHourlyConstraint(usersWeek3, this.weeklyLimit);
        addHourlyConstraint(usersWeek4, this.weeklyLimit);

    }

    private void addToUserHashmap(HashMap<Long, ArrayList<Integer>> usersWeek, Schedule schedule, int i){
        // if the user is not present in the dictionary, add him and his first index
        if (!usersWeek.containsKey(schedule.getUser().getId())) {
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(i);
            usersWeek.put(schedule.getUser().getId(), tmp);
        }
        else{
            // if the user is already there, just add a new index into the list
            usersWeek.get(schedule.getUser().getId()).add(i);
        }
    }

    private void addHourlyConstraint(HashMap<Long, ArrayList<Integer>> usersWeek, int limit) throws LpSolveException {
        // number of hours should not exceed 40 h for each week
        for (Long key : usersWeek.keySet()) {
            double[] req = new double[nCols +1];
            for (Integer idx : usersWeek.get(key)) {
                int hours = result.get(idx-1).getSlot().getTimeTo() - result.get(idx-1).getSlot().getTimeFrom(); // here -1 because I store the result normally ( starting from 0, and in lp_solve they start with 1........)
                req[idx] = hours;
            }
            this.solver.addConstraint(req, LpSolve.LE, limit); // work not more than 40 h
        }
    }

    public void addHourLimitConstraintDaily() throws LpSolveException {
        int i = 1;


        for (Day day : teamCalendar.getBasePlan()) {
            HashMap<Long, ArrayList<Integer>> usersDay = new HashMap<>(); // key: id of the user, value: indices of columns related to him in this particular day
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    addToUserHashmap(usersDay,schedule, i);
                    i += 1;
                }
            }
            addHourlyConstraint(usersDay,  this.dailyLimit);

        }
    }


    private void addInternalCollisionsConstraint() throws LpSolveException {


        // fill out the hashmap
        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            HashMap<Long, ArrayList<Integer>> users = new HashMap<>(); // key: id of the user, value: his slots
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    if ((!schedule.getFinal())&& (!users.containsKey(schedule.getUser().getId()))) {
                        ArrayList<Integer> tmp = new ArrayList<>();
                        tmp.add(i);
                        users.put(schedule.getUser().getId(), tmp);
                    }
                    else{
                        // if the user is already there, just add a new index into the list
                        users.get(schedule.getUser().getId()).add(i);
                    }
                    i += 1;
                }
            }
            for (Long key : users.keySet()) { // for each user
                for (int slot1 : users.get(key)) { // for each slot of that user
                    for (int slot2 : users.get(key)){
                        if (checkForOverlaps(slot1, slot2)){
                            double[] row = new double[nCols +1];
                            row[slot1] =1;
                            row[slot2] =1;
                            this.solver.addConstraint(row, LpSolve.LE, 1);

                        }
                    }
                }
            }
        }

    }

    private Boolean checkForOverlaps(int slot1, int slot2){
        // slot1 starts during slot2, slot2 finishes during slot2, they are the same
       return (((result.get(slot1-1).getSlot().getTimeFrom()< result.get(slot1-1).getSlot().getTimeTo()) && (result.get(slot2-1).getSlot().getTimeFrom()> result.get(slot2-1).getSlot().getTimeFrom()))||((result.get(slot1-1).getSlot().getTimeTo()> result.get(slot2-1).getSlot().getTimeFrom()) && (result.get(slot1-1).getSlot().getTimeTo()< result.get(slot2-1).getSlot().getTimeTo()))||((result.get(slot1-1).getSlot().getTimeFrom()== result.get(slot2-1).getSlot().getTimeFrom()) && (result.get(slot1-1).getSlot().getTimeTo()== result.get(slot2-1).getSlot().getTimeTo())));

    }

    private void addExternalCollisionsConstraint() throws LpSolveException {
        int i = 1;
        for (Day day : teamCalendar.getBasePlan()) {
            for (Slot slot : day.getSlots()) {
                for (Schedule schedule : slot.getSchedules()) {
                    if (checkExternalCollisions(schedule)){ // is the user already has slots assigned at this time, dont assign him here
                        double[] row = new double[nCols +1];
                        row[i] = 1;
                        this.solver.addConstraint(row, LpSolve.EQ, 0);
                    }
                    i += 1;
                }
            }
        }
    }

    private boolean checkExternalCollisions(Schedule schedule){
        boolean res = false;
        for (Schedule anotherSchedule: schedule.getUser().getSchedules()){ // iterate over all the slots the user is assigned to
            if (!Objects.equals(anotherSchedule.getSlot().getDay().getTeamCalendar().getId(), schedule.getSlot().getDay().getTeamCalendar().getId())){ // if the slot belongs to another calendar
                if (anotherSchedule.getAssigned() == 1){ // if the user is already assigned there
                    if (schedule.getSlot().getDay().getTeamCalendar().getStartingDate().plusDays(schedule.getSlot().getDay().getWeekday()).getDayOfMonth() == anotherSchedule.getSlot().getDay().getTeamCalendar().getStartingDateFixed().plusDays(anotherSchedule.getSlot().getDay().getWeekday()).getDayOfMonth() ) { // if it is the same day
                        // if our schedule starts WHILE another schedule, or finishes WHILE another schedule, or they conicide
                        if (((schedule.getSlot().getTimeFrom()> anotherSchedule.getSlot().getTimeFrom()) && (schedule.getSlot().getTimeFrom()< anotherSchedule.getSlot().getTimeTo()))||((schedule.getSlot().getTimeTo()> anotherSchedule.getSlot().getTimeFrom()) && (schedule.getSlot().getTimeTo()< anotherSchedule.getSlot().getTimeTo()))||((schedule.getSlot().getTimeFrom()== anotherSchedule.getSlot().getTimeFrom()) && (schedule.getSlot().getTimeTo()== anotherSchedule.getSlot().getTimeTo()))){
                            res = true;
                        }
                    }
                }
            }
        }
        return res;
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
    // TODO: check all the conditions once again
}

