package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;
import ch.uzh.ifi.hase.soprafs22.entity.PreferenceSlot;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public PreferenceCalendar createPreferenceCalendar(long userId, PreferenceCalendar newCalendar) {

        //check if user exists and has a preferenceCalendar
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()){
            User foundUser = user.get();
            foundUser.setPreferenceCalendar(newCalendar);
            newCalendar.setUser(foundUser);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "couldn't find user");

        //fill new calendar
        if (newCalendar.getPreferencePlan() != null){
            for (PreferenceDay day : newCalendar.getPreferencePlan()) {
                day.setPreferenceCalendar(newCalendar);

                if (day.getSlots() != null) {
                    for (PreferenceSlot slot : day.getSlots()) {
                        slot.setDay(day);
                   }
                }
            }
        }else{
            List<PreferenceDay> preferencePlan = new ArrayList<>();
            for (int i = 0; i < 7; i++){
                PreferenceDay preferenceDay = new PreferenceDay();
                preferencePlan.add(preferenceDay);
            }
            newCalendar.setPreferencePlan(preferencePlan);
        }

        PreferenceCalendar savedCalendar = preferenceCalendarRepository.save(newCalendar);
        preferenceCalendarRepository.flush();
        log.debug("Created preferenceCalendar for user: {}",userId);
        return savedCalendar;
    }

    public PreferenceCalendar updatePreferenceCalendar (User user, PreferenceCalendar updatedCalendar){
        PreferenceCalendar oldCalendar = user.getPreferenceCalendar();

        for (PreferenceDay day : oldCalendar.getPreferencePlan()){
            for (PreferenceSlot slot : day.getSlots()){
                slot.setBase(updatedCalendar.getPreferencePlan().get(oldCalendar.getPreferencePlan().indexOf(day)).getSlots().get(day.getSlots().indexOf(slot)).getBase());
            }
        }
        PreferenceCalendar savedCalendar = preferenceCalendarRepository.save(oldCalendar);
        preferenceCalendarRepository.flush();
        return savedCalendar;
    }

}
