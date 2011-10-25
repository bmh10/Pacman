import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * Enemy.java
 * Defines a Pacman enemy and its state
 */

public class Enemy {

	Game game;
	enum dir {NORTH, EAST, SOUTH, WEST};
	Image[] imgs = new Image[12];
	Line2D vert, horiz;
	private static int SCAREDSPEED = 1, SPEED = 2;
	
	// Enemy state
	private static boolean showView, debug;
	private static Point origin;
	private dir direction;
	private static int xmin, xmax;
	int ghostType, state, flashstate, quadrant, startCount;
	boolean scared, startup, flash;
	boolean cangoLeft, cangoRight, cangoUp, cangoDown;
	private Point pos;
	int scaredCount;
	TurnPoint lastVisited;

	
	Enemy(Point startPos, int ghostType, Game game) {
		this.game = game;
		this.scaredCount = 0;
		this.startup = true;
		this.startCount = 0;
		this.scared = false;
		this.flash = false;
		this.state = 0;
		this.flashstate = 0;
		origin = new Point((game.winSize.width - game.gameBackground.getWidth(null))/2, (game.winSize.height - game.gameBackground.getHeight(null))/2);
		Enemy.setRange(origin.x, origin.x+game.gameBackground.getWidth(null));
		if(ghostType == 0 || ghostType == 3)
			this.direction = dir.NORTH;
		else this.direction = dir.WEST;
		
		// Load images depending on ghost type
		this.ghostType = ghostType;
		switch(this.ghostType) {
		case 0:
			imgs[0] = getImage("blueGhostUP1.png");
			imgs[1] = getImage("blueGhostUP2.png");
			imgs[2] = getImage("blueGhostDWN1.png");
			imgs[3] = getImage("blueGhostDWN2.png");
			imgs[4] = getImage("blueGhostLEFT1.png");
			imgs[5] = getImage("blueGhostLEFT2.png");
			imgs[6] = getImage("blueGhostRIGHT1.png");
			imgs[7] = getImage("blueGhostRIGHT2.png");
			break;
		case 1:
			imgs[0] = getImage("redGhostUP1.png");
			imgs[1] = getImage("redGhostUP2.png");
			imgs[2] = getImage("redGhostDWN1.png");
			imgs[3] = getImage("redGhostDWN2.png");
			imgs[4] = getImage("redGhostLEFT1.png");
			imgs[5] = getImage("redGhostLEFT2.png");
			imgs[6] = getImage("redGhostRIGHT1.png");
			imgs[7] = getImage("redGhostRIGHT2.png");
			break;
		case 2:
			imgs[0] = getImage("pinkGhostUP1.png");
			imgs[1] = getImage("pinkGhostUP2.png");
			imgs[2] = getImage("pinkGhostDWN1.png");
			imgs[3] = getImage("pinkGhostDWN2.png");
			imgs[4] = getImage("pinkGhostLEFT1.png");
			imgs[5] = getImage("pinkGhostLEFT2.png");
			imgs[6] = getImage("pinkGhostRIGHT1.png");
			imgs[7] = getImage("pinkGhostRIGHT2.png");
			break;
		case 3:
			imgs[0] = getImage("orangeGhostUP1.png");
			imgs[1] = getImage("orangeGhostUP2.png");
			imgs[2] = getImage("orangeGhostDWN1.png");
			imgs[3] = getImage("orangeGhostDWN2.png");
			imgs[4] = getImage("orangeGhostLEFT1.png");
			imgs[5] = getImage("orangeGhostLEFT2.png");
			imgs[6] = getImage("orangeGhostRIGHT1.png");
			imgs[7] = getImage("orangeGhostRIGHT2.png");
			break;
		}
		imgs[8] = getImage("slowGhost1.png");
		imgs[9] = getImage("slowGhost2.png");
		imgs[10] = getImage("slowGhostWhite1.png");
		imgs[11] = getImage("slowGhostWhite2.png");
		
		this.pos = new Point(startPos.x - imgs[0].getWidth(null)/2, startPos.y - imgs[0].getHeight(null)/2);
	}
	
