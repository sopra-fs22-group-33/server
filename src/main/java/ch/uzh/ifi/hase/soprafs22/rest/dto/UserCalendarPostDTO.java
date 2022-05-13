package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.UserDay;

import java.time.LocalDate;
import java.util.List;

public class UserCalendarPostDTO {

    private LocalDate startingDate;

    private List<UserDay> days;

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public  List<UserDay> getDays() {
        return this.days;
    }

    public void setDays(List<UserDay> days) {
        this.days = days;
    }
}
