package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.List;

import ch.uzh.ifi.hase.soprafs22.entity.Team;

public class TeamGetDTO {
    private Long id;
    private String name;

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
    //TODO
    /* public List<Team> getTeams(){
        return team;
    } 
    
    public void addTeam(Team team){
        
    }*/ 
}
