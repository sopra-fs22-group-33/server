package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TeamCalendarPostDTO {

    private String name;
    private Map<Weekday, Day> basePlan;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Weekday, Day> getBasePlan() {
        return basePlan;
    }

    public void setBasePlan(Map<Weekday, Day> basePlan) {
        this.basePlan = basePlan;
    }
}
