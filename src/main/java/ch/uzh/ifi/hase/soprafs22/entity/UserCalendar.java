package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "UserCalendar")
public class UserCalendar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user", updatable = true, insertable = true)
    @MapsId
    @JsonIgnore
    private User user;

    @Column
    private LocalDate startingDate;

    @OneToMany(mappedBy = "userCalendar", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<UserDay> userPlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartingDate() { return startingDate;}

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserDay> getUserPlan() {
        return userPlan;
    }

    public void setUserPlan(List<UserDay> userPlan) {
        this.userPlan = userPlan;
    }
}
