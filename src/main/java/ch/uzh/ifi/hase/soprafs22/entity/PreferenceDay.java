package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

public class PreferenceDay implements Serializable {

    // define composite key
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "preferenceCalendar")
    @JsonIgnore
    private PreferenceCalendar preferenceCalendar;

    @OneToMany (mappedBy = "preferenceDay",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<PreferenceSlot> preferenceSlots;

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

    public List<PreferenceSlot> getPreferenceSlots() {
        return preferenceSlots;
    }

    public void setPreferenceSlots(List<PreferenceSlot> preferenceSlots) {
        this.preferenceSlots = preferenceSlots;
    }
}

