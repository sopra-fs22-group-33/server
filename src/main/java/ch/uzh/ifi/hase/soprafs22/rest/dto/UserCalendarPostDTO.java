package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.UserDay;

import java.util.List;

public class UserCalendarPostDTO {

    private String startingDate;
    private List<UserDay> userDays;

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public  List<UserDay> getUserDays() {
        return this.userDays;
    }

    public void setUserDays(List<UserDay> userDays) {
        this.userDays = userDays;
    }
}
