package ch.uzh.ifi.hase.soprafs22.controller;


import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TeamCalendarControllerTest
 * This is a WebMvcTest which allows to test the TeamCalendarController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the TeamCalendarController works.
 */
@WebMvcTest(TeamCalendarController.class)
public class TeamCalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private TeamCalendarService teamCalendarService;


    @Test
    public void givenTeamCalendar_whenGetTeamCalendar_thenReturnJsonArray() throws Exception {
        // given
        TeamCalendar teamCalendar = new TeamCalendar();
        //teamCalendar.setStartingDate("123");

        List<TeamCalendar> allTeamCalendars = Collections.singletonList(teamCalendar);

        given(teamCalendarService.getCalendars()).willReturn( allTeamCalendars);

        // when
        MockHttpServletRequestBuilder getRequest = get("/teamCalendars").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].startingDate", is(teamCalendar.getStartingDate())));
    }

    @Test
    public void createTeamCalendar_validInput_teamCalendarCreated() throws Exception {
        // given
        TeamCalendar teamCalendar = new TeamCalendar();
        Day day = new Day ();
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
        teamCalendar.setStartingDate(LocalDate.parse("2000-10-10"));

        //creating teamCalendarPostDTO
        TeamCalendarPostDTO teamCalendarPostDTO = new TeamCalendarPostDTO();

        //defining mocks
        given(teamCalendarService.createTeamCalendar(Mockito.anyLong(), Mockito.any(TeamCalendar.class))).willReturn(teamCalendar);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/teams/1/calendars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(teamCalendarPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

    }

    @Test
    public void changeTeamCalendar_validInput_teamCalendarChanged() throws Exception {
        // given
        TeamCalendar teamCalendar = new TeamCalendar();
        Day day = new Day ();
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
        teamCalendar.setStartingDate(LocalDate.now());

        TeamCalendarPostDTO teamCalendarPostDTO = new TeamCalendarPostDTO();


        given(teamCalendarService.updateTeamCalendar(Mockito.anyLong(), Mockito.any(TeamCalendar.class), Mockito.anyString())).willReturn(teamCalendar);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/teams/1/calendars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(teamCalendarPostDTO));


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isCreated());

    }

    /**
     * Helper Method to convert teamPostDTO into a JSON string such that the input
     * can be processed
     *
     *
     * @param object
     * @return string
     */

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}