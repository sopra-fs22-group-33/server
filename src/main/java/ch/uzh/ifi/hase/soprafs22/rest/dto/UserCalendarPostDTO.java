package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.UserDay;

import java.util.List;

public class UserCalendarPostDTO {

    private long startingDate;

    private List<UserDay> userDays;

    public long getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(long startingDate) {
        this.startingDate = startingDate;
    }

    public  List<UserDay> getUserDays() {
        return this.userDays;
    }

    public void setUserDays(List<UserDay> userDays) {
        this.userDays = userDays;
    }
}
