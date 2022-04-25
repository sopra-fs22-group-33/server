package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayKeyRepository extends JpaRepository <Event, Long> {
}
