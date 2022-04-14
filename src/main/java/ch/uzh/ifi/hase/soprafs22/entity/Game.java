package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Internal Game Representation
 * This class composes the internal representation of the game and defines how
 * the game is stored in the database.
 */
@Entity
@Table(name = "GAME")
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue
    private long id;

    @ElementCollection
    private Set<Player> players;

    @ElementCollection
    private Set<Location> apples;

    // associated shift

    // nr. of winner(s) that there will be (== nr. of people that can have that shift)

    // list of users that play including their status (playing the game or opted out, maybe also if winner or not)

    // game status (still going on, finished)

    // whatever the users enter (input depends on type of game)

    // winner(s) 1 or more
}
