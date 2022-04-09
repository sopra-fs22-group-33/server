package ch.uzh.ifi.hase.soprafs22.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.MembershipRepository;


@Service
@Transactional
public class MembershipService {

  private final Logger log = LoggerFactory.getLogger(TeamService.class);

  private final MembershipRepository membershipRepository;

  @Autowired
  public MembershipService(@Qualifier("membershipRepository") MembershipRepository membershipRepository) {
    this.membershipRepository = membershipRepository;
  }

  public User createMembership(Team team, User user) {    
    Membership membership = new Membership();
    membership.setUser(user);
    membership.setTeam(team);
    membership.setIsAdmin(false);
    membership = membershipRepository.save(membership);
    membershipRepository.flush();

    log.debug("Created Information for Membership: {}", membership);
    return user;
  }

//   public Team updateMembership(Team team, long id) {
//     Team updatedTeam = teamRepository.findById(id)
//       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "team could not be updated, not found"));
//     if (team.getName() != null){updatedTeam.setName(team.getName());}           
    
//     teamRepository.save(updatedTeam);
//     teamRepository.flush();
//     return updatedTeam;
//   }

  public void deleteMembership(long id){
    membershipRepository.deleteById(id);
  }



  //helper
//   private void checkIfTeamExists(Team teamToBeCreated) {
//     Team TeamByName = teamRepository.findByName(teamToBeCreated.getName());

//     String baseErrorMessage = "The %s provided %s not unique. Therefore, the team could not be created!";
//     if (TeamByName != null) {
//       throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//           String.format(baseErrorMessage, "name", "is"));
//     } 
//   }
}
