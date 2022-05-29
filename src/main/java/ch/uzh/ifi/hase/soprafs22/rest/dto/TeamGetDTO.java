package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;

import java.util.Set;

public class TeamGetDTO {
    private Long id;
    private String name;
    private Set<Membership> memberships;
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

    public Set<Membership> getMemberships(){
      return memberships;
    }
  
    public void setMemberships(Set<Membership> memberships){
      this.memberships = memberships;
    }

    public Set<Invitation> getInvitations(){
      return invitations;
    }
  
    public void setInvitations(Set<Invitation> invitations){
      this.invitations = invitations;
    }    
}
