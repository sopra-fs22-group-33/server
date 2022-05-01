package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.*;

import ch.uzh.ifi.hase.soprafs22.entity.Day;

public class TeamCalendarGetDTO {

    private Long id;

    private String startingDate;

    private List <Day> days;

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public  List <Day> getDays() {
        return days;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }
}
