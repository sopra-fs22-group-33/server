package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Event;

import java.util.*;

public class TeamCalendarPostDTO {

    private String name;
    private List<DayAPI> days;

    static class SlotAPI {
        public int from;
        public int to;
        public Long[] A;
    }

    static class DayAPI {
        public int weekday;
        public List <SlotAPI>  slots;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public Map<Weekday, Day>  getDays() {

        Map<Weekday, Day> basePlan = null;
        for (DayAPI day : this.days) {
            basePlan = new LinkedHashMap<Weekday, Day>();
            Day dayEntity = new Day();
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
                eventEntity.setTimeFrom(slot.from);
                eventEntity.setTimeTo(slot.to);
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
