package edu.uchicago.cs.java.finalproject.game.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.SheetCollate;
import javax.swing.JFrame;


import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.*;
import sun.tools.tree.ShiftLeftExpression;


public class GamePanel extends Panel {
	
	// ==============================================================
	// FIELDS 
	// ============================================================== 
	 
	// The following "off" vars are used for the off-screen double-bufferred image. 
	private Dimension dimOff;
	private Image imgOff;
	private Graphics grpOff;
	
	private GameFrame gmf;
	private Font fnt = new Font("SansSerif", Font.BOLD, 12);
	private Font fntBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
	private FontMetrics fmt; 
	private int nFontWidth;
	private int nFontHeight;
	private String strDisplay = "";

    private BufferedImage myImage;

	

	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public GamePanel(Dimension dim){
	    gmf = new GameFrame();
		gmf.getContentPane().add(this);
		gmf.pack();
		initView();
		
		gmf.setSize(dim);
		gmf.setTitle("Game Base");
		gmf.setResizable(false);
		gmf.setVisible(true);
		this.setFocusable(true);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================
	
	private void drawScore(Graphics g) {
		g.setColor(Color.white);
		g.setFont(fnt);
		if (CommandCenter.getScore() != 0 || CommandCenter.isPlaying()) {
			g.drawString("SCORE :  " + CommandCenter.getScore() + "   Shield : " + CommandCenter.getFalcon().getShieldTime()+  "   Cruise : " + CommandCenter.getFalcon().getIntervalTime(), nFontWidth, nFontHeight);
		}
//        else {
//			g.drawString("NO SCORE", nFontWidth, nFontHeight);
//		}
	}
	
	@SuppressWarnings("unchecked")
	public void update(Graphics g) {
		if (grpOff == null || Game.DIM.width != dimOff.width
				|| Game.DIM.height != dimOff.height) {
			dimOff = Game.DIM;
			imgOff = createImage(Game.DIM.width, Game.DIM.height);
			grpOff = imgOff.getGraphics();
		}
		// Fill in background with black.
//        String Path = "Image/background.jpg";
//        try {
//            myImage = ImageIO.read(new File(Path));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        grpOff.drawImage(myImage,0,0,Game.DIM.width, Game.DIM.height,Color.black,null);
		grpOff.setColor(Color.black);
		grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

		drawScore(grpOff);

		
		if (!CommandCenter.isPlaying()) {
			displayTextOnScreen();
		} else if (CommandCenter.isPaused()) {
			strDisplay = "Game Paused";
			grpOff.drawString(strDisplay,
					(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
		}
		
		//playing and not paused!
		else {
			
			//draw them in decreasing level of importance
			//friends will be on top layer and debris on the bottom
			iterateMovables(grpOff, 
					   CommandCenter.movDebris,
			           CommandCenter.movFloaters, 
			           CommandCenter.movFoes,
			           CommandCenter.movFriends);
			
			
			drawNumberShipsLeft(grpOff);
            drawNumberShieldLeft(grpOff);
            drawNumberMissilesLeft(grpOff);
			if (CommandCenter.isGameOver()) {
				CommandCenter.setPlaying(false);
				//bPlaying = false;
			}
		}
		//draw the double-Buffered Image to the graphics context of the panel
		g.drawImage(imgOff, 0, 0, this);
	} 


	
	//for each movable array, process it.
	private void iterateMovables(Graphics g, CopyOnWriteArrayList<Movable>...movMovz){
		
		for (CopyOnWriteArrayList<Movable> movMovs : movMovz) {
			for (Movable mov : movMovs) {

				mov.move();
				mov.draw(g);
				mov.fadeInOut();
				mov.expire();
			}
		}
		
	}
	

	// Draw the number of falcons left on the bottom-right of the screen. 
	private void drawNumberShipsLeft(Graphics g) {
		Falcon fal = CommandCenter.getFalcon();
		double[] dLens = fal.getLengths();
		int nLen = fal.getDegrees().length;
		Point[] pntMs = new Point[nLen];
		int[] nXs = new int[nLen];
		int[] nYs = new int[nLen];
	
		//convert to cartesean points
		for (int nC = 0; nC < nLen; nC++) {
			pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
					.toRadians(90) + fal.getDegrees()[nC])),
					(int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
							+ fal.getDegrees()[nC])));
		}
		
