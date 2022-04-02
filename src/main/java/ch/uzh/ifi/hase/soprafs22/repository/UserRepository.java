package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByName(String name);
  User findByEmail(String email);
  User findByUsername(String username);
  List<User> findUsersByTeamsId(long teamId);  
}
