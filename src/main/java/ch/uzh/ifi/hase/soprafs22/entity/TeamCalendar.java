package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "TeamCalendar")
public class TeamCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    // foreign key of the team entity is used as a primary key
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Team team;

    @Column(nullable = false) // probably delete this
    private String name;

    @OneToMany(mappedBy = "teamCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


