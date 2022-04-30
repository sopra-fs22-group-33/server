package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Location;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
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

        // todo: check whether playerId is in game

        return game;
    }

    public Game startGame(Game game) {
        // check if a game for this shift has been started already

        // if it has, return the game information of that game

        // if it hasn't, create a game and return its game information

        // when creating the game set client that sent api post call to active/participting/whatever

        // problem: what to set the other users at (-> if they clicked just a little bit after that?)

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

    public Game updateGame(Game game, Player player) {


        // if they opted out set status to inactive/optedout/Whatever

        // if they made a move enter it

        // return the current game information (or no response, if we only update through get calls)
        return null;
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

        //playerRepository.save(foundPlayer);
        //playerRepository.flush();
        gameRepository.save(foundGame); // should propagate by cascade to players
        playerRepository.flush();


    }

    public void makeMove(Game game, Player currentPlayer){

        currentPlayer.setStatus(null);
        List<Location> chunks = currentPlayer.getChunks();
        Location head = chunks.get(0);

        for (int i = 0; i< game.getApples().size(); i++){
             Location appleLocation = game.getApples().get(i);
                if ((head.getX() == appleLocation.getX()) && ((head.getY() == appleLocation.getY()))){
                    currentPlayer.setStatus("ate");

                    // change location  of apple to random
                    Random rand = new Random();
                    int x = rand.nextInt((10) + 1) + 0;
                    int y = rand.nextInt((10) + 1) + 0;
                    appleLocation.setX(x);
                    appleLocation.setY(y);

                }
            }

        for (Player player:game.getPlayers()) {
            if (player.getId() != currentPlayer.getId()) {
                List<Location> playerChunks = player.getChunks();
                for (Location chunkLocation : playerChunks) {
                    if ((head.getX() == chunkLocation.getX()) && ((head.getY() == chunkLocation.getY()))) {
                        currentPlayer.setStatus("dead");
                    }
                    // handle the case when two heads meet

                }
            }
        }
    }

}
