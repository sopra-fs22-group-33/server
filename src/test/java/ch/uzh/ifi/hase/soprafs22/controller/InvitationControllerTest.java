package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(InvitationController.class)
public class InvitationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private TeamService teamService;

  @MockBean
  private MembershipService membershipService;

    @MockBean
    private TeamCalendarService teamCalendarService;

  @MockBean
  private InvitationService invitationService;

   @Test
   public void getInvitations_returnsTeam() throws Exception {
     // given
     Team team = new Team();
     team.setName("team1");
     team.setId(1L);
     User user = new User();
     user.setToken("1");
     Invitation invitation = new Invitation();
     invitation.setTeam(team);
     user.setInvitations(new HashSet<>());
     user.getInvitations().add(invitation);

     given(userService.findUserById(Mockito.anyLong())).willReturn(user);
     given(userService.authorizeUser(Mockito.anyLong(), Mockito.anyString())).willReturn(true);

       Mockito.doNothing().when(teamCalendarService).addTeamMemberToCalendar(Mockito.anyLong());

     // when
     MockHttpServletRequestBuilder getRequest = get("/users/1/invitations", 1)
       .header("token", "1");

     // then
     mockMvc.perform(getRequest)
       .andExpect(status().isOk())
       .andExpect(jsonPath("$", hasSize(1)))
       .andExpect(jsonPath("$.[0].id", is(team.getId().intValue())))
       .andExpect(jsonPath("$.[0].name", is(team.getName())));
   }

    @Test
    public void getInvitations_notAuthorized_throwsException() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        team.setId(1L);
        User user = new User();
        user.setToken("1");
        Invitation invitation = new Invitation();
        invitation.setTeam(team);
        user.setInvitations(new HashSet<>());
        user.getInvitations().add(invitation);

        given(userService.findUserById(Mockito.anyLong())).willReturn(user);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.anyString())).willReturn(false);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/invitations", 1)
                .header("token", "1");

        // then
        mockMvc.perform(getRequest)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getInvitedUsers_success_returnsUser() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        team.setId(1L);
        User user = new User();
        user.setEmail("test@test");
        user.setToken("1");
        user.setId(2L);
        Invitation invitation = new Invitation();
        invitation.setUser(user);
        team.setInvitations(new HashSet<>());
        team.getInvitations().add(invitation);

        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(true);

        // when
        MockHttpServletRequestBuilder getRequest = get("/teams/1/invitations", 1)
                .header("token", "1");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.[0].email", is(user.getEmail())));
    }

    @Test
    public void getInvitedUsers_notAuthorized_throwsException() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        team.setId(1L);
        User user = new User();
        user.setToken("1");
        Invitation invitation = new Invitation();
        invitation.setTeam(team);
        user.setInvitations(new HashSet<>());
        user.getInvitations().add(invitation);

        given(userService.findUserById(Mockito.anyLong())).willReturn(user);
        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(false);

        // when
        MockHttpServletRequestBuilder getRequest = get("/teams/1/invitations", 1)
                .header("token", "1");

        // then
        mockMvc.perform(getRequest)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(status().isForbidden());
    }

    @Test
    public void answerInvitation_membershipCreated() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        User user = new User();
        user.setToken("1");
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        invitation.setTeam(team);
        invitation.setUser(user);
        user.setInvitations(new HashSet<>());
        user.getInvitations().add(invitation);
        team.setInvitations(new HashSet<>());
        team.getInvitations().add(invitation);

        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(membershipService.createMembership(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).willReturn(new Membership());
        given(invitationService.findInvitation(Mockito.any(), Mockito.anyLong())).willReturn(invitation);
        given(invitationService.findInvitationById(Mockito.anyLong())).willReturn(invitation);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.anyString())).willReturn(true);

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/2/invitations/1", 1)
                .param("accept", "true")
                .header("token", "1");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        Mockito.verify(membershipService, Mockito.times(1)).createMembership(Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        Mockito.verify(invitationService, Mockito.times(1)).deleteInvitation(Mockito.anyLong());
    }

    @Test
    public void declineInvitation_invitationDeleted() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        User user = new User();
        user.setToken("1");
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        invitation.setTeam(team);
        invitation.setUser(user);
        user.setInvitations(new HashSet<>());
        user.getInvitations().add(invitation);
        team.setInvitations(new HashSet<>());
        team.getInvitations().add(invitation);

        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(invitationService.findInvitation(Mockito.any(), Mockito.anyLong())).willReturn(invitation);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.anyString())).willReturn(true);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/users/2/invitations/1", 1)
                .header("token", "1");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(invitationService, Mockito.times(1)).deleteInvitation(Mockito.anyLong());
    }

    @Test
    public void deleteInvitation_invitationDeleted() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        User user = new User();
        user.setToken("1");
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        invitation.setTeam(team);
        invitation.setUser(user);
        user.setInvitations(new HashSet<>());
        user.getInvitations().add(invitation);
        team.setInvitations(new HashSet<>());
        team.getInvitations().add(invitation);

        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(true);

        // when
        MockHttpServletRequestBuilder deleteRequest = delete("/teams/2/invitations/1", 1)
                .header("token", "1");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(invitationService, Mockito.times(1)).deleteInvitation(Mockito.anyLong());
    }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test Team", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e));
    }
  }
}