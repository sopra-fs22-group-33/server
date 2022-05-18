package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

public class PreferenceDay implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne (cascade = {CascadeType.ALL, CascadeType.REFRESH})
    @JoinColumn(name = "preference_calendar_id")
    @JsonIgnore
    private PreferenceCalendar preferenceCalendar;

    @JoinColumn
    private int weekday;

    @OneToMany (mappedBy = "day", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PreferenceSlot> slots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PreferenceCalendar getPreferenceCalendar() {
        return preferenceCalendar;
    }

    public void setPreferenceCalendar(PreferenceCalendar preferenceCalendar) {
        this.preferenceCalendar = preferenceCalendar;
    }

    public List<PreferenceSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<PreferenceSlot> slots) {
        this.slots = slots;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }
}

