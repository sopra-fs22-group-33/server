package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Event;
import ch.uzh.ifi.hase.soprafs22.entity.Schedule;

import java.util.*;

public class TeamCalendarPostDTO {

    private String startingDate;
    private List<DayAPI> days;

    static class SlotAPI {
        public Long id;
        public int weekday;
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




    public Map<Weekday, Day>  getDays() {

        Map<Weekday, Day> basePlan = null;
        for (DayAPI day : this.days) {
            basePlan = new LinkedHashMap<Weekday, Day>();
            Day dayEntity = new Day();

            // TODO: change to smth else than int
            switch (day.weekday) {
                case 0:
                    dayEntity.setWeekday(Weekday.MONDAY);
                    break;
                case 1:
                    dayEntity.setWeekday(Weekday.TUESDAY);
                    break;
                case 2:
                    dayEntity.setWeekday(Weekday.WEDNESDAY);
                    break;
                case 3:
                    dayEntity.setWeekday(Weekday.THURSDAY);
                    break;
                case 4:
                    dayEntity.setWeekday(Weekday.FRIDAY);
                    break;
                case 5:
                    dayEntity.setWeekday(Weekday.SATURDAY);
                    break;
                case 6:
                    dayEntity.setWeekday(Weekday.SUNDAY);
                    break;
            }

            Set<Event> eventsEntity = new HashSet<Event>();

            for (SlotAPI slot : day.slots) {
                Event eventEntity = new Event();
                Set<Schedule> schedules = new HashSet<>();

                eventEntity.setTimeFrom(slot.from);
                eventEntity.setTimeTo(slot.to);
                eventEntity.setId(slot.id);

                // TODO: handle the case when special requirement is not give when value2 is null.......
                for (Map.Entry<Long, Long> entry : slot.base.entrySet()) {
                    Long key = entry.getKey();
                    Long value = entry.getValue();
                    Long value2 = slot.special.get(key);
                    Schedule schedule = new Schedule();

                    schedule.setId(key);
                    schedule.setBasePreference(value);
                    // try with value passing to special
                    schedule.setSpecialPreference(value2);
                    schedules.add(schedule);
                }
                eventEntity.setSchedules(schedules);
                eventsEntity.add(eventEntity);
            }
            dayEntity.setEvents(eventsEntity);
            basePlan.put(dayEntity.getWeekday(), dayEntity);

        }
        return basePlan;
    }

    public void setDays(List<DayAPI> days) {
        this.days = days;
    }
}
