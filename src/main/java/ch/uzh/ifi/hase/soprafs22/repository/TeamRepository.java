package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("teamRepository")
public interface TeamRepository extends JpaRepository<Team, Long>{
    Team findByName (String name);
    //List<Team> findTeamsByUsersId(long userId);
    Optional<Team> findById (Long id);
}
