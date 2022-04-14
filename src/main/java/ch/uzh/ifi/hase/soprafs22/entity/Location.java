package ch.uzh.ifi.hase.soprafs22.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Location {
    @Column
    private int x;

    @Column
    private int y;
}
