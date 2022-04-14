package ch.uzh.ifi.hase.soprafs22.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.ArrayList;

@Embeddable
@Getter
@Setter
public class Player {

    @Column
    private Long id;

    @Column
    private ArrayList<Location> chunks;

}
