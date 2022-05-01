package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
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

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private InvitationRepository invitationRepository;

  @Autowired
  private TeamRepository teamRepository;
  
  @Autowired
  private MembershipRepository membershipRepository;

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
  public void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByEmail("firstname@lastname"));

    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getEmail(), createdUser.getEmail());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateEmail_throwsException() {
    assertNull(userRepository.findByEmail("firstname@lastname"));

    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the email
    testUser2.setUsername("testName2");
    testUser2.setEmail("firstname@lastname");
    testUser2.setPassword("password");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }

  @Test
  public void updateUser_validInputs_success() {
    // given
    assertNull(userRepository.findByEmail("firstname@lastname"));

    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);
    testUser.setUsername("changed");
    User updatedUser = userService.updateUser(testUser, createdUser.getId(), createdUser.getToken());

    // then
    assertEquals(testUser.getId(), updatedUser.getId());
    assertEquals(testUser.getEmail(), updatedUser.getEmail());
    assertEquals(testUser.getUsername(), updatedUser.getUsername());
    assertNotNull(updatedUser.getToken());
    assertEquals(UserStatus.ONLINE, updatedUser.getStatus());
  }

  @Test
  public void deleteUser_validInputs_success() {
    // given
    assertNull(userRepository.findByEmail("firstname@lastname"));

    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setUsername("testUsername");
    testUser.setPassword("password");

    // when
    User createdUser = userService.createUser(testUser);
    assertEquals(testUser.getId(), createdUser.getId());    

    // then
    userService.deleteUser(createdUser.getId(), createdUser.getToken());
    assertThrows(ResponseStatusException.class, () -> userService.findUserById(createdUser.getId()));
  }
}
