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

    // TODO: add overlapping constraint and daily limit constraint

    public Optimizer(TeamCalendar teamCalendar) throws LpSolveException {
        this.teamCalendar = teamCalendar;
        computeN();
        this.result = new ArrayList<>();
        this.solver = LpSolve.makeLp(0, nCols);

        defineObjective();
        addRequirementConstraint();
        addSpecialPreferenceConstraint();
        addHourLimitConstraintWeekly();

        // solve the problem
        this.solver.setMaxim();
        int sol = this.solver.solve();

        // TODO: if the solution returned is optimal, store it else  relax some constraints
        readSolution();
        this.solver.deleteLp();
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
                        this.solver.addConstraint(special, LpSolve.EQ, schedule.getSpecial());
                    }
                    i += 1;
                }
            }
        }
    }

    private void addHourLimitConstraintWeekly() throws LpSolveException {
        HashMap<Long, ArrayList<Integer>> usersWeek1 = new HashMap<>(); // id of the user, indices of columns related to him
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

                    // if week three
                    else if (day.getWeekday() < 28) {
                        addToUserHashmap( usersWeek4, schedule, i);
                    }

                    i += 1;
                }
            }
        }

        addConstraintWeekly(usersWeek1);
        addConstraintWeekly(usersWeek2);
        addConstraintWeekly(usersWeek3);
        addConstraintWeekly(usersWeek4);

    }

    private void addToUserHashmap(HashMap<Long, ArrayList<Integer>> usersWeek, Schedule schedule, int i){
        // if the user is not present in the dictionary, add him and his first index
        if (!usersWeek.containsKey(schedule.getUser())) {
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(i);
            usersWeek.put(schedule.getUser().getId(), tmp);
        }
        else{
            // if the user is already there, just add a new index into the list
            usersWeek.get(schedule.getUser().getId()).add(i);
        }
    }

    public void addConstraintWeekly(HashMap<Long, ArrayList<Integer>> usersWeek) throws LpSolveException {
        // number of hours should not exceed 40 h for each week
        for (Long key : usersWeek.keySet()) {
            double[] req = new double[nCols +1];
            for (Integer idx : usersWeek.get(key)) {
                int hours = result.get(idx).getSlot().getTimeTo() - result.get(idx).getSlot().getTimeFrom();
                req[idx] = hours;
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

