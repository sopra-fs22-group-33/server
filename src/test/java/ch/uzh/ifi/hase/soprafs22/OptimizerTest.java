package ch.uzh.ifi.hase.soprafs22;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OptimizerTest {
/*
    @Test
    public void OptimizerTest_One_User() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        teamCalendar.setId(1L);
        Day day = new Day();
        day.setTeamCalendar(teamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        slot.setTimeFrom(11);
        slot.setTimeTo(14);
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        User user = new User();
        user.setId(1L);
        schedule.setUser(user);
        schedule.setSlot(slot);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        user.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate(LocalDate.now());

        Optimizer optimizer = new Optimizer (teamCalendar);
        assertEquals(1, schedule.getAssigned());
    }



    @Test
    public void OptimizerTest_Two_Users() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        teamCalendar.setId(1L);
        Day day = new Day();
        day.setTeamCalendar(teamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        slot.setTimeFrom(11);
        slot.setTimeTo(14);

        Schedule schedule1 = new Schedule();
        schedule1.setSpecial(-1);
        schedule1.setBase(1);
        User user1 = new User();
        user1.setId(1L);
        schedule1.setUser(user1);
        schedule1.setSlot(slot);

        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(2);
        User user2 = new User();
        user2.setId(2L);
        schedule2.setUser(user2);
        schedule2.setSlot(slot);


        List<Schedule> schedules = new ArrayList<Schedule>();
        schedules.add(schedule1);
        schedules.add(schedule2);
        slot.setSchedules(schedules);
        user1.setSchedules(Collections.singletonList(schedule1));
        user2.setSchedules(Collections.singletonList(schedule2));
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate(LocalDate.now());

        Optimizer optimizer = new Optimizer (teamCalendar);
        assertEquals(0, schedule1.getAssigned());
        assertEquals(1, schedule2.getAssigned());
    }

    @Test
    public void OptimizerTest_Special_Preference_Collision() throws Exception { // should ignore the special preference to stick to the requirements
        TeamCalendar teamCalendar = new TeamCalendar();
        teamCalendar.setId(1L);
        Day day = new Day();
        day.setTeamCalendar(teamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        slot.setTimeFrom(11);
        slot.setTimeTo(14);
        Schedule schedule = new Schedule();
        schedule.setSpecial(0);
        schedule.setBase(1);
        User user = new User();
        user.setId(1L);
        schedule.setUser(user);
        schedule.setSlot(slot);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        user.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate(LocalDate.now());

        Optimizer optimizer = new Optimizer (teamCalendar);
        assertEquals(1, schedule.getAssigned());
    }

    @Test
    public void OptimizerTest_Daily_Hour_Collision() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        teamCalendar.setId(1L);
        Day day = new Day();
        day.setTeamCalendar(teamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        slot.setTimeFrom(1);
        slot.setTimeTo(19);
        Schedule schedule = new Schedule();
        schedule.setSpecial(0);
        schedule.setBase(1);
        User user = new User();
        user.setId(1L);
        schedule.setUser(user);
        schedule.setSlot(slot);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        user.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate(LocalDate.now());


        ArithmeticException thrown = Assertions.assertThrows(ArithmeticException.class, () -> {
            new Optimizer (teamCalendar);
        }, "Not possible to optimize, ask the admin to change his requirements");
        Assertions.assertEquals("Not possible to optimize, ask the admin to change his requirements", thrown.getMessage());

    }

    @Test
    public void OptimizerTest_Internal_Collision() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        teamCalendar.setId(1L);
        Day day = new Day();
        day.setTeamCalendar(teamCalendar);

        Slot slot1 = new Slot();
        slot1.setDay(day);
        slot1.setTimeFrom(1);
        slot1.setTimeTo(23);
        Schedule schedule1 = new Schedule();
        schedule1.setSpecial(-1);
        schedule1.setBase(1);
        User user = new User();
        user.setId(1L);
        schedule1.setUser(user);
        schedule1.setSlot(slot1);
        List<Schedule> schedules = Collections.singletonList(schedule1);
        slot1.setSchedules(schedules);
        slot1.setRequirement(1);

        Slot slot2 = new Slot();
        slot2.setDay(day);
        slot2.setTimeFrom(2);
        slot2.setTimeTo(22);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(1);
        schedule2.setUser(user);
        schedule2.setSlot(slot2);
        List<Schedule> schedules2 = Collections.singletonList(schedule2);
        slot2.setSchedules(schedules2);
        slot2.setRequirement(1);


        List<Schedule> schedulesUser = new ArrayList<>();
        schedulesUser.add(schedule1);
        schedulesUser.add(schedule2);
        user.setSchedules(schedulesUser);

        List<Slot> slots = new ArrayList<>();
        slots.add(slot1);
        slots.add(slot2);
        day.setSlots(slots);

        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate(LocalDate.now());


        ArithmeticException thrown = Assertions.assertThrows(ArithmeticException.class, () -> {
            new Optimizer (teamCalendar);
        }, "Not possible to optimize, ask the admin to change his requirements");
        Assertions.assertEquals("Not possible to optimize, ask the admin to change his requirements", thrown.getMessage());


    }

    @Test
    public void OptimizerTest_Weekly_Collision() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        teamCalendar.setId(1L);
        Day day1 = new Day();
        day1.setTeamCalendar(teamCalendar);
        Day day2 = new Day();
        day2.setTeamCalendar(teamCalendar);

        Slot slot1 = new Slot();
        slot1.setDay(day1);
        slot1.setTimeFrom(1);
        slot1.setTimeTo(23);
        Schedule schedule1 = new Schedule();
        schedule1.setSpecial(-1);
        schedule1.setBase(1);
        User user = new User();
        user.setId(1L);
        schedule1.setUser(user);
        schedule1.setSlot(slot1);
        List<Schedule> schedules = Collections.singletonList(schedule1);
        slot1.setSchedules(schedules);
        slot1.setRequirement(1);
        slot1.setDay(day1);

        Slot slot2 = new Slot();
        slot2.setDay(day2);
        slot2.setTimeFrom(2);
        slot2.setTimeTo(22);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(1);
        schedule2.setUser(user);
        schedule2.setSlot(slot2);
        List<Schedule> schedules2 = Collections.singletonList(schedule2);
        slot2.setSchedules(schedules2);
        slot2.setRequirement(1);
        slot2.setDay(day2);


        List<Schedule> schedulesUser = new ArrayList<>();
        schedulesUser.add(schedule1);
        schedulesUser.add(schedule2);
        user.setSchedules(schedulesUser);

        List<Slot> slots = Collections.singletonList(slot1);
        day1.setSlots(slots);
        List<Slot> slots2 = Collections.singletonList(slot2);
        day2.setSlots(slots2);

        List<Day> days = new ArrayList<>();
        days.add(day1);
        days.add(day2);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate(LocalDate.now());


        ArithmeticException thrown = Assertions.assertThrows(ArithmeticException.class, () -> {
            new Optimizer (teamCalendar);
        }, "Not possible to optimize, ask the admin to change his requirements");
        Assertions.assertEquals("Not possible to optimize, ask the admin to change his requirements", thrown.getMessage());
    }

*/
}