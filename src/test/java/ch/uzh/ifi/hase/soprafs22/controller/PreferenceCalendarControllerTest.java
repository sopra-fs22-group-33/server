package ch.uzh.ifi.hase.soprafs22.controller;


import ch.uzh.ifi.hase.soprafs22.entity.PreferenceCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;
import ch.uzh.ifi.hase.soprafs22.entity.PreferenceSlot;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PreferenceCalendarPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.PreferenceCalendarService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNotNull;
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
@WebMvcTest(PreferenceCalendarController.class)
public class PreferenceCalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PreferenceCalendarService preferenceCalendarService;

    @MockBean
    private UserService userService;

    @Test
    public void givenPreferenceCalendar_whenGetPreferenceCalendar_thenReturnJsonArray() throws Exception {
        // given
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();

//        List<PreferenceCalendar> allPreferenceCalendars = Collections.singletonList(preferenceCalendar);

        given(preferenceCalendarService.getPreferenceCalendar(Mockito.any())).willReturn( preferenceCalendar);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/preferences").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(preferenceCalendar.getId())));
    }

    @Test
    public void createPreferenceCalendar_validInput_preferenceCalendarCreated() throws Exception {
        // given
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();
        PreferenceDay day = new PreferenceDay();
        PreferenceSlot slot = new PreferenceSlot();
        slot.setBase(1);
        List<PreferenceSlot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<PreferenceDay> days = Collections.singletonList(day);
        preferenceCalendar.setPreferencePlan(days);
        User user = new User();
        user.setId(1L);
        user.setToken("1");

        //creating teamCalendarPostDTO
        PreferenceCalendarPostDTO preferenceCalendarPostDTO = new PreferenceCalendarPostDTO();

        //defining mocks
        given(preferenceCalendarService.createPreferenceCalendar(Mockito.anyLong(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);
        given(userService.findUserById(Mockito.anyLong())).willReturn(user);
        given(userService.findUserByToken(Mockito.any())).willReturn(user);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.any())).willReturn(true);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(preferenceCalendarPostDTO))
                .header("token", "1");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(preferenceCalendar.getId())));
    }

    @Test
    public void postPreferenceCalendar_notAuthorized_throwsException() throws Exception {
        // given
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();
        PreferenceDay day = new PreferenceDay();
        PreferenceSlot slot = new PreferenceSlot();
        List<PreferenceSlot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<PreferenceDay> days = Collections.singletonList(day);
        preferenceCalendar.setPreferencePlan(days);

        //creating teamCalendarPostDTO
        PreferenceCalendarPostDTO preferenceCalendarPostDTO = new PreferenceCalendarPostDTO();

        //defining mocks
        given(preferenceCalendarService.createPreferenceCalendar(Mockito.anyLong(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.any())).willReturn(false);
        given(preferenceCalendarService.updatePreferenceCalendar(Mockito.any(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(preferenceCalendarPostDTO))
                .header("token", "1");;

        // then
        mockMvc.perform(postRequest)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(status().isForbidden());

    }

    @Test
    public void changePreferenceCalendar_validInput_preferenceCalendarChanged() throws Exception {
        // given
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();
        PreferenceDay day = new PreferenceDay();
        PreferenceSlot slot = new PreferenceSlot();
        List<PreferenceSlot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<PreferenceDay> days = Collections.singletonList(day);
        preferenceCalendar.setPreferencePlan(days);

        //creating teamCalendarPostDTO
        PreferenceCalendarPostDTO preferenceCalendarPostDTO = new PreferenceCalendarPostDTO();


        //defining mocks
        given(preferenceCalendarService.createPreferenceCalendar(Mockito.anyLong(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.any())).willReturn(true);
        given(preferenceCalendarService.updatePreferenceCalendar(Mockito.any(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(preferenceCalendarPostDTO))
                .header("token", "1");;


        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

    }

    @Test
    public void changePreferenceCalendar_notAuthorized_throwsException() throws Exception {
        // given
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();
        PreferenceDay day = new PreferenceDay();
        PreferenceSlot slot = new PreferenceSlot();
        List<PreferenceSlot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<PreferenceDay> days = Collections.singletonList(day);
        preferenceCalendar.setPreferencePlan(days);

        //creating teamCalendarPostDTO
        PreferenceCalendarPostDTO preferenceCalendarPostDTO = new PreferenceCalendarPostDTO();

        //defining mocks
        given(preferenceCalendarService.createPreferenceCalendar(Mockito.anyLong(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);
        given(userService.authorizeUser(Mockito.anyLong(), Mockito.any())).willReturn(false);
        given(preferenceCalendarService.updatePreferenceCalendar(Mockito.any(), Mockito.any(PreferenceCalendar.class))).willReturn(preferenceCalendar);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(preferenceCalendarPostDTO))
                .header("token", "1");;

        // then
        mockMvc.perform(putRequest)
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(status().isForbidden());

    }

    /**
     * Helper Method to convert PostDTO into a JSON string such that the input
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