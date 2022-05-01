package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Location {

    private Integer x;

    private Integer y;

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }
}
