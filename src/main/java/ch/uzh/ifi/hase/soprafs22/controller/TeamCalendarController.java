package ch.uzh.ifi.hase.soprafs22.controller;


import ch.uzh.ifi.hase.soprafs22.Optimizer;
import lpsolve.LpSolveException;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamCalendarPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.TeamCalendarService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TeamCalendarController {
    private final TeamCalendarService teamCalendarService;
    private final Logger log = LoggerFactory.getLogger(TeamCalendarController.class);
    TeamCalendarController(TeamCalendarService teamCalendarService) {
        this.teamCalendarService = teamCalendarService;
    }


    @PostMapping("/teams/{teamId}/calendars")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TeamCalendarGetDTO createTeamCalendar(@RequestBody TeamCalendarPostDTO teamCalendarPostDTO, @PathVariable("teamId") long id) {
        // convert API team to internal representation
        TeamCalendar userInput = DTOMapper.INSTANCE.convertTeamCalendarPostDTOtoEntity(teamCalendarPostDTO);

        // create teamCalendar
        TeamCalendar createdCalendar = teamCalendarService.createTeamCalendar(id, userInput);

        return DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(createdCalendar);
    }

    @PutMapping("/teams/{teamId}/calendars")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateTeamCalendar(@RequestBody TeamCalendarPostDTO teamCalendarPostDTO, @PathVariable("teamId") long id) {
        // convert API team to internal representation
        TeamCalendar userInput = DTOMapper.INSTANCE.convertTeamCalendarPostDTOtoEntity(teamCalendarPostDTO);

        TeamCalendar createdCalendar = teamCalendarService.updateTeamCalendar(id, userInput);
    }

    @GetMapping("/teams/{teamId}/calendars/optimize")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TeamCalendarGetDTO getOptimizedTeamCalendars(@PathVariable("teamId") long id) {
        TeamCalendar teamCalendar = teamCalendarService.getCalendar(id);

        // if there are no collisions
        if (!teamCalendarService.checkCollisionsWithoutGameStart(teamCalendar)) {
            try {
                new Optimizer(teamCalendar);
                 teamCalendarService.updateOptimizedTeamCalendar(id, teamCalendar);
                 TeamCalendarGetDTO teamCalendarGetDTO = DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(teamCalendar);
                 return teamCalendarGetDTO;
            }

            catch (LpSolveException ex) {
                log.debug("Something did not work with optimizer (I dont know what)" + ex);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something did not work with optimizer, it though an error" );
            }

            catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "lp_solve is not supported (probably)");
            }
        }

        log.debug("There are still collisions, you need to play games" );
        throw new ResponseStatusException(HttpStatus.TOO_EARLY);

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

}




