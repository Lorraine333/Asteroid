package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;

/**
 * Created by lorraine on 11/21/14.
 */
public abstract  class MoveAdapter implements Movable {
    @Override
    public void move() {

    }

    @Override
    public void draw(Graphics g) {

    }

    @Override
    public int points() {
        return 0;
    }

    @Override
    public Point getCenter() {
        return null;
    }

    @Override
    public int getRadius() {
        return 0;
    }

    @Override
    public void expire() {

    }

    @Override
    public void fadeInOut() {

    }
}
