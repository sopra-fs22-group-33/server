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
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TeamService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final TeamRepository teamRepository;

  @Autowired
  public TeamService(@Qualifier("teamRepository") TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public List<Team> getUsers() {
    return this.teamRepository.findAll();
  }

  public Team createTeam(Team newTeam) {
    
    checkIfTeamExists(newTeam);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newTeam = teamRepository.save(newTeam);
    teamRepository.flush();

    log.debug("Created Information for User: {}", newTeam);
    return newTeam;
  }

  private void checkIfTeamExists(Team teamToBeCreated) {
    Team TeamByName = teamRepository.findByName(teamToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (TeamByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "name", "is"));
    } 
  }
}
