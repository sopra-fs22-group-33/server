package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Location;

import javax.persistence.Column;
import java.util.List;

public class PlayerPutDTO {
    private List<Location> chunks;


    private UserStatus statusOnline;
    public List<Location> getChunks() {
        return chunks;
    }

    public void setChunks(List<Location> chunks) {
        this.chunks = chunks;
    }

    public UserStatus getStatusOnline() {
        return statusOnline;
    }

    public void setStatusOnline(UserStatus statusOnline) {
        this.statusOnline = statusOnline;
    }
}