	/*
	 * Loads an image
	 */
	private Image getImage(String filename) {
		Image img = game.getImage(game.getDocumentBase(), filename);
		return img;
		
	}
	
	/*
	 * Sets minimum and maximum range
	 */
	public static void setRange(int xmin, int xmax) {
		Enemy.xmin = xmin;
		Enemy.xmax = xmax;
	}
	
	public void setDirection(dir d) {
		this.direction = d;
	}
	
	public String getDirection() {
		return direction.toString();
	}
	
	public Point getPos() {
		return pos;
	}
	
	/*
	 * Returns rectangle outline of enemy
	 */
	public Rectangle getOutline() {
		return new Rectangle(pos.x, pos.y, 32, 32);
	}
	
	/*
	 * Returns rectangle outline enemy field of vision
	 */
	public Rectangle getView() {
		int area = 500;
		return new Rectangle(pos.x-(area-imgs[0].getWidth(null))/2, pos.y-(area-imgs[0].getHeight(null))/2, area, area);
	}
	
	/*
	 * Turns ghost view graphics on/off
	 */
	public static void showView(boolean b) {
		Enemy.showView = b;
	}
	
	/*
	 * Turns debugging graphics on/off
	 */
	public static void showDebug(boolean b) {
		Enemy.debug = b;
	}
	
	
	public static void setWait(boolean b) {
		//Enemy.wait = b;
		if(b) SPEED = 0;
		else SPEED = 2;
	}
	
	/*
	 * Puts ghost in scared state and resets counter
	 */
	public void makeScared() {
		this.scared = true;
		this.flash = false;
		scaredCount = 0;
	}
	
	/*
	 * Resets the ghost to normal state
	 */
	public void reset() {
		this.scared = false;
		this.flash = false;
		this.scaredCount = 0;
		this.state = 0;
		this.flashstate = 0;
	}
	
