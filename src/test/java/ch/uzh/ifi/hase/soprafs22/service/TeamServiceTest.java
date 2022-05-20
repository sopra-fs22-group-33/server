package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @InjectMocks
  private TeamService teamService;

  private Team testTeam;
  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testTeam = new Team();
    testTeam.setId(1L);
    testTeam.setName("testTeamname");
    testUser = new User();
    testUser.setId(2L);
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    testUser.setToken("token");

    // when -> any object is being save in the TeamRepository -> return the dummy
    // testTeam
    Mockito.when(teamRepository.save(Mockito.any())).thenReturn(testTeam);
  }

  @Test
  public void createTeam_validInputs_success() {
    // when -> any object is being save in the TeamRepository -> return the dummy
    // testTeam
    Team createdTeam = teamService.createTeam(testTeam, testUser);

    // then
    Mockito.verify(teamRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testTeam.getId(), createdTeam.getId());
    assertEquals(testTeam.getName(), createdTeam.getName());
  }

    @Test
    public void updateTeam_validInputs_success(){
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findById(Mockito.any())).thenReturn(Optional.of(testTeam));

        Team teamToUpdate = new Team();
        teamToUpdate.setName("new TeamName");

        Team updatedTeam = teamService.updateTeam(teamToUpdate, testTeam.getId(), "token");
        assertEquals(updatedTeam.getName(), testTeam.getName());
    }

    @Test
    public void updateTeam_notAuthorized_throwsException(){
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findById(Mockito.any())).thenReturn(Optional.of(testTeam));

        Team teamToUpdate = new Team();
        teamToUpdate.setName("new TeamName");

        assertThrows(ResponseStatusException.class, () -> teamService.updateTeam(teamToUpdate, testTeam.getId(), "invalid"));
    }

    @Test
    public void findTeamById_teamExists_success(){
        testTeam.setId(1L);
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testTeam));

        Team foundTeam = teamService.findTeamById(createdTeam.getId());

        assertEquals(foundTeam.getName(), testTeam.getName());
    }

    @Test
    public void findTeamById_teamDoesNotExists_throwsException(){
        testTeam.setId(1L);
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findById(testTeam.getId())).thenReturn(Optional.of(testTeam));

        assertThrows(ResponseStatusException.class, () -> teamService.findTeamById(2L));
    }

    @Test
    public void deleteTeam_authorized_success(){
        testTeam.setId(1L);
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findById(testTeam.getId())).thenReturn(Optional.of(testTeam));

        teamService.deleteTeam(testTeam.getId(), testUser.getToken());
        Mockito.verify(teamRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    public void deleteTeam_notAuthorized_trowsException(){
        testTeam.setId(1L);
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findById(testTeam.getId())).thenReturn(Optional.of(testTeam));

        assertThrows(ResponseStatusException.class, () -> teamService.deleteTeam(testTeam.getId(), "invalid"));
    }

    @Test
    public void authorizeAdmin_success(){
        Team createdTeam = teamService.createTeam(testTeam, testUser);

        assertTrue(teamService.authorizeAdmin(testTeam, testUser.getToken()));
    }

    @Test
    public void authorizeAdmin_notAuthorized_throwsException(){
        Team createdTeam = teamService.createTeam(testTeam, testUser);

        assertThrows(ResponseStatusException.class, () -> teamService.authorizeAdmin(testTeam, "invalid"));
    }

    @Test
    public void getAllTeams_success(){
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        Mockito.when(teamRepository.findAll()).thenReturn(Collections.singletonList(createdTeam));

        List<Team> teams = teamService.getTeams();
        assertEquals(1, teams.size());
    }

    @Test
    public void getAllUsersOfTeam_authorized_success(){
        Team createdTeam = teamService.createTeam(testTeam, testUser);

        //mocks
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(createdTeam));

        Set<User> users = teamService.getAllUsersOfTeam(createdTeam.getId());
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertTrue(users.contains(testUser));
    }
}
