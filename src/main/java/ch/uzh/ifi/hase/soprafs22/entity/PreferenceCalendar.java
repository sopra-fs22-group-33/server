package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "PreferenceCalendar")
public class PreferenceCalendar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user", updatable = true, insertable = true)
    @MapsId
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "preferenceCalendar", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<PreferenceDay> preferencePlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PreferenceDay> getPreferencePlan() {
        return preferencePlan;
    }

    public void setPreferencePlan(List<PreferenceDay> preferencePlan) {
        this.preferencePlan = preferencePlan;
    }
}
