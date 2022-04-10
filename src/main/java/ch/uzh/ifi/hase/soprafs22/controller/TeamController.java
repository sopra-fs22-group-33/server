package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.MembershipService;
import ch.uzh.ifi.hase.soprafs22.service.TeamService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class TeamController {

  private final TeamService teamService;
  private final UserService userService;

  TeamController(TeamService teamService, UserService userService, MembershipService membershipService) {
    this.teamService = teamService;
    this.userService = userService;
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
  public TeamGetDTO createTeam(@RequestBody TeamPostDTO teamPostDTO, @RequestHeader("token") String token) {
    // convert API team to internal representation
    Team teamToCreate = DTOMapper.INSTANCE.convertTeamPostDTOtoEntity(teamPostDTO);

    // create team   
    User user = userService.findUserByToken(token);
    Team createdTeam = teamService.createTeam(teamToCreate, user);
    
    // convert internal representation of team back to API
    return DTOMapper.INSTANCE.convertEntityToTeamGetDTO(createdTeam);
  }

  @PutMapping("/teams/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public TeamGetDTO updateTeam(@RequestBody TeamPostDTO teamPostDTO, @PathVariable("id") long id, @RequestHeader("token") String token) {
    // convert API user to internal representation
    Team userInput = DTOMapper.INSTANCE.convertTeamPostDTOtoEntity(teamPostDTO);

    // update user
    Team updatedTeam = teamService.updateTeam(userInput, id, token);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToTeamGetDTO(updatedTeam);
  }

  @DeleteMapping("/teams/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteTeam(@PathVariable("id") long id){
    teamService.deleteTeam(id);
  }

  // @GetMapping("/users/{userId}/teams")
  // @ResponseStatus(HttpStatus.OK)
  // @ResponseBody
  // public List<TeamGetDTO> getAllTeamsByUserId(@PathVariable("userId") long userId) {
  //   List<Team> teams = userService.getAllTeamsOfUser(userId);
  //   List<TeamGetDTO> teamGetDTOs = new ArrayList<>();

  //   for (Team team : teams) {
  //     teamGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team));
  //   }
  //   return teamGetDTOs;
  // }

  @DeleteMapping("/users/{userId}/teams/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteTeamFromUser(@PathVariable("userId") long userId, @PathVariable("teamId") long teamId){
    //TODO
  }
}
