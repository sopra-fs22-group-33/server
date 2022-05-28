package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
        testGame.setBoardLength(10);
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
        l.setY(2);
        List<Location> chunks = new ArrayList<>();
        chunks.add(l);

        Location l2 = new Location();
        l2.setX(2);
        l2.setY(2);


        List<Location> chunks2 = new ArrayList<>();
        chunks2.add(l2);
        testPlayer1.setChunks(chunks);
        testPlayer2.setChunks(chunks2);


        Location l3 = new Location();
        l3.setX(3);
        l3.setY(3);
        List<Location> chunks3 = new ArrayList<>();
        chunks3.add(l3);

        Player playerInput = new Player();
        playerInput.setChunks(chunks3);

        gameService.updatePlayerInGame(playerInput,1L, 3L);
        assertEquals(playerInput.getChunks(), testPlayer2.getChunks());
    }

    @Test
    public void updatePlayerInGameTest_apple(){
        Location apple = new Location();
        apple.setX(3);
        apple.setY(3);
        List<Location> apples = new ArrayList<>();
        apples.add(apple);
        testGame.setApples(apples);
        Location l = new Location();
        l.setX(1);
        l.setY(2);
        List<Location> chunks = new ArrayList<>();
        chunks.add(l);

        Location l2 = new Location();
        l2.setX(2);
        l2.setY(2);


        List<Location> chunks2 = new ArrayList<>();
        chunks2.add(l2);
        testPlayer1.setChunks(chunks);
        testPlayer2.setChunks(chunks2);


        Location l3 = new Location();
        l3.setX(3);
        l3.setY(3);
        List<Location> chunks3 = new ArrayList<>();
        chunks3.add(l3);

        Player playerInput = new Player();
        playerInput.setChunks(chunks3);

        gameService.updatePlayerInGame(playerInput,1L, 3L);
        assertEquals(playerInput.getChunks(), testPlayer2.getChunks());
        assertEquals("ate", testPlayer2.getStatus());
    }

    @Test
    public void updatePlayerInGameTest_collision(){
        Location apple = new Location();
        apple.setX(3);
        apple.setY(3);
        List<Location> apples = new ArrayList<>();
        apples.add(apple);
        testGame.setApples(apples);
        Location l = new Location();
        l.setX(3);
        l.setY(3);
        List<Location> chunks = new ArrayList<>();
        chunks.add(l);

        Location l2 = new Location();
        l2.setX(3);
        l2.setY(3);


        List<Location> chunks2 = new ArrayList<>();
        chunks2.add(l2);
        testPlayer1.setChunks(chunks);
        testPlayer2.setChunks(chunks2);


        Location l3 = new Location();
        l3.setX(3);
        l3.setY(3);
        List<Location> chunks3 = new ArrayList<>();
        chunks3.add(l3);

        Player playerInput = new Player();
        playerInput.setChunks(chunks3);

        gameService.updatePlayerInGame(playerInput,1L, 3L);
        assertEquals("dead", testPlayer2.getStatus());
    }

    @Test
    public void finishGame_test(){

        Team testTeam = new Team();
        testTeam.setId(1L);
        User testUser1 = new User();
        testUser1.setId(1L);
        User testUser2 = new User();
        testUser2.setId(2L);
        User testUser3 = new User();
        testUser3.setId(3L);


        TeamCalendar testTeamCalendar = new TeamCalendar();
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setCollisions(2);
        testTeam.setTeamCalendar(testTeamCalendar);


        Day day = new Day ();
        Slot slot = new Slot();
        slot.setRequirement(2);

        Schedule schedule1 = new Schedule();
        schedule1.setId(1L);
        schedule1.setSpecial(0);
        schedule1.setBase(1);
        schedule1.setUser(testUser1);

        Schedule schedule2 = new Schedule();
        schedule2.setId(2L);
        schedule2.setSpecial(0);
        schedule2.setBase(1);
        schedule2.setUser(testUser2);

        Schedule schedule3 = new Schedule();
        schedule3.setId(2L);
        schedule3.setSpecial(0);
        schedule3.setBase(1);
        schedule3.setUser(testUser3);


        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);

        slot.setSchedules(schedules);
        slot.setDay(day);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setBasePlan(days);
        day.setTeamCalendar(testTeamCalendar);



        testPlayer1.setRank(3);
        testPlayer2.setRank(2);
        Player testPlayer3 = new Player();
        testPlayer3.setRank(1);
        testPlayer1.setSpecial(0);
        testPlayer2.setSpecial(0);
        testPlayer3.setSpecial(0);
        Set <Player> players = new HashSet<>();
        players.add(testPlayer1);
        players.add(testPlayer2);
        players.add(testPlayer3);
        testGame.setPlayers(players);

        testPlayer1.setUser(testUser1);
        testPlayer2.setUser(testUser2);
        testPlayer3.setUser(testUser3);


        testPlayer1.setStatus("dead");
        testPlayer2.setStatus("dead");
        testPlayer3.setStatus("dead");

        Location l1 = new Location();
        l1.setX(3);
        l1.setY(3);
        List<Location> chunks1 = new ArrayList<>();
        chunks1.add(l1);
        testPlayer1.setChunks(chunks1);
        testPlayer2.setChunks(chunks1);
        testPlayer3.setChunks(chunks1);



        testGame.setStatus("on");
        testGame.setSlot(slot);
        gameService.updatePlayerInGame(testPlayer1, 1L, 3L);
        assertEquals("dead", testPlayer1.getStatus());
        assertEquals("dead", testPlayer2.getStatus());
        assertEquals("dead", testPlayer3.getStatus());
        assertEquals("off", testGame.getStatus());
        assertEquals(0, schedule1.getSpecial());
        assertEquals(-1, schedule2.getSpecial());
        assertEquals(-1, schedule3.getSpecial());




    }


}
