package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.InvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class InvitationServiceTest {

  @Mock
  private InvitationRepository invitationRepository;

  @InjectMocks
  private InvitationService invitationService;

  private Team testTeam;
  private User testUser;
  private Invitation testInvitation;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testTeam = new Team();
    testTeam.setId(3L);
    testTeam.setName("testTeamname");
    testUser = new User();
    testUser.setId(2L);
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    testInvitation = new Invitation();
    testInvitation.setId(1L);
    testInvitation.setTeam(testTeam);
    testInvitation.setUser(testUser);

    // when -> any object is being save in the TeamRepository -> return the dummy
    // testTeam
    Mockito.when(invitationRepository.save(Mockito.any())).thenReturn(testInvitation);
  }

  @Test
  public void createinvitation_validInputs_success() {
    // when -> any object is being save in the TeamRepository -> return the dummy
    // testTeam
    Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);

    // then
    Mockito.verify(invitationRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testInvitation.getId(), createdInvitation.getId());
    assertEquals(testInvitation.getTeam(), createdInvitation.getTeam());
  }
}
