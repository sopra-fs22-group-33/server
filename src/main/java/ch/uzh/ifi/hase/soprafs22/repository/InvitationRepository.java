package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("invitationRepository")
public interface InvitationRepository extends JpaRepository<Invitation, Long>{
    Invitation findById (long id);
}
