package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.Set;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;

public class TeamPostDTO {
    private Long id;
    private String name;
    private Set<Membership> memberships;

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
    
    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }
  
    public Set<Membership> getMemberships() {
        return memberships;
    }
}
