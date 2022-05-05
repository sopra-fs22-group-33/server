package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TeamCalendar")
public class TeamCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "team", updatable = true, insertable = true)
    @MapsId
    @JsonIgnore
    private Team team;

    @Column
    private String startingDate;

    @Column
    private int collisions;


    @OneToMany(mappedBy = "teamCalendar", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Day> basePlan;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Day> getBasePlan() { return basePlan; }

    public void setBasePlan(List<Day> basePlan) { this.basePlan = basePlan;}

    public String getStartingDate() { return startingDate;}

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCollisions() {
        return collisions;
    }

    public void setCollisions(int collisions) {
        this.collisions = collisions;
    }
}



