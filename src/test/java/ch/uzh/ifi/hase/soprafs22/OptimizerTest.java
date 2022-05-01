package ch.uzh.ifi.hase.soprafs22;

import ch.uzh.ifi.hase.soprafs22.controller.TeamCalendarController;
import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Schedule;
import ch.uzh.ifi.hase.soprafs22.entity.Slot;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(Optimizer.class)
public class OptimizerTest {

    @Test
    public void OptimizerTest_One_User() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        Day day = new Day();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate("123");

        Optimizer optimizer = new Optimizer (teamCalendar);
        assertEquals(true, optimizer.isFeasible());
        assertEquals(1, schedule.getAssigned());
    }



    @Test
    public void OptimizerTest_Two_Users() throws Exception {
        TeamCalendar teamCalendar = new TeamCalendar();
        Day day = new Day();
        Slot slot = new Slot();
        Schedule schedule = new Schedule();
        schedule.setSpecial(-1);
        schedule.setBase(1);
        Schedule schedule2 = new Schedule();
        schedule2.setSpecial(-1);
        schedule2.setBase(2);
        List<Schedule> schedules =new ArrayList<>();
        schedules.add(schedule);
        schedules.add(schedule2);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        teamCalendar.setBasePlan(days);
        teamCalendar.setStartingDate("123");

        Optimizer optimizer = new Optimizer (teamCalendar);
        assertEquals(true, optimizer.isFeasible());
        assertEquals(0, schedule.getAssigned());
        assertEquals(1, schedule2.getAssigned());
    }

}