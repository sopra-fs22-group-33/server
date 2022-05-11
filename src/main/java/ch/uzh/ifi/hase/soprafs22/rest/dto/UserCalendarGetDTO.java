package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.UserDay;

import java.util.List;

public class UserCalendarGetDTO {

    private Long id;

    private String startingDate;

    private List <UserDay> userDays;

    public void setUserDays(List<UserDay> userDays) {
        this.userDays = userDays;
    }

    public  List <UserDay> getUserDays() {
        return userDays;
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
