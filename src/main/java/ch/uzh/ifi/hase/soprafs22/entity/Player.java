package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.util.List;

@Entity(name = "Player")
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    private List<Location> chunks;

    @Column
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Location> getChunks() {
        return chunks;
    }

    public void setChunks(List<Location> chunks) {
        this.chunks = chunks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
