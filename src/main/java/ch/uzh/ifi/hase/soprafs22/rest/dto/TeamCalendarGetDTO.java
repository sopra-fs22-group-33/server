package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.*;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Event;
import ch.uzh.ifi.hase.soprafs22.entity.Schedule;

public class TeamCalendarGetDTO {

    private Long id;

    private String startingDate;


    static class SlotAPI {
        public int from ;
        public int to;
        public HashMap<Long,Long> base;
        public HashMap<Long,Long> special;
        public List<Long> assignedUsers;
    }

    static class DayAPI {
        public Weekday weekday;
        public List <SlotAPI>  slots;
    }

    private List <DayAPI> days;




    public  List <DayAPI> getDays() {
        return days;
    }


    public void setDays(Map<Weekday, Day> days) {
        List <DayAPI> daysToSave = new ArrayList<>();
        for (Day day : days.values()) {
            DayAPI d = new DayAPI();
            List <SlotAPI>  slots = new ArrayList<>();

            d.weekday = day.getWeekday();

            Set<Event> events = day.getEvents();
            for (Event event:events){
                            SlotAPI s = new SlotAPI();
                            HashMap<Long,Long> base = new HashMap<>();
                            HashMap<Long,Long> special = new HashMap<>();
                            List<Long> userId = new ArrayList<>();
                            s.from = event.getTimeFrom();
                            s.to = event.getTimeTo();
                            Set<Schedule> schedules = event.getSchedules();
                            for (Schedule schedule:schedules){
                                base.put(schedule.getId(), schedule.getBasePreference());
                                special.put(schedule.getId(), schedule.getSpecialPreference());
                                if (schedule.getAssigned() == 1){
                                    userId.add(schedule.getId());
                                }

                            }
                            s.assignedUsers = userId;
                            s.base = base;
                            s.special = special;
                            slots.add(s);
            }
            d.slots = slots;

        daysToSave.add(d);
        }
        this.days = daysToSave;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }
}
