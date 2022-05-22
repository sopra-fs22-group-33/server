package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;
import ch.uzh.ifi.hase.soprafs22.entity.PreferenceSlot;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.PreferenceCalendarRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

public class PreferenceCalendarServiceTest {

    @Mock
    private PreferenceCalendarRepository preferenceCalendarRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PreferenceCalendarService preferenceCalendarService;

    private User testUser;
    private PreferenceCalendar testPreferenceCalendar;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(2L);
        testPreferenceCalendar = new PreferenceCalendar();
        PreferenceDay day = new PreferenceDay ();
        PreferenceSlot slot = new PreferenceSlot();
        slot.setBase(1);
        List<PreferenceSlot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<PreferenceDay> days = Collections.singletonList(day);
        testPreferenceCalendar.setPreferencePlan(days);


        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testUser));
        Mockito.when(preferenceCalendarRepository.save(Mockito.any())).then(returnsFirstArg());
    }

    @Test
    @Transactional
    public void getPreferenceCalendar_success() {
        testUser.setPreferenceCalendar(testPreferenceCalendar);

        PreferenceCalendar prefCal = preferenceCalendarService.getPreferenceCalendar(testUser);

        assertEquals(prefCal.getPreferencePlan(), testPreferenceCalendar.getPreferencePlan());
    }

    @Test
    public void createUserCalendar_noCalendar_emptyCalendarReturned() {
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();
        PreferenceCalendar newCalendar = preferenceCalendarService.createPreferenceCalendar(testUser.getId(), preferenceCalendar);

        assertEquals(7, newCalendar.getPreferencePlan().size());
    }

    @Test
    public void createUserCalendar_noUser_throwsException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        PreferenceCalendar preferenceCalendar = new PreferenceCalendar();

        assertThrows(ResponseStatusException.class, () -> preferenceCalendarService.createPreferenceCalendar(testUser.getId(), preferenceCalendar));
    }

    @Test
    public void createUserCalendar_notEmptyCalendar_calendarReturned() {
        PreferenceCalendar newCalendar = preferenceCalendarService.createPreferenceCalendar(testUser.getId(), testPreferenceCalendar);

        assertFalse(newCalendar.getPreferencePlan().isEmpty());
        assertFalse(newCalendar.getPreferencePlan().get(0).getSlots().isEmpty());
    }

    @Test
    public void updateUserCalendar_notEmptyCalendar_calendarUpdated() {
        PreferenceCalendar preferenceCalendar = preferenceCalendarService.createPreferenceCalendar(testUser.getId(), testPreferenceCalendar);

        assertEquals(1, preferenceCalendar.getPreferencePlan().get(0).getSlots().get(0).getBase());

        PreferenceCalendar update = new PreferenceCalendar();
        PreferenceDay day = new PreferenceDay ();
        PreferenceSlot slot = new PreferenceSlot();
        slot.setBase(-1);
        slot.setTimeFrom(0);
        slot.setTimeTo(0);
        List<PreferenceSlot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<PreferenceDay> days = Collections.singletonList(day);
        update.setPreferencePlan(days);

        PreferenceCalendar updatedCalendar = preferenceCalendarService.updatePreferenceCalendar(testUser, update);

        //check if slot was overridden
        assertEquals(slot.getBase(), updatedCalendar.getPreferencePlan().get(0).getSlots().get(0).getBase());
        assertEquals(slot.getTimeFrom(), updatedCalendar.getPreferencePlan().get(0).getSlots().get(0).getTimeTo());
        assertEquals(slot.getTimeTo(), updatedCalendar.getPreferencePlan().get(0).getSlots().get(0).getTimeTo());
    }
}


