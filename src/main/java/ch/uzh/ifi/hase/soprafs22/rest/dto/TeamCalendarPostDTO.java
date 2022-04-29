package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Event;
import ch.uzh.ifi.hase.soprafs22.entity.Schedule;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import java.util.*;

public class TeamCalendarPostDTO {

    private String startingDate;
    private List<DayAPI> days;

    static class SlotAPI {
        public int from;
        public int to;
        public HashMap<Long,Long> base;
        public HashMap<Long,Long> special;

    }

    static class DayAPI {
        public int weekday;
        public List <SlotAPI>  slots;
    }


    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }




    public Map<Integer, Day>  getDays() {

        Map<Integer, Day> basePlan = null;
        basePlan = new LinkedHashMap<Integer, Day>();

        for (DayAPI day : this.days) {
            Day dayEntity = new Day();
            dayEntity.setWeekday(day.weekday);
            Set<Event> eventsEntity = new HashSet<Event>();

            for (SlotAPI slot : day.slots) {
                Event eventEntity = new Event();
                Set<Schedule> schedules = new HashSet<>();

                eventEntity.setTimeFrom(slot.from);
                eventEntity.setTimeTo(slot.to);
                // works only if the length of the basea and special preferences os the same
                // and in the same order
                for (Map.Entry<Long, Long> entry : slot.base.entrySet()) {
                    Long key = entry.getKey();
                    Long value = entry.getValue();
                    Long value2 = slot.special.get(key);
                    Schedule schedule = new Schedule();
                    User user = new User();
                    user.setId(key);
                    schedule.setUser(user);
                    schedule.setBasePreference(value);
                    // try with value passing to special
                    schedule.setSpecialPreference(value);
                    schedules.add(schedule);
                }
                eventEntity.setSchedules(schedules);
                eventsEntity.add(eventEntity);
            }
            dayEntity.setEvents(eventsEntity);
            basePlan.put(day.weekday, dayEntity);

        }
        return basePlan;
    }

    public void setDays(List<DayAPI> days) {
        this.days = days;
    }
}
