package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;

import java.util.List;

public class PreferenceCalendarPostDTO {

    private Long id;

    private List <PreferenceDay> days;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PreferenceDay> getDays() {
        return days;
    }

    public void setDays(List<PreferenceDay> days) {
        this.days = days;
    }
}
