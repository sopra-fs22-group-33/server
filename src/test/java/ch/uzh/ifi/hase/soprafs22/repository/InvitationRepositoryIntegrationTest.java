package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

@DataJpaTest
public class InvitationRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private InvitationRepository invitationRepository;

  @BeforeEach
  public void setup() {
    invitationRepository.deleteAll();
  }

  @Test
  public void findById_success() {
    // create User
    User user = new User();
    user.setEmail("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setPassword("password");

    // create Team
    Team team = new Team();
    team.setName("teamName");

    //create invitation with user and team
    Invitation invitation = new Invitation();
    invitation.setTeam(team);
    invitation.setUser(user);

    entityManager.persist(invitation);

    // when
    List <Invitation> found = invitationRepository.findAll();

    // then
    assertNotNull(found.get(0).getId());
    assertEquals(found.get(0).getId(), invitation.getId());
    assertEquals(team, found.get(0).getTeam());
  }
}
