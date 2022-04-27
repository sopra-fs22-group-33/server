package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    checkIfUserExists(newUser);

    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);

    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User updateUser(User user, long id, String token) {
    if (authorizeUser(id, token)){
      User updatedUser = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));;
      if (user.getUsername() != null){updatedUser.setUsername(user.getUsername());}
      if (user.getEmail() != null){updatedUser.setEmail(user.getEmail());}
      if (user.getPassword() != null){updatedUser.setPassword(user.getPassword());}
      if (user.getStatus() != null){updatedUser.setStatus(user.getStatus());        
      }
      userRepository.save(updatedUser);
      userRepository.flush();
      return updatedUser;
    }else{
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user cannot be updated, you can only change your own profile.");
    } 
  }

  public void deleteUser(long userId, String token){
    if (authorizeUser(userId, token)){
      userRepository.deleteById(userId);
      userRepository.flush();
    }else{
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user cannot be deleted, you can only delete your own account.");
    }    
  }

  public boolean authorizeUser(long userId, String token){
     return findUserById(userId) == userRepository.findByToken(token);
  }

  public boolean authorizeAdmin(Team team, String token){
    for (Membership membership : team.getMemberships()){
      if (membership.getUser().getToken().matches(token) && membership.getIsAdmin()){
        return true;
      }
    }
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you have no admin rights in this team");
  }

  public User findUserById(@PathVariable Long id){    
    return userRepository.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public User findUserByUsername(@PathVariable String username){
    User userByUsername = userRepository.findByUsername(username);
    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "there is no user with the username " + username);
    }
    return userByUsername;
  }

  public User findUserByEmail(@PathVariable String email){
    User userByEmail = userRepository.findByEmail(email);
    if (userByEmail == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "there is no user with this email adress");
    }
    return userByEmail;
  }

  public User findUserByToken(@PathVariable String token){
    User userByToken = userRepository.findByToken(token);
    if (userByToken == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "there is no user with this token");
    }
    return userByToken;
  }

  public Set<Team> getAllTeamsOfUser(long userId){
    User user = findUserById(userId);
    Set<Team> teams = new HashSet<>();

    for (Membership membership : user.getMemberships()){
      teams.add(membership.getTeam());
    }
   
    if (teams.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this user belongs to no teams");
    }
   return teams;
 }

  //TODO to be changed
  public User loginUser(User userInput){
    User userByEmail = findUserByEmail(userInput.getEmail());
    if (!userByEmail.getPassword().toString().matches(userInput.getPassword().toString())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid password");
    }
    userByEmail.setStatus(UserStatus.ONLINE);
    return userByEmail;
  }
  

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByEmail = userRepository.findByEmail(userToBeCreated.getEmail());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "email", "is"));
    } 
  }
}