		//set the color to white
		g.setColor(Color.white);
		//for each falcon left (not including the one that is playing)
		for (int nD = 1; nD < CommandCenter.getNumFalcons(); nD++) {
			//create x and y values for the objects to the bottom right using cartesean points again
			for (int nC = 0; nC < fal.getDegrees().length; nC++) {
				nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
				nYs[nC] = pntMs[nC].y + Game.DIM.height - 40;
			}
			g.drawPolygon(nXs, nYs, nLen);
		} 
	}

     private void drawNumberShieldLeft(Graphics g) {
         Shield shield = CommandCenter.getFalcon().getRealShield();
                 double[] dLens = shield.getLengths();
                 int nLen = shield.getDegrees().length;
                 Point[] pntMs = new Point[nLen];
                 int[] nXs = new int[nLen];
                 int[] nYs = new int[nLen];
                 //convert to cartesean points
                 for (int nC = 0; nC < nLen; nC++) {
                     pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
                             .toRadians(90) + shield.getDegrees()[nC])),
                             (int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
                                     + shield.getDegrees()[nC])));
                 }

                 //set the color to white
                 g.setColor(Color.white);
                 //for each falcon left (not including the one that is playing)
                 for (int nD = 1; nD < CommandCenter.getFalcon().getShield(); nD++) {
                     //create x and y values for the objects to the bottom right using cartesean points again
                     for (int nC = 0; nC < shield.getDegrees().length; nC++) {
                         nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
                         nYs[nC] = pntMs[nC].y + Game.DIM.height - 60;
                     }
                     g.drawPolygon(nXs, nYs, nLen);

             }
         }



    private void drawNumberMissilesLeft(Graphics g) {
        //Falcon fal = CommandCenter.getFalcon();

        for (Movable movFloater : CommandCenter.movFloaters)
        {
            if(movFloater instanceof GetMissiles) {
                GetMissiles missiles = (GetMissiles) movFloater;
                double[] dLens = missiles.getLengths();
                int nLen = missiles.getDegrees().length;
                Point[] pntMs = new Point[nLen];
                int[] nXs = new int[nLen];
                int[] nYs = new int[nLen];
                //convert to cartesean points
                for (int nC = 0; nC < nLen; nC++) {
                    pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
                            .toRadians(90) + missiles.getDegrees()[nC])),
                            (int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
                                    + missiles.getDegrees()[nC])));
                }

                //set the color to white
                g.setColor(Color.white);
                //for each falcon left (not including the one that is playing)
                for (int nD = 1; nD < CommandCenter.getFalcon().getMissilesNumber()+1; nD++) {
                    //create x and y values for the objects to the bottom right using cartesean points again
                    for (int nC = 0; nC < missiles.getDegrees().length; nC++) {
                        nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
                        nYs[nC] = pntMs[nC].y + Game.DIM.height - 80;
                    }
                    g.drawPolygon(nXs, nYs, nLen);
                }
                break;
            }
        }


    }
	
	private void initView() {
		Graphics g = getGraphics();			// get the graphics context for the panel
		g.setFont(fnt);						// take care of some simple font stuff
		fmt = g.getFontMetrics();
		nFontWidth = fmt.getMaxAdvance();
		nFontHeight = fmt.getHeight();
		g.setFont(fntBig);					// set font info
	}
	
	// This method draws some text to the middle of the screen before/after a game
	private void displayTextOnScreen() {

		strDisplay = "GAME OVER";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

		strDisplay = "use the arrow keys to turn and thrust";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 40);

		strDisplay = "use the space bar to fire";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 80);

		strDisplay = "'S' to Start";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 120);

		strDisplay = "'P' to Pause";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 160);

		strDisplay = "'Q' to Quit";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 200);
		strDisplay = "left pinkie on 'A' for Shield";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 240);

		strDisplay = "left index finger on 'F' for Cruise";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 280);

		strDisplay = "left index finger on 'D' for Guided Missile";
		grpOff.drawString(strDisplay,
				(Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
						+ nFontHeight + 320);
	}
	
	public GameFrame getFrm() {return this.gmf;}
	public void setFrm(GameFrame frm) {this.gmf = frm;}	
}