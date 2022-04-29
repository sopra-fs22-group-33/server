package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class MembershipServiceTest {

  @Mock
  private MembershipRepository membershipRepository;

  @InjectMocks
  private MembershipService membershipService;

  private Team testTeam;
  private User testUser;
  private Membership testMembership;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testTeam = new Team();
    testTeam.setId(3L);
    testTeam.setName("testTeamname");
    testUser = new User();
    testUser.setId(2L);
    testUser.setEmail("firstname@lastname");
    testUser.setPassword("password");
    testMembership = new Membership();
    testMembership.setId(1L);
    testMembership.setTeam(testTeam);
    testMembership.setUser(testUser);

    // when -> any object is being save in the TeamRepository -> return the dummy
    // testTeam
    Mockito.when(membershipRepository.save(Mockito.any())).thenReturn(testMembership);
  }

  @Test
  public void createMembership_validInputs_success() {

    Membership createdMembership = membershipService.createMembership(testTeam, testUser, false);

    // then
    Mockito.verify(membershipRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testMembership.getId(), createdMembership.getId());
    assertEquals(testMembership.getTeam(), createdMembership.getTeam());
  }
}
