package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.UserDay;

import java.time.LocalDate;
import java.util.List;

public class UserCalendarPostDTO {

    private LocalDate startingDate;

    private List<UserDay> userDays;

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public  List<UserDay> getUserDays() {
        return this.userDays;
    }

    public void setUserDays(List<UserDay> userDays) {
        this.userDays = userDays;
    }
}
