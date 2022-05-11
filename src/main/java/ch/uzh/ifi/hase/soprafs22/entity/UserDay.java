package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity

public class UserDay implements Serializable {

    // define composite key
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userCalendar")
    @JsonIgnore
    private UserCalendar userCalendar;

    @OneToMany (mappedBy = "userDay",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<UserSlot> userSlots;

    public UserCalendar getUserCalendar() {
        return userCalendar;
    }

    public void setUserCalendar(UserCalendar userCalendar) {
        this.userCalendar = userCalendar;
    }

    public List<UserSlot> getUserSlots(){
        return userSlots;
    }

    public void setUserSlots(List<UserSlot> userSlots){
        this.userSlots = userSlots;}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

