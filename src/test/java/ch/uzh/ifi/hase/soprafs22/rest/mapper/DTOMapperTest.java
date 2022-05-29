package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.*;

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


    @Test
    public void testGetTeamCalendar_fromTeamCaelndar_toTeamCaelndarGetDTO_success() {

        Team testTeam = new Team();
        testTeam.setId(1L);
        User testUser = new User();
        testUser.setId(2L);
        testUser.setToken("token");
        testTeam.setName("testTeamname");
        TeamCalendar testTeamCalendar = new TeamCalendar();
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setBasePlan(new ArrayList<>());
        testTeam.setTeamCalendar(testTeamCalendar);

        Day day = new Day ();
        day.setTeamCalendar(testTeamCalendar);
        testTeam.setTeamCalendar(testTeamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        Schedule schedule = new Schedule();
        schedule.setSlot(slot);
        schedule.setId(10L);
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setId(1L);
        testTeamCalendar.setBasePlan(days);
        List<Day> daysFixed = new ArrayList<>();
        testTeamCalendar.setBasePlanFixed(daysFixed);
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setStartingDateFixed(LocalDate.now());

        // MAP -> Create TeamGetDTO
        TeamCalendarGetDTO teamCalendarGetDTO = DTOMapper.INSTANCE.convertEntityToTeamCalendarGetDTO(testTeamCalendar);

        // check content
        assertEquals(testTeamCalendar.getId(), teamCalendarGetDTO.getId());
        assertEquals(testTeamCalendar.getBasePlan().size(), teamCalendarGetDTO.getDays().size());
        assertEquals(testTeamCalendar.getStartingDate(), teamCalendarGetDTO.getStartingDate());
        assertEquals(testTeamCalendar.getStartingDateFixed(), teamCalendarGetDTO.getStartingDateFixed());
        assertEquals(testTeamCalendar.getBusy(), teamCalendarGetDTO.getBusy());
    }

    @Test
    public void testGetGame_fromGame_toGameGetDTO_success() {

        Team testTeam = new Team();
        testTeam.setId(1L);
        User testUser = new User();
        testUser.setId(2L);
        testUser.setToken("token");
        testTeam.setName("testTeamname");
        TeamCalendar testTeamCalendar = new TeamCalendar();
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setBasePlan(new ArrayList<>());
        testTeam.setTeamCalendar(testTeamCalendar);



        Day day = new Day ();
        day.setTeamCalendar(testTeamCalendar);
        testTeam.setTeamCalendar(testTeamCalendar);
        Slot slot = new Slot();
        slot.setDay(day);
        Schedule schedule = new Schedule();
        schedule.setSlot(slot);
        schedule.setId(10L);
        schedule.setSpecial(-1);
        schedule.setBase(1);
        schedule.setUser(testUser);
        List<Schedule> schedules = Collections.singletonList(schedule);
        slot.setSchedules(schedules);
        slot.setRequirement(1);
        List<Slot> slots = Collections.singletonList(slot);
        day.setSlots(slots);
        List<Day> days = Collections.singletonList(day);
        testTeamCalendar.setId(1L);
        testTeamCalendar.setBasePlan(days);
        List<Day> daysFixed = new ArrayList<>();
        testTeamCalendar.setBasePlanFixed(daysFixed);
        testTeamCalendar.setStartingDate(LocalDate.now());
        testTeamCalendar.setStartingDateFixed(LocalDate.now());

        Game testGame = new Game();
        testGame.setId(1L);
        testGame.setBoardLength(10);
        Player testPlayer1= new Player();
        Player testPlayer2 = new Player();
        testPlayer1.setId(2L);
        testPlayer2.setId(3L);
        testPlayer1.setGame(testGame);
        testPlayer2.setGame(testGame);
        Set<Player> players = new HashSet<Player>();
        players.add(testPlayer1);
        players.add(testPlayer2);
        testGame.setPlayers(players);
        testGame.setBoardLength(10);

        Location apple = new Location();
        apple.setX(3);
        apple.setY(3);
        List<Location> apples = new ArrayList<>();
        apples.add(apple);
        testGame.setApples(apples);

        // MAP -> Create TeamGetDTO
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(testGame);

        // check content
        assertEquals(testGame.getId(), gameGetDTO.getId());
        assertEquals(testGame.getPlayers().size(),gameGetDTO.getPlayers().size());
        assertEquals(testGame.getBoardLength(), gameGetDTO.getBoardLength());
        assertEquals(testGame.getApples().get(0).getX(), gameGetDTO.getApples().get(0).getX());
        assertEquals(testGame.getApples().get(0).getY(), gameGetDTO.getApples().get(0).getY());
        assertEquals(testGame.getStatus(), gameGetDTO.getStatus());

    }
}
