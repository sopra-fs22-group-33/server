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

import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeamCalendarService {



    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;
    private final TeamRepository teamRepository;
    private final DayRepository dayRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Autowired
    public TeamCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                               @Qualifier("eventRepository") EventRepository eventRepository, @Qualifier("dayRepository") DayRepository dayRepository, @Qualifier("userRepository") UserRepository userRepository) {
        this.teamCalendarRepository = teamCalendarRepository;
        this.teamRepository = teamRepository;
        this.dayRepository = dayRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public  List<TeamCalendar> getCalendars() {
        return this.teamCalendarRepository.findAll();
    }

    public  TeamCalendar getCalendar(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            return foundTeam.getTeamCalendar();
      }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public TeamCalendar updateTeamCalendar(Long id, TeamCalendar newCalendar){
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            TeamCalendar oldCalendar = foundTeam.getTeamCalendar();
            if(oldCalendar != null){
                teamCalendarRepository.delete(oldCalendar);
            }

            return (createTeamCalendar2(id, newCalendar));
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public TeamCalendar createTeamCalendar2(long id, TeamCalendar newCalendar) {


        //checkIfTeamHasCalendar(id);

        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            foundTeam.setTeamCalendar(newCalendar);
            newCalendar.setTeam(foundTeam);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        for (Day day: newCalendar.getBasePlan().values()){
            day.setTeamCalendar(newCalendar);

            for (Event event: day.getEvents()){
                event.setDay(day);
                for(Schedule schedule:event.getSchedules()){
                    schedule.setEvent(event);
                    Optional<User> user = userRepository.findById(schedule.getUser().getId());
                    if (user.isPresent()){
                        User foundUser = user.get();
                        foundUser.addSchedule(schedule);
                        schedule.setUser(foundUser);
                    }
                    else throw new ResponseStatusException(HttpStatus.NOT_FOUND);

                };

            }
            }

        newCalendar = teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();
        log.debug("Created calendar for Team: {}",id);
        return newCalendar;
    }


    public void deleteCalendar (Long id){
        //
    }

    private void checkIfTeamHasCalendar(Long id) {
        Optional<TeamCalendar> CalendarByTeam = teamCalendarRepository.findById(id);

        String baseErrorMessage = "This team already has base calendar. If you want to update it, make put request";
       if (CalendarByTeam != null) { // TOD: open up optional correctly
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage));
        }

    }

    private void checkIfUserExists(Long id) {
        Optional<User> user = userRepository.findById(id);

        String baseErrorMessage = "This team already has base calendar. If you want to update it, make put request";
        if (user == null) { // TOD: open up optional correctly
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format(baseErrorMessage));
        }

    }

}
