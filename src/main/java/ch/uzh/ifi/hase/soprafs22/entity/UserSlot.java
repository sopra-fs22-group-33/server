package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class UserSlot implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private int timeFrom;

    @Column(nullable = false)
    private int timeTo;

    @ManyToOne
    @JoinColumn(name = "user_day_id")
    @JsonIgnore
    private UserDay day;

    @OneToMany(mappedBy = "slot", cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private List<UserSchedule> schedules;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDay getDay() {
        return day;
    }

    public void setDay(UserDay day) {
        this.day = day;
    }

    public List<UserSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<UserSchedule> schedules) {
        this.schedules = schedules;
    }
}
