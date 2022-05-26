package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.time.LocalDate;
import java.util.*;

import ch.uzh.ifi.hase.soprafs22.entity.Day;

public class TeamCalendarGetDTO {

    private Long id;

    private Boolean isBusy;

    private LocalDate startingDate;

    private List <Day> days;

    private LocalDate startingDateFixed;

    private List <Day> daysFixed;

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

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDate getStartingDateFixed() {
        return startingDateFixed;
    }

    public List<Day> getDaysFixed() {
        return daysFixed;
    }

    public void setDaysFixed(List<Day> daysFixed) {
        this.daysFixed = daysFixed;
    }

    public void setStartingDateFixed(LocalDate startingDateFixed) {
        this.startingDateFixed = startingDateFixed;
    }

    public Boolean getBusy() {
        return isBusy;
    }

    public void setBusy(Boolean busy) {
        isBusy = busy;
    }
}
