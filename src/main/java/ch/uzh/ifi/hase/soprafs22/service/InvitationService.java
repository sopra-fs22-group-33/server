package ch.uzh.ifi.hase.soprafs22.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  public void createInvitation(Team team, User user) {    
    Invitation invitation = new Invitation();
    invitation.setUser(user);
    invitation.setTeam(team);
    invitation = invitationRepository.save(invitation);
    invitationRepository.flush();

    log.debug("Created Information for invitation: {}", invitation);
  }

  public void acceptInvitation(long invitationId){
  }

  public void declineInvitation(long invitationId){
      this.deleteInvitation(invitationId);
  }

  public void deleteInvitation(long invitationId){
    invitationRepository.deleteById(invitationId);
    invitationRepository.flush();
  }
}
