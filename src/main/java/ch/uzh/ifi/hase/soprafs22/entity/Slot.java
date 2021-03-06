package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Slot implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int timeFrom;

    @Column(nullable = false)
    private int timeTo;

    @ManyToOne
    @JoinColumn(name = "day_id")
    @JsonIgnore
    private Day day;


    @Column
    private int requirement;


    @OneToOne(mappedBy = "slot", cascade = CascadeType.REMOVE)
    @JsonIgnore
    public Game game;


    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    public Day getDay() {
        return day;
    }



    public void setDay(Day day) {
        this.day = day;
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

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRequirement() {
        return this.requirement;
    }

    public void setRequirement(int requirement) {
        this.requirement = requirement;
    }


}
