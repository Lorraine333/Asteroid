package edu.uchicago.cs.java.finalproject.controller;

import org.omg.CORBA.CODESET_INCOMPATIBLE;
import sun.audio.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.sampled.Clip;
import javax.swing.*;

import edu.uchicago.cs.java.finalproject.game.model.*;
import edu.uchicago.cs.java.finalproject.game.view.*;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(1100, 700); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nLevel = 1;
	private static int nTick = 0;
	private ArrayList<Tuple> tupMarkForRemovals;
	private ArrayList<Tuple> tupMarkForAdds;
	private boolean bMuted = true;
	

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 37, // rotate left; left arrow
			RIGHT = 39, // rotate right; right arrow
			UP = 38, // thrust; up arrow
			START = 83, // s key
			FIRE = 32, // space key
			MUTE = 77, // m-key mute

	// for possible future use
	 Missile = 68, 					// d key
	 SHIELD = 65, 				// a key arrow
	 HyperSpace = 69, 				// hyper space
	 SPECIAL = 70; 					// fire special weapon;  F key

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 120;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);

		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
	

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
			spawnNewShipFloater();
            spawnShield();
            spawnHyperSpace();
            spawnNuiBullets();
            spawnGetMissiles();




			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();
			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level. 
			checkNewLevel();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run



	private void checkCollisions() {

		
		//@formatter:off
		//for each friend in movFriends
			//for each foe in movFoes
				//if the distance between the two centers is less than the sum of their radii
					//mark it for removal
		
		//for each mark-for-removal
			//remove it
		//for each mark-for-add
			//add it
		//@formatter:on
		
		//we use this ArrayList to keep pairs of movMovables/movTarget for either
		//removal or insertion into our arrayLists later on
		tupMarkForRemovals = new ArrayList<Tuple>();
		tupMarkForAdds = new ArrayList<Tuple>();

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;

		for (Movable movFriend : CommandCenter.movFriends) {
			for (Movable movFoe : CommandCenter.movFoes) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

					//falcon
					if ((movFriend instanceof Falcon) ){
						if (!CommandCenter.getFalcon().getProtected()){
                            if(!CommandCenter.getFalcon().isbShield()) {
                                tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
                                CommandCenter.spawnFalcon(false);
                                killFoe(movFoe);
                            }
						}
					}
					//not the falcon
					else {
						tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
						killFoe(movFoe);
					}//end else 

					//explode/remove foe
					
					
				
				}//end if 
			}//end inner for
		}//end outer for






        //check for collisions between falcon and floaters
		if (CommandCenter.getFalcon() != null){
			Point pntFalCenter = CommandCenter.getFalcon().getCenter();
			int nFalRadiux = CommandCenter.getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.movFloaters) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {

                    if (movFloater instanceof  NewShipFloater) {
                        tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                        CommandCenter.setNumFalcons(CommandCenter.getNumFalcons() + 1);
                        // killFoe(movFoe);
                    }
                    else if(movFloater instanceof Shield)
                    {
                        tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                        CommandCenter.getFalcon().setShield(CommandCenter.getFalcon().getShield()+1);
                    }

                    else if (movFloater instanceof HyperSpace)
                    {
                        tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                        CommandCenter.getFalcon().setCenter(new Point(R.nextInt(Game.DIM.width), R.nextInt(Game.DIM.height)));
                    }

                    else if (movFloater instanceof GetMissiles)
                    {
                        tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                        CommandCenter.getFalcon().setMissilesNumber(CommandCenter.getFalcon().getMissilesNumber()+1);
                    }
                    //not the falcon

                    tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
					Sound.playSound("pacman_eatghost.wav");
	
				}//end if 
			}//end inner for
		}//end if not null
		
		//remove these objects from their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForRemovals) 
			tup.removeMovable();
		
		//add these objects to their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForAdds) 
			tup.addMovable();

		//call garbage collection
		System.gc();
		
	}//end meth

	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getSize() == 0){
				//spawn two medium Asteroids
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
				
			} 
			//medium size aseroid exploded
			else if(astExploded.getSize() == 1){
				//spawn three small Asteroids
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
				tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
                //tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new Asteroid(astExploded)));
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
			}
            else {
                //remove the original Foe
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
                //tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Explosion(astExploded)));
                //tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Debris(astExploded)));
                for (int i = 0; i < 20; i++) {
                    tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Debris(astExploded)));
                }

                CommandCenter.setScore(CommandCenter.getScore() + 10);
            }
			
		}
        else if(movFoe instanceof Nuissance)
        {
            Nuissance nuiExploded = (Nuissance)movFoe;

            tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
            //tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Explosion(nuiExploded)));
            CommandCenter.setScore(CommandCenter.getScore() + 30);

        }
        else if (movFoe instanceof UFOs)
        {
            UFOs UFOExploded = (UFOs) movFoe;


            if(UFOExploded.getSize() == 0 ||UFOExploded.getSize() == 1){
                tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new UFOs(UFOExploded)));
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));

            }
            //medium size aseroid exploded
            else if(UFOExploded.getSize() == 2){
                //spawn three small Asteroids
                tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new UFOs(UFOExploded)));
                tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new UFOs(UFOExploded)));
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
            }
            else if (UFOExploded.getSize() ==3 ){
                tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new UFOs(UFOExploded)));
                tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new UFOs(UFOExploded)));
               // tupMarkForAdds.add(new Tuple(CommandCenter.movFoes,new UFOs(UFOExploded)));
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));

            }
            else {

                tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Explosion(UFOExploded)));
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
                CommandCenter.setScore(CommandCenter.getScore() + 30);
            }


        }


		//not an asteroid
		else {
			//remove the original Foe
			tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
		}
		
		
		

		
		
		
		
	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public static int getTick() {
		return nTick;
	}


	private void spawnNewShipFloater() {
		//make the appearance of power-up dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0) {
        //if (nTick % 50 == 0){
			CommandCenter.movFloaters.add(new NewShipFloater());
		}
	}

    private void spawnHyperSpace() {
        //make the appearance of power-up dependent upon ticks and levels
        //the higher the level the more frequent the appearance
        //if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 20) == 0) {
            if (nTick % 300 == 0){
            CommandCenter.movFloaters.add(new HyperSpace());
        }
    }

    private void spawnGetMissiles(){
        if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0) {
            //if (nTick % 50 == 0){
            CommandCenter.movFloaters.add(new GetMissiles());
        }
    }

    private void spawnShield() {
        //make the appearance of power-up dependent upon ticks and levels
        //the higher the level the more frequent the appearance
        if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0) {
            //if (nTick % 50 == 0){
            CommandCenter.movFloaters.add(new Shield());
        }
    }



	// Called when user presses 's'
	private void startGame() {
		CommandCenter.clearAll();
		CommandCenter.initGame();
		CommandCenter.setLevel(0);
		CommandCenter.setPlaying(true);
		CommandCenter.setPaused(false);
        CommandCenter.setScore(0);//Score
		//if (!bMuted)
		   // clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			CommandCenter.movFoes.add(new Asteroid(0));
		}
	}

    private void spawnUFOs(int number)
    {
        for (int i = 0; i <number ; i++) {
            CommandCenter.movFoes.add(new UFOs(0));
        }

    }

    private void spawnNuissance(int number){
        for (int i = 0; i < number; i++) {
            CommandCenter.movFoes.add(new Nuissance());
        }
    }

    private void spawnNuiBullets() {
        if (nTick % 20 ==0) {
                for (Movable movFoe : CommandCenter.movFoes) {
                    if (movFoe instanceof Nuissance) {
                        Nuissance nuissance = (Nuissance) movFoe;
                        CommandCenter.movFoes.add(new Bullet(nuissance));
                        break;
                    }
                }
        }

    }

	
	
	private boolean isLevelClear(){
		//if there are no more Asteroids on the screen
		
		boolean bAsteroidFree = true;
		for (Movable movFoe : CommandCenter.movFoes) {
			if (movFoe instanceof Asteroid){
				bAsteroidFree = false;
				break;
			}
            else if(movFoe instanceof Nuissance){
                bAsteroidFree = false;
                break;
            }
            else if (movFoe instanceof UFOs)
            {
                bAsteroidFree = false;
                break;
            }
		}
		
		return bAsteroidFree;

		
	}
	
	private void checkNewLevel(){
		
		if (isLevelClear() ){
			if (CommandCenter.getFalcon() !=null)
				CommandCenter.getFalcon().setProtected(true);
			
			spawnAsteroids(CommandCenter.getLevel() + 2);
            //you will have UFO in level 2
            spawnUFOs(CommandCenter.getLevel());
            //you won't get a Nuissance until you are already level 4
            spawnNuissance(CommandCenter.getLevel()-1);

			CommandCenter.setLevel(CommandCenter.getLevel() + 1);
            CommandCenter.setScore(0);
            if(CommandCenter.getLevel()>1) {
                JOptionPane.showConfirmDialog(null, "Congratulations! Level "+CommandCenter.getLevel());
            }

		}
	}
	
	
	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (nKey == START && !CommandCenter.isPlaying())
			startGame();

		if (fal != null) {

			switch (nKey) {
			case PAUSE:
				CommandCenter.setPaused(!CommandCenter.isPaused());
				if (CommandCenter.isPaused())
					stopLoopingSounds(clpMusicBackground, clpThrust);
				else
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case QUIT:
				System.exit(0);
				break;
			case UP:
				fal.thrustOn();
				if (!CommandCenter.isPaused())
					clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case LEFT:
				fal.rotateLeft();
				break;
			case RIGHT:
				fal.rotateRight();
				break;


			// possible future use
			// case KILL:

			// case NUM_ENTER:

			default:
				break;
			}
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		 //System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
			case FIRE:
				CommandCenter.movFriends.add(new Bullet(fal));
				Sound.playSound("laser.wav");
				break;
				
			//special is a special weapon, current it just fires the cruise missile. 
//			case SPECIAL:
//				CommandCenter.movFriends.add(new Cruise(fal));
//				Sound.playSound("laser.wav");
//				break;
				
			case LEFT:
				fal.stopRotating();
				break;
			case RIGHT:
				fal.stopRotating();
				break;
			case UP:
				fal.thrustOff();
                fal.setDeltaY(0);
                fal.setDeltaX(0);
				clpThrust.stop();
				break;

				
			case MUTE:
				if (!bMuted){
					stopLoopingSounds(clpMusicBackground);
					bMuted = !bMuted;
				} 
				else {
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					bMuted = !bMuted;
				}
				break;

            case SHIELD:
                if(!CommandCenter.getFalcon().isbShield() && CommandCenter.getFalcon().getShield()>0) {

                    CommandCenter.getFalcon().setbShield(true);
                    CommandCenter.getFalcon().setShieldTime(110);
                    CommandCenter.getFalcon().setShield(CommandCenter.getFalcon().getShield() - 1);
                    Sound.playSound("Tick.wav");
                }
                break;

            case SPECIAL:
                if(CommandCenter.getFalcon().getIntervalTime() < 0)
                {
                    CommandCenter.getFalcon().setbCruise(true);
                    CommandCenter.getFalcon().setCruiseTime(50);
                }
                 break;

            case Missile:
                if(CommandCenter.getFalcon().getMissilesNumber() > 0) {
                    CommandCenter.getFalcon().setbSuper(true);
                    for (Movable movFoe : CommandCenter.movFoes) {
                        if (movFoe instanceof Asteroid) {
                            Asteroid astFire = (Asteroid) movFoe;
                            CommandCenter.movFriends.add(new Missiles(astFire, CommandCenter.getFalcon()));
                        }
                    }

                    CommandCenter.getFalcon().setMissilesNumber(CommandCenter.getFalcon().getMissilesNumber()-1);
                }
                break;
            case HyperSpace:
                CommandCenter.getFalcon().setCenter(new Point(R.nextInt(Game.DIM.width), R.nextInt(Game.DIM.height)));


				
			default:
				break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}
	

	
}

// ===============================================
// ==A tuple takes a reference to an ArrayList and a reference to a Movable
//This class is used in the collision detection method, to avoid mutating the array list while we are iterating
// it has two public methods that either remove or add the movable from the appropriate ArrayList 
// ===============================================

class Tuple{
	//this can be any one of several CopyOnWriteArrayList<Movable>
	private CopyOnWriteArrayList<Movable> movMovs;
	//this is the target movable object to remove
	private Movable movTarget;
	
	public Tuple(CopyOnWriteArrayList<Movable> movMovs, Movable movTarget) {
		this.movMovs = movMovs;
		this.movTarget = movTarget;
	}
	
	public void removeMovable(){
		movMovs.remove(movTarget);
	}
	
	public void addMovable(){
		movMovs.add(movTarget);
	}

}
