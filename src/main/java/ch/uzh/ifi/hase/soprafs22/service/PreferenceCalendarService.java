package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PreferenceCalendarService {
    private final Logger log = LoggerFactory.getLogger(PreferenceCalendarService.class);

    private final PreferenceCalendarRepository preferenceCalendarRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final PreferenceDayRepository preferenceDayRepository;
    private final PreferenceSlotRepository preferenceSlotRepository;


    @Autowired
    public PreferenceCalendarService(@Qualifier("preferenceCalendarRepository") PreferenceCalendarRepository preferenceCalendarRepository,
                                     @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,
                                     @Qualifier("preferenceDayRepository") PreferenceDayRepository preferenceDayRepository, PreferenceSlotRepository preferenceSlotRepository) {

        this.preferenceCalendarRepository = preferenceCalendarRepository;
        this.preferenceSlotRepository = preferenceSlotRepository;
        this.userRepository = userRepository;
        this.playerRepository= playerRepository;
        this.preferenceDayRepository= preferenceDayRepository;
    }

    public PreferenceCalendar getPreferenceCalendar (User user){
        return user.getPreferenceCalendar();
    }

    public PreferenceCalendar updatePreferenceCalendar (User user, PreferenceCalendar updatedCalendar){
        PreferenceCalendar oldCalendar = user.getPreferenceCalendar();
        oldCalendar.getPreferencePlan().clear();
        preferenceCalendarRepository.save(oldCalendar);
        preferenceCalendarRepository.flush();

        for (PreferenceDay day : updatedCalendar.getPreferencePlan()){
            oldCalendar.getPreferencePlan().add(day);
            day.setPreferenceCalendar(oldCalendar);

            for (PreferenceSlot slot : day.getPreferenceSlots()){
                slot.setPreferenceDay(day);
            }
        }
        PreferenceCalendar savedCalendar = preferenceCalendarRepository.save(oldCalendar);
        preferenceCalendarRepository.flush();
        return savedCalendar;
    }

}
