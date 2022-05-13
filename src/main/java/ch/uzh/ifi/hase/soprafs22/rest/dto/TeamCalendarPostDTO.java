package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Day;

import java.time.LocalDate;
import java.util.*;

public class TeamCalendarPostDTO {

    private LocalDate startingDate;
    private List<Day> days;

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public  List<Day> getDays() {
        return this.days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
