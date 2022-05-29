package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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

  @OneToMany(mappedBy = "team", cascade = {CascadeType.ALL})//, CascadeType.REFRESH, CascadeType.REMOVE})
  @JsonIgnore
  private Set<Membership> memberships;

  @OneToOne(mappedBy = "team", cascade = CascadeType.ALL)
  @JoinColumn
  private TeamCalendar teamCalendar;
  
  @OneToMany(mappedBy = "team", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JsonIgnore
  private Set<Invitation> invitations;

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


  public Set<Invitation> getInvitations() {
    return invitations;
  }

  public void setInvitations(Set<Invitation> invitations) {
      this.invitations = invitations;
  }
}