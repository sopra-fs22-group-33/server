package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.UserCalendar;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserCalendarGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
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
  private final InvitationService invitationService;
  private final UserCalendarService userCalendarService;

    UserController(UserService userService, TeamService teamService, MembershipService membershipService, InvitationService invitationService, UserCalendarService userCalendarService) {
    this.userService = userService;
    this.teamService = teamService;
    this.membershipService = membershipService;
    this.invitationService = invitationService;
    this.userCalendarService = userCalendarService;
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
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);

    //create header with token
    response.setHeader("Access-Control-Expose-Headers", "token");
    response.addHeader("token", createdUser.getToken());

    try {
        EmailService emailService = new EmailService();
        emailService.sendEmail(createdUser.getEmail(), "welcome to shift planner", "Hi " + createdUser.getUsername() + "\nWelcome to shift planner.\n" +
                "\nhttps://sopra-fs22-group-33-client.herokuapp.com");
    } catch (Exception e) {
        //do nothing
    }

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable("id") long id) {
    User user = userService.findUserById(id);

      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public UserGetDTO updateUser(@RequestBody UserPostDTO userPostDTO, @PathVariable("id") long id, @RequestHeader("token") String token) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // update user
    User updatedUser = userService.updateUser(userInput, id, token);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);
  }

  @DeleteMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteUser(@PathVariable("id") long id, @RequestHeader("token") String token){
    userService.deleteUser(id, token);
  }
  
  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response){
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User returnUser = userService.loginUser(userInput);
    response.setHeader("Access-Control-Expose-Headers", "token");
    response.addHeader("token", returnUser.getToken());
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(returnUser);
  }

  @GetMapping("/users/{userId}/teams")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Set<TeamGetDTO> getAllTeamsOfUser(@PathVariable("userId") long userId, @RequestHeader("token") String token) {
    if (userService.authorizeUser(userId, token)){
      Set<Team> teams = userService.getAllTeamsOfUser(userId);
      Set<TeamGetDTO> teamGetDTOs = new HashSet<>();

      for (Team team : teams) {
        teamGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team));
      }
      return teamGetDTOs;
    }
    return null;
  }

  @PostMapping("/teams/{teamId}/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO inviteUser(@RequestBody UserPostDTO userPostDTO, @PathVariable("teamId") long teamId, @RequestHeader("token") String token){
    Team team = teamService.findTeamById(teamId);
    User userToAdd = userService.findUserByEmail(userPostDTO.getEmail());

    userService.authorizeAdmin(team, token);

    Invitation invitation = invitationService.createInvitation(team, userToAdd);
    try {
        EmailService emailService = new EmailService();
        emailService.sendEmail(userToAdd.getEmail(), "invitation to team " + team.getName(),
                "Hi " + userToAdd.getUsername() + "\nYou have been invited to team " + team.getName() + "\nplease log in to your shift planner account to check you invitations\n" +
                        "\nhttps://sopra-fs22-group-33-client.herokuapp.com");
    } catch (Exception e) {
        //do nothing
    }
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userToAdd);
  }

  @PutMapping("/teams/{teamId}/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void updateMembership(@RequestBody Boolean isAdmin, @PathVariable("teamId") long teamId, @PathVariable("userId") long userId, @RequestHeader("token") String token){
    Team team = teamService.findTeamById(teamId);
    if (userService.authorizeAdmin(team, token)){
      membershipService.updateMembership(team, userId, isAdmin);
    }
  }

  @DeleteMapping("/teams/{teamId}/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void deleteMembership(@PathVariable("teamId") long teamId, @PathVariable("userId") long userId, @RequestHeader("token") String token){
    Team team = teamService.findTeamById(teamId);
    if (userService.authorizeAdmin(team, token)){
      membershipService.deleteMembership(team, userId);
    }else{
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you have no admin rights");
    }
  }

  @GetMapping("/users/{userId}/calendars")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserCalendarGetDTO getUserCalendar(@PathVariable("userId") long userId ){ //@RequestHeader("token") String token
          User user = userService.findUserById(userId);
          UserCalendar userCalendar = userCalendarService.getUserCalendar(user);
      return DTOMapper.INSTANCE.convertEntityToUserCalendarGetDTO(userCalendar);
  }
}
