package ch.uzh.ifi.hase.soprafs22.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs22.entity.Event;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("eventRepository")
public interface EventRepository extends JpaRepository <Event, Long> {
    Optional<Event> findById(Long id);
}
