package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.Set;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;

public class UserPostDTO {

  private String username;
  private String email;
  private String password;
  private UserStatus status;
  private Set<Membership> memberships;
  private Set<Invitation> invitations;

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Set<Membership> getMemberships() {
      return memberships;
  }

  public void setMemberships(Set<Membership> memberships) {
      this.memberships = memberships;
  }

  public Set<Invitation> getInvitations(){
    return invitations;
  }

  public void setInvitations(Set<Invitation> invitations){
    this.invitations = invitations;
  } 
}
