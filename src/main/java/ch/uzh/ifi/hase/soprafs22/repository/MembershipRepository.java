package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("membershipRepository")
public interface MembershipRepository extends JpaRepository<Membership, Long>{
    Membership findById (long id);
    //List<Team> findTeamsByUsersId(long userId);  
}
