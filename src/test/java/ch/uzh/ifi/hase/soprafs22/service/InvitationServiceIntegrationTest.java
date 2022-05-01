package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
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

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;


/**
 * Test class for the TeamResource REST resource.
 *
 * @see TeamService
 */
@WebAppConfiguration
@SpringBootTest
public class InvitationServiceIntegrationTest {

  @Qualifier("invitationRepository")
  @Autowired
  private InvitationRepository invitationRepository;

  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MembershipRepository membershipRepository;

  @Autowired
  private InvitationService invitationService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    invitationRepository.deleteAll();
    membershipRepository.deleteAll();    
    teamRepository.deleteAll();
    userRepository.deleteAll();    
  }

  @Test
  @Transactional
  public void createInvitation_validInputs_success() {
    // given
    assertTrue(invitationRepository.findAll().isEmpty());

    Team testTeam = new Team();
    testTeam.setName("team1");
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    User testUser2 = new User();
    testUser2.setEmail("2@test");
    testUser2.setPassword("password");
    
    // when
    User createdUser = userService.createUser(testUser);
    User createdUser2 = userService.createUser(testUser2);
    Team createdTeam = teamService.createTeam(testTeam, createdUser);
    Invitation invitation = invitationService.createInvitation(testTeam, testUser2);

    // then
    assertEquals(testTeam.getId(), invitation.getTeam().getId());
    assertEquals(testTeam.getName(), createdTeam.getName());
  }

  @Test
  @Transactional
  public void deleteInvitation_success() {
    // given
    assertTrue(invitationRepository.findAll().isEmpty());

    Team testTeam = new Team();
    testTeam.setName("team1");
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    User testUser2 = new User();
    testUser2.setEmail("2@test");
    testUser2.setPassword("password");
    
    // when
    User createdUser = userService.createUser(testUser);
    User createdUser2 = userService.createUser(testUser2);
    Team createdTeam = teamService.createTeam(testTeam, createdUser);
    Invitation invitation = invitationService.createInvitation(testTeam, testUser2);
    assertNotNull(invitationRepository.findAll());

    // then
    invitationService.deleteInvitation(invitation.getId());
    assertTrue(invitationRepository.findAll().isEmpty());
    assertNull(testTeam.getInvitations());
  }
}
