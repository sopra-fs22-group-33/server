package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.PreferenceType;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@IdClass(DayKey.class)
public class Day implements Serializable {

    // define composite key

    @ManyToOne
    @MapsId
    @JoinColumn(name = "team_calendar_id")
    private TeamCalendar teamCalendar;

    @Id
    private Weekday weekday;

    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }

    @OneToMany (mappedBy = "day", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Event> events = new HashSet<Event>();

    public void setTeamCalendar(TeamCalendar teamcalendar) {
        this.teamCalendar = teamcalendar;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }



     public Set<Event> getEvents(){
        return events;
    }

    public void setEvents(Set<Event> events){
        this.events = events;}


}

