package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import javax.persistence.ElementCollection;

public class TeamCalendarGetDTO {
    private Long id;
    private String name;
    private Map<Weekday, Day> basePlan;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


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
