package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    private final PlayerRepository playerRepository;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;

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



    public Game startGame(Game game) { // this one is for API to call from front end -only used for DEMO

        // random chunks generation
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

        game = gameRepository.save(game);
        gameRepository.flush();

        return game;
    }


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

        foundPlayer.setChunks(playerInput.getChunks());

        makeMove(foundGame, foundPlayer);

        // playerRepository.save(foundPlayer);
        //playerRepository.flush();
        gameRepository.save(foundGame); // should propagate by cascade to players
        playerRepository.flush(); // TODO: prob shouod be gamerepo


    }

    public void makeMove(Game game, Player currentPlayer){
        int size = game.getBoardLength();

        Boolean stop = true;
        for (Player player:game.getPlayers()) {
            if (player.getStatus()!= "dead") {
                stop = false;
            }
        }
        if (stop){
            finishGame(game);
        }

        currentPlayer.setStatus(null);
        List<Location> chunks = currentPlayer.getChunks();
        Location head = chunks.get(0);

        for (int i = 0; i< game.getApples().size(); i++){
             Location appleLocation = game.getApples().get(i);
                if ((head.getX() == appleLocation.getX()) && ((head.getY() == appleLocation.getY()))){
                    currentPlayer.setStatus("ate");

                    // change location  of apple to random
                    Random rand = new Random();
                    int x = rand.nextInt((size) + 1) + 0;
                    int y = rand.nextInt((size) + 1) + 0;
                    appleLocation.setX(x);
                    appleLocation.setY(y);

                }
            }
        int rank = 0;
        for (Player player:game.getPlayers()) {
            if (player.getRank()> rank ){rank = player.getRank();} // upsate the current max rank
                    // if that player is not dead and it is not us
            if (player.getStatus()!="dead" && player.getId() != currentPlayer.getId()) {
                List<Location> playerChunks = player.getChunks();
                for (Location chunkLocation : playerChunks) {
                    if ((head.getX() == chunkLocation.getX()) && ((head.getY() == chunkLocation.getY()))) {
                        currentPlayer.setStatus("dead");
                        currentPlayer.setRank(rank+1);  // 1 - looser ... n - winner

                    }
                    // handle the case when two heads meet

                }
            }
        }
    }

    public void finishGame(Game game){
        game.setStatus("off");
        int requirement = game.getSlot().getRequirement();
        int assignment = 0; // make 0 - does not want, 1 - wants, -1 - no  prference
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
            mismatch= assignment - requirement;
        }
        else {mismatch = requirement - (assignment+possible);}

        for (Player player:game.getPlayers()) {
            if (player.getRank()<= mismatch){ // TODO: check that it is not strict inequality
                removeSpecialPreference(player.getUser(), game.getSlot());
            }
        }
    }

    public void removeSpecialPreference(User user, Slot slot){
        for (Schedule schedule: slot.getSchedules()){
            if (schedule.getUser().getId() == user.getId()){
                schedule.setSpecial(-1);
            }
        }

    }

}
