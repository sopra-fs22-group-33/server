package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Location;

import java.util.List;

public class PlayerPutDTO {
    private List<Location> chunks;

    public List<Location> getChunks() {
        return chunks;
    }

    public void setChunks(List<Location> chunks) {
        this.chunks = chunks;
    }
}
