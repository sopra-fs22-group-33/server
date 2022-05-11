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

    @JoinColumn
    private int weekday;

    @OneToMany (mappedBy = "userDay",  cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Slot> slots;

    public UserCalendar getUserCalendar() {
        return userCalendar;
    }

    public void setUserCalendar(UserCalendar userCalendar) {
        this.userCalendar = userCalendar;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }


    public List<Slot> getSlots(){
        return slots;
    }

    public void setSlots(List<Slot> slots){
        this.slots = slots;}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

