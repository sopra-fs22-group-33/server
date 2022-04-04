package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.TeamCalendarService;
import ch.uzh.ifi.hase.soprafs22.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
public class TeamCalendarController {
    private final TeamCalendarService teamCalendarService;

    TeamCalendarController(TeamCalendarService teamCalendarService) {
        this.teamCalendarService = teamCalendarService;
    }

    @PostMapping("/calendars/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TeamCalendarGetDTO createTeamCalendar(@RequestBody TeamCalendarPostDTO teamCalendarPostDTO, @PathVariable("teamId") long id) {
        // convert API team to internal representation
        TeamCalendar userInput = DTOMapper.INSTANCE.convertTeamCalendarPostDTOtoEntity(teamCalendarPostDTO);

        // create team
        TeamCalendar createdCalendar = teamCalendarService.createTeamCalendar(id, userInput);

        // convert internal representation of team back to API
        return DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(createdCalendar);
    }
}
