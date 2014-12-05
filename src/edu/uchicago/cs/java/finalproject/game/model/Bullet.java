package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;


public class Bullet extends Sprite {

	  private final double FIRE_POWER = 35.0;
    private final double Feo_FIRE_POWER = 15.0;
	 
	
public Bullet(Falcon fal){
		
		super();
		
		
		//defined the points on a cartesean grid
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		pntCs.add(new Point(0,3)); //top point
		
		pntCs.add(new Point(1,-1));
		pntCs.add(new Point(0,-2));
		pntCs.add(new Point(-1,-1));

		assignPolarPoints(pntCs);

		//a bullet expires after 20 frames
	    setExpire( 20 );
	    setRadius(6);
	    

	    //everything is relative to the falcon ship that fired the bullet
	    setDeltaX( fal.getDeltaX() +
	               Math.cos( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setDeltaY( fal.getDeltaY() +
	               Math.sin( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setCenter( fal.getCenter() );

	    //set the bullet orientation to the falcon (ship) orientation
	    setOrientation(fal.getOrientation());


	}

    public Bullet(Nuissance nuissance){

        super();


        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,3)); //top point

        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(0,-2));
        pntCs.add(new Point(-1,-1));

        assignPolarPoints(pntCs);

        //a bullet expires after 20 frames
        setExpire( 20 );
        setRadius(6);


        //everything is relative to the falcon ship that fired the bullet
        setDeltaX( nuissance.getDeltaX() +
                Math.cos( Math.toRadians( nuissance.getOrientation() ) ) * Feo_FIRE_POWER );
        setDeltaY( nuissance.getDeltaY() +
                Math.sin( Math.toRadians( nuissance.getOrientation() ) ) * Feo_FIRE_POWER );
        setCenter(nuissance.getCenter());

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(Game.R.nextInt(360));

        setColor(Color.red);


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

    @Override
    public void move() {
        Point pnt = getCenter();
        double dX = pnt.x + getDeltaX();
        double dY = pnt.y + getDeltaY();

        //this just keeps the sprite inside the bounds of the frame
        if (pnt.x > 0 && pnt.x < getDim().width && pnt.y > 0 && pnt.y < getDim().height) {
            setCenter(new Point((int) dX, (int) dY));
        }
        else
            setExpire(0);
    }
}
