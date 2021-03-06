package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("preferenceSlotRepository")
public interface PreferenceSlotRepository extends JpaRepository <PreferenceSlot, Long> {
    Optional<PreferenceSlot> findById(Long id);
}
