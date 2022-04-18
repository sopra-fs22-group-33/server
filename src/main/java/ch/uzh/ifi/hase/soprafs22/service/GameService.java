package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
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

import java.util.List;
import java.util.Optional;

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

    public Game getGame() {

        // must return the current game information
        return null;
    }

    public Game startGame(Game game) {
        // check if a game for this shift has been started already

        // if it has, return the game information of that game

        // if it hasn't, create a game and return its game information

        // when creating the game set client that sent api post call to active/participting/whatever

        // problem: what to set the other users at (-> if they clicked just a little bit after that?)

        game = gameRepository.save(game);
        gameRepository.flush();

        return game;
    }

    public Game updateGame() {

        // if they opted out set status to inactive/optedout/Whatever

        // if they made a move enter it

        // return the current game information (or no response, if we only update through get calls)
        return null;
    }

    public Game updatePlayerInGame(Player playerInput, Long gameId, Long playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player does not exist"));

        // todo: check whether playerId is in game

        player.setChunks(playerInput.getChunks());

        playerRepository.save(player);
        playerRepository.flush();

        game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game used to exist, but does not exist anymore"));

        return game;
    }
}
