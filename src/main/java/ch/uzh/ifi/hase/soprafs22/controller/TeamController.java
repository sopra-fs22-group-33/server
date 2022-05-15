package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.MembershipService;
import ch.uzh.ifi.hase.soprafs22.service.TeamService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RestController
public class TeamController {

  private final TeamService teamService;
  private final UserService userService;
  private final MembershipService membershipService;

  TeamController(TeamService teamService, UserService userService, MembershipService membershipService) {
    this.teamService = teamService;
    this.userService = userService;
    this.membershipService = membershipService;
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

  @GetMapping("/teams/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public TeamGetDTO getTeam(@PathVariable("id") long id) {
    Team team = teamService.findTeamById(id);
    TeamGetDTO teamGetDTO = DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team);
    
    return teamGetDTO;
  }

  @PutMapping("/teams/{teamId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public TeamGetDTO updateTeam(@RequestBody TeamPostDTO teamPostDTO, @PathVariable("teamId") long teamId, @RequestHeader("token") String token) {
    // convert API user to internal representation
    Team userInput = DTOMapper.INSTANCE.convertTeamPostDTOtoEntity(teamPostDTO);

    // update user
    Team updatedTeam = teamService.updateTeam(userInput, teamId, token);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToTeamGetDTO(updatedTeam);
  }

    @PutMapping("/teams/{teamId}/notify")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void notifyUsersToProvidePreferences( @RequestBody ArrayList<Long> userIds,@PathVariable("teamId") long id) {
        teamService.notifyUsersToUpdatePreferences(userIds, id);

    }

  @DeleteMapping("/teams/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteTeam(@PathVariable("id") long id, @RequestHeader("token") String token){
    teamService.deleteTeam(id, token);
  }

  @GetMapping("/teams/{teamId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsersByTeamId(@PathVariable("teamId") long teamId, @RequestHeader("token") String token) {
    if (membershipService.findMembership(teamService.findTeamById(teamId), userService.findUserByToken(token).getId()) != null){
      Set<User> users = teamService.getAllUsersOfTeam(teamId);
      List<UserGetDTO> userGetDTOs = new ArrayList<>();

      for (User user : users) {
        userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
      }
      return userGetDTOs;
    }return null;
  }

  @DeleteMapping("/users/{userId}/teams/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteTeamFromUser(@PathVariable("userId") long userId, @PathVariable("teamId") long teamId, @RequestHeader("token") String token){
    Team team = teamService.findTeamById(teamId);
    if (userService.authorizeUser(userId, token)){
      membershipService.deleteMembership(team, userId);
    }
  }
}
