package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Event  implements Serializable {
    @Id
    @GeneratedValue
    private int id;

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


        public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    private int timeFrom;

    private int timeTo;


    public int getId() {
        return id;
    }

    public void setId(int id) {
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
