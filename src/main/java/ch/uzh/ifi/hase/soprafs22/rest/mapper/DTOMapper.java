package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.TeamCalendar;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.Team;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "memberships", target = "memberships")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "email", target = "email")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "memberships", target = "memberships")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "memberships", target = "memberships")
  Team convertTeamPostDTOtoEntity(TeamPostDTO teamPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "memberships", target = "memberships")
  TeamGetDTO convertEntityToTeamGetDTO(Team team);

  @Mapping(source = "startingDate", target = "startingDate")
  @Mapping(source = "days", target = "basePlan")
  TeamCalendar convertTeamCalendarPostDTOtoEntity(TeamCalendarPostDTO teamCalendarPostDTO);

  @Mapping(source = "startingDate", target = "startingDate")
  @Mapping(source = "basePlan", target = "days")
  TeamCalendarGetDTO convertEntityToTeamCalendarGetDTO(TeamCalendar teamCalendar);

  @Mapping(source = "apples", target = "apples")
  @Mapping(source = "players", target = "players")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "apples", target = "apples")
  @Mapping(source = "players", target = "players")
  GameGetDTO convertEntityToGameGetDTO(Game game);
}

