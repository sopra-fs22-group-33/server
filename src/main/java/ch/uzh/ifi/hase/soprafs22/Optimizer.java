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

    double[] obj;
    HashMap<Long, ArrayList<Integer>> usersWeek1 = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> usersWeek2 = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> usersWeek3 = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> usersWeek4 = new HashMap<>();

    // TODO:create similar per day
    // TODO: add overlapping constraint or remove such a possibility

    public Optimizer(TeamCalendar teamCalendar) throws LpSolveException {
        this.teamCalendar = teamCalendar;
        computeN();

        // intialize arrays
        this.obj = new double[nCols];
        this.result =  new ArrayList<>();

        this.solver = LpSolve.makeLp(0, nCols);
        int i = 0;

        // 1) create array obj and define obj function, should be done before constraints for better performance
        for (Day day:teamCalendar.getBasePlan()){
            for (Slot slot: day.getSlots()){
                for (Schedule schedule: slot.getSchedules()){
                    this.result.add(schedule);
                    double b =  schedule.getBase();
                    obj[i] = b;
                    this.solver.setBinary(i+1,true); // if this does not work change to i, because I dont know yet exactly at which point 0 column is reserved for rhs
                    i++;
                }
            }
        }

        // set objective function
        solver.setObjFn(obj);
        solver.setMaxim();


        //define constraints


        i = 0;
        for (Day day:teamCalendar.getBasePlan()){
            for (Slot slot: day.getSlots()){
                double[] req = new double[nCols];
                for (Schedule schedule: slot.getSchedules()){

                    //this.solver.setBinary(i, true);
                    req[i] = 1;

                    //store schedules for each user
                    // TODO:add similar for 2-3-4 weeks
                    if (day.getId()<7) {
                        if (!usersWeek1.containsKey(schedule.getUser())) {
                            ArrayList<Integer> tmp = new ArrayList<>();
                            tmp.add(i);
                            usersWeek1.put(schedule.getUser().getId(), tmp);
                        }
                    }

                    // special req whenever they are present should be satisfied
                    if(schedule.getSpecial()!= -1){
                        double[] special = new double[nCols];
                        special[i] = 1;
                        solver.addConstraint(special, LpSolve.EQ, schedule.getSpecial());
                    }
                    i+=1;
                }

                // add constraint that for each slot requirements should be satisfied
                solver.addConstraint(req, LpSolve.EQ, slot.getRequirement());
            }

        }



        // number of hours should not exceed 40 h for each week
        for (Long key :usersWeek1.keySet()){
            double[] req = new double[nCols];
            for (Integer value: usersWeek1.get(key)){
                int hours = result.get(value).getSlot().getTimeTo() - result.get(value).getSlot().getTimeFrom(); // TODO: change this
                req[value] = hours;
            }
            solver.addConstraint(req, LpSolve.EQ, 40);

        }


        // solve the problem
        solver.solve();
        double [] solution = solver.getPtrPrimalSolution();


        // add solution to assigned
        for ( int j = 0; j< solution.length; j++){
           this.result.get(j).setAssigned((int)solution[j]);
        }


        // print resulting value of the objective function
        System.out.println("Value of objective function: " + solver.getObjective());

    }

    public void computeN(){
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

