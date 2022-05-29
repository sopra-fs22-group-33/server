package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.MembershipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@Transactional
public class MembershipService {

  private final Logger log = LoggerFactory.getLogger(TeamService.class);

  private final MembershipRepository membershipRepository;

  @Autowired
  public MembershipService(@Qualifier("membershipRepository") MembershipRepository membershipRepository) {
    this.membershipRepository = membershipRepository;
  }

  public Membership createMembership(Team team, User user, Boolean isAdmin) {
    Membership membership = new Membership();
    membership.setUser(user);
    membership.setTeam(team);
    membership.setIsAdmin(isAdmin);
    membership = membershipRepository.save(membership);
    membershipRepository.flush();

    log.debug("Created Information for Membership: {}", membership);
    return membership;
  }

  public Membership findMembership(Team team, long userId){
    if (team.getMemberships() != null){
      for (Membership membership : team.getMemberships()){
        if (membership.getUser().getId() == userId){
          return membership;
        }
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "membership not found");
  }

  public void updateMembership(Team team, long userId, Boolean isAdmin){
    Membership membershipToUpdate = findMembership(team, userId);
    membershipToUpdate.setIsAdmin(isAdmin);
    membershipRepository.flush();
  }

  public void deleteMembership(Team team, long userId){
    Membership membership = findMembership(team, userId);
    team.getMemberships().remove(membership);
    User user = membership.getUser();
    user.getMemberships().remove(membership);
    membershipRepository.delete(membership);
    membershipRepository.flush();
  }
}
