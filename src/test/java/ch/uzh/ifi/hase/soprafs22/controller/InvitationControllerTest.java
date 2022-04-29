package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.InvitationService;
import ch.uzh.ifi.hase.soprafs22.service.MembershipService;
import ch.uzh.ifi.hase.soprafs22.service.TeamService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
  private InvitationService invitationService;

  // @Test
  // public void givenInvitation_whenGetInvitation_thenReturnJsonArray() throws Exception {
  //   // given
  //   Team team = new Team();
  //   team.setName("team1");
  //   team.setId(1L);
  //   User user = new User();
  //   user.setToken("1");
  //   Invitation invitation = new Invitation();
  //   invitation.setTeam(team);
  //   invitation.setUser(user);

  //   List<Team> allTeams = Collections.singletonList(team);

  //   // this mocks the UserService -> we define above what the userService should
  //   // return when getUsers() is called
  //   given(invitation.getTeam()).willReturn(team);

  //   // when
  //   MockHttpServletRequestBuilder getRequest = get("/teams")
  //     .contentType(MediaType.APPLICATION_JSON)
  //     .header("token", "1");

  //   // then
  //   mockMvc.perform(getRequest).andExpect(status().isOk())
  //       .andExpect(jsonPath("$", hasSize(1)));
  // }

  // @Test
  // public void createInvitation_validInput_invitationCreated() throws Exception {
  //   // given
  //   Team team = new Team();
  //   team.setName("team1");
  //   User user = new User();
  //   user.setEmail("1@test.ch");
  //   user.setPassword("123");
  //   userService.createUser(user);
  //   teamService.createTeam(team, user);

  //   User user2 = new User();
  //   user2.setEmail("2@test.ch");
  //   user2.setPassword("123");
  //   userService.createUser(user);

  //   UserPostDTO userPostDTO = new UserPostDTO();
  //   userPostDTO.setEmail("2@test.ch");

  //   given(userService.authorizeAdmin(Mockito.any(), Mockito.any())).willReturn(true);
  //   given(userService.findUserByEmail(Mockito.any())).willReturn(user);
  //   given(teamService.findTeamById(Mockito.any())).willReturn(team);
    

  //   // when/then -> do the request + validate the result
  //   MockHttpServletRequestBuilder postRequest = post("/teams/1/users")
  //       .contentType(MediaType.APPLICATION_JSON)
  //       .content(asJsonString(userPostDTO));
  //       //.header("token", "1");

  //   // then
  //   mockMvc.perform(postRequest)
  //       .andExpect(status().isOk())
  //       .andExpect(jsonPath("$.id", is(user.getId().intValue())))
  //       .andExpect(jsonPath("$.email", is(user.getEmail())));
  // }

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
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}