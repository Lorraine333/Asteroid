package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.sounds.Sound;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	
	private final double THRUST = 20.00;

	final int DEGREE_STEP = 10;
	
	private boolean bShield = false;
    private boolean bCruise = false;
	private boolean bFlame = false;
    private boolean bSuper = false;

    public boolean isbSuper() {
        return bSuper;
    }

    public void setbSuper(boolean bSuper) {
        this.bSuper = bSuper;
    }

    private boolean bProtected; //for fade in and out


	
	private boolean bThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;

	
	private int nShield;

    private int ShieldTime;

    // The time of user can send a cruise without stopping
    private int CruiseTime;

    // The time between two Cruise
    private int IntervalTime;

    //number of missiles
    private int MissilesNumber;

    public int getMissilesNumber() {
        return MissilesNumber;
    }

    public void setMissilesNumber(int missilesNumber) {
        MissilesNumber = missilesNumber;
    }

    public int getIntervalTime() {
        return IntervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        IntervalTime = intervalTime;
    }

    public int getCruiseTime() {
        return CruiseTime;
    }

    public void setCruiseTime(int cruiseTime) {
        CruiseTime = cruiseTime;
    }

    public boolean isbCruise() {
        return bCruise;
    }

    public void setbCruise(boolean bCruise) {
        this.bCruise = bCruise;
    }

    private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];


    private static Shield shield;





	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();

		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// top of ship
		pntCs.add(new Point(0, 18)); 
		
		//right points
		pntCs.add(new Point(3, 3)); 
		pntCs.add(new Point(12, 0)); 
		pntCs.add(new Point(13, -2)); 
		pntCs.add(new Point(13, -4)); 
		pntCs.add(new Point(11, -2)); 
		pntCs.add(new Point(4, -3)); 
		pntCs.add(new Point(2, -10)); 
		pntCs.add(new Point(4, -12)); 
		pntCs.add(new Point(2, -13)); 

		//left points
		pntCs.add(new Point(-2, -13)); 
		pntCs.add(new Point(-4, -12));
		pntCs.add(new Point(-2, -10)); 
		pntCs.add(new Point(-4, -3)); 
		pntCs.add(new Point(-11, -2));
		pntCs.add(new Point(-13, -4));
		pntCs.add(new Point(-13, -2)); 
		pntCs.add(new Point(-12, 0)); 
		pntCs.add(new Point(-3, 3)); 
		

		assignPolarPoints(pntCs);

		setColor(Color.white);
		
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));

        //hyperspace
        //setCenter(new Point(Game.R.nextInt(Game.DIM.width), Game.R.nextInt(Game.DIM.height)));
		
		//with random orientation
		//setOrientation(Game.R.nextInt(360));
        setOrientation(270);
		
		//this is the size of the falcon
		setRadius(50);

        setbSuper(false);
        setMissilesNumber(0);

        setbShield(false);
        setbCruise(false);
        setIntervalTime(5);
        setShield(3);
		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
        shield = new Shield();
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

	public void move() {
		super.move();
		if (bThrusting) {
			bFlame = true;
			double dAdjustX = Math.cos(Math.toRadians(getOrientation()))
					* THRUST;
			double dAdjustY = Math.sin(Math.toRadians(getOrientation()))
					* THRUST;

//            setDeltaX(getDeltaX() + dAdjustX);
//            setDeltaY(getDeltaY() + dAdjustY);

            setDeltaX(dAdjustX);
            setDeltaY(dAdjustY);
		}
		if (bTurningLeft) {

			if (getOrientation() <= 0 && bTurningLeft) {
				setOrientation(360);
			}
			setOrientation(getOrientation() - DEGREE_STEP);
		} 
		if (bTurningRight) {
			if (getOrientation() >= 360 && bTurningRight) {
				setOrientation(0);
			}
			setOrientation(getOrientation() + DEGREE_STEP);
		} 
	} //end move

	public void rotateLeft() {
		bTurningLeft = true;
	}

	public void rotateRight() {
		bTurningRight = true;
	}

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
	}

	public void thrustOn() {
		bThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bFlame = false;
	}

	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;
		if (getFadeValue() == 255) {
			colShip = Color.white;
		} else {
			colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
					getFadeValue(), 175), getFadeValue());
		}

		//shield on
		if (bShield && getShieldTime()>0) {

            setShieldTime(getShieldTime()-1);

			g.setColor(Color.cyan);
			g.drawOval(getCenter().x - getRadius(),
					getCenter().y - getRadius(), getRadius() * 2,
					getRadius() * 2);

		} //end if shield
        else {
            setbShield(false);

        }

        if (bCruise && getCruiseTime()>0) {

            setCruiseTime(getCruiseTime() - 1);
            CommandCenter.movFriends.add(new Cruise(CommandCenter.getFalcon()));
                Sound.playSound("laser.wav");


        } //end if shield
        else if(bCruise && getCruiseTime()==0){
            setbCruise(false);
            setIntervalTime(200);
        }

        if(!bCruise && getIntervalTime() >= 0)
        {
            setIntervalTime(getIntervalTime()-1);
        }



		//thrusting
		if (bFlame) {


            Graphics2D g2d = (Graphics2D) g;
            Paint paint=new GradientPaint(0, 0, Color.RED, 50,50, Color.ORANGE, true);
            g2d.setPaint(paint);

			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame


        if(isbSuper() && getMissilesNumber()>0)
        {
            ArrayList<Point> pntCs = new ArrayList<Point>();

            g.setColor(colShip);

            // top of ship
            pntCs.add(new Point(0, 5));

            //right points
            pntCs.add(new Point(1, 4));
            pntCs.add(new Point(1, 3));
            pntCs.add(new Point(2, 3));
            pntCs.add(new Point(2, 2));
            pntCs.add(new Point(3, 2));
            pntCs.add(new Point(3, 1));
            pntCs.add(new Point(4, 1));

            pntCs.add(new Point(5, 0));

            pntCs.add(new Point(4, -1));
            pntCs.add(new Point(3, -1));
            pntCs.add(new Point(3, -2));
            pntCs.add(new Point(2, -2));
            pntCs.add(new Point(2, -3));
            pntCs.add(new Point(1, -3));
            pntCs.add(new Point(1, -4));

            pntCs.add(new Point(0, -5));

            pntCs.add(new Point(-1, -4));
            pntCs.add(new Point(-1, -3));
            pntCs.add(new Point(-2, -3));
            pntCs.add(new Point(-2, -2));
            pntCs.add(new Point(-3, -2));
            pntCs.add(new Point(-3, -1));
            pntCs.add(new Point(-4, -1));

            pntCs.add(new Point(-5, 0));

            pntCs.add(new Point(-4, 1));
            pntCs.add(new Point(-3, 1));
            pntCs.add(new Point(-3, 2));
            pntCs.add(new Point(-2, 2));
            pntCs.add(new Point(-2, 3));
            pntCs.add(new Point(-1, 3));
            pntCs.add(new Point(-1, 4));

            drawShipWithColor(g, colShip);
            assignPolarPoints(pntCs);
            setCenter(getCenter());
            setRadius(50);
        }
        else if(getMissilesNumber() <=0)
        {
            ArrayList<Point> pntCs = new ArrayList<Point>();

            g.setColor(colShip);
            // top of ship
            pntCs.add(new Point(0, 18));

            //right points
            pntCs.add(new Point(3, 3));
            pntCs.add(new Point(12, 0));
            pntCs.add(new Point(13, -2));
            pntCs.add(new Point(13, -4));
            pntCs.add(new Point(11, -2));
            pntCs.add(new Point(4, -3));
            pntCs.add(new Point(2, -10));
            pntCs.add(new Point(4, -12));
            pntCs.add(new Point(2, -13));

            //left points
            pntCs.add(new Point(-2, -13));
            pntCs.add(new Point(-4, -12));
            pntCs.add(new Point(-2, -10));
            pntCs.add(new Point(-4, -3));
            pntCs.add(new Point(-11, -2));
            pntCs.add(new Point(-13, -4));
            pntCs.add(new Point(-13, -2));
            pntCs.add(new Point(-12, 0));
            pntCs.add(new Point(-3, 3));


            assignPolarPoints(pntCs);
            drawShipWithColor(g, colShip);
            setCenter(getCenter());
            setRadius(50);
        }



		drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}
//
	public void fadeInOut() {
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}
	}
	
	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

    public int getShieldTime() {
        return ShieldTime;
    }

    public void setShieldTime(int shieldTime) {
        ShieldTime = shieldTime ;
    }

	public boolean getProtected() {return bProtected;}
	public void setShield(int n) {nShield = n;}
	public int getShield() {return nShield;}

    public boolean isbShield() {
        return bShield;
    }

    public static Shield getRealShield(){
        return shield;
    }

    public void setbShield(boolean bShield) {
        this.bShield = bShield;
    }

    //
} //end class
