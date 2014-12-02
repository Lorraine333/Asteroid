package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by lorraine on 11/29/14.
 */
public class Debris extends Sprite {
    private int nSpin;

    //radius of a large asteroid
    private final int RAD = 20;

    //nSize determines if the Asteroid is Large (0), Medium (1), or Small (2)
    //when you explode a Large asteroid, you should spawn 2 or 3 medium asteroids
    //same for medium asteroid, you should spawn small asteroids
    //small asteroids get blasted into debris
    public Debris(Asteroid astExploded){


        //call Sprite constructor
        super();

        int  nSizeNew =	astExploded.getSize() + 1;


        //the spin will be either plus or minus 0-9
        int nSpin = Game.R.nextInt(10);
        if(nSpin %2 ==0)
            nSpin = -nSpin;
        setSpin(nSpin);

        //random delta-x
        int nDX = Game.R.nextInt(100 + nSizeNew*2);
        if(nDX %2 ==0)
            nDX = -nDX;
        setDeltaX(nDX);

        //random delta-y
        int nDY = Game.R.nextInt(100+ nSizeNew*2);
        if(nDY %2 ==0)
            nDY = -nDY;
        setDeltaY(nDY);

        assignRandomShape();

        //an nSize of zero is a big asteroid
        //a nSize of 1 or 2 is med or small asteroid respectively

        setRadius(RAD/(nSizeNew * 2));
        setCenter(astExploded.getCenter());
        setExpire(30);
        setColor(new Color(Game.R.nextInt(256), Game.R.nextInt(256),Game.R.nextInt(256)));

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

    //this is for an asteroid only
    public void assignRandomShape ()
    {
        int nSide = Game.R.nextInt( 7 ) + 7;
        int nSidesTemp = nSide;

        int[] nSides = new int[nSide];
        for ( int nC = 0; nC < nSides.length; nC++ )
        {
            int n = nC * 48 / nSides.length - 4 + Game.R.nextInt( 8 );
            if ( n >= 48 || n < 0 )
            {
                n = 0;
                nSidesTemp--;
            }
            nSides[nC] = n;
        }

        Arrays.sort(nSides);

        double[]  dDegrees = new double[nSidesTemp];
        for ( int nC = 0; nC <dDegrees.length; nC++ )
        {
            dDegrees[nC] = nSides[nC] * Math.PI / 24 + Math.PI / 2;
        }
        setDegrees( dDegrees);

        double[] dLengths = new double[dDegrees.length];
        for (int nC = 0; nC < dDegrees.length; nC++) {
            if(nC %3 == 0)
                dLengths[nC] = 1 - Game.R.nextInt(40)/100.0;
            else
                dLengths[nC] = 1;
        }
        setLengths(dLengths);

    }

    //override the expire method - once an object expires, then remove it from the arrayList.
    public void expire(){
        if (getExpire() == 0) {
            CommandCenter.movDebris.remove(this);
            //CommandCenter.movDebris.add(new Explosion(this));
        }
        else
            setExpire(getExpire() - 1);
    }

}
