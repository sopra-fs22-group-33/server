package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class TeamRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TeamRepository teamRepository;

  @Test
  public void findByName_success() {
    // given
    Team team = new Team();
    team.setName("teamName");

    entityManager.persist(team);
    entityManager.flush();

    // when
    Team found = teamRepository.findByName(team.getName());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getName(), team.getName());
  }
}
