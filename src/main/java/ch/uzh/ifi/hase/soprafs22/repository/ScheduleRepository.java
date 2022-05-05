package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.entity.Schedule;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs22.entity.Slot;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("scheduleRepository")
public interface ScheduleRepository extends JpaRepository <Schedule, Long> {
    Schedule findbyUser(User user);
}
