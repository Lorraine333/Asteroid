package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by lorraine on 11/24/14.
 */
public class Nuissance extends Sprite {
    private int nSpin;

    public Nuissance( ) {


        super();


        ArrayList<Point> pntCs = new ArrayList<Point>();
        // top of ship
        pntCs.add(new Point(0, 5));

        pntCs.add(new Point(2,5));
        pntCs.add(new Point(1, 4));
        pntCs.add(new Point(1,1));
        pntCs.add(new Point(5, 1));
        pntCs.add(new Point(5,0));
        pntCs.add(new Point(1, 0));
        pntCs.add(new Point(0,-6));

        pntCs.add(new Point(0,-6));
        pntCs.add(new Point(-1, 0));
        pntCs.add(new Point(-5,0));
        pntCs.add(new Point(-5, 1));
        pntCs.add(new Point(-1,1));
        pntCs.add(new Point(-1, 4));
        pntCs.add(new Point(-2,5));


        assignPolarPoints(pntCs);

        //the spin will be either plus or minus 0-9
        int nSpin = Game.R.nextInt(10);
        if(nSpin %2 ==0)
            nSpin = -nSpin;
        setSpin(nSpin);

        //random delta-x
        int nDX = Game.R.nextInt(10);
        if(nDX %2 ==0)
            nDX = -nDX;
        setDeltaX(nDX);

        //random delta-y
        int nDY = Game.R.nextInt(10);
        if(nDY %2 ==0)
            nDY = -nDY;
        setDeltaY(nDY);


        setExpire(250);
        setColor(Color.RED);

        //random point on the screen
        setCenter(new Point(Game.R.nextInt(Game.DIM.width),
                Game.R.nextInt(Game.DIM.height)));

        //random orientation
        setOrientation(Game.R.nextInt(360));

    }


    public void move() {
        super.move();

        setOrientation(getOrientation() + getSpin());

    }

    public int getSpin() {
        return this.nSpin;
    }

    public void setSpin(int nSpin) {
        this.nSpin = nSpin;
    }

    //override the expire method - once an object expires, then remove it from the arrayList.
    @Override
    public void expire() {
        if (getExpire() == 0)
            CommandCenter.movFoes.remove(this);
        else
            setExpire(getExpire() - 1);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        //now draw a white border
        g.setColor(Color.red);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
