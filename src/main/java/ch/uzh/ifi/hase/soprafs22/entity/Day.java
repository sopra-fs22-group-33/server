package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.PreferenceType;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@IdClass(DayKey.class)
public class Day implements Serializable {

    // define composite key

    @ManyToOne
    @MapsId
    @JoinColumn(name = "team_calendar_id", updatable = true, insertable = true)
    private TeamCalendar teamCalendar;

    @Id
    private Weekday weekday; // change it to storing int 0-30

    @JsonIgnore
    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }


    public void setTeamCalendar(TeamCalendar teamcalendar) {
        this.teamCalendar = teamcalendar;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }





    @OneToMany (mappedBy = "day",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private Set<Event> events = new HashSet<Event>();


    public Set<Event> getEvents(){
        return events;
    }

    public void setEvents(Set<Event> events){
        this.events = events;}

/*
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
     */
}

