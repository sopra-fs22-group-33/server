package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userDayRepository")
public interface UserDayRepository extends JpaRepository< Day, Long>{

    //@Query("SELECT d FROM Day d where d.id=:id and d.weekday=:weekday ")
    //Day getDayFromDayKey(Weekday w, Long id);
}
