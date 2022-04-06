package ch.uzh.ifi.hase.soprafs22.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;

import java.util.List;

@Service
@Transactional
public class TeamService {

  private final Logger log = LoggerFactory.getLogger(TeamService.class);

  private final TeamRepository teamRepository;

  @Autowired
  public TeamService(@Qualifier("teamRepository") TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public List<Team> getTeams() {
    return this.teamRepository.findAll();
  }

  public Team createTeam(Team newTeam) {
    
    checkIfTeamExists(newTeam);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newTeam = teamRepository.save(newTeam);
    teamRepository.flush();

    log.debug("Created Information for Team: {}", newTeam);
    return newTeam;
  }

  public Team updateTeam(Team team, long id) {
    Team updatedTeam = teamRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "team could not be updated, not found"));
    if (team.getName() != null){updatedTeam.setName(team.getName());}           
    
    teamRepository.save(updatedTeam);
    teamRepository.flush();
    return updatedTeam;
  }

  public void deleteTeam(long id){
    teamRepository.deleteById(id);
  }

  public List<Team> getAllTeamsOfUser(long userId){
    List<Team> teams = teamRepository.findTeamsByUsersId(userId);
    if (teams == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this user belongs to no teams");
    }
    return teams;
  }

  public User addUser(User userToAdd, Long teamId) {
    // User user = userRepository.findByEmail(userToAdd.getEmail());
    if (userToAdd.getId() == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found, could not be added to team");
    }
    Team team = teamRepository.findById(teamId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User could not be added, team not found"));

    if (teamRepository.findTeamsByUsersId(userToAdd.getId()).contains(team)){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is already part of this team");
    }
    
    team.addUser(userToAdd);
    teamRepository.save(team);
    teamRepository.flush();
    return userToAdd;
  }

  //helper
  private void checkIfTeamExists(Team teamToBeCreated) {
    Team TeamByName = teamRepository.findByName(teamToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the team could not be created!";
    if (TeamByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "name", "is"));
    } 
  }
}