	/*
	 * Returns whether Pacman is blocked by a wall or not
	 */
	private boolean isBlocked(Game game) {
		Rectangle ghost = new Rectangle((int)this.getOutline().getCenterX()-1, (int)this.getOutline().getCenterY()-1,2 ,2 ); //this.getOutline();
		Rectangle pacman = game.pacman.getOutline();
			
				Iterator<TurnPoint> j = game.level.turnPoints.iterator();
				while(j.hasNext()) {
					TurnPoint currTurnPoint = j.next();
						if(ghost.contains(currTurnPoint.pos)) {
								cangoUp = currTurnPoint.cangoUP; cangoRight = currTurnPoint.cangoRIGHT; cangoDown = currTurnPoint.cangoDOWN; cangoLeft = currTurnPoint.cangoLEFT;
								
								// Startup procedure
								// Blue startup
								if(ghostType == 0 && startup && cangoLeft && cangoRight) {
									if(startCount < 2) {
										direction = dir.WEST;
										startCount++;
									}
									if(startCount == 2) {
										direction = dir.EAST;
										startup = false;
									}
								}
								// Orange startup
								else if(ghostType == 3 && startup && cangoLeft && cangoRight) {
									if(startCount < 4) {
										direction = dir.EAST;
										startCount++;
									}
									if(startCount == 4) {
										direction = dir.WEST;
										startup = false;
									}
								}
								// End Startup procedure
								
								//If pacman is in ghost's field of vision
								else if(pacman.intersects(getView()) && !scared) {
									
									//cWhen pacman is horizontally or vertically in-line with enemy
									if(pacman.x == ghost.x && pacman.y > ghost.y) {
										if(cangoDown) 
											direction = dir.SOUTH;
									}
									if(pacman.x == ghost.x && pacman.y < ghost.y) {
										if(cangoUp) 
											direction = dir.NORTH;
									}
									if(pacman.y == ghost.y && pacman.x > ghost.x) {
										if(cangoRight) 
											direction = dir.EAST;
									}
									if(pacman.y == ghost.y && pacman.x < ghost.x) {
										if(cangoLeft) 
											direction = dir.WEST;
									}

									// When pacman is in vicinity of enemy
										else {
											// quad1 = bottom left
											if(pacman.x < ghost.x && pacman.y > ghost.y)
												quadrant = 1;
											// quad2 = top left
											if(pacman.x < ghost.x && pacman.y < ghost.y)
												quadrant = 2;
											// quad3 = top right
											if(pacman.x > ghost.x && pacman.y < ghost.y)
												quadrant = 3;
											// quad4 = bottom right
											if(pacman.x > ghost.x && pacman.y > ghost.y)
												quadrant = 4;

											// Take appropriate action depending on which quadrant pacman is in
											switch(quadrant) {
											case 1:
												if(cangoDown && cangoLeft) {
													if(Math.random()<0.5) direction = dir.SOUTH;
													else direction = dir.WEST;
												}
												else if(cangoDown)
													direction = dir.SOUTH;
												else if(cangoLeft)
													direction = dir.WEST;
												else if(cangoUp)
													direction = dir.NORTH;
												else if(cangoRight)
													direction = dir.EAST;
												break;
											case 2:
												if(cangoUp && cangoLeft) {
													if(Math.random()<0.5) direction = dir.NORTH;
													else direction = dir.WEST;
												}
												else if(cangoUp)
													direction = dir.NORTH;
												else if(cangoLeft)
													direction = dir.WEST;
												else if(cangoRight)
													direction = dir.EAST;
												else if(cangoDown)
													direction = dir.SOUTH;
												break;
											case 3:
												if(cangoUp && cangoRight) {
													if(Math.random()<0.5) direction = dir.EAST;
													else direction = dir.NORTH;
												}
												else if(cangoUp)
													direction = dir.NORTH;
												else if(cangoRight)
													direction = dir.EAST;
												else if(cangoDown)
													direction = dir.SOUTH;
												else if(cangoLeft)
													direction = dir.WEST;
												break;

											case 4:
												if(cangoDown && cangoRight) {
													if(Math.random()<0.5) direction = dir.SOUTH;
													else direction = dir.EAST;
												}
												else if(cangoDown)
													direction = dir.SOUTH;
												else if(cangoRight)
													direction = dir.EAST;
												else if(cangoLeft)
													direction = dir.WEST;
												else if(cangoUp)
													direction = dir.NORTH;
												break;
											}
										}
								}

								//General movement
									else {
										ArrayList<dir> possDirs = new ArrayList<dir>();
										if(direction == dir.NORTH) {
											if(cangoUp) possDirs.add(dir.NORTH);
											//									if(cangoDown) possDirs.add(dir.SOUTH);
											if(cangoRight) possDirs.add(dir.EAST);
											if(cangoLeft) possDirs.add(dir.WEST);
										}
										if(direction == dir.SOUTH) {
											//									if(cangoUp) possDirs.add(dir.NORTH);
											if(cangoDown) possDirs.add(dir.SOUTH);
											if(cangoRight) possDirs.add(dir.EAST);
											if(cangoLeft) possDirs.add(dir.WEST);
										}
										if(direction == dir.EAST) {
											if(cangoUp) possDirs.add(dir.NORTH);
											if(cangoDown) possDirs.add(dir.SOUTH);
											if(cangoRight) possDirs.add(dir.EAST);
											//if(cangoLeft) possDirs.add(dir.WEST);
										}
										if(direction == dir.WEST) {
											if(cangoUp) possDirs.add(dir.NORTH);
											if(cangoDown) possDirs.add(dir.SOUTH);
											//									if(cangoRight) possDirs.add(dir.EAST);
											if(cangoLeft) possDirs.add(dir.WEST);
										}
										int rand = (int) Math.round(Math.random()*(possDirs.size()-1));
										direction = possDirs.get(rand);
									}
							}
					}
			return false;
		}
	
