package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.TeamCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import ilog.concert.IloException;
@RestController
public class TeamCalendarController {
    private final TeamCalendarService teamCalendarService;

    TeamCalendarController(TeamCalendarService teamCalendarService) {
        this.teamCalendarService = teamCalendarService;
    }


    @PostMapping("/teams/{teamId}/calendars")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TeamCalendarGetDTO createTeamCalendar(@RequestBody TeamCalendarPostDTO teamCalendarPostDTO, @PathVariable("teamId") long id) throws IloException {
        // convert API team to internal representation
        TeamCalendar userInput = DTOMapper.INSTANCE.convertTeamCalendarPostDTOtoEntity(teamCalendarPostDTO);

        // create teamCalendar
        TeamCalendar createdCalendar = teamCalendarService.createTeamCalendar2(id, userInput);

        //Optimizer optimizer = new Optimizer(createdCalendar);
        //optimizer.solve();
        // convert internal representation of teamCalendar back to API
        return DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(createdCalendar);
    }


    @GetMapping("/teams/{teamId}/calendars")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TeamCalendarGetDTO getTeamCalendars(@PathVariable("teamId") long id) {
        TeamCalendar teamCalendar = teamCalendarService.getCalendar(id);
        TeamCalendarGetDTO teamCalendarGetDTOs = DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(teamCalendar);

        return teamCalendarGetDTOs;
    }


    @GetMapping("/teamCalendars")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TeamCalendarGetDTO> getAllCalendars() {
        List<TeamCalendar> calendars = teamCalendarService.getCalendars();
        List<TeamCalendarGetDTO> teamCalendarGetDTOs = new ArrayList<>();

        for (TeamCalendar teamCalendar : calendars) {
            teamCalendarGetDTOs.add(DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(teamCalendar));
        }
        return teamCalendarGetDTOs;
    }

    @DeleteMapping("/teams/{teamId}/calendars")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    void deleteTeamCalendar(@PathVariable("teamId") long id) {
        //TeamCalendar teamCalendar = teamCalendarService.deleteCalendar(id);
        return;
    }


/*

    @GetMapping(value = "/teamCalendar", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String helloWorld() {
        return "The application is running.";
    }
 */
}




