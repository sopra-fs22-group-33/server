package ch.uzh.ifi.hase.soprafs22.controller;


import ch.uzh.ifi.hase.soprafs22.entity.PreferenceCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PreferenceCalendarGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PreferenceCalendarPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.PreferenceCalendarService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PreferenceCalendarController {
    private final PreferenceCalendarService preferenceCalendarService;
    private final UserService userService;

    PreferenceCalendarController(PreferenceCalendarService preferenceCalendarService, UserService userService) {
        this.preferenceCalendarService = preferenceCalendarService;
        this.userService = userService;
    }


    @PostMapping("/users/{userId}/preferences")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PreferenceCalendarGetDTO createPreferenceCalendar(@RequestBody PreferenceCalendarPostDTO preferenceCalendarPostDTO, @PathVariable("userId") long userId, @RequestHeader("token") String token) {
        if (userService.authorizeUser(userId, token)) {
            // convert API team to internal representation
            PreferenceCalendar userInput = DTOMapper.INSTANCE.convertPreferenceCalendarPostDTOtoEntity(preferenceCalendarPostDTO);
            User user = userService.findUserById(userId);
            // create preferenceCalendar
            PreferenceCalendar createdCalendar = preferenceCalendarService.createPreferenceCalendar(user.getId(), userInput);

            return DTOMapper.INSTANCE.convertEntityToPreferenceCalendarGetDTO(createdCalendar);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized");
    }

    @GetMapping("/users/{userId}/preferences")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PreferenceCalendarGetDTO getPreferenceCalendar(@PathVariable("userId") long userId){
        User user = userService.findUserById(userId);
        PreferenceCalendar preferenceCalendar = preferenceCalendarService.getPreferenceCalendar(user);

        return DTOMapper.INSTANCE.convertEntityToPreferenceCalendarGetDTO(preferenceCalendar);
    }

    @PutMapping("/users/{userId}/preferences")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PreferenceCalendarGetDTO updatePreferenceCalendar(@PathVariable("userId") long userId, @RequestBody PreferenceCalendarPostDTO preferenceCalendarPostDTO, @RequestHeader("token") String token) {
        if (userService.authorizeUser(userId, token)) {
            User user = userService.findUserById(userId);
            PreferenceCalendar updatedCalendar = DTOMapper.INSTANCE.convertPreferenceCalendarPostDTOtoEntity(preferenceCalendarPostDTO);
            PreferenceCalendar preferenceCalendar = preferenceCalendarService.updatePreferenceCalendar(user, updatedCalendar);

            return DTOMapper.INSTANCE.convertEntityToPreferenceCalendarGetDTO(preferenceCalendar);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized");
    }
}




