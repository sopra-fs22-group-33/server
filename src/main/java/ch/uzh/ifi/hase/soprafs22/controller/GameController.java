package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.EmailService;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private final UserService userService;

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames() {
        List<Game> games = gameService.getGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();

        for (Game game : games) {
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(game));
        }
        return gameGetDTOs;
    }


    @GetMapping("/games/{gameId}/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {
        // get the current game information

        // return the current game information

        Game game = gameService.getGame(gameId, playerId);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    @GetMapping("/users/{userId}/games")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getUserGames(@PathVariable("userId") Long userId) {
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();
        Set<Player> players = userService.getUserById(userId).getPlayers();
        for (Player player:players){
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(player.getGame()));
        }
        return gameGetDTOs;

    }


    /* what is  going to be the mapping?
    because as soon as the game is started /game/{gameId}/{userId} makes sense
    since there is one game and multiple users playing
    but the game is created when the first user clicks "start game" (or sth similar) ***
    and at that point all we have is the user id and information on the shift,
    but no game id, which is only created when the game is created
    *** start game is only displayed when all users affected by this scheduling conflict are online
     */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO startGame(@RequestBody GamePostDTO gamePostDTO) {
        // start a new game, only possible if game associated with that shift hasn't been started by another user
        // check that in the GameService

        // Game Service needs to be given UserID of the client that sent the post call

        // returns the game information
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

        Game createdGame = gameService.startGame(gameInput);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(createdGame);
    }


    @PutMapping("/games/{gameId}/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateGame(@RequestBody PlayerPutDTO playerPutDTO, @PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {
        // updates the game when a user enters information (makes their move or opts out)

        // GameService needs the userId and what their action is

        // either return the current game information or no response because of the frequent GET request

        Player playerInput = DTOMapper.INSTANCE.convertPlayerPutDTOtoEntity(playerPutDTO);

        gameService.updatePlayerInGame(playerInput, gameId, playerId);
    }
}
