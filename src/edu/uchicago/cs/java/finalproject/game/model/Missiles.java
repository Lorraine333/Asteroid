package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by lorraine on 12/1/14.
 */
public class Missiles extends Sprite {
    private final double FIRE_POWER = 30.0;
    private double x;
    private double y;
    private Asteroid asteroid;



    public Missiles(Asteroid asteroid, Falcon falcon){

        super();


        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,6)); //top point

        pntCs.add(new Point(1,4));
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(2,-2));

        pntCs.add(new Point(-2,-2));
        pntCs.add(new Point(-1,-1));
        pntCs.add(new Point(-1,4));

        this.asteroid = asteroid;

        assignPolarPoints(pntCs);

        //a missiles expires after 50 frames
        setExpire( 5000 );
        setRadius(20);




        //everything is relative to the asteroid that fired the missiles

        setDeltaX( falcon.getDeltaX() +
                Math.cos( Math.toRadians( falcon.getOrientation() ) ) * FIRE_POWER );
        setDeltaY( falcon.getDeltaY() +
                Math.sin( Math.toRadians( falcon.getOrientation() ) ) * FIRE_POWER );

        setCenter( falcon.getCenter() );

        //set the asteroid orientation to the falcon (ship) orientation
        setOrientation(falcon.getOrientation());




    }


    @Override
    public void move() {
        super.move();
        x= (asteroid.getCenter().getY() - CommandCenter.getFalcon().getCenter().getY())
                /(asteroid.getCenter().getX() - CommandCenter.getFalcon().getCenter().getX());
        y = (asteroid.getCenter().getY() - CommandCenter.getFalcon().getCenter().getY())
                /(asteroid.getCenter().getX() - CommandCenter.getFalcon().getCenter().getX());

        setDeltaX( CommandCenter.getFalcon().getDeltaX() +
                Math.cos( Math.atan(x))* FIRE_POWER );
        setDeltaY( CommandCenter.getFalcon().getDeltaY() +
                Math.sin( Math.atan(y))* FIRE_POWER );
        setOrientation(asteroid.getOrientation());

    }

    //override the expire method - once an object expires, then remove it from the arrayList.
    public void expire(){
        if (getExpire() == 0) {
            CommandCenter.movFriends.remove(this);
            //CommandCenter.movDebris.add(new Explosion(this));
        }
        else
            setExpire(getExpire() - 1);
    }
}
