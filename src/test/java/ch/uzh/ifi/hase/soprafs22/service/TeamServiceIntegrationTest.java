package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.InvitationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MembershipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;


/**
 * Test class for the TeamResource REST resource.
 *
 * @see TeamService
 */
@WebAppConfiguration
@SpringBootTest
public class TeamServiceIntegrationTest {

  @Qualifier("teamRepository")
  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MembershipRepository membershipRepository;

  @Autowired
  private InvitationRepository invitationRepository;

  @Autowired
  private TeamService teamService;

  @Autowired
  private UserService userService;

  @Autowired
  private MembershipService membershipService;

  @BeforeEach
  public void setup() {   
    invitationRepository.deleteAll(); 
    membershipRepository.deleteAll();
    teamRepository.deleteAll();    
    userRepository.deleteAll();
  }

  @Test
  @Transactional
  public void createTeam_validInputs_success() {
    // given
    assertNull(teamRepository.findByName("team1"));

    Team testTeam = new Team();
    testTeam.setName("team1");
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);
    Team createdTeam = teamService.createTeam(testTeam, createdUser);

    // then
    assertEquals(testTeam.getId(), createdTeam.getId());
    assertEquals(testTeam.getName(), createdTeam.getName());
  }

  @Test
  @Transactional
  public void updateTeam_validInputs_success() {
    // given
    assertNull(teamRepository.findByName("team1"));

    Team testTeam = new Team();
    testTeam.setName("team1");
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);
    Team createdTeam = teamService.createTeam(testTeam, createdUser);

    // then
    assertEquals(testTeam.getName(), createdTeam.getName());
    testTeam.setName("changed");
    Team updatedTeam = teamService.updateTeam(testTeam, createdTeam.getId(), createdUser.getToken());

    assertEquals(testTeam.getName(), updatedTeam.getName());
  }

  @Test
  @Transactional
  public void deleteTeam_userNotDeleted_success() {
    // given
    assertNull(teamRepository.findByName("team1"));

    //create user
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    User createdUser = userService.createUser(testUser);
    
    //create team
    Team testTeam = new Team();
    testTeam.setName("team1");
    Team createdTeam = teamService.createTeam(testTeam, createdUser);
    
    //verify that team and user are created properly
    assertEquals(testTeam.getId(), createdTeam.getId());
    assertEquals(testTeam.getName(), createdTeam.getName());
    assertEquals(testUser.getEmail(), createdUser.getEmail());

    //check if membership is created
    assertNotNull(membershipRepository.findAll());
    assertEquals(createdUser, createdTeam.getMemberships().iterator().next().getUser());

    // then
    teamService.deleteTeam(createdTeam.getId(), createdUser.getToken());

    //check if team and membership are deleted
    //user must not be deleted
    assertThrows(ResponseStatusException.class, () -> teamService.findTeamById(createdTeam.getId()));
    assertNotNull(userService.findUserById(createdUser.getId()));
  }


  //TODO use for report
  @Test
  @Transactional
  public void deleteTeam_noAdmin_throwsException() {

    //create user
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    User createdUser = userService.createUser(testUser);
    
    //create team
    Team testTeam = new Team();
    testTeam.setName("team1");
    Team createdTeam = teamService.createTeam(testTeam, createdUser);
    
    //verify that team and user are created properly
    assertEquals(testTeam.getId(), createdTeam.getId());
    assertEquals(testTeam.getName(), createdTeam.getName());
    assertEquals(testUser.getEmail(), createdUser.getEmail());

    //check if membership is created
    assertEquals(createdUser, createdTeam.getMemberships().iterator().next().getUser());

    // then
    assertThrows(ResponseStatusException.class, () -> teamService.deleteTeam(createdTeam.getId(), "invalid_token"));
    
    //delete team with valid token
    teamService.deleteTeam(createdTeam.getId(), createdUser.getToken());

    //check if team is deleted
    assertThrows(ResponseStatusException.class, () -> teamService.findTeamById(createdTeam.getId()));
  }
}
