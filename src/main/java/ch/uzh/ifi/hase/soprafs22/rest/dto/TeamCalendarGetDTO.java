package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.*;

import ch.uzh.ifi.hase.soprafs22.constant.Weekday;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import ch.uzh.ifi.hase.soprafs22.entity.Event;

public class TeamCalendarGetDTO {

    private Long id;

    private String name;

    public Map<Weekday, Day> getBasePlan() {
        return basePlan;
    }

    public void setBasePlan(Map<Weekday, Day> basePlan) {
        this.basePlan = basePlan;
    }

    static class SlotAPI {
        int from;
        int to;
        Long[] A;
    }

    static class DayAPI {
        public int weekday;
        public List <SlotAPI>  slots;
    }

    public List<DayAPI> days;
    private Map<Weekday, Day> basePlan;

    public List<DayAPI> getDays() {
        return days;
    }

    public void setDays(Map<Weekday, Day>  entityDays) {

        this.days = new ArrayList<DayAPI>();
        for(Weekday key: entityDays.keySet()){
            DayAPI day = new DayAPI();
            day.weekday = 0;

            day.slots = new ArrayList<SlotAPI>();
            for (Event slot:entityDays.get(key).getEvents()){
                SlotAPI s = new SlotAPI();
                s.from = slot.getTimeFrom();
                s.to = slot.getTimeTo();
                day.slots.add(s);
            }


            this.days.add(day);
        }

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
