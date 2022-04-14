package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    public Game getGame() {

        // must return the current game information
        return null;
    }

    public Game startGame() {
        // check if a game for this shift has been started already

        // if it has, return the game information of that game

        // if it hasn't, create a game and return its game information

        // when creating the game set client that sent api post call to active/participting/whatever

        // problem: what to set the other users at (-> if they clicked just a little bit after that?)

        return null;
    }

    public Game updateGame() {

        // if they opted out set status to inactive/optedout/Whatever

        // if they made a move enter it

        // return the current game information (or no response, if we only update through get calls)
        return null;
    }
}
