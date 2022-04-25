package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "TEAM")
public class Team implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
  @JsonIgnore
  private Set<Membership> memberships;

  @OneToOne(mappedBy = "team", cascade = CascadeType.ALL)
  @JoinColumn
  private TeamCalendar teamCalendar;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Membership> getMemberships() {
      return memberships;
  }

  public void setMemberships(Set<Membership> memberships) {
      this.memberships = memberships;
  }

    public TeamCalendar getTeamCalendar() {
        return teamCalendar;
    }

    public void setTeamCalendar(TeamCalendar teamCalendar) {
        this.teamCalendar = teamCalendar;
    }


}