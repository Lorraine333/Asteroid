package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by lorraine on 12/2/14.
 */
public class GetMissiles extends Sprite {

    private int nSpin;

    public GetMissiles() {

        super();

        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,6)); //top point

        pntCs.add(new Point(1,4));
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(2,-2));

        pntCs.add(new Point(-2,-2));
        pntCs.add(new Point(-1,-1));
        pntCs.add(new Point(-1,4));

        assignPolarPoints(pntCs);

        setExpire(250);
        setRadius(40);
        setColor(Color.GREEN);


        int nX = Game.R.nextInt(10);
        int nY = Game.R.nextInt(10);
        int nS = Game.R.nextInt(5);

        //set random DeltaX
        if (nX % 2 == 0)
            setDeltaX(nX);
        else
            setDeltaX(-nX);

        //set rnadom DeltaY
        if (nY % 2 == 0)
            setDeltaX(nY);
        else
            setDeltaX(-nY);

        //set random spin
        if (nS % 2 == 0)
            setSpin(nS);
        else
            setSpin(-nS);

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
            CommandCenter.movFloaters.remove(this);
        else
            setExpire(getExpire() - 1);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        //now draw a white border
        g.setColor(Color.white);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
