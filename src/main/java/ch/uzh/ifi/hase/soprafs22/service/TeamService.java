package ch.uzh.ifi.hase.soprafs22.service;

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

import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;

import java.util.*;

@Service
@Transactional
public class TeamService {

  private final Logger log = LoggerFactory.getLogger(TeamService.class);

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  @Autowired
  public TeamService(@Qualifier("teamRepository") TeamRepository teamRepository, @Qualifier("userRepository")UserRepository userRepository) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
  }

  public List<Team> getTeams() {
    return this.teamRepository.findAll();
  }

  public Team createTeam(Team newTeam, User user) {
    
    // checkIfTeamExists(newTeam);
    Membership membership = new Membership();
    membership.setUser(user);
    membership.setTeam(newTeam);
    membership.setIsAdmin(true);
    Set<Membership> mSet = new HashSet<>();
    mSet.add(membership);
    newTeam.setMemberships(mSet);
    newTeam = teamRepository.save(newTeam);
    teamRepository.flush();

    log.debug("Created Information for Team: {}", newTeam);
    return newTeam;
  }

  public Team updateTeam(Team team, long teamId, String token) {
    Team teamToUpdate = teamRepository.findById(teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "team could not be updated, not found"));
    if (authorizeAdmin(teamToUpdate, token)){        
      if (team.getName() != null){teamToUpdate.setName(team.getName());}           
      
      teamRepository.save(teamToUpdate);
      teamRepository.flush();
      return teamToUpdate;
    }else{
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are no admin");
    }
  }

  public void deleteTeam(long id, String token){
    if (authorizeAdmin(teamRepository.findById(id).get(), token)){
      teamRepository.deleteById(id);
    }
  }

  public Team findTeamById(@PathVariable Long id){    
    return teamRepository.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
  }

  public Set<User> getAllUsersOfTeam(long teamId){
    Set<User> users = new HashSet<>();
    Team team = findTeamById(teamId);

    for (Membership membership : team.getMemberships()){
      users.add(membership.getUser());
    }
    if (users.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "there are no users in this team");
    }
    return users;
  }

  public void notifyUsersToUpdatePreferences(ArrayList<Long> userIds, Long id){
      Team team  = findTeamById(id);
      EmailService emailService = new EmailService();
      for (Long userId:userIds){
          Optional<User> user = userRepository.findById(userId);
          if(user.isPresent()){
            User foundUser = user.get(); // TODO: check if the user belongs to this team, optionally
              try {
                  emailService.sendEmail(foundUser.getEmail(), "Fill out your preferences!",
                          "Hi " + foundUser.getUsername() + "\nYour team" + team.getName()+" is waiting for your input! \nLog in to your shift planner account to provide it.");
              }
              catch (Exception e) {
                  //do nothing
              }
          }
          else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
      }
  }

  //helpers
  // private void checkIfTeamExists(Team teamToBeCreated) {
  //   Team TeamByName = teamRepository.findByName(teamToBeCreated.getName());

  //   String baseErrorMessage = "The %s provided %s not unique. Therefore, the team could not be created!";
  //   if (TeamByName != null) {
  //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
  //         String.format(baseErrorMessage, "name", "is"));
  //   } 
  // }

  public boolean authorizeAdmin(Team team, String token){
    for (Membership membership : team.getMemberships()){
      if (membership.getUser().getToken().matches(token) && membership.getIsAdmin()){
        return true;
      }
    }
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you have no admin rights in this team");
  }
}
