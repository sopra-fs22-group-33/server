package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@WebMvcTest(TeamController.class)
public class TeamControllerTest {

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

  @Test
  public void givenTeams_whenGetTeams_thenReturnJsonArray() throws Exception {
    // given
    Team team = new Team();
    team.setName("team1");

    List<Team> allTeams = Collections.singletonList(team);

    given(teamService.getTeams()).willReturn(allTeams);

    // when
    MockHttpServletRequestBuilder getRequest = get("/teams").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(team.getName())));
  }

  @Test
  public void createTeam_validInput_teamCreated() throws Exception {
    // given
    Team team = new Team();
    team.setName("team1");
    team.setId(1L);
    User user = new User();
    user.setToken("1");

    TeamPostDTO teamPostDTO = new TeamPostDTO();
    teamPostDTO.setName("team1");

    given(teamService.createTeam(Mockito.any(), Mockito.any())).willReturn(team);
    given(userService.findUserByToken("1")).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/teams")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(teamPostDTO))
        .header("token", "1");

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(team.getId().intValue())))
        .andExpect(jsonPath("$.name", is(team.getName())));
  }

    @Test
    public void getTeam_validInput_teamReturned() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        team.setId(1L);

        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/teams/1");


        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(team.getId().intValue())))
                .andExpect(jsonPath("$.name", is(team.getName())));
    }

    @Test
    public void updateTeam_validInput_teamReturned() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        team.setId(1L);
        User user = new User();
        user.setToken("1");

        TeamPostDTO teamPostDTO = new TeamPostDTO();
        teamPostDTO.setName("team1");

        given(userService.findUserByToken("1")).willReturn(user);
        given(teamService.updateTeam(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).willReturn(team);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/teams/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(teamPostDTO))
                .header("token", "1");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.id", is(team.getId().intValue())))
                .andExpect(jsonPath("$.name", is(team.getName())));
    }

    @Test
    public void deleteTeam_validInput_success() throws Exception {
        // given
        Team team = new Team();
        team.setName("team1");
        team.setId(1L);
        User user = new User();
        user.setToken("1");

        given(userService.findUserByToken("1")).willReturn(user);
        given(teamService.updateTeam(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).willReturn(team);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest =
                MockMvcRequestBuilders.delete("/teams/1")
                        .header("token", "token");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());
        Mockito.verify(teamService, Mockito.times(1)).deleteTeam(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void getAllUsersOfTeam_validInput_usersReturned() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        Team team = new Team();
        team.setName("name");
        team.setMemberships(new HashSet<>());
        Set<User> users = new HashSet<>();
        users.add(user);

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setTeam(team);

        team.getMemberships().add(membership);

        given(userService.findUserByToken(Mockito.anyString())).willReturn(user);
        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(membershipService.findMembership(Mockito.any(), Mockito.anyLong())).willReturn(membership);
        given(teamService.getAllUsersOfTeam(Mockito.anyLong())).willReturn(users);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/teams/1/users")
                .header("token", "token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void getAllUsersOfTeam_doesNotExist_emptyReturned() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        Team team = new Team();
        team.setName("name");
        team.setMemberships(new HashSet<>());
        Set<User> users = new HashSet<>();
        users.add(user);

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setTeam(team);

        team.getMemberships().add(membership);

        given(userService.findUserByToken(Mockito.anyString())).willReturn(user);
        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(teamService.getAllUsersOfTeam(Mockito.anyLong())).willReturn(users);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/teams/1/users")
                .header("token", "token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void deleteMembership_validInput_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        Team team = new Team();
        team.setName("name");

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setTeam(team);

        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(membershipService.findMembership(Mockito.any(), Mockito.anyLong())).willReturn(membership);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.anyString())).willReturn(true);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest =
                MockMvcRequestBuilders.delete("/users/1/teams/1")
                        .header("token", "token");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(membershipService, Mockito.times(1)).deleteMembership(Mockito.any(), Mockito.anyLong());
    }

  /**
   * Helper Method to convert teamPostDTO into a JSON string such that the input
   * can be processed
   * 
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