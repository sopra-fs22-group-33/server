package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "SCHEDULE")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne ()
    @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;



    @Column
    private String role;

    @Column
    private Long basePreference;

    @Column
    private Long specialPreference;

    @Column
    private int assigned = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getBasePreference() {
        return basePreference;
    }

    public void setBasePreference(Long basePreference) {
        this.basePreference = basePreference;
    }

    public Long getSpecialPreference() {
        return specialPreference;
    }

    public void setSpecialPreference(Long specialPreference) {
        this.specialPreference = specialPreference;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAssigned() {
        return assigned;
    }

    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }

}