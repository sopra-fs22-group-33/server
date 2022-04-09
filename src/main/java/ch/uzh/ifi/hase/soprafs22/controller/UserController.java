package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import ch.uzh.ifi.hase.soprafs22.service.TeamService;
import ch.uzh.ifi.hase.soprafs22.service.MembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;
  private final TeamService teamService;
  private final MembershipService membershipService;

  UserController(UserService userService, TeamService teamService, MembershipService membershipService) {
    this.userService = userService;
    this.teamService = teamService;
    this.membershipService = membershipService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public UserGetDTO updateUser(@RequestBody UserPostDTO userPostDTO, @PathVariable("id") long id) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // update user
    User updatedUser = userService.updateUser(userInput, id);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
  }

  @DeleteMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteUser(@PathVariable("id") long id){
    userService.deleteUser(id);
  }
  //to be changed
  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO){
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User returnUser = userService.loginUser(userInput);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(returnUser);
  }

  @GetMapping("/teams/{teamId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsersByTeamId(@PathVariable("teamId") long teamId) {
    Set<User> users = teamService.getAllUsersOfTeam(teamId);
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @GetMapping("/users/{userId}/teams")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Set<TeamGetDTO> getAllTeamsByUserId(@PathVariable("userId") long userId) {
    Set<Team> teams = userService.getAllTeamsOfUser(userId);
    Set<TeamGetDTO> teamGetDTOs = new HashSet<>();

    for (Team team : teams) {
      teamGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team));
    }
    return teamGetDTOs;
  }

  @PostMapping("/teams/{teamId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO addUser(@RequestBody UserPostDTO userPostDTO, @PathVariable("teamId") long teamId){
    User userToAdd = userService.findUserByEmail(userPostDTO.getEmail());
    Team team = teamService.findTeamById(teamId);
    User addedUser = membershipService.createMembership(team, userToAdd);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(addedUser);
  }


  @DeleteMapping("/teams/{teamId}/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteUserFromTeam(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId){
    //TODO
  }
}
