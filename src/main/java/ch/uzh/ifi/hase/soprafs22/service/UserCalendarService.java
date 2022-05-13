package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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
        LocalDate startingDate = LocalDate.of(2022,5,10);
        LocalDate today = LocalDate.now();
        int i;

        for (Membership membership : user.getMemberships()) {
            if (startingDate.isAfter(membership.getTeam().getTeamCalendar().getStartingDate())){
                startingDate = membership.getTeam().getTeamCalendar().getStartingDate();
            }
        }

        UserCalendar userCalendar = new UserCalendar();
//        userCalendar.setStartingDate(startingDate);
        for (Membership membership : user.getMemberships()){
            differenceInDays = (int) (DAYS.between(startingDate, today));

            for (Day day : membership.getTeam().getTeamCalendar().getBasePlan()){
                i = membership.getTeam().getTeamCalendar().getBasePlan().indexOf(day);
                for (Slot slot : day.getSlots()) {
                    UserSlot userSlot = new UserSlot();
                    for (Schedule schedule : slot.getSchedules()) {
                        if (schedule.getUser() == user) {
                            UserSchedule userSchedule = new UserSchedule();
                            userSchedule.setTeam(membership.getTeam());
                            userSlot.getUserSchedules().add(userSchedule);
                        }
                    }
                    if (!userSlot.getUserSchedules().isEmpty()) {
                        userSlot.setTimeFrom(slot.getTimeFrom());
                        userSlot.setTimeTo(slot.getTimeTo());
                        userCalendar.getUserPlan().get(i - differenceInDays).getUserSlots().add(userSlot);
                    }
                }
            }
        }
        return userCalendar;
    }

}
