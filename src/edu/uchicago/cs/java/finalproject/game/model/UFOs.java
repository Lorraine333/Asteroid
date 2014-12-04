package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lorraine on 11/30/14.
 */
public class UFOs extends Sprite{
    private int nSpin;

    private final int RAD = 70;

    public UFOs(int nSize){

        //call Sprite constructor
        super();


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

        //an nSize of 0,1,2 is a big asteroid with different color
        //a nSize of 3 or 4 is med or small asteroid respectively
        if (nSize == 0) {
            setRadius(RAD);
            setColor(Color.red);
        }

        //assignRandomShape();

        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,1)); //top point

        pntCs.add(new Point(4,1));
        pntCs.add(new Point(2,0));
        pntCs.add(new Point(2,-1));
        pntCs.add(new Point(1,-2));

        pntCs.add(new Point(-1,-2));
        pntCs.add(new Point(-2,-1));
        pntCs.add(new Point(-2,0));
        pntCs.add(new Point(-4,1));

        assignPolarPoints(pntCs);



    }




    public UFOs(UFOs UFOExploded){


        //call Sprite constructor
        super();

        int  nSizeNew =	UFOExploded.getSize() + 1;


        //the spin will be either plus or minus 0-9
        int nSpin = Game.R.nextInt(10);
        if(nSpin %2 ==0)
            nSpin = -nSpin;
        setSpin(nSpin);

        //random delta-x
        int nDX = Game.R.nextInt(10 + nSizeNew*2);
        if(nDX %2 ==0)
            nDX = -nDX;
        setDeltaX(nDX);

        //random delta-y
        int nDY = Game.R.nextInt(10+ nSizeNew*2);
        if(nDY %2 ==0)
            nDY = -nDY;
        setDeltaY(nDY);

        if(nSizeNew == 1) {

            setRadius(RAD);
            setColor(Color.yellow);
        }
        else if(nSizeNew == 2) {
            setRadius(RAD);
            setColor(Color.white);
        }
        else if (nSizeNew == 3 || nSizeNew==4) {
            setRadius(RAD/((nSizeNew-2) * 2));
            setColor(Color.white);
        }

        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,1)); //top point

        pntCs.add(new Point(4,1));
        pntCs.add(new Point(2,0));
        pntCs.add(new Point(2,-1));
        pntCs.add(new Point(1,-2));

        pntCs.add(new Point(-1,-2));
        pntCs.add(new Point(-2,-1));
        pntCs.add(new Point(-2,0));
        pntCs.add(new Point(-4,1));

        assignPolarPoints(pntCs);

        setCenter(UFOExploded.getCenter());


    }

    public int getSize(){

        int nReturn = 0;

        if(getRadius()==RAD && getColor()==Color.red)
            nReturn =0;
        else if(getRadius()==RAD && getColor() ==Color.yellow)
            nReturn =1;
        else if (getRadius() ==RAD && getColor() == Color.white)
            nReturn =2;
        else if (getRadius() ==RAD /2 )
            nReturn = 3;
        else if(getRadius()==RAD / 4)
            nReturn =4;
        return nReturn;

    }

    //overridden
    public void move(){
        if(Game.getTick() % 3 ==0) {
            super.move();

            //an asteroid spins, so you need to adjust the orientation at each move()
            setOrientation(getOrientation() + getSpin());
        }

    }

    public int getSpin() {
        return this.nSpin;
    }


    public void setSpin(int nSpin) {
        this.nSpin = nSpin;
    }
}

