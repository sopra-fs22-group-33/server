package ch.uzh.ifi.hase.soprafs22.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs22.entity.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository <Event, Long> {
    Optional<Event> findById(Long id);
}
