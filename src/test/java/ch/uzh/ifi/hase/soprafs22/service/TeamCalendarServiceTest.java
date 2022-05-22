package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.TeamCalendarRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeamCalendarServiceTest {

    @Mock
    private TeamCalendarRepository teamCalendarRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamCalendarService teamCalendarService;



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
        testUser.setToken("token");
        testTeam.setName("testTeamname");
        testTeamCalendar = new TeamCalendar();

        Mockito.when(teamCalendarRepository.save(Mockito.any())).thenReturn(testTeamCalendar);
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testTeam));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));

    }

    @Test
    public void createTeamCalendar_validInputs_success_empty_calendar() {
        // when -> any object is being save in the TeamCalendarRepository -> return the dummy
        // testTeam

        TeamCalendar createdTeamCalendar = teamCalendarService.createTeamCalendar(1L, testTeamCalendar );

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), createdTeamCalendar.getStartingDate());


    }

    @Test
    public void createTeamCalendar_validInputs_success_nonEmpty_calendar() {


        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setBasePlan(days);
        //testTeamCalendar.setStartingDate("123");

        TeamCalendarPostDTO teamCalendarPostDTO = new TeamCalendarPostDTO();
        teamCalendarPostDTO.setStartingDate(LocalDate.now());

        TeamCalendar createdTeamCalendar = teamCalendarService.createTeamCalendar(1L, testTeamCalendar );

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), createdTeamCalendar.getStartingDate());
        assertEquals(testTeamCalendar.getBasePlan().size(), createdTeamCalendar.getBasePlan().size());

    }

    @Test
    @Transactional
    public void modifyTeamCalendar_validInputs_success() {
        testTeam.setTeamCalendar(testTeamCalendar);
        Set <Membership> memberships = new HashSet<>();
        Membership m = new Membership();
        m.setTeam(testTeam);
        m.setIsAdmin(true);
        m.setUser(testUser);
        memberships.add(m);
        testTeam.setMemberships(memberships);

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = new ArrayList<>();
        days.add(day);
        testTeamCalendar.setBasePlan(days);
        testTeamCalendar.setStartingDate(LocalDate.now());

        //create calendar
        TeamCalendar createdTeamCalendar = teamCalendarService.createTeamCalendar(1L, testTeamCalendar );
        assertEquals(testTeamCalendar.getBasePlan().get(0).getSlots().size(), createdTeamCalendar.getBasePlan().get(0).getSlots().size());

        //add another day
        Day day2 = new Day ();
        Slot slot2 = new Slot();
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(1);
        schedule2.setUser(testUser);
        List<Schedule> schedules2 = Collections.singletonList(schedule2);
        slot2.setSchedules(schedules2);
        List<Slot> slots2 = Collections.singletonList(slot2);
        day2.setSlots(slots2);
        testTeamCalendar.getBasePlan().add(day2);
        TeamCalendar updatedTeamCalendar = teamCalendarService.updateTeamCalendar(1L, testTeamCalendar, "token" );
        assertEquals(testTeamCalendar.getBasePlan().size(), updatedTeamCalendar.getBasePlan().size());

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(3)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), updatedTeamCalendar.getStartingDate());
        assertEquals(testTeamCalendar.getBasePlan().size(), updatedTeamCalendar.getBasePlan().size());
    }


}


