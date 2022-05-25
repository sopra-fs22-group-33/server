package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional
public class UserCalendarService {
    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final UserCalendarRepository userCalendarRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final UserDayRepository userDayRepository;


    @Autowired
    public UserCalendarService(@Qualifier ("userCalendarRepository") UserCalendarRepository userCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                               @Qualifier("userRepository") UserRepository userRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,
                               @Qualifier ("userDayRepository") UserDayRepository userDayRepository) {
        this.userCalendarRepository = userCalendarRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.playerRepository= playerRepository;
        this.userDayRepository = userDayRepository;
    }

    public UserCalendar getUserCalendar (User user){
        int differenceInDays;
        LocalDate startingDate = LocalDate.now();
        int i;

        //set startingDate to the lowest date of all teamCalendars
        for (Membership membership : user.getMemberships()) {
            if (startingDate.isAfter(membership.getTeam().getTeamCalendar().getStartingDateFixed())){
                startingDate = membership.getTeam().getTeamCalendar().getStartingDateFixed();
            }
        }

        UserCalendar userCalendar = new UserCalendar();
        userCalendar.setStartingDate(startingDate);
        userCalendar.setUserPlan(new ArrayList<>());

        //fill with empty days
        for (int j=0; j<7; j++){
            UserDay userD = new UserDay();
            userD.setWeekday(j);
            userD.setSlots(new ArrayList<>());
            userCalendar.getUserPlan().add(userD);
        }

        for (Membership membership : user.getMemberships()){
            differenceInDays = (int) (DAYS.between(startingDate, membership.getTeam().getTeamCalendar().getStartingDateFixed()));

                for (Day day : membership.getTeam().getTeamCalendar().getBasePlanFixed()){
                UserDay uD = new UserDay();
                uD.setSlots(new ArrayList<>());
                userCalendar.getUserPlan().add(uD);
                uD.setWeekday(userCalendar.getUserPlan().indexOf(uD));
                i = membership.getTeam().getTeamCalendar().getBasePlanFixed().indexOf(day);
                for (Slot slot : day.getSlots()) {
                    UserSlot userSlot = new UserSlot();
                    userSlot.setSchedules(new ArrayList<>());
                    for (Schedule schedule : slot.getSchedules()) {
                        if (schedule.getUser() == user && schedule.getAssigned() == 1) {
                            UserSchedule userSchedule = new UserSchedule();
                            userSchedule.setTeam(membership.getTeam());
                            userSlot.getSchedules().add(userSchedule);
                        }
                    }
                    if (!(userSlot.getSchedules() == null)) {
                        userSlot.setTimeFrom(slot.getTimeFrom());
                        userSlot.setTimeTo(slot.getTimeTo());
                        if (!userSlot.getSchedules().isEmpty()) {
                            userCalendar.getUserPlan().get(i - differenceInDays).getSlots().add(userSlot);
                        }
                    }
                }
            }
        }
        return userCalendar;
    }

}
