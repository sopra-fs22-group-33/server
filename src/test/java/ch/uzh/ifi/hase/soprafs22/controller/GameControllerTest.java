package ch.uzh.ifi.hase.soprafs22.controller;


import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Player;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
 public class GameControllerTest {

     @Autowired
     private MockMvc mockMvc;
     @MockBean
     private GameService gameService;

     @MockBean
     private UserService userService;



     @Test
    public void givenGame_whenGetGame_thenReturnJsonArray() throws Exception {
       // given
         Game game = new Game();
        game.setId(1L);
        List<Game> allGames= Collections.singletonList(game);

        given(gameService.getGames()).willReturn( allGames);

     // when
         MockHttpServletRequestBuilder getRequest = get("/games").contentType(MediaType.APPLICATION_JSON);

         // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void givenGame_whenGetGame_forconcretePlayer__thenReturnthatGame() throws Exception {
        // given
        Game game = new Game();
        game.setId(1L);

        given(gameService.getGame(Mockito.anyLong(), Mockito.anyLong())).willReturn( game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/games/1/1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void givenGame_whenGetGame_forconcretePlayer__thenReturnJsonArray() throws Exception {
        // given
        Game game = new Game();
        game.setId(1L);
        Player player1 = new Player();
        player1.setId(1L);
        player1.setGame(game);
        Player player2 = new Player();
        player2.setId(2L);
        player2.setGame(game);
        User testUser = new User();
        Set<Player> players = new HashSet<>();
        players.add(player1);
        players.add(player2);
        testUser.setPlayers(players);

        given(userService.getUserById(Mockito.anyLong())).willReturn(testUser);
        given(gameService.getGame(Mockito.anyLong(), Mockito.anyLong())).willReturn( game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/games").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

 }