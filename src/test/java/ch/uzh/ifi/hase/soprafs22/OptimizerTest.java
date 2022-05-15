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

    }


}