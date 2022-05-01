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

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.InvitationRepository;


@Service
@Transactional
public class InvitationService {

  private final Logger log = LoggerFactory.getLogger(TeamService.class);

  private final InvitationRepository invitationRepository;

  @Autowired
  public InvitationService(@Qualifier("invitationRepository") InvitationRepository invitationRepository) {
    this.invitationRepository = invitationRepository;
  }

  //TODO check if user is invited or member already
  public Invitation createInvitation(Team team, User user) {    
    Invitation invitation = new Invitation();
    invitation.setUser(user);
    invitation.setTeam(team);
    invitation = invitationRepository.save(invitation);
    invitationRepository.flush();

    log.debug("Created Information for invitation: {}", invitation);
    return invitation;
  }

  public void deleteInvitation(long invitationId){
    invitationRepository.deleteById(invitationId);
    invitationRepository.flush();
  }

  public Invitation findInvitation(Team team, long userId){
    if (team.getInvitations() != null){
      for (Invitation invitation : team.getInvitations()){
        if (invitation.getUser().getId() == userId){
          return invitation;
        }
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invitation not found");
  }

  public Invitation findInvitationById(@PathVariable Long id){    
    return invitationRepository.findById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
  }
}
