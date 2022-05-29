package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.UserSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("userSlotRepository")
public interface UserSlotRepository extends JpaRepository <UserSlot, Long> {
    Optional<UserSlot> findById(Long id);
}
