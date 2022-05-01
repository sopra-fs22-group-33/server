package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import java.io.Serializable;

public class DayKey implements Serializable {  //part of the composite key definition

    private TeamCalendar teamCalendar;

    private int weekday;


    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }

    public void setTeamCalendar(TeamCalendar teamCalendar) {
        this.teamCalendar = teamCalendar;
    }
}