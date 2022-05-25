package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCalendarServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCalendarService userCalendarService;

    private Team testTeam;
    private User testUser;
    private TeamCalendar testTeamCalendar;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testTeam = new Team();
        testTeam.setId(1L);
        testUser = new User();
        testUser.setId(2L);
        testTeam.setName("testTeamName");
        testTeamCalendar = new TeamCalendar();

        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testTeam));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));
    }

    @Test
    public void createUserCalendar_validInputs_success_empty_calendar() {
        testUser.setMemberships(new HashSet<>());
        UserCalendar createdUserCalendar = userCalendarService.getUserCalendar(testUser);

        LocalDate lD = LocalDate.now();
        assertEquals(createdUserCalendar.getStartingDate(), lD);
        assertTrue(createdUserCalendar.getUserPlan().isEmpty());
    }

    @Test
    public void createUserCalendar_nonEmptyTeamCalendar_success() {
        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setAssigned(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setBasePlanFixed(days);
        testTeamCalendar.setStartingDateFixed(LocalDate.of(2000,1,1));
        testTeam.setTeamCalendar(testTeamCalendar);

        Membership membership = new Membership();
        membership.setTeam(testTeam);
        membership.setUser(testUser);
        testUser.setMemberships(new HashSet<>());
        testUser.getMemberships().add(membership);

        UserCalendar createdUserCalendar = userCalendarService.getUserCalendar(testUser);

        // then
        assertEquals(testTeamCalendar.getStartingDateFixed(), createdUserCalendar.getStartingDate());
        assertEquals(testTeamCalendar.getBasePlanFixed().get(0).getWeekday(), createdUserCalendar.getUserPlan().get(0).getWeekday());
    }
}


