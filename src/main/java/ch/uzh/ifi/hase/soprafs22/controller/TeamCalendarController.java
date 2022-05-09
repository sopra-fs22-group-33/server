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
import java.util.ArrayList;
import java.util.List;

@RestController
public class TeamCalendarController {
    private final TeamCalendarService teamCalendarService;
    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

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
        /*try {
            Optimizer optimizer = new Optimizer(createdCalendar);
            TeamCalendar modifiedCalendar = teamCalendarService.createTeamCalendar(id, createdCalendar);
            return DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(createdCalendar);
        }
        // TODO: catch exception that cplex lib is not found

        catch (NullPointerException ex){
            log.debug("Something was null");
            return DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(createdCalendar);
        }

         */
        return DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(createdCalendar);
    }

    @PutMapping("/teams/{teamId}/calendars")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateTeamCalendar(@RequestBody TeamCalendarPostDTO teamCalendarPostDTO, @PathVariable("teamId") long id) {
        // convert API team to internal representation
        TeamCalendar userInput = DTOMapper.INSTANCE.convertTeamCalendarPostDTOtoEntity(teamCalendarPostDTO);

        TeamCalendar createdCalendar = teamCalendarService.updateTeamCalendar(id, userInput);



        try {
            new Optimizer(createdCalendar);
            TeamCalendar modifiedCalendar = teamCalendarService.updateTeamCalendar(id, createdCalendar);
            // TODO: implement check that there are np collisions
        }

        catch (LpSolveException ex){
            log.debug("Something did not work with optimizer");
        }
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

}




