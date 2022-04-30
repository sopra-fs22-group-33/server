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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class TeamCalendarService {



    private final Logger log = LoggerFactory.getLogger(TeamCalendarService.class);

    private final TeamCalendarRepository teamCalendarRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;


    @Autowired
    public TeamCalendarService(@Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository, @Qualifier("teamRepository") TeamRepository teamRepository,
                               @Qualifier("userRepository") UserRepository userRepository,  @Qualifier("gameRepository") GameRepository gameRepository) {
        this.teamCalendarRepository = teamCalendarRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.gameRepository= gameRepository;
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

            oldCalendar.setBasePlan(newCalendar.getBasePlan());
            oldCalendar.setStartingDate(newCalendar.getStartingDate());

            for (Day day : oldCalendar.getBasePlan()) {
                day.setTeamCalendar(oldCalendar);

                if (day.getSlots() != null) {
                    for (Slot slot : day.getSlots()) {
                        slot.setDay(day);
                        if (slot.getSchedules() != null) {
                            for (Schedule schedule : slot.getSchedules()) {
                                schedule.setSlot(slot);
                                Optional<User> user = userRepository.findById(schedule.getUser().getId());
                                if (user.isPresent()) {
                                    User foundUser = user.get();
                                    foundUser.addSchedule(schedule);
                                    schedule.setUser(foundUser);
                                }
                                else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                            }
                        }
                    }
                }
            }

            TeamCalendar savedCalendar = teamCalendarRepository.save(oldCalendar);
            teamCalendarRepository.flush();
            checkCollisions(savedCalendar);
            return savedCalendar;
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public TeamCalendar createTeamCalendar(long id, TeamCalendar newCalendar) {

        //checkIfTeamHasCalendar(id);
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()){
            Team foundTeam = team.get();
            foundTeam.setTeamCalendar(newCalendar);
            newCalendar.setTeam(foundTeam);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        for (Day day : newCalendar.getBasePlan()) {
            day.setTeamCalendar(newCalendar);

            if (day.getSlots() != null) {
                for (Slot slot : day.getSlots()) {
                    slot.setDay(day);
                    if (slot.getSchedules() != null) {
                        for (Schedule schedule : slot.getSchedules()) {
                            schedule.setSlot(slot);
                            Optional<User> user = userRepository.findById(schedule.getUser().getId());
                            if (user.isPresent()) {
                                User foundUser = user.get();
                                foundUser.addSchedule(schedule);
                                schedule.setUser(foundUser);
                            }
                            else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                        }
                    }
                }
            }
        }

        newCalendar = teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();
        log.debug("Created calendar for Team: {}",id);
        return newCalendar;
    }

    public void checkCollisions(TeamCalendar teamCalendar){
        for (Day day : teamCalendar.getBasePlan()) {
            if (day.getSlots() != null) {
                for (Slot slot : day.getSlots()) {
                    int requirement = slot.getRequirement();
                    int assignment = 0; // make 0 - does not want, 1 - wants, -1 - no  prference
                    int possible = 0;
                    if (slot.getSchedules() != null) {
                        for (Schedule schedule : slot.getSchedules()) {
                           if (schedule.getSpecial()!=-1){
                            assignment  += schedule.getSpecial();} // should be or should not be assigned.
                           else{ possible  += 1;} // dont have special preference - could theoretically be asigned
                        }
                    }
                    if (assignment > requirement || (assignment+possible) < requirement ){
                        initializeGame(slot);

                    }
                }
            }

        }
    }

    public void initializeGame(Slot slot) {
        Game game = new Game();
        List<Player> players = new ArrayList<>();
        // put the required players into the game
        for (Schedule schedule:slot.getSchedules()) {
            if (schedule.getSpecial() != -1) { // user has special preference, he should become player
                Player player = new Player();
                player.setId(schedule.getUser().getId());
                players.add(player);
            }
        }
        game.setPlayers(players);
        Random rand = new Random();
        for (Player player:game.getPlayers()){
            Location chunck = new Location();
            int x = rand.nextInt((10) + 1) + 0;
            int y = rand.nextInt((10) + 1) + 0;
            chunck.setX(x);
            chunck.setY(y);
            List<Location> chunks = new ArrayList<>();
            chunks.add(chunck);
            player.setChunks(chunks);
        }

        List<Location> apples = new ArrayList<>();
        for (int j = 0; j<5; j++){
            Location apple = new Location();
            int x = rand.nextInt((10) + 1) + 0;
            int y = rand.nextInt((10) + 1) + 0;
            apple.setX(x);
            apple.setY(y);
            apples.add(j, apple);
        }
        game.setApples(apples);

        gameRepository.save(game);
        gameRepository.flush();

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
