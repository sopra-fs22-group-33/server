package ch.uzh.ifi.hase.soprafs22.controller;


import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void givenGame_whenGetGame_forconcretePlayer__thenReturnJsonArray() throws Exception {
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


    //@Test
    // public void postGame_getGame_thenReturnJsonArray() throws Exception {
         // given
      //   Game game = new Game();
       //  game.setId(1L);

         //given(gameService.startGame(Mockito.any())).willReturn(game);

//         GamePostDTO gamePostDTO = new GamePostDTO();

//         // when
//         MockHttpServletRequestBuilder postRequest =
//                          post("/games")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(asJsonString(gamePostDTO));;

//         // then
//         mockMvc.perform(postRequest).andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.id", is(1)));
//     }

//     private String asJsonString(final Object object) {
//         try {
//             return new ObjectMapper().writeValueAsString(object);
//         } catch (JsonProcessingException e) {
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                     String.format("The request body could not be created.%s", e.toString()));
//         }
//     }

 }