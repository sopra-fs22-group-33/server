package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import java.io.Serializable;

public class DayKey implements Serializable {  //part of the composite key definition

    private TeamCalendar teamCalendar;

    private Weekday weekday;


    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }

    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }

    public void setTeamCalendar(TeamCalendar teamCalendar) {
        this.teamCalendar = teamCalendar;
    }
}
