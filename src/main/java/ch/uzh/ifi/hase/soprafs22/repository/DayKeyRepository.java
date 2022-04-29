package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayKeyRepository extends JpaRepository <Slot, Long> {
}
