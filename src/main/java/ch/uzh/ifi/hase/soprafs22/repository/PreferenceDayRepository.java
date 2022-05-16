package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.PreferenceDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("preferenceDayRepository")
public interface PreferenceDayRepository extends JpaRepository<PreferenceDay, Long>{

}
