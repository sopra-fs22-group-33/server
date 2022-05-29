package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("slotRepository")
public interface SlotRepository extends JpaRepository <Slot, Long> {
    Optional<Slot> findById(Long id);
}
