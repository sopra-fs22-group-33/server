package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;

import java.util.*;

@Entity
public class Event  implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne (cascade = CascadeType.ALL)
    private Day day;
/*
    @ManyToMany (fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "event_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<User>  users = new HashSet<User>();
*/
    private int from;

    private int to;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }
/*T
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "event_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<User> users = new HashSet<User>();/* maybe not required


    /*TODO
    @Column(nullable = false)
    private Map<Role, Quantity> requiredRoles = new LinkedHashMap<<Role, Quantity>();
 */


}
