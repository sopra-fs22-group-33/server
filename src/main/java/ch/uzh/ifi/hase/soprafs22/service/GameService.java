package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.Optimizer;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamCalendarRepository;
import lpsolve.LpSolveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to
 * the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final GameRepository gameRepository;

    private final TeamCalendarRepository teamCalendarRepository;
    private final PlayerRepository playerRepository;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("playerRepository") PlayerRepository playerRepository,  @Qualifier("teamCalendarRepository") TeamCalendarRepository teamCalendarRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.teamCalendarRepository = teamCalendarRepository;

    }

    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    public Game getGame(Long gameId, Long playerId) {

        // must return the current game information

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player does not exist"));

        return game;
    }

    @Transactional
    public void deleteGame(Long id){
        gameRepository.deleteById(id);
    }



    @Transactional
    public void updatePlayerInGame(Player playerInput, Long gameId, Long playerId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if (!game.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist");
        }

        Optional<Player> player = playerRepository.findById(playerId);
        if (!player.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player does not exist");
        }
        Player foundPlayer =  player.get();
        Game foundGame = game.get();

        foundPlayer.setStatusOnline(playerInput.getStatusOnline());
        foundPlayer.setChunks(playerInput.getChunks());

        makeMove(foundGame, foundPlayer);

        //playerRepository.save(foundPlayer);
        //playerRepository.flush();
        gameRepository.save(foundGame); // should propagate by cascade to players
        gameRepository.flush();


    }

    public void makeMove(Game game, Player currentPlayer){
        int size = game.getBoardLength();

// if the current player is not yet dead
        if (!Objects.equals(currentPlayer.getStatus(), "dead")){

            // if the player just ate, change his status to null so thathe stops eating...
            if (Objects.equals(currentPlayer.getStatus(), "ate")){
                currentPlayer.setStatus(null);
            }
            List<Location> chunks = currentPlayer.getChunks();
            Location head = chunks.get(0);

            // check if he eats again
            if (game.getApples()!=null){
            for (int i = 0; i< game.getApples().size(); i++){
                 Location appleLocation = game.getApples().get(i);
                    if ((head.getX() == appleLocation.getX()) && ((head.getY() == appleLocation.getY()))){
                        currentPlayer.setStatus("ate");

                        // change location  of apple to random
                        Random rand = new Random();
                        int x = rand.nextInt(size);
                        int y = rand.nextInt(size);
                        appleLocation.setX(x);
                        appleLocation.setY(y);

                    }
                }
            }
            // compute the current max rank
            int rank = 0;
            Location playerHead;
            for (Player player:game.getPlayers()) {
                if (player.getRank()> rank ){rank = player.getRank();}
            }// update the current max rank

            // check wall collision
            if ((head.getX() < 0 || head.getY() < 0 || head.getX() >= size || head.getY() >= size)) {
                    currentPlayer.setStatus("dead");
                    currentPlayer.setChunks(null);
                    currentPlayer.setRank(rank+1);
                }
            // if the player did not die because of wall maybe he crashed into others

            else  for (Player player:game.getPlayers()) {
                // if that player is not dead and it is not us
            if (!Objects.equals(player.getStatus(), "dead") && player.getId() != currentPlayer.getId()) {
                    List<Location> playerChunks = player.getChunks();
                    for (Location chunkLocation : playerChunks) {
                        // handle the case when two heads meet
                        playerHead = player.getChunks().get(0);
                        if ((head.getX() == playerHead.getX()) && (head.getY() == playerHead.getY())) {
                            currentPlayer.setStatus("dead");
                            currentPlayer.setChunks(null);
                            currentPlayer.setRank(rank+1);

                            player.setStatus("dead");
                            player.setChunks(null);
                            player.setRank(rank+1);
                        }
                        else if ((head.getX() == chunkLocation.getX()) && ((head.getY() == chunkLocation.getY()))) {
                            currentPlayer.setStatus("dead");
                            currentPlayer.setChunks(null);
                            currentPlayer.setRank(rank+1);  // 1 - looser ... n - winner

                        }
                    }
                }
            }
        }
        // check if all the players are dead  // TODO: maybe change to only one player left - then stop game
        Boolean stop = true;
        for (Player player:game.getPlayers()) {
            if (!Objects.equals(player.getStatus(), "dead")) {
                stop = false;
            }
        }
        if (stop && Objects.equals(game.getStatus(), "on")){
            finishGame(game); //  if all the players are dead, finish it
        }
    }

    private void finishGame(Game game){
        // TODO: Maybe need to delete the game from the database - > discuss
        game.setStatus("off");
        game.getSlot().getDay().getTeamCalendar().setCollisions( game.getSlot().getDay().getTeamCalendar().getCollisions()-1);
        int requirement = game.getSlot().getRequirement();
        int assignment = 0; // make 0 - does not want, 1 - wants, -1 - no  preference
        int possible = 0;


        if (game.getSlot().getSchedules() != null) {
            for (Schedule schedule :  game.getSlot().getSchedules()) {
                if (schedule.getSpecial()!=-1){
                    assignment  += schedule.getSpecial();} // should be or should not be assigned.
                else{ possible  += 1;} // dont have special preference - could theoretically be asigned
            }
        }
        int mismatch = 0;
        if (assignment > requirement ){
            ArrayList<Player> sortedPlayers = sortPlayersByRank(game.getPlayers(), 1); // if too many get assigned, block special of 1
            mismatch= assignment - requirement;
            int i = 0;
            while(mismatch >0 && i< sortedPlayers.size()){
                removeSpecialPreference(sortedPlayers.get(i).getUser(), game.getSlot());
                i+=1;
                mismatch -=1;
            }
        }
        else {
            ArrayList<Player> sortedPlayers = sortPlayersByRank(game.getPlayers(), 0); // if too little of players get assigned, block special of 0
            mismatch = requirement - (assignment+possible);
            int i = 0;
            while(mismatch >0 && i< sortedPlayers.size()){
                removeSpecialPreference(sortedPlayers.get(i).getUser(), game.getSlot());
                i+=1;
                mismatch -=1;
            }
        }

        if ( game.getSlot().getDay().getTeamCalendar().getCollisions() == 0){
            try {
                new Optimizer(game.getSlot().getDay().getTeamCalendar());
                updateOptimizedTeamCalendar(game.getSlot().getDay().getTeamCalendar());

            }
            catch (Exception e) {
                ;
            }
        }
    }

    private ArrayList<Player>  sortPlayersByRank(Set<Player> players, int i){

        ArrayList<Player> selectedPlayers = new ArrayList<>();
        for (Player player: players){
            if (player.getSpecial() == i){
                selectedPlayers.add(player);
            }
        }

        Collections.sort(selectedPlayers, (o1, o2) -> Integer.valueOf(o1.getRank()).compareTo(Integer.valueOf(o2.getRank()))); // this sorts in increasing order by rank
        return selectedPlayers;
    }

    private void removeSpecialPreference(User user, Slot slot){
        for (Schedule schedule: slot.getSchedules()){
            if (schedule.getUser().getId().equals(user.getId())){
                schedule.setSpecial(-1);
            }
        }
    }



    public void updateOptimizedTeamCalendar( TeamCalendar newCalendar){
        // clean existing data from the fixed calendar
        Long id = newCalendar.getId();

        newCalendar.setStatus("free");
        teamCalendarRepository.save(newCalendar);
        teamCalendarRepository.flush();

        // create copy of the base plan
        List<Day> basePlan = new ArrayList<>();
        int latestDay = 0;
        for (Day dayFixed: newCalendar.getBasePlan()){
            if (dayFixed.getWeekday()>latestDay){
                latestDay = dayFixed.getWeekday();
            }

            Day day = new Day();
            day.setWeekday(dayFixed.getWeekday());
            List<Slot> slots = new ArrayList<>();
            for (Slot fixedSlot: dayFixed.getSlots()){
                Slot slot = new Slot();
                slot.setDay(day);
                slot.setTimeTo(fixedSlot.getTimeTo());
                slot.setTimeFrom(fixedSlot.getTimeFrom());
                slot.setRequirement(fixedSlot.getRequirement());
                slots.add(slot);
                List<Schedule> schedules = new ArrayList<>();
                for (Schedule fixedSchedule: fixedSlot.getSchedules()){
                    Schedule schedule = new Schedule();
                    schedule.setSlot(slot);
                    schedule.setBase(fixedSchedule.getBase());
                    schedule.setSpecial(fixedSchedule.getSpecial());
                    schedule.setUser(fixedSchedule.getUser());
                    schedule.setAssigned(fixedSchedule.getAssigned());
                    schedule.setFinal(true);

                    // this is not required anymore
                    fixedSchedule.setAssigned(0);
                    fixedSchedule.setSpecial(-1);
                    schedules.add(schedule);
                }
                slot.setSchedules(schedules);
            }
            day.setSlots(slots);
            basePlan.add(day);
        }


        Optional<TeamCalendar> teamCalendar = teamCalendarRepository.findById(id);

        if (teamCalendar.isPresent()){

            TeamCalendar foundCalendar =teamCalendar.get();
            int diff = (int) DAYS.between( foundCalendar.getStartingDateFixed(), foundCalendar.getStartingDate());


            for (Day day: basePlan){
                day.setTeamCalendar(foundCalendar);
                day.setWeekday(day.getWeekday()+diff);
                foundCalendar.getBasePlanFixed().add(day);

            }

            // update the dates
            //foundCalendar.setStartingDateFixed(foundCalendar.getStartingDate()); - dont update starting date fixed
            foundCalendar.setStartingDate(foundCalendar.getStartingDate().plusDays(latestDay+ 1));


            teamCalendarRepository.save(foundCalendar);
            teamCalendarRepository.flush();
        }
    }

}
