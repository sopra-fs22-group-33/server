package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the TeamResource REST resource.
 *
 * @see TeamService
 */
@WebAppConfiguration
@SpringBootTest
public class TeamServiceIntegrationTest {

  @Qualifier("teamRepository")
  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private TeamService teamService;

  @BeforeEach
  public void setup() {
    teamRepository.deleteAll();
  }

  @Test
  public void createTeam_validInputs_success() {
    // given
    assertNull(teamRepository.findByName("team1"));

    Team testTeam = new Team();
    testTeam.setName("team1");
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    // when
    Team createdTeam = teamService.createTeam(testTeam, testUser);

    // then
    assertEquals(testTeam.getId(), createdTeam.getId());
    assertEquals(testTeam.getName(), createdTeam.getName());
  }

  @Test
  public void createTeam_duplicateEmail_throwsException() {
    assertNull(teamRepository.findByName("team1"));

    Team testTeam = new Team();
    testTeam.setName("team1");
    User testUser = new User();
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    Team createdTeam = teamService.createTeam(testTeam, testUser);

    // attempt to create second Team with same Teamname
    Team testTeam2 = new Team();

    // enter same name
    testTeam2.setName("team1");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> teamService.createTeam(testTeam2, testUser));
  }
}
