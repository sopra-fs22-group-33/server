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
    private Long From;

    private Long To;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getFrom() {
        return From;
    }

    public void setFrom(Long from) {
        From = from;
    }

    public Long getTo() {
        return To;
    }

    public void setTo(Long to) {
        To = to;
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
