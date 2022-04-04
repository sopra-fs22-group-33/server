package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;
import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TeamCalendarPostDTO {
    private Long id;
    private Map<Weekday, Day> basePlan = new LinkedHashMap<Weekday, Day>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<Weekday, Day> getBasePlan() {
        return basePlan;
    }

    public void setBasePlan(Map<Weekday, Day> basePlan) {
        this.basePlan = basePlan;
    }
}
