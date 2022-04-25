package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Event  implements Serializable {
    @Id
    private Long id;

    @Column(nullable = false)
    private int timeFrom;

    @Column(nullable = false)
    private int timeTo;

    @Column (nullable = true)
    @Type( type = "json" )
    private Map<String, Integer> requirements = new LinkedHashMap<>();

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(
                    name = "weekday",
                    referencedColumnName = "weekday",
                    updatable = true,
                    insertable = true

            ),
            @JoinColumn(
                    name = "team_calendar_id",
                    referencedColumnName = "team_calendar_id",
                    insertable = true,
                    updatable = true
            )
    })
    @JsonIgnore
    private Day day;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<Schedule> schedules;

    public Event() {
    }
    class Role{
        String role;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Map<String, Integer> getRequirements() {
        return requirements;
    }

    public void setRequirements(Map<String, Integer> requirements) {
        this.requirements = requirements;
    }

/*
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "event_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<User> users = new HashSet<User>();/* maybe not required
    @ManyToMany (fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
        @JoinTable(
                name = "event_user",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "event_id"))
        private Set<User>  users = new HashSet<User>();
 */


    /*TODO
    @Column(nullable = false)
    private Map<Role, Quantity> requiredRoles = new LinkedHashMap<<Role, Quantity>();
 */


}
