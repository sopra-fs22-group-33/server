package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
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
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TeamCalendarServiceTest {

    @Mock
    private TeamCalendarRepository teamCalendarRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PlayerRepository playerRepository;

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
        testTeam.setName("testTeamname");
        testTeamCalendar = new TeamCalendar();
        testTeamCalendar.setStartingDate(LocalDate.now());

        Mockito.when(teamCalendarRepository.save(Mockito.any())).thenReturn(testTeamCalendar);
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testTeam));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));

    }

    @Test
    public void getTeamCalendars_validInputs_success() {
        Mockito.when(teamCalendarRepository.findAll()).thenReturn(Collections.singletonList(testTeamCalendar));

        // then
        List <TeamCalendar> teamCalendars = teamCalendarService.getCalendars();

        assertEquals(teamCalendars.get(0).getStartingDate(), testTeamCalendar.getStartingDate());
    }

    @Test
    public void getTeamCalendar_validInputs_success() {
        testTeam.setTeamCalendar(testTeamCalendar);

        TeamCalendar teamCalendar = teamCalendarService.getCalendar(testTeam.getId());

        assertEquals(teamCalendar.getStartingDate(), testTeamCalendar.getStartingDate());
    }

    @Test
    public void getTeamCalendar_notFound_success() {
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> teamCalendarService.getCalendar(testTeam.getId()));
    }

    @Test
    public void createTeamCalendar_validInputs_success_empty_calendar() {
        TeamCalendar createdTeamCalendar = teamCalendarService.createTeamCalendar(1L, testTeamCalendar );

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), createdTeamCalendar.getStartingDate());
    }

    @Test
    public void updateOptimizedTeamCalendar_validInputs_success_empty_calendar() {
        teamCalendarService.updateOptimizedTeamCalendar(1L, testTeamCalendar );

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(1)).save(Mockito.any());

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
        List<Day> days2 = new ArrayList<>();
        days2.add(day2);
        TeamCalendar update = new TeamCalendar();
        update.setBasePlan(days2);
        update.setStartingDate(LocalDate.now());
        TeamCalendar updatedTeamCalendar = teamCalendarService.updateTeamCalendar(1L, update );
        assertEquals(testTeamCalendar.getBasePlan().size(), updatedTeamCalendar.getBasePlan().size());

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(3)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), updatedTeamCalendar.getStartingDate());
        assertEquals(testTeamCalendar.getBasePlan().size(), updatedTeamCalendar.getBasePlan().size());
    }

    @Test
    @Transactional
    public void modifyTeamCalendar_noUser_ThrowsException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        testTeam.setTeamCalendar(testTeamCalendar);

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
        List<Day> days2 = new ArrayList<>();
        days2.add(day2);
        TeamCalendar update = new TeamCalendar();
        update.setBasePlan(days2);
        update.setStartingDate(LocalDate.now());

        assertThrows(ResponseStatusException.class, () -> teamCalendarService.updateTeamCalendar(1L, update ));
    }

    @Test
    @Transactional
    public void modifyTeamCalendar_noTeam_ThrowsException() {
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        testTeam.setTeamCalendar(testTeamCalendar);

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
        List<Day> days2 = new ArrayList<>();
        days2.add(day2);
        TeamCalendar update = new TeamCalendar();
        update.setBasePlan(days2);
        update.setStartingDate(LocalDate.now());

        assertThrows(ResponseStatusException.class, () -> teamCalendarService.updateTeamCalendar(1L, update ));
    }

    @Test
    @Transactional
    public void createTeamCalendar_noUser_ThrowsException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        testTeam.setTeamCalendar(testTeamCalendar);

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

        assertThrows(ResponseStatusException.class, () -> teamCalendarService.createTeamCalendar(1L, testTeamCalendar ));
    }

    @Test
    @Transactional
    public void createTeamCalendar_noTeam_ThrowsException() {
        Mockito.when(teamRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        testTeam.setTeamCalendar(testTeamCalendar);

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

        assertThrows(ResponseStatusException.class, () -> teamCalendarService.createTeamCalendar(1L, testTeamCalendar ));
    }

    @Test
    public void checkCollisionsWithoutGameStart_noCollision_returnFalse() {
        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(1);
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

        assertFalse(teamCalendarService.checkCollisionsWithoutGameStart(testTeamCalendar));
    }

    @Test
    public void checkCollisionsWithoutGameStart_Collision_returnTrue() {
        User testUser2 = new User();

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(1);
        schedule2.setBase(1);
        schedule2.setUser(testUser2);
        List<Schedule> schedules = List.of((new Schedule[]{schedule, schedule2}));
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = new ArrayList<>();
        days.add(day);
        testTeamCalendar.setBasePlan(days);
        testTeamCalendar.setStartingDate(LocalDate.now());

        assertTrue(teamCalendarService.checkCollisionsWithoutGameStart(testTeamCalendar));
    }

    @Test
    public void checkCollisions_noCollision_return0() {

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(1);
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

        assertEquals(0, teamCalendarService.checkCollisions(testTeamCalendar));
    }

    @Test
    public void checkCollisions_CollisionPart1_return1() {
        User testUser2 = new User();

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(1);
        schedule2.setBase(1);
        schedule2.setUser(testUser2);
        List<Schedule> schedules = List.of((new Schedule[]{schedule, schedule2}));
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = new ArrayList<>();
        days.add(day);
        testTeamCalendar.setBasePlan(days);
        testTeamCalendar.setStartingDate(LocalDate.now());

        assertEquals(1, teamCalendarService.checkCollisions(testTeamCalendar));
    }

    @Test
    public void checkCollisions_CollisionTrivialResolutionPart1_return0() {
        User testUser2 = new User();

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(1);
        schedule2.setBase(1);
        schedule2.setUser(testUser2);
        List<Schedule> schedules = List.of((new Schedule[]{schedule, schedule2}));
        slot.setSchedules(schedules);
        slot.setRequirement(0);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = new ArrayList<>();
        days.add(day);
        testTeamCalendar.setBasePlan(days);
        testTeamCalendar.setStartingDate(LocalDate.now());

        assertEquals(0, teamCalendarService.checkCollisions(testTeamCalendar));
    }

    @Test
    public void checkCollisions_Collision_irresolvable_returnMinus1() {
        User testUser2 = new User();

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(1);
        schedule2.setUser(testUser2);
        List<Schedule> schedules = List.of((new Schedule[]{schedule, schedule2}));
        slot.setSchedules(schedules);
        slot.setRequirement(3);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = new ArrayList<>();
        days.add(day);
        testTeamCalendar.setBasePlan(days);
        testTeamCalendar.setStartingDate(LocalDate.now());

        assertEquals(-1, teamCalendarService.checkCollisions(testTeamCalendar));
    }

    @Test
    public void checkCollisions_CollisionPart2_return1() {

        User testUser2 = new User();
        User testUser3 = new User();

        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(1);
        schedule2.setUser(testUser2);
        Schedule schedule3 = new Schedule();
        schedule3.setSpecial(1);
        schedule3.setBase(1);
        schedule3.setUser(testUser3);
        List<Schedule> schedules = List.of((new Schedule[]{schedule, schedule2, schedule3}));
        slot.setSchedules(schedules);
        slot.setRequirement(3);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = new ArrayList<>();
        days.add(day);
        testTeamCalendar.setBasePlan(days);
        testTeamCalendar.setStartingDate(LocalDate.now());

        teamCalendarService.initializeGame(slot);
        Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());
    }

//    @Test
//    //TODO
//    public void finalCalendarSubmission_returnString1() {
//        testTeam.setTeamCalendar(testTeamCalendar);
//        TeamCalendarService teamCalendarService;
////        Mockito.when(teamCalendarService.checkCollisions(Mockito.any())).thenReturn(0);
//        testTeamCalendar.setCollisions(0);
//
//        String answer = teamCalendarService.finalCalendarSubmission(testTeam.getId());
//
//    }
}


