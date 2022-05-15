package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("preferenceCalendarRepository")
public interface PreferenceCalendarRepository extends JpaRepository<PreferenceCalendar, Long> {
}
