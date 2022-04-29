package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class InvitationRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private InvitationRepository invitationRepository;

  @Test
  public void findById_success() {
    // given
    User user = new User();
    // user.setId(2L);
    user.setEmail("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setPassword("password");

    Team team = new Team();
    team.setName("teamname");
    Invitation invitation = new Invitation();
    // invitation.setId(1L);
    invitation.setTeam(team);
    invitation.setUser(user);

    // invitationRepository.save(invitation);
    entityManager.persist(user);
    entityManager.persist(team);
    entityManager.persist(invitation);
    invitationRepository.flush();

    // when
    Invitation found = invitationRepository.findById(3L);

    // then
    assertNotNull(found.getId());
    assertEquals(found.getId(), invitation.getId());
  }
}
