package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class PreferenceSlot implements Serializable {
//    private static final long serialVersionUID = 2L;
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int timeFrom;

    @Column(nullable = false)
    private int timeTo;

    @Column
    private int base;

    @ManyToOne
    @JoinColumn(name = "preference_day_id")
    @JsonIgnore
    private PreferenceDay day;

    public int getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(int from) {
        this.timeFrom = from;
    }

    public int getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(int to) {
        this.timeTo = to;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PreferenceDay getDay() {
        return day;
    }

    public void setDay(PreferenceDay day) {
        this.day = day;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }
}
