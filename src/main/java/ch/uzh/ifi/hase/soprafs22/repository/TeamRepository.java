package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Team;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("teamRepository")
public interface TeamRepository extends JpaRepository<Team, Long>{
    Team findByName (String name);
    //List<Team> findTeamsByUsersId(long userId);
    Optional<Team> findById (Long id);
}
