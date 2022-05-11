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
    @JoinColumns(value = {
            @JoinColumn(
                    name = "weekday",
                    referencedColumnName = "weekday",
                    updatable = true,
                    insertable = true

            ),
            @JoinColumn(
                    name = "userCalendar",
                    referencedColumnName = "userCalendar",
                    insertable = true,
                    updatable = true
            )
    })
    @JsonIgnore
    private UserDay userDay;

    @OneToMany(mappedBy = "userSlot", cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private List<UserSchedule> UserSchedules;

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

    public UserDay getUserDay() {
        return userDay;
    }

    public void setUserDay(UserDay userDay) {
        this.userDay = userDay;
    }

    public List<UserSchedule> getUserSchedules() {
        return UserSchedules;
    }

    public void setUserSchedules(List<UserSchedule> userSchedules) {
        UserSchedules = userSchedules;
    }
}
