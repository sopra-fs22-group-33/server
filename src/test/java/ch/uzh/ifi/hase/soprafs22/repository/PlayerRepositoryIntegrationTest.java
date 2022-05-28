package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;


    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void findById_success() {
        // create User
        Player player = new Player();
        Game game = new Game();
        User user =new User();
        user.setEmail("qq");
        user.setPassword("qq");
        user.setToken("qq");
        user.setStatus(UserStatus.ONLINE);
        player.setUser(user);
        player.setGame(game);



        entityManager.persist(player);
        entityManager.flush();

        // when
        List<Player> found = playerRepository.findAll();

        // then
        assertNotNull(found.get(0).getId());
        assertEquals(found.get(0).getId(), player.getId());

    }


}