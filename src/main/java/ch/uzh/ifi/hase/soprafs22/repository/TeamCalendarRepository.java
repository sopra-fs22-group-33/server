package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("teamCalendarRepository")
public interface TeamCalendarRepository extends JpaRepository<TeamCalendar, Long>{
    Optional<TeamCalendar> findById (Long id);
}
