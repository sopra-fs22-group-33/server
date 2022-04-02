package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.Set;

import ch.uzh.ifi.hase.soprafs22.entity.User;

public class TeamGetDTO {
    private Long id;
    private String name;
    private Set<User> users;

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

    public Set<User> getUsers(){
      return users;
    }
  
    public void setUsers(Set<User> users){
      this.users = users;
    }
}
