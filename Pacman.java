import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Iterator;


public class Pacman {

	Image[] imgs = new Image[8]; // Image for each direction
	private static int SPEED = 2;
	private Point pos;
	enum dir {NORTH, EAST, SOUTH, WEST};
	dir direction;
	private static int xmin, xmax;
	int state; // controls whether mouth is open/closed
	boolean cangoLeft, cangoRight, cangoUp, cangoDown;
	dir[] keyBuffer;
	private static boolean debug;
	Game game;
	private static Point origin;
	
	Line2D vert;
	Line2D horiz;
	
	
	Pacman(Game game) {
		this.game = game;
		this.state = 0;
		keyBuffer = new dir[3];
		origin = new Point((game.winSize.width - game.gameBackground.getWidth(null))/2, (game.winSize.height - game.gameBackground.getHeight(null))/2);
		Pacman.setRange(origin.x, origin.x+game.gameBackground.getWidth(null));
		this.pos = new Point(origin.x+450, origin.y+535);
		if(Math.random()<0.5)
			this.direction = dir.WEST;
		else this.direction = dir.EAST;
		cangoLeft = true;
		cangoRight = true;
		
		imgs[0] = getImage("pacDC.gif");
		imgs[1] = getImage("pacDO.gif");
		imgs[2] = getImage("pacLC.gif");
		imgs[3] = getImage("pacLO.gif");
		imgs[4] = getImage("pacRC.gif");
		imgs[5] = getImage("pacRO.gif");
		imgs[6] = getImage("pacUC.gif");
		imgs[7] = getImage("pacUO.gif");
	}
	
	// Creates an image
	private Image getImage(String filename) {
		return game.getImage(game.getDocumentBase(), filename);
		
	}
	
	// Getter and setter methods
	public static void setRange(int xmin, int xmax) {
		Pacman.xmin = xmin;
		Pacman.xmax = xmax;
	}
	
	// Getter and setter methods
	public static int[] getRange() {
		int[] range = new int[2];
		range[0] = xmin;
		range[1] = xmax;
		return range;
	}
	
	public void addToKeyBuffer(dir d){
		for (int i = 0; i < keyBuffer.length; i++)
			if(keyBuffer[i] == null) {
				keyBuffer[i] = d;
				break;
			}
	}
	
	private void removeFromKeyBuffer(){
		keyBuffer[0] = keyBuffer[1];
		keyBuffer[1] = keyBuffer[2];
		keyBuffer[2] = null;
	}
	
	public String getDirection() {
		return direction.toString();
	}
	
	public void setDirection(dir d) {
		this.direction = d;
	}
	
	public Point getPos() {
		return pos;
	}
	
	/*
	 * Turns debugging graphics on/off
	 */
	public static void showDebug(boolean b) {
		Pacman.debug = b;
	}
	
	public static void setWait(boolean b) {
		//Pacman.wait = b;
		if(b) SPEED = 0;
		else SPEED = 2;
	}
	
	/*
	 * Returns rectangle outline of pacman
	 */
	public Rectangle getOutline() {
		return new Rectangle(pos.x, pos.y, 32, 32);//imgs[0].getWidth(null), imgs[0].getHeight(null));
	}
	
	/*
	 * Returns whether Pacman is blocked by a wall or not and manages changing direction
	 */
	private boolean isBlocked(Game game) {
		Rectangle pacman = new Rectangle((int)this.getOutline().getCenterX()-3, (int)this.getOutline().getCenterY()-3,6 ,6 ); //this.getOutline();
		
		Iterator<TurnPoint> a = game.level.turnPoints.iterator();
		while(a.hasNext()) {
			
				TurnPoint currTurnPoint = a.next();
				//if((direction == dir.EAST || direction == dir.WEST && vert.intersectsLine(next)) || (direction == dir.NORTH || direction == dir.SOUTH && horiz.intersectsLine(next)))
				if(pacman.contains(currTurnPoint.pos)) {
					cangoUp = currTurnPoint.cangoUP; cangoRight = currTurnPoint.cangoRIGHT; cangoDown = currTurnPoint.cangoDOWN; cangoLeft = currTurnPoint.cangoLEFT;
					if(keyBuffer[0] == dir.NORTH && cangoUp)
						direction = dir.NORTH;
					if(keyBuffer[0] == dir.SOUTH && cangoDown)
						direction = dir.SOUTH;
					if(keyBuffer[0] == dir.EAST && cangoRight)
						direction = dir.EAST;
					if(keyBuffer[0] == dir.WEST && cangoLeft)
						direction = dir.WEST;
					removeFromKeyBuffer();
				}
		}
		return false;
	}
	
	/*
	 * Moves Pacman in current direction unless blocked
	 */
	public void move(Game game) {
		state = (state+1)%12;
			if(!isBlocked(game)) {
				switch(direction) {
				case NORTH:
					if(cangoUp) {
						pos.y -= SPEED;
						cangoLeft = false;
						cangoRight = false;
						cangoDown = true;
					}
					break;
				case EAST:
					// Wrap around
					if (pos.x > xmax)
						pos.x = xmin;
					if(cangoRight) {
						pos.x += SPEED;
						cangoUp = false;
						cangoDown = false;
						cangoLeft = true;
					}
					break;
				case SOUTH:
					if(cangoDown) {
						pos.y += SPEED;
						cangoLeft = false;
						cangoRight = false;
						cangoUp = true;
					}
					break;
				case WEST:
					// Wrap around
					if (pos.x < xmin)
						pos.x = xmax;
					if(cangoLeft) {
						pos.x -= SPEED;
						cangoUp = false;
						cangoDown = false;
						cangoRight = true;
					}
					break;
				}
			}
//		}
	}
	
	public void draw(Graphics g) {
		switch(direction) {
		case NORTH:
			if(state < 6)
				g.drawImage(imgs[7], pos.x, pos.y, null);
			else g.drawImage(imgs[6], pos.x, pos.y, null);
			break;
		case EAST:
			if(state < 6)
				g.drawImage(imgs[5], pos.x, pos.y, null);
			else g.drawImage(imgs[4], pos.x, pos.y, null);
			break;
		case SOUTH:
			if(state < 6)
				g.drawImage(imgs[1], pos.x, pos.y, null);
			else g.drawImage(imgs[0], pos.x, pos.y, null);
			break;
		case WEST:
			if(state < 6)
				g.drawImage(imgs[3], pos.x, pos.y, null);
			else g.drawImage(imgs[2], pos.x, pos.y, null);
			break;
		}

		if(debug) {
			//Debugging
			g.setColor(Color.RED);
			vert = new Line2D.Double(this.getOutline().getCenterX(), this.getOutline().getMinY(), this.getOutline().getCenterX(), this.getOutline().getMaxY());
			horiz = new Line2D.Double(this.getOutline().getMinX(), this.getOutline().getCenterY(), this.getOutline().getMaxX(), this.getOutline().getCenterY());
			g.drawLine((int)vert.getX1(), (int)vert.getY1(), (int)vert.getX2(), (int)vert.getY2());
			g.drawLine((int)horiz.getX1(), (int)horiz.getY1(), (int)horiz.getX2(), (int)horiz.getY2());
			g.drawRect(this.getOutline().x, this.getOutline().y, this.getOutline().width, this.getOutline().height);
		}
	}

}
