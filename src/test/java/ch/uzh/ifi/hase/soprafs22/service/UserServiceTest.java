package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.MembershipRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private MembershipRepository membershipRepository;

  @Mock
  private TeamRepository teamRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("firstname@lastname");
    testUser.setUsername("testUsername");
    testUser.setPassword("123");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getEmail(), createdUser.getEmail());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void updateUser_validInputs_success(){
    userService.createUser(testUser);
    Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));

    User userToUpdate = new User();
    userToUpdate.setUsername("new Username");

    User updatedUser = userService.updateUser(userToUpdate, testUser.getId(), "token");
    assertEquals(updatedUser.getUsername(), testUser.getUsername());
  }


    @Test
    public void updateUser_notAuthorized_throwsException(){
        userService.createUser(testUser);
        Mockito.when(userRepository.findByToken("token")).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));

        User userToUpdate = new User();
        userToUpdate.setUsername("new Username");

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(userToUpdate, testUser.getId(), "invalid"));
    }

  @Test
  public void loginUser_validInputs_success(){
    testUser.setStatus(UserStatus.OFFLINE);
    userService.createUser(testUser);
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);

    // User userToLogin = new User();
    User loggedInUser = userService.loginUser(testUser);
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus()); 
  }

    @Test
    public void loginUser_invalidPassword_throwsException(){
        testUser.setStatus(UserStatus.OFFLINE);
        userService.createUser(testUser);
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);

        User userToLogin = new User();
        userToLogin.setEmail(testUser.getEmail());
        userToLogin.setPassword("invalid");
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(userToLogin));
    }

  @Test
  public void authorizeUser_validInputs_success(){
    testUser.setToken("token");
    User createdUser = userService.createUser(testUser);
    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
    Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

    userService.authorizeUser(createdUser.getId(), createdUser.getToken());

    User loggedInUser = userService.loginUser(testUser);
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus()); 
  }

  @Test
  public void findUserById_userExists_success(){
    testUser.setId(1L);
    User createdUser = userService.createUser(testUser);
    Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(testUser));

    User foundUser = userService.findUserById(createdUser.getId());

    assertEquals(foundUser.getEmail(), testUser.getEmail());
  }

  @Test
  public void findUserById_userDoesNotExists_throwsException(){
      testUser.setId(1L);
      User createdUser = userService.createUser(testUser);
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));

      assertThrows(ResponseStatusException.class, () -> userService.findUserById(2L));
  }

    @Test
    public void getUserById_userExists_success(){
        testUser.setId(1L);
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(testUser));

        User foundUser = userService.getUserById(createdUser.getId());

        assertEquals(foundUser.getEmail(), testUser.getEmail());
    }

    @Test
    public void getUserById_userDoesNotExists_throwsException(){
        testUser.setId(1L);
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(2L));
    }

    @Test
    public void findUserByEmail_userExists_success(){
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);

        User foundUser = userService.findUserByEmail(createdUser.getEmail());

        assertEquals(foundUser.getEmail(), testUser.getEmail());
        assertEquals(foundUser.getUsername(), testUser.getUsername());
    }

    @Test
    public void findUserByEmail_userDoesNotExists_throwsException(){
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findByEmail(createdUser.getEmail())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.findUserByEmail("invalid"));
    }

    @Test
    public void findUserByToken_userExists_success(){
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        User foundUser = userService.findUserByToken(createdUser.getToken());

        assertEquals(foundUser.getEmail(), testUser.getEmail());
        assertEquals(foundUser.getUsername(), testUser.getUsername());
    }

    @Test
    public void findUserByToken_userDoesNotExists_throwsException(){
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findByToken(createdUser.getToken())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.findUserByToken("invalid"));
    }

    @Test
    public void deleteUser_authorized_success(){
        testUser.setId(1L);
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(testUser));

        userService.deleteUser(testUser.getId(), testUser.getToken());
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    public void deleteUser_notAuthorized_trowsException(){
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findByToken(createdUser.getToken())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(createdUser.getId(),"invalid"));
    }

    @Test
    public void getAllUsers_success(){
        User createdUser = userService.createUser(testUser);
        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));

        List <User> users = userService.getUsers();
        assertEquals(1, users.size());
    }


}
