package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;

/**
 * Created by lorraine on 11/21/14.
 */
public class Explosion extends MoveAdapter{

    private Point pntCenter;
    //this causes movement; change in x and change in y

    //the radius of circumscibing circle
    private int nRadius =20;
    //is this DEBRIS, FRIEND, FOE, OR FLOATER
    //private byte yFriend;
    //degrees (where the sprite is pointing out of 360)
  //  private int nOrientation;
    private int nExpiry = 20; //natural mortality (short-living objects)
    //the color of this sprite
 //   private Color col;


    public Explosion(UFOs UFO) {
        this.pntCenter = UFO.getCenter();
    }

    @Override
    public void move() {
        super.move();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(Game.R.nextInt(256), Game.R.nextInt(256),Game.R.nextInt(256)));
        g.fillOval(pntCenter.x - nRadius/2, pntCenter.y - nRadius/ 2, nRadius , nRadius);
        //g.fillOval(pntCenter.x - nRadius/2, pntCenter.y - nRadius/ 2, nRadius-10 , nRadius-10);
        //for (int i = 0; i < 20 ; i++) {
            //g.fillOval(pntCenter.x, pntCenter.y, nRadius-15 , nRadius-15);
        //}
    }

    @Override
    public int points() {
        return super.points();
    }

    @Override
    public Point getCenter() {
        return super.getCenter();
    }

    @Override
    public int getRadius() {
        return super.getRadius();
    }

    @Override
    //called every 45 ms
    public void expire() {
      //  super.expire();
    if(nExpiry>0) {
        if (nExpiry > 10) {
            nRadius += 10;
        }
        else {
            nRadius -= 10;
        }
        nExpiry--;
    }

    else
        CommandCenter.getMovDebris().remove(this);
        //if(nExpiry<1)
        //{
            //CommandCenter.getMovDebris()
        //}


    }

    @Override
    public void fadeInOut() {
        super.fadeInOut();
    }
}
