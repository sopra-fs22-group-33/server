package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.Set;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Team;

public class UserGetDTO {

  private Long id;
  private String username;
  private String email;
  private UserStatus status;
  private Set<Team> teams;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Set<Team> getTeams(){
    return teams;
  }

  public void setTeams(Set<Team> teams){
    this.teams = teams;
  }
}
