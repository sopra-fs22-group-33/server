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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  public void createInvitation_validInputs_success() {
    // when -> any object is being save in the TeamRepository -> return the dummy
    // testTeam
    Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);

    // then
    Mockito.verify(invitationRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testInvitation.getId(), createdInvitation.getId());
    assertEquals(testInvitation.getTeam(), createdInvitation.getTeam());
  }

    @Test
    public void createInvitation_twice_throwsException() {
        Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);
        Set<Invitation> invitations = new HashSet<>();
        invitations.add(createdInvitation);
        testTeam.setInvitations(invitations);
        testUser.setInvitations(invitations);

        Mockito.verify(invitationRepository, Mockito.times(1)).save(Mockito.any());

        assertThrows(ResponseStatusException.class, () -> invitationService.createInvitation(testTeam, testUser));
    }

    @Test
    public void findInvitation_exists_success() {
        Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);
        Set<Invitation> invitations = new HashSet<>();
        invitations.add(createdInvitation);
        testTeam.setInvitations(invitations);
        testUser.setInvitations(invitations);

        // then
        Invitation foundInvitation = invitationService.findInvitation(testTeam, testUser.getId());

        assertEquals(foundInvitation.getUser(), createdInvitation.getUser());
        assertEquals(foundInvitation.getTeam(), createdInvitation.getTeam());
    }

    @Test
    public void findInvitation_notFound_throwsException() {
        Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);
        Set<Invitation> invitations = new HashSet<>();
        invitations.add(createdInvitation);
        testTeam.setInvitations(invitations);
        testUser.setInvitations(invitations);

        assertThrows(ResponseStatusException.class, () -> invitationService.findInvitation(testTeam, 5L));
    }

    @Test
    public void findInvitationById_exists_success() {
        Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);
        Set<Invitation> invitations = new HashSet<>();
        invitations.add(createdInvitation);
        testTeam.setInvitations(invitations);
        testUser.setInvitations(invitations);

        //mocks
        Mockito.when(invitationRepository.findById(testInvitation.getId())).thenReturn(Optional.of(testInvitation));
        // then
        Invitation foundInvitation = invitationService.findInvitationById(testInvitation.getId());

        assertEquals(foundInvitation.getUser(), createdInvitation.getUser());
        assertEquals(foundInvitation.getTeam(), createdInvitation.getTeam());
    }

    @Test
    public void findInvitationById_notFound_throwsException() {
        Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);
        Set<Invitation> invitations = new HashSet<>();
        invitations.add(createdInvitation);
        testTeam.setInvitations(invitations);
        testUser.setInvitations(invitations);

        assertThrows(ResponseStatusException.class, () -> invitationService.findInvitationById(5L));
    }

    @Test
    public void deleteInvitation_success() {
        Invitation createdInvitation = invitationService.createInvitation(testTeam, testUser);
        Set<Invitation> invitations = new HashSet<>();
        invitations.add(createdInvitation);
        testTeam.setInvitations(invitations);
        testUser.setInvitations(invitations);

        invitationService.deleteInvitation(createdInvitation.getId());

        Mockito.verify(invitationRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }
}
