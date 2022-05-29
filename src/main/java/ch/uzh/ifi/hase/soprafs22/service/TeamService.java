package ch.uzh.ifi.hase.soprafs22.service;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    authorizeAdmin(teamToUpdate, token);

    if (team.getName() != null){teamToUpdate.setName(team.getName());}

    teamRepository.save(teamToUpdate);
    teamRepository.flush();
    return teamToUpdate;
  }

  public void deleteTeam(long id, String token){
      if (teamRepository.findById(id).isPresent()){
          if (authorizeAdmin(teamRepository.findById(id).get(), token)){
            teamRepository.deleteById(id);
          }
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
    return users;
  }

  public boolean authorizeAdmin(Team team, String token){
    for (Membership membership : team.getMemberships()){
      if (membership.getUser().getToken().matches(token) && membership.getIsAdmin()){
        return true;
      }
    }
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you have no admin rights in this team");
  }
}
