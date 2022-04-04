package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.TeamCalendarRepository;


import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamCalendarService {



    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;

    @Autowired
    public TeamCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository) {
        this.teamCalendarRepository = teamCalendarRepository;
    }


    public TeamCalendar createTeamCalendar(long id, TeamCalendar newCalendar) {

        newCalendar = teamCalendarRepository.save(newCalendar);
        checkIfTeamHasCalendar(id);

        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newCalendar = teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();

        log.debug("Created calendar for Team: {}",id);
        return newCalendar;
    }

    private void checkIfTeamHasCalendar(Long id) {
        Optional<TeamCalendar> CalendarByTeam = teamCalendarRepository.findById(id);

        String baseErrorMessage = "This team already has base calendar. If you want to update it, make put request";
        if (CalendarByTeam != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage));
        }
    }
}
