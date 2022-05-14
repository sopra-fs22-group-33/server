package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;

import java.util.List;

public class PreferenceCalendarPostDTO {

    private List <PreferenceDay> days;

    public List<PreferenceDay> getDays() {
        return days;
    }

    public void setDays(List<PreferenceDay> days) {
        this.days = days;
    }
}
