package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.PreferenceType;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Day implements Serializable {

    // define composite key

   @Id
   @GeneratedValue
   private Long id;

    @ManyToOne
    @JoinColumn(name = "team_calendar_id", updatable = true, insertable = true)
    private TeamCalendar teamCalendar;

    /*@OneToMany (mappedBy = "day",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private Set<Event> events = new HashSet<Event>();


    public Set<Event> getEvents(){
        return events;
    }

    public void setEvents(Set<Event> events){
        this.events = events;}

     */

    private Weekday weekday;

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



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

