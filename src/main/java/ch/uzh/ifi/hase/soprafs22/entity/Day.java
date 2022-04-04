package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.PreferenceType;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@IdClass(DayKey.class)
public class Day implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "team_calendar_id")
    private TeamCalendar teamcalendar;


    @Id
    private Weekday weekday;


    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Event> events = new HashSet<Event>();

    public TeamCalendar getTeamcalendar() {
        return teamcalendar;
    }

    public void setTeamcalendar(TeamCalendar teamcalendar) {
        this.teamcalendar = teamcalendar;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }
}

class DayKey implements Serializable{
    @ManyToOne
    @JoinColumn(name = "team_calendar_id")
    private TeamCalendar teamcalendar;

    private Weekday weekday;

}