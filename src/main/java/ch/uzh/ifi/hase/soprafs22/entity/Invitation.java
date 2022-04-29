package ch.uzh.ifi.hase.soprafs22.entity;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "INVITATION")
public class Invitation implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne (cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "team", nullable = false)
  private Team team;

  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "user", nullable = false)
  private User user; 
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}