package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class TeamController {

  private final TeamService teamService;

  TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @GetMapping("/teams")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<TeamGetDTO> getAllTeams() {
    List<Team> teams = teamService.getTeams();
    List<TeamGetDTO> teamGetDTOs = new ArrayList<>();

    for (Team team : teams) {
      teamGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team));
    }
    return teamGetDTOs;
  }

  @PostMapping("/teams")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public TeamGetDTO createTeam(@RequestBody TeamPostDTO teamPostDTO) {
    // convert API team to internal representation
    Team userInput = DTOMapper.INSTANCE.convertTeamPostDTOtoEntity(teamPostDTO);

    // create team
    Team createdTeam = teamService.createTeam(userInput);

    // convert internal representation of team back to API
    return DTOMapper.INSTANCE.convertEntityToTeamGetDTO(createdTeam);
  }

  @PutMapping("/teams/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public TeamGetDTO updateTeam(@RequestBody TeamPostDTO teamPostDTO, @PathVariable("id") long id) {
    // convert API user to internal representation
    Team userInput = DTOMapper.INSTANCE.convertTeamPostDTOtoEntity(teamPostDTO);

    // update user
    Team updatedTeam = teamService.updateTeam(userInput, id);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToTeamGetDTO(updatedTeam);
  }

  @DeleteMapping("/teams/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteTeam(@PathVariable("id") long id){
    teamService.deleteTeam(id);
  }
}
