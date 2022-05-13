package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

public class PreferenceCalendarService {
    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final DayRepository dayRepository;


    @Autowired
    public PreferenceCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                                     @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,
                                     @Qualifier("dayRepository") DayRepository dayRepository) {
        this.teamCalendarRepository = teamCalendarRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.playerRepository= playerRepository;
        this.dayRepository= dayRepository;
    }

    public PreferenceCalendar getPreferenceCalendar (User user){
        return user.get
    }

}
