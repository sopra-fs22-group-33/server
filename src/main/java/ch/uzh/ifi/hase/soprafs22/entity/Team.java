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

  @OneToMany(mappedBy = "team", cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  @JsonIgnore
  private Set<Membership> memberships;

  @OneToMany(mappedBy = "team", cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
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

  public Set<Invitation> getInvitations() {
    return invitations;
  }

  public void setInvitations(Set<Invitation> invitations) {
      this.invitations = invitations;
  }
}