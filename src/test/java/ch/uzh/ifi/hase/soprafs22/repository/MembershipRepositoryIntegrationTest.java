package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

@DataJpaTest
public class MembershipRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private MembershipRepository membershipRepository;

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
    team.setName("teamName");
    Membership membership = new Membership();
    // membership.setId(200L);
    membership.setTeam(team);
    membership.setUser(user);

    // MembershipRepository.save(Membership);
    entityManager.persist(user);
    entityManager.persist(team);
    membershipRepository.save(membership);
    membershipRepository.flush();

    // when
    List <Membership> found = membershipRepository.findAll();

    // then
    assertNotNull(found.get(0).getId());
    assertEquals(found.get(0).getId(), membership.getId());
  }
}