	/*
	 * Moves ghost in current direction unless blocked
	 */
	public void move(Game game) {
		state = (state+1)%6;
		flashstate = (flashstate+1)%12;
		int currSpeed;
		if(scared) currSpeed = SCAREDSPEED;
		else currSpeed = SPEED;
			if(!isBlocked(game)) {
				switch(direction) {
				case NORTH:
					if(cangoUp) {
						pos.y -= currSpeed;
						cangoLeft = false;
						cangoRight = false;
						cangoDown = false;
					}
					break;
				case EAST:
					// Wrap around
					if (pos.x > xmax)
						pos.x = xmin;
					if(cangoRight) {
						pos.x += currSpeed;
						cangoUp = false;
						cangoDown = false;
						cangoLeft = false;
					}
					break;
				case SOUTH:
					if(cangoDown) {
						pos.y += currSpeed;
						cangoLeft = false;
						cangoRight = false;
						cangoUp = false;
					}
					break;
				case WEST:
					// Wrap around
					if (pos.x < xmin)
						pos.x = xmax;
					if(cangoLeft) {
						pos.x -= currSpeed;
						cangoUp = false;
						cangoDown = false;
						cangoRight = false;
					}
					break;
				}
			}
		}
	
	public void draw(Graphics g) {
		if (!scared) {
			switch(direction) {
			case NORTH:
				if(state < 3)
					g.drawImage(imgs[0], pos.x, pos.y, null);
				else g.drawImage(imgs[1], pos.x, pos.y, null);
				break;
			case EAST:
				if(state < 3)
					g.drawImage(imgs[6], pos.x, pos.y, null);
				else g.drawImage(imgs[7], pos.x, pos.y, null);
				break;
			case SOUTH:
				if(state < 3)
					g.drawImage(imgs[2], pos.x, pos.y, null);
				else g.drawImage(imgs[3], pos.x, pos.y, null);
				break;
			case WEST:
				if(state < 3)
					g.drawImage(imgs[4], pos.x, pos.y, null);
				else g.drawImage(imgs[5], pos.x, pos.y, null);
				break;
			}
		}
		// Images for when scared and also controls scare timer
		else {
			scaredCount++;
			if(scaredCount > 200) this.flash = true;
			if(scaredCount > 270) {
				this.reset();
			}
			if(flash) {
				if(flashstate < 3)
					g.drawImage(imgs[8], pos.x, pos.y, null);
				else if(flashstate < 6)
					g.drawImage(imgs[11], pos.x, pos.y, null);
				else if(flashstate < 9)
					g.drawImage(imgs[9], pos.x, pos.y, null);
				else g.drawImage(imgs[10], pos.x, pos.y, null);
			}
			else {
				if(state < 3)
					g.drawImage(imgs[8], pos.x, pos.y, null);
				else g.drawImage(imgs[9], pos.x, pos.y, null);
			}
		}
		
		// Draw enemy view if in 'show view' mode
		if(showView) {
			g.setColor(Color.RED);
			g.drawRect(getView().x, getView().y, getView().width, getView().height);
		}
		
		// Draw debugging graphics if in 'debug' mode
		if(debug) {
			g.setColor(Color.RED);
			vert = new Line2D.Double(this.getOutline().getCenterX(), this.getOutline().getMinY(), this.getOutline().getCenterX(), this.getOutline().getMaxY());
			horiz = new Line2D.Double(this.getOutline().getMinX(), this.getOutline().getCenterY(), this.getOutline().getMaxX(), this.getOutline().getCenterY());
			g.drawLine((int)vert.getX1(), (int)vert.getY1(), (int)vert.getX2(), (int)vert.getY2());
			g.drawLine((int)horiz.getX1(), (int)horiz.getY1(), (int)horiz.getX2(), (int)horiz.getY2());
			g.drawRect(this.getOutline().x, this.getOutline().y, this.getOutline().width, this.getOutline().height);
		}
	}

}
