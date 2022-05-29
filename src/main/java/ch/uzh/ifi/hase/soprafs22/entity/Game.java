package ch.uzh.ifi.hase.soprafs22.entity;

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

    @OneToOne(cascade = CascadeType.REFRESH)
    private  Slot slot;

    @Column
    private String status;

    @Column
    private int boardLength;


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

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBoardLength() {
        return boardLength;
    }

    public void setBoardLength(int boardLength) {
        this.boardLength = boardLength;
    }

}
