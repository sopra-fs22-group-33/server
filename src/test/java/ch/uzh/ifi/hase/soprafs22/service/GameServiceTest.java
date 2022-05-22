package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamCalendarRepository;
import ch.uzh.ifi.hase.soprafs22.repository.TeamRepository;
import org.assertj.core.util.diff.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private PlayerRepository playerRepository;
    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private Player testPlayer1;
    private Player testPlayer2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testGame = new Game();
        testGame.setId(1L);
        testPlayer1= new Player();
        testPlayer2 = new Player();
        testPlayer1.setId(2L);
        testPlayer2.setId(3L);
        testPlayer1.setGame(testGame);
        testPlayer2.setGame(testGame);
        Set<Player> players = new HashSet<Player>();
        players.add(testPlayer1);
        players.add(testPlayer2);
        testGame.setPlayers(players);



        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.ofNullable(testGame));
        Mockito.when(gameRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        Mockito.when(playerRepository.findById(2L)).thenReturn(Optional.ofNullable(testPlayer1));
        Mockito.when(playerRepository.findById(3L)).thenReturn(Optional.ofNullable(testPlayer2));
        Mockito.when(playerRepository.findById(4L)).thenReturn(Optional.ofNullable(null));
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);

    }

    @Test
    public void getGameTestSuccess(){
       Game game = gameService.getGame(1L, 2L);
       assertEquals(1L, game.getId());
       assertEquals(2, game.getPlayers().size());
    }


    @Test
    public void getGameTestFail(){
        assertThrows(ResponseStatusException.class, () -> gameService.getGame(2L, 2L));
        assertThrows(ResponseStatusException.class, () -> gameService.getGame(1L, 4L));
    }

    @Test
    public void updatePlayerInGameTest(){
        Location l = new Location();
        l.setX(1);
        l.setY(1);
        List<Location> chunks = new ArrayList<>();
        chunks.add(l);

        Location l2 = new Location();
        l.setX(2);
        l.setY(2);
        List<Location> chunks2 = new ArrayList<>();
        chunks2.add(l2);
        testPlayer1.setChunks(chunks);
        testPlayer2.setChunks(chunks2);

        gameService.updatePlayerInGame(testPlayer1,1L, 3L);
        assertEquals(testPlayer1.getChunks(), testPlayer2.getChunks());
    }


    @Test
    public void makeMove_Apple_eaten_test(){
        Location l = new Location();
        l.setX(1);
        l.setY(1);
        List<Location> chunks = new ArrayList<>();
        chunks.add(l);

        Location l2 = new Location();
        l.setX(2);
        l.setY(2);
        List<Location> chunks2 = new ArrayList<>();
        chunks2.add(l2);
        testPlayer1.setChunks(chunks);
        testPlayer2.setChunks(chunks2);

        gameService.updatePlayerInGame(testPlayer1,1L, 3L);
        assertEquals(testPlayer1.getChunks(), testPlayer2.getChunks());
    }

}
