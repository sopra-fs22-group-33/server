package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Calendar;
import java.util.Date;
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
        int differenceInDays;
        int i;

        UserCalendar userCalendar = new UserCalendar();
        userCalendar.setStartingDate(new Date().getTime());
        for (Membership membership : user.getMemberships()){
            //TODO refactor startingdate of teamcalendar
            differenceInDays = (int) userCalendar.getStartingDate() - membership.getTeam().getTeamCalendar().getStartingDate())/86’400’000;

            for (Day day : membership.getTeam().getTeamCalendar().getBasePlan()){
                i = membership.getTeam().getTeamCalendar().getBasePlan().indexOf(day);

                //only future slots will be considered
                if (i-differenceInDays >= 0){
                    for (Slot slot : day.getSlots()){
                        UserSlot userSlot = new UserSlot();
                        for (Schedule schedule : slot.getSchedules()){
                            if (schedule.getUser() == user){
                                UserSchedule userSchedule = new UserSchedule();
                                userSchedule.setTeam(membership.getTeam());
                                userSlot.getUserSchedules().add(userSchedule);
                            }
                        }
                        userCalendar.getUserPlan().get(i - differenceInDays).getUserSlots().add(userSlot);
                    }

                }
            }

        }
        return userCalendar;
    }

}
