package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Location;
import ch.uzh.ifi.hase.soprafs22.entity.Player;

import java.util.List;

public class GamePostDTO {
    private List<Player> players;
    private List<Location> apples;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Location> getApples() {
        return apples;
    }

    public void setApples(List<Location> apples) {
        this.apples = apples;
    }
}
