package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Event;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;

import java.util.*;

public class TeamCalendarPostDTO {

    private String name;
    private List<DayAPI> days;

    static class SlotAPI {
        Long From;
        Long To;
        Long[] A;
    }

    static class DayAPI {
        public int weekday;
        SlotAPI[] slots;
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
                case 1:
                    dayEntity.setWeekday(Weekday.TUESDAY);
                case 2:
                    dayEntity.setWeekday(Weekday.WEDNESDAY);
                case 3:
                    dayEntity.setWeekday(Weekday.THURSDAY);
                case 4:
                    dayEntity.setWeekday(Weekday.FRIDAY);
                case 5:
                    dayEntity.setWeekday(Weekday.SATURDAY);
                case 6:
                    dayEntity.setWeekday(Weekday.SUNDAY);
            }

            Set<Event> eventsEntity = new HashSet<Event>();

            for (SlotAPI slot : day.slots) {
                Event eventEntity = new Event();
                eventEntity.setFrom(slot.From);
                eventEntity.setTo(slot.From);
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
