package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the game.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result to all clients active on that game instance.
 */
@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }


    @GetMapping("/game/{gameId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO getGame() {
        // get the current game information

        // return the current game information
        return null;
    }


    /* what is  going to be the mapping?
    because as soon as the game is started /game/{gameId}/{userId} makes sense
    since there is one game and multiple users playing
    but the game is created when the first user clicks "start game" (or sth similar) ***
    and at that point all we have is the user id and information on the shift,
    but no game id, which is only created when the game is created
    *** start game is only displayed when all users affected by this scheduling conflict are online
     */
    @PostMapping("/game")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GamePostDTO startGame() {
        // start a new game, only possible if game associated with that shift hasn't been started by another user
        // check that in the GameService

        // Game Service needs to be given UserID of the client that sent the post call

        // returns the game information
        return null;
    }


    @PutMapping("/game/{gameId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePostDTO updateGame() {
        // updates the game when a user enters information (makes their move or opts out)

        // GameService needs the userId and what their action is

        // either return the current game information or no response because of the frequent GET request
        return null;
    }


}
