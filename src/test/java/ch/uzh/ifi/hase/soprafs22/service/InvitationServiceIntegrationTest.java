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
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


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
    public void createInvitation_twice_throwsException() {
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
        testTeam.setInvitations(new HashSet<>());
        testTeam.getInvitations().add(invitation);

        // then
        assertEquals(testTeam.getId(), invitation.getTeam().getId());
        assertEquals(testTeam.getName(), createdTeam.getName());

        assertThrows(ResponseStatusException.class, () -> invitationService.createInvitation(testTeam, testUser2));
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
    
    Set <Invitation> invitations = new HashSet<>();
    invitations.add(invitation);
    testUser2.setInvitations(invitations);
    testTeam.setInvitations(invitations);

    assertNotNull(invitationRepository.findAll());
    assertNotNull(invitationService.findInvitation(testTeam, testUser2.getId()));

    // then
    invitationService.deleteInvitation(invitation.getId());
    assertFalse(invitationRepository.findAll().isEmpty());
    assertNotNull(testTeam.getInvitations());
  }
}
