package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
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

    @DeleteMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteGame(@PathVariable("gameId") Long gameId){
        gameService.deleteGame(gameId);
    }


    @PutMapping("/games/{gameId}/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateGame(@RequestBody PlayerPutDTO playerPutDTO, @PathVariable("gameId") Long gameId, @PathVariable("playerId") Long playerId) {


        Player playerInput = DTOMapper.INSTANCE.convertPlayerPutDTOtoEntity(playerPutDTO);

        gameService.updatePlayerInGame(playerInput, gameId, playerId);
    }
}
