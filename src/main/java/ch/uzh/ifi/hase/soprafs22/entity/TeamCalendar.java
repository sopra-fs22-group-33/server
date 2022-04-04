package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class TeamCalendar implements Serializable {

    @Id
    private Long id;

    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    private Team team;

    @OneToMany(mappedBy = "teamcalendar", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "weekday")
    private Map<Weekday, Day> basePlan = new LinkedHashMap<Weekday, Day>();

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Map<Weekday, Day> getBasePlan() {
        return basePlan;
    }

    public void setBasePlan(Map<Weekday, Day> basePlan) {
        this.basePlan = basePlan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}


//@Embeddable
//class Day {


   // @ElementCollection
    //private ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();}

// @Embeddable
//  class Timeslot {

    //@ManyToMany
    //@JoinColumn(name = "User_id")
//private ArrayList<User> assignedUsers;
//}


