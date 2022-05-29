package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@RestController
public class InvitationController {

  private final TeamService teamService;
  private final UserService userService;
  private final MembershipService membershipService;
  private final InvitationService invitationService;
  private final TeamCalendarService teamCalendarService;

  InvitationController(TeamService teamService, UserService userService, MembershipService membershipService, InvitationService invitationService, TeamCalendarService teamCalendarService) {
    this.teamService = teamService;
    this.userService = userService;
    this.membershipService = membershipService;
    this.invitationService = invitationService;
    this.teamCalendarService = teamCalendarService;
  }

  @GetMapping("/users/{userId}/invitations")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<TeamGetDTO> getInvitations(@PathVariable("userId") long userId, @RequestHeader("token") String token){
    if (userService.authorizeUser(userId, token)){
        User user = userService.findUserById(userId);
        List<TeamGetDTO> teamGetDTOs = new ArrayList<>();

        for (Invitation invitation : user.getInvitations()) {
            teamGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTeamGetDTO(invitation.getTeam()));
        }
        return teamGetDTOs;
    }throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized");
  }

  @GetMapping("/teams/{teamId}/invitations")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getInvitedUsers(@PathVariable("teamId") long teamId, @RequestHeader("token") String token){
    Team team = teamService.findTeamById(teamId);
    if (userService.authorizeAdmin(team, token)){
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (Invitation invitation : team.getInvitations()) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(invitation.getUser()));
        }
        return userGetDTOs;
    }throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no admin rights");
  }

  @PutMapping("/users/{userId}/invitations/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  // @Transactional
  public void answerInvitation(@PathVariable("userId") long userId, @PathVariable("teamId") long teamId, @RequestParam String accept, @RequestHeader("token") String token){
    if (userService.authorizeUser(userId, token)){
      Team team = teamService.findTeamById(teamId);
      Invitation invitation = invitationService.findInvitation(team, userId);
      //creating a new membership if invitation is accepted
      if (accept.matches("true")){
        Membership membership = membershipService.createMembership(invitationService.findInvitationById(invitation.getId()).getTeam(), invitationService.findInvitationById(invitation.getId()).getUser(), false);
        teamCalendarService.addTeamMemberToCalendar(teamId);
      }
      //deleting the invitation
      invitationService.deleteInvitation(invitation.getId());
    }
  }

  @DeleteMapping("/users/{userId}/invitations/{teamId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Transactional
  public void declineInvitation(@PathVariable("userId") long userId, @PathVariable("teamId") long teamId, @RequestHeader("token") String token){
    if (userService.authorizeUser(userId, token)){
      Team team = teamService.findTeamById(teamId);
      Invitation invitation = invitationService.findInvitation(team, userId);
      //deleting the invitation
      invitationService.deleteInvitation(invitation.getId());
    }
  }

  @DeleteMapping("/teams/{teamId}/invitations/{invitationId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Transactional
  public void deleteInvitation(@PathVariable("teamId") long teamId, @PathVariable("invitationId") long invitationId, @RequestHeader("token") String token){
    if (userService.authorizeAdmin(teamService.findTeamById(teamId), token)){
      //deleting the invitation
      invitationService.deleteInvitation(invitationId);
    }
  }
}
