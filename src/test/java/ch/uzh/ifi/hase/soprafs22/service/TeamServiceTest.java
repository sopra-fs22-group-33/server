package ch.uzh.ifi.hase.soprafs22.service;

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
  public void createTeam_duplicateName_throwsException() {
    // given -> a first Team has already been created
    teamService.createTeam(testTeam, testUser);

    // when -> setup additional mocks for TeamRepository
    Mockito.when(teamRepository.findByName(Mockito.any())).thenReturn(testTeam);

    // then -> attempt to create second Team with same Team -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> teamService.createTeam(testTeam, testUser));
  }
}
