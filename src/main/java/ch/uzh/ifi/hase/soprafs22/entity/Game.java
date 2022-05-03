package ch.uzh.ifi.hase.soprafs22.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.util.List;
import java.util.Set;

/**
 * Internal Game Representation
 * This class composes the internal representation of the game and defines how
 * the game is stored in the database.
 */
@Entity(name = "Game")
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue
    private long id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Player> players;

    @ElementCollection
    private List<Location> apples;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public List<Location> getApples() {
        return apples;
    }

    public void setApples(List<Location> apples) {
        this.apples = apples;
    }

    // associated shift

    // nr. of winner(s) that there will be (== nr. of people that can have that shift)

    // list of users that play including their status (playing the game or opted out, maybe also if winner or not)

    // game status (still going on, finished)

    // whatever the users enter (input depends on type of game)

    // winner(s) 1 or more
}
