package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

public class Day implements Serializable {

    // define composite key
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_calendar_id", updatable = true, insertable = true)
    private TeamCalendar teamCalendar;

    private int weekday;

    @JsonIgnore
    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }


    public void setTeamCalendar(TeamCalendar teamcalendar) {
        this.teamCalendar = teamcalendar;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    @OneToMany (mappedBy = "day",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Slot> slots;


    public List<Slot> getSlots(){
        return slots;
    }

    public void setSlots(List<Slot> slots){
        this.slots = slots;}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

