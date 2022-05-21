package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.UserCalendar;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.*;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.RequestEntity.put;
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
@WebMvcTest(UserController.class)
public class UserControllerTest {

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

  @MockBean
  private UserCalendarService userCalendarService;

  @MockBean
  private PreferenceCalendarService preferenceCalendarService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setEmail("firstname@lastname");
    user.setPassword("1234");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].email", is(user.getEmail())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setEmail("firstname@lastname");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    user.setPassword("password");

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setEmail("firstname@lastname");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.email", is(user.getEmail())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

    @Test
    public void getUser_validInput_userReturned() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setEmail("firstname@lastname");
        userPostDTO.setUsername("testUsername");

        given(userService.findUserById(Mockito.anyLong())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void updateUser_validInput_userReturned() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setEmail("firstname@lastname");
        userPostDTO.setUsername("testUsername");


        given(userService.updateUser(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest =
                MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPostDTO))
                        .header("token", "token");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void deleteUser_validInput_userDeleted() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest =
                MockMvcRequestBuilders.delete("/users/1")
                        .header("token", "token");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).deleteUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void loginUser_validInput_tokenReturned() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setEmail("firstname@lastname");
        userPostDTO.setUsername("testUsername");

        // when/then -> do the request + validate the result
        given(userService.loginUser(Mockito.any(User.class))).willReturn(user);
        given(userService.findUserByEmail(Mockito.anyString())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

    @Test
    public void getAllTeamsOfUser_validInput_teamsReturned() throws Exception {
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
        Set <Team> teams = new HashSet<>();
        teams.add(team);

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setTeam(team);

        team.getMemberships().add(membership);

        given(userService.authorizeUser(Mockito.anyLong(), Mockito.anyString())).willReturn(true);
        given(userService.getAllTeamsOfUser(Mockito.anyLong())).willReturn(teams);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1/teams")
                .header("token", "token");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void inviteUser_validInput_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("token");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        Team team = new Team();
        team.setName("name");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setEmail("firstname@lastname");
        userPostDTO.setUsername("testUsername");

        given(userService.findUserByEmail(Mockito.anyString())).willReturn(user);
        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(true);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/teams/1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO))
                .header("token", "token");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void updateMembership_validInput_success() throws Exception {
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

        Boolean isAdmin = true;

        given(teamService.findTeamById(Mockito.anyLong())).willReturn(team);
        given(membershipService.findMembership(Mockito.any(), Mockito.anyLong())).willReturn(membership);
        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(true);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest =
                MockMvcRequestBuilders.put("/teams/1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(isAdmin))
                        .header("token", "token");

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        Mockito.verify(membershipService, Mockito.times(1)).updateMembership(Mockito.any(), Mockito.anyLong(), Mockito.anyBoolean());
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
        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(true);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest =
                MockMvcRequestBuilders.delete("/teams/1/users/1")
                        .header("token", "token");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        Mockito.verify(membershipService, Mockito.times(1)).deleteMembership(Mockito.any(), Mockito.anyLong());
    }

    @Test
    public void deleteMembership_notAuthorized_throwsException() throws Exception {
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
//        given(userService.authorizeAdmin(Mockito.any(), Mockito.anyString())).willReturn(true);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder deleteRequest =
                MockMvcRequestBuilders.delete("/teams/1/users/1")
                        .header("token", "token");

        // then
        mockMvc.perform(deleteRequest)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserCalendar_validInput_success() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("firstname@lastname");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setPassword("password");

        UserCalendar userCalendar = new UserCalendar();

        given(userService.findUserById(Mockito.anyLong())).willReturn(user);
        given(userCalendarService.getUserCalendar(Mockito.any())).willReturn(userCalendar);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1/calendars");

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
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