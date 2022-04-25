package ch.uzh.ifi.hase.soprafs22.repository;
import ch.uzh.ifi.hase.soprafs22.constant.Weekday;
import ch.uzh.ifi.hase.soprafs22.entity.Day;

import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository("dayRepository")
public interface DayRepository extends JpaRepository< Day, Long>{

    //@Query("SELECT d FROM Day d where d.id=:id and d.weekday=:weekday ")
    //Day getDayFromDayKey(Weekday w, Long id);
}
