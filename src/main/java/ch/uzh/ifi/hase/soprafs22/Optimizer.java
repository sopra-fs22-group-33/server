package ch.uzh.ifi.hase.soprafs22;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import ch.uzh.ifi.hase.soprafs22.entity.*;

import java.util.*;


public class Optimizer {

    int nRows = 4;
    ArrayList<Schedule> result= new ArrayList<>();

    double[] obj = new double[nRows];
    HashMap<Integer, ArrayList<Integer>> usersWeek1 = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> usersWeek2 = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> usersWeek3 = new HashMap<>();
    HashMap<Integer, ArrayList<Integer>> usersWeek4 = new HashMap<>();
    // TODO:create similar per day
    // TODO: add overlapping constraint or remove such a possibility

    TeamCalendar teamCalendar = new TeamCalendar();

    public String optimize() throws LpSolveException {
        LpSolve solver = LpSolve.makeLp(0, nRows);
        int i = 0;
        for (Day day:teamCalendar.getDays()){
            for (Slot slot: day.getSlots()){
                double[] req = new double[nRows];
                for (Schedule schedule: slot.getSchedules()){

                    result.add(schedule);
                    int b = schedule.getBase(); // cast to double to make sure
                    obj[i] = b;
                    solver.setBinary(i, true);
                    req[i] = 1;

                    //store schedules for each user
                    // TODO:add similar for 2-3-4 weeks
                    if (day.getId()<7) {
                        if (!usersWeek1.containsKey(schedule.getUser())) {
                            ArrayList<Integer> tmp = new ArrayList<>();
                            tmp.add(i);
                            usersWeek1.put(schedule.getUser(), tmp);
                        }
                    }

                    // special req whenever they are present should be satisfied
                    if(schedule.getSpecial()!= -1){
                        double[] special = new double[nRows];
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
        for (Integer key :usersWeek1.keySet()){
            double[] req = new double[nRows];
            for (Integer value: usersWeek1.get(key)){
                int hours = result.get(value).To - result.get(value).From; // TODO: change this
                req[value] = hours;
            }
            solver.addConstraint(req, LpSolve.EQ, 40);

        }

        // set objective function
        solver.setObjFn(obj);
        solver.setMaxim();

        // solve the problem
        solver.solve();

        // print solution
        System.out.println("Value of objective function: " + solver.getObjective());

        return "works The application is running" + Double.toString(solver.getObjective());

    }

}

