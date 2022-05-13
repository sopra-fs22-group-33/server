package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;
import ch.uzh.ifi.hase.soprafs22.entity.UserDay;

import java.util.List;

public class PreferenceCalendarGetDTO {

    private Long id;

    private List <PreferenceDay> preferenceDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PreferenceDay> getPreferenceDays() {
        return preferenceDays;
    }

    public void setPreferenceDays(List<PreferenceDay> preferenceDays) {
        this.preferenceDays = preferenceDays;
    }
}
