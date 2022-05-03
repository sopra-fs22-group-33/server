package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Membership;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.TeamPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setEmail("firstname@lastname");
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getEmail(), user.getEmail());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setEmail("firstname@lastname");
    user.setUsername("username");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setPassword("password");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getEmail(), userGetDTO.getEmail());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  @Test
  public void testCreateTeam_fromTeamPostDTO_toTeam_success() {
    // create TeamPostDTO
    TeamPostDTO teamPostDTO = new TeamPostDTO();
    teamPostDTO.setName("team1");
    
    // MAP -> Create user
    Team team = DTOMapper.INSTANCE.convertTeamPostDTOtoEntity(teamPostDTO);

    // check content
    assertEquals(teamPostDTO.getName(), team.getName());
  }

  @Test
  public void testGetTeam_fromTeam_toTeamGetDTO_success() {
    // create Team
    Team team = new Team();
    team.setName("team1");
    
    // MAP -> Create TeamGetDTO
    TeamGetDTO teamGetDTO = DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team);

    // check content
    assertEquals(team.getId(), teamGetDTO.getId());
    assertEquals(team.getName(), teamGetDTO.getName());
  }

  //TODO use for report
  @Test
  public void testGetTeam_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setEmail("firstname@lastname");
    user.setUsername("username");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");
    user.setPassword("password");
    
    //create Team
    Team team = new Team();
    team.setName("team1");

    //create Membership
    Membership membership = new Membership();
    membership.setUser(user);
    membership.setTeam(team);

    Set<Membership> memberships = new HashSet<>();
    memberships.add(membership);

    user.setMemberships(memberships);
    team.setMemberships(memberships);

    // MAP -> Create UserGetDTO and TeamGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    TeamGetDTO teamGetDTO = DTOMapper.INSTANCE.convertEntityToTeamGetDTO(team);

    // check if memberships are equal
    assertEquals(user.getMemberships().iterator().next(), userGetDTO.getMemberships().iterator().next());
    assertEquals(team.getMemberships(), teamGetDTO.getMemberships());
    assertEquals(team.getMemberships(), userGetDTO.getMemberships());

    //check if user belongs to team and vice versa
    assertEquals(userGetDTO.getMemberships().iterator().next().getTeam(), team);
    assertEquals(teamGetDTO.getMemberships().iterator().next().getUser(), user);
  }  
}
