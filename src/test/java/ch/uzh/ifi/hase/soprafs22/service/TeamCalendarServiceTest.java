package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

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

    @Mock
    private PreferenceCalendarRepository preferenceCalendarRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

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
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setBasePlan(new ArrayList<>());
        testTeam.setTeamCalendar(testTeamCalendar);

        Set <Membership> memberships = new HashSet<>();
        Membership m = new Membership();
        m.setTeam(testTeam);
        m.setIsAdmin(true);
        m.setUser(testUser);
        memberships.add(m);
        testTeam.setMemberships(memberships);

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

//    TODO
//    @Test
//    public void updateOptimizedTeamCalendar_validInputs_success_empty_calendar() {
//        teamCalendarService.updateOptimizedTeamCalendar(1L, testTeamCalendar );
//
//        // then
//        Mockito.verify(teamCalendarRepository, Mockito.times(2)).save(Mockito.any());
//
//    }

    @Test
    public void createTeamCalendar_validInputs_success_nonEmpty_calendar() {


        Day day = new Day ();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setId(10L);
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
        TeamCalendar updatedTeamCalendar = teamCalendarService.updateTeamCalendar(1L, update, "token");
        assertEquals(testTeamCalendar.getBasePlan().size(), updatedTeamCalendar.getBasePlan().size());

        // then
        Mockito.verify(teamCalendarRepository, Mockito.times(3)).save(Mockito.any());

        assertEquals(testTeamCalendar.getStartingDate(), updatedTeamCalendar.getStartingDate());
        assertEquals(testTeamCalendar.getBasePlan().size(), updatedTeamCalendar.getBasePlan().size());
    }

    @Test
    @Transactional
    public void Mapping_base_preferences_normal_case() {
        PreferenceCalendar prefCalendar = new PreferenceCalendar();
        prefCalendar.setUser(testUser);
        PreferenceDay prefDay = new PreferenceDay();
        prefDay.setId(5L);
        prefDay.setWeekday(0);
        List<PreferenceDay>  prefdays = Collections.singletonList(prefDay);
        PreferenceSlot prefSlot = new PreferenceSlot();
        prefSlot.setDay(prefDay);
        prefSlot.setId(6L);
        prefSlot.setTimeFrom(1);
        prefSlot.setTimeTo(4);
        prefSlot.setBase(5);
        List<PreferenceSlot>  prefSlots = Collections.singletonList(prefSlot);
        prefDay.setSlots(prefSlots);
        prefCalendar.setPreferencePlan(prefdays);
        testUser.setPreferenceCalendar(prefCalendar);
        Mockito.when(preferenceCalendarRepository.save(Mockito.any())).thenReturn(prefCalendar);


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


        // create new calendar  ( you cant get with the old one as it is passed by pointer and gets destroyed when)
        TeamCalendar testCalendarUpdate = new TeamCalendar();
        Day day2 = new Day ();
        Slot slot2 = new Slot();
        slot2.setTimeFrom(1);
        slot2.setTimeTo(3);
        slot2.setRequirement(1);
        List<Slot> slots2 = Collections.singletonList(slot2);
        day2.setSlots(slots2);
        List<Day> days2 = new ArrayList<>();
        days2.add(day2);
        testCalendarUpdate.setBasePlan(days2);
        testCalendarUpdate.setStartingDate(LocalDate.now());
        while(testCalendarUpdate.getStartingDate().plusDays(day2.getWeekday()).getDayOfWeek()!= DayOfWeek.MONDAY){
            day2.setWeekday(day2.getWeekday()+1);
        }


        TeamCalendar updatedTeamCalendar = teamCalendarService.updateTeamCalendar(1L, testCalendarUpdate, "token" );
        assertEquals(testTeamCalendar.getBasePlan().get(0).getSlots().size(), updatedTeamCalendar.getBasePlan().get(0).getSlots().size());
        assertEquals(5,updatedTeamCalendar.getBasePlan().get(0).getSlots().get(0).getSchedules().get(0).getBase());

    }


    @Test
    @Transactional
    public void Mapping_base_preferences_default_case() {
        testTeam.setTeamCalendar(testTeamCalendar);
        Set <Membership> memberships = new HashSet<>();
        Membership m = new Membership( );
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


        // create new calendar  ( you cant get with the old one as it is passed by pointer and gets destroyed when)
        TeamCalendar testCalendarUpdate = new TeamCalendar();
        Day day2 = new Day ();
        Slot slot2 = new Slot();
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(1);
        schedule2.setUser(testUser);
        List<Schedule> schedules2 = Collections.singletonList(schedule2);
        slot2.setSchedules(schedules2);
        slot2.setRequirement(1);
        List<Slot> slots2 = Collections.singletonList(slot2);
        day2.setSlots(slots2);
        List<Day> days2 = new ArrayList<>();
        days2.add(day2);
        testCalendarUpdate.setBasePlan(days2);
        testCalendarUpdate.setStartingDate(LocalDate.now());


        TeamCalendar updatedTeamCalendar = teamCalendarService.updateTeamCalendar(1L, testCalendarUpdate, "token" );
        assertEquals(testTeamCalendar.getBasePlan().get(0).getSlots().size(), updatedTeamCalendar.getBasePlan().get(0).getSlots().size());
        assertEquals(0,updatedTeamCalendar.getBasePlan().get(0).getSlots().get(0).getSchedules().get(0).getBase());

    }

    @Test
    @Transactional
    public void UpdatePreferencesTest() {

        Day day = new Day ();
        day.setTeamCalendar(testTeamCalendar);
        testTeam.setTeamCalendar(testTeamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        Schedule schedule = new Schedule();
        schedule.setSlot(slot);
        schedule.setId(10L);
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setId(1L);
        testTeamCalendar.setBasePlan(days);

        Mockito.when(scheduleRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(schedule));

        TeamCalendar testCalendarUpdate = new TeamCalendar();
        Day day2 = new Day ();
        day2.setTeamCalendar(testCalendarUpdate);
        Slot slot2 = new Slot();
        slot.setDay(day2);
        Schedule schedule2 = new Schedule();
        schedule2.setId(10L);
        schedule2.setSpecial(-4);
        schedule2.setBase(6);
        schedule2.setSlot(slot2);
        schedule2.setUser(testUser);
        List<Schedule> schedules2 = Collections.singletonList(schedule2);
        slot2.setSchedules(schedules2);
        slot2.setRequirement(1);
        List<Slot> slots2 = Collections.singletonList(slot2);
        day2.setSlots(slots2);
        List<Day> days2 = new ArrayList<>();
        days2.add(day2);
        testCalendarUpdate.setBasePlan(days2);
        testCalendarUpdate.setStartingDate(LocalDate.now());
        testCalendarUpdate.setId(1L);


        //create calendar
        TeamCalendar updatedTeamCalendar = teamCalendarService.updatePreferences(1L, testCalendarUpdate, 2L );
        assertEquals(testTeamCalendar.getBasePlan().get(0).getSlots().size(), updatedTeamCalendar.getBasePlan().get(0).getSlots().size());
        assertEquals(6,updatedTeamCalendar.getBasePlan().get(0).getSlots().get(0).getSchedules().get(0).getBase());
        assertEquals(-4,updatedTeamCalendar.getBasePlan().get(0).getSlots().get(0).getSchedules().get(0).getSpecial());
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

        assertThrows(ResponseStatusException.class, () -> teamCalendarService.updateTeamCalendar(1L, update, "token" ));
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
    public void update_calendar_after_optimizer_test() {

        Day day = new Day ();
        day.setTeamCalendar(testTeamCalendar);
        testTeam.setTeamCalendar(testTeamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        Schedule schedule = new Schedule();
        schedule.setSlot(slot);
        schedule.setId(10L);
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setId(1L);
        testTeamCalendar.setBasePlan(days);
        List<Day> daysFixed = new ArrayList<>();
        testTeamCalendar.setBasePlanFixed(daysFixed);
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setStartingDateFixed(LocalDate.now());

        assertEquals(1, testTeamCalendar.getBasePlan().size());
        TeamCalendarPostDTO teamCalendarPostDTO = new TeamCalendarPostDTO();
        teamCalendarPostDTO.setStartingDate(LocalDate.now());
        teamCalendarService.updateOptimizedTeamCalendar(1L, testTeamCalendar );

        assertEquals(1,testTeamCalendar.getBasePlanFixed().size());
        assertEquals(1,testTeamCalendar.getBasePlan().size());

        Optional<Team> team = teamRepository.findById(1L);

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



}


