package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.TeamCalendarRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
/*
public class TeamCalendarTest {

    @Mock
    private TeamCalendarRepository teamCalendarRepository;

    @InjectMocks
    private TeamCalendarService teamCalendarService;



    private Team testTeam;
    private User testUser;
    private TeamCalendar testTeamCalendar;
    private TeamService teamService = new TeamService();


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("testTeamname");
        testTeamCalendar = new TeamCalendar();

        Mockito.when(teamCalendarRepository.save(Mockito.any())).thenReturn(testTeamCalendar);

    }

    @Test
    public void createTeam_validInputs_success() {
        // when -> any object is being save in the TeamRepository -> return the dummy
        // testTeam
        Team createdTeam = teamService.createTeam(testTeam, testUser);
        TeamCalendar createdTeamCalendar = teamCalendarService.createTeamCalendar(1L, testTeamCalendar );

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), createdTeamCalendar.getStartingDate());
        assertEquals(testTeamCalendar.getBasePlan().size(), createdTeamCalendar.getBasePlan().size());

    }
}

 */
