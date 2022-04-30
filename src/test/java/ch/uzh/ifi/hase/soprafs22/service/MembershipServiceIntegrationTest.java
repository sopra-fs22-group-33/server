package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.InvitationRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MembershipRepository;
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
public class MembershipServiceIntegrationTest {

  @Qualifier("membershipRepository")
  @Autowired
  private MembershipRepository membershipRepository;

  @Autowired
  private InvitationRepository invitationRepository;

  @Autowired
  private MembershipRepository teamRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MembershipService membershipService;

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
  public void createMembership_validInputs_success() {
    // given
    assertTrue(membershipRepository.findAll().isEmpty());

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
    Membership membership = membershipService.createMembership(createdTeam, createdUser2, false);

    // then
    assertFalse(membershipRepository.findAll().isEmpty());
    assertNotNull(createdTeam.getMemberships());
    assertEquals(membership.getTeam().getId(), createdTeam.getId());
    assertEquals(membership.getUser().getId(), createdUser2.getId());
  }

  @Test
  @Transactional
  public void updateMembership_validInputs_success() {
    // given
    assertTrue(membershipRepository.findAll().isEmpty());

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
    Membership membership = membershipService.createMembership(testTeam, createdUser2, false);

    // then
    createdTeam = teamService.findTeamById(createdTeam.getId());
    assertFalse(membership.getIsAdmin());
    //TODO
    // membershipService.updateMembership(testTeam, createdUser2.getId(), true);
    // assertTrue(membership.getIsAdmin());
  }

  // @Test
  // @Transactional
  // public void deleteMembership_success() {
  //   // given
  //   assertTrue(membershipRepository.findAll().isEmpty());

  //   Team testTeam = new Team();
  //   testTeam.setName("team1");
  //   User testUser = new User();
  //   testUser.setEmail("firstname@lastname");
  //   testUser.setPassword("password");
  //   User testUser2 = new User();
  //   testUser2.setEmail("2@test");
  //   testUser2.setPassword("password");
    
  //   // when
  //   User createdUser = userService.createUser(testUser);
  //   User createdUser2 = userService.createUser(testUser2);
  //   Team createdTeam = teamService.createTeam(testTeam, createdUser);
  //   Membership membership = membershipService.createMembership(testTeam, testUser2, true);
  //   assertNotNull(membershipRepository.findAll());

  //   // then
  //   membershipService.deleteMembership(testTeam, testUser2.getId());
  //   assertTrue(testUser2.getMemberships().isEmpty());
  // }

}
