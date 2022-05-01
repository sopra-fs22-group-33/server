package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Day;

import java.util.*;

public class TeamCalendarPostDTO {

    private String startingDate;
    private List<Day> days;

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public  List<Day> getDays() {
        return this.days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
