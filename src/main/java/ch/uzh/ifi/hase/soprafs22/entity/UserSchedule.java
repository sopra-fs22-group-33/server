package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "USERSCHEDULE")
public class UserSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne ()
    @JoinColumn(name = "userSlot")
    @JsonIgnore
    private UserSlot userSlot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team")
    private Team team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSlot getUserSlot() {
        return userSlot;
    }

    public void setUserSlot(UserSlot userSlot) {
        this.userSlot = userSlot;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}