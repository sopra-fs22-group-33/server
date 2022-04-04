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

    private Map<Weekday, Day> concreteDays = new LinkedHashMap<Weekday, Day>();
}
