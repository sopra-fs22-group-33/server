package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;

public class UserCalendarService {
    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final DayRepository dayRepository;


    @Autowired
    public UserCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                               @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,
                               @Qualifier("dayRepository") DayRepository dayRepository) {
        this.teamCalendarRepository = teamCalendarRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.playerRepository= playerRepository;
        this.dayRepository= dayRepository;
    }

    public UserCalendar getUserCalendar (User user){
//        List<Schedule> userSchedules = user.getSchedules();
//        for (Schedule schedule : user.getSchedules()){
//            Slot slot = schedule.getSlot();
//            UserDay userDay = new UserDay();
//            userDay.setWeekday(slot.getDay().getWeekday());
//        }
        UserCalendar userCalendar = new UserCalendar();
        for (Membership membership : user.getMemberships()){

        }
        return null;
    }

}
