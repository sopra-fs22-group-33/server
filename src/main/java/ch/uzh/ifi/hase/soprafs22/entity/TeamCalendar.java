package ch.uzh.ifi.hase.soprafs22.entity;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
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
    private LocalDate startingDate;

    @Column
    private LocalDate startingDateFixed;

    @Column
    private int collisions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name = "base")
    private List<Day> basePlan;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinTable(name = "fixed")
    private List<Day> basePlanFixed;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Day> getBasePlan() { return basePlan; }

    public void setBasePlan(List<Day> basePlan) { this.basePlan = basePlan;}

    public LocalDate getStartingDate() { return startingDate;}

    public void setStartingDate(LocalDate startingDate) {
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

    public LocalDate getStartingDateFixed() {
        return startingDateFixed;
    }

    public void setStartingDateFixed(LocalDate startingDateFixed) {
        this.startingDateFixed = startingDateFixed;
    }

    public List<Day> getBasePlanFixed() {
        return basePlanFixed;
    }

    public void setBasePlanFixed(List<Day> basePlanFixed) {
        this.basePlanFixed = basePlanFixed;
    }
}



