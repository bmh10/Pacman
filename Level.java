import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * Level.java
 */

public class Level {

	ArrayList<Point> coinPoints;
	ArrayList<Point> powerupPoints;
	ArrayList<TurnPoint> turnPoints;
	//Image coin;
	int state;
	private static boolean debug;
	public static Point origin, rightOrigin;
	
	
	Level(Game game, int levelNum) {
		coinPoints = new ArrayList<Point>();
		powerupPoints = new ArrayList<Point>();
		turnPoints = new ArrayList<TurnPoint>();
		origin = new Point((game.winSize.width - game.gameBackground.getWidth(null))/2, (game.winSize.height - game.gameBackground.getHeight(null))/2);
		rightOrigin = new Point((game.winSize.width - game.gameBackground.getWidth(null))/2 +game.gameBackground.getWidth(null)-5, (game.winSize.height - game.gameBackground.getHeight(null))/2);

		switch(levelNum) {
		case 1: createLevelOne(game); break;
		default: break;
		}
	}
	
	/*
	 * Turns debugging graphics on/off
	 */
	public static void showDebug(boolean b) {
		Level.debug = b;
	}
	
	
	private void createLevelOne(Game game) {
		
		//Place coins - drawn in symmetrical pairs (left then right)
		// Vertical coins
		// Top
		drawVerticalCoinLinePair(9, 30, 30);
		
		drawVerticalCoinLinePair(31, 170, 30);
		
		drawVerticalCoinLinePair(4, 250, 130);
		
		drawVerticalCoinLinePair(6, 330, 30);
		
		// Bottom
		drawVerticalCoinLinePair(4, 30, 490);
		
		drawVerticalCoinLinePair(4, 30, 630);
		
		drawVerticalCoinLinePair(5, 90, 550);
		
		drawVerticalCoinLinePair(5, 250, 550);
		
		drawVerticalCoinLinePair(4, 330, 490);
		
		drawVerticalCoinLinePair(4, 330, 630);
		
		// Horizontal coins
		// Top
		drawHorizontalCoinLinePair(15, 30, 30);
		
		drawHorizontalCoinLinePair(18, 30, 130);
		
		drawHorizontalCoinLinePair(7, 30, 190);
		
		drawHorizontalCoinLinePair(5, 250, 190);
		
		// Bottom
		drawHorizontalCoinLinePair(18, 30, 690);
		
		drawHorizontalCoinLinePair(16, 30, 490);
		
		drawHorizontalCoinLinePair(3, 30, 550);
		
		drawHorizontalCoinLinePair(11, 170, 550);
		
		drawHorizontalCoinLinePair(7, 30, 630);
		
		drawHorizontalCoinLinePair(4, 250, 630);
		
		int x = origin.x;
		int y = origin.y;
		int mx = rightOrigin.x;
		//int my = rightOrigin.y;
		
		
		//Place power-ups
		drawPowerupPair(30, 70);
		drawPowerupPair(30, 550);
		
		//Add turn points
		//Bottom line
		turnPoints.add(new TurnPoint(x+30, y+690, true, true, false, false));
		turnPoints.add(new TurnPoint(x+330, y+690, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-330, y+690, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-30, y+690, true, false, false, true));
		
		//Line 2 (from bottom)
		turnPoints.add(new TurnPoint(x+30, y+630, false, true, true, false));
		turnPoints.add(new TurnPoint(x+90, y+630, true, true, false, true));
		turnPoints.add(new TurnPoint(x+170, y+630, true, false, false, true));
		turnPoints.add(new TurnPoint(x+250, y+630, true, true, false, false));
		turnPoints.add(new TurnPoint(x+330, y+630, false, false, true, true));
		turnPoints.add(new TurnPoint(mx-330, y+630, false, true, true, false));
		turnPoints.add(new TurnPoint(mx-250, y+630, true, false, false, true));
		turnPoints.add(new TurnPoint(mx-170, y+630, true, true, false, false));
		turnPoints.add(new TurnPoint(mx-90, y+630, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-30, y+630, false, false, true, true));

		//Line 3
		turnPoints.add(new TurnPoint(x+30, y+550, true, true, false, false));
		turnPoints.add(new TurnPoint(x+90, y+550, false, false, true, true));
		turnPoints.add(new TurnPoint(x+170, y+550, true, true, true, false));
		turnPoints.add(new TurnPoint(x+250, y+550, false, true, true, true));
		turnPoints.add(new TurnPoint(x+330, y+550, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-330, y+550, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-250, y+550, false, true, true, true));
		turnPoints.add(new TurnPoint(mx-170, y+550, true, false, true, true));
		turnPoints.add(new TurnPoint(mx-90, y+550, false, true, true, false));
		turnPoints.add(new TurnPoint(mx-30, y+550, true, false, false, true));
		
		//Line 4
		turnPoints.add(new TurnPoint(x+30, y+480, false, true, true, false));
		turnPoints.add(new TurnPoint(x+170, y+480, true, true, true, true));
		turnPoints.add(new TurnPoint(x+250, y+480, true, true, false, true));
		turnPoints.add(new TurnPoint(x+330, y+480, false, false, true, true));
		turnPoints.add(new TurnPoint(mx-330, y+480, false, true, true, false));
		turnPoints.add(new TurnPoint(mx-250, y+480, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-170, y+480, true, true, true, true));
		turnPoints.add(new TurnPoint(mx-30, y+480, false, false, true, true));
		
		//Line 5
		turnPoints.add(new TurnPoint(x+250, y+410, true, true, true, false));
		turnPoints.add(new TurnPoint(mx-250, y+410, true, false, true, true));
		
		//Line 5
		turnPoints.add(new TurnPoint(x+170, y+340, true, true, true, true));
		turnPoints.add(new TurnPoint(x+250, y+340, true, false, true, true));
		turnPoints.add(new TurnPoint(mx-250, y+340, true, true, true, false));
		turnPoints.add(new TurnPoint(mx-170, y+340, true, true, true, true));
		
		//Line 6
		turnPoints.add(new TurnPoint(x+250, y+270, false, true, true, false));
		turnPoints.add(new TurnPoint(x+330, y+270, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-330, y+270, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-250, y+270, false, false, true, true));
		
		//Line 7
		turnPoints.add(new TurnPoint(x+30, y+200, true, true, false, false));
		turnPoints.add(new TurnPoint(x+170, y+200, true, false, true, true));
		turnPoints.add(new TurnPoint(x+250, y+200, true, true, false, false));
		turnPoints.add(new TurnPoint(x+330, y+200, false, false, true, true));
		turnPoints.add(new TurnPoint(mx-330, y+200, false, true, true, false));
		turnPoints.add(new TurnPoint(mx-250, y+200, true, false, false, true));
		turnPoints.add(new TurnPoint(mx-170, y+200, true, true, true, false));
		turnPoints.add(new TurnPoint(mx-30, y+200, true, false, false, true));
		
		//Line 8
		turnPoints.add(new TurnPoint(x+30, y+130, true, true, true, false));
		turnPoints.add(new TurnPoint(x+170, y+130, true, true, true, true));
		turnPoints.add(new TurnPoint(x+250, y+130, false, true, true, true));
		turnPoints.add(new TurnPoint(x+330, y+130, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-330, y+130, true, true, false, true));
		turnPoints.add(new TurnPoint(mx-250, y+130, false, true, true, true));
		turnPoints.add(new TurnPoint(mx-170, y+130, true, true, true, true));
		turnPoints.add(new TurnPoint(mx-30, y+130, true, false, true, true));
		
		//Line 9
		turnPoints.add(new TurnPoint(x+30, y+30, false, true, true, false));
		turnPoints.add(new TurnPoint(x+170, y+30, false, true, true, true));
		turnPoints.add(new TurnPoint(x+330, y+30, false, false, true, true));
		turnPoints.add(new TurnPoint(mx-330, y+30, false, true, true, false));
		turnPoints.add(new TurnPoint(mx-170, y+30, false, true, true, true));
		turnPoints.add(new TurnPoint(mx-30, y+30, false, false, true, true));
		
		//Central points
		turnPoints.add(new TurnPoint(x+370, y+270, false, true, false, true));  // Red spawn point (top center)
		
		turnPoints.add(new TurnPoint(x+440, y+360, true, false, false, false)); // Orange spawn point (right) - anticlockwise
		turnPoints.add(new TurnPoint(x+400, y+360, false, true, false, true));
		turnPoints.add(new TurnPoint(x+400, y+320, false, false, true, false));
		turnPoints.add(new TurnPoint(x+440, y+320, false, false, false, true));
		
		turnPoints.add(new TurnPoint(x+370, y+360, true, false, false, false)); // Pink spawn point (central)
		
		turnPoints.add(new TurnPoint(x+300, y+360, true, false, false, false)); // Blue spawn point (left) - clockwise
		turnPoints.add(new TurnPoint(x+300, y+320, false, true, false, false)); 
		turnPoints.add(new TurnPoint(x+340, y+320, false, false, true, false));
		turnPoints.add(new TurnPoint(x+340, y+360, false, true, false, true)); 
	}
	
	private void drawCoinLine(int numCoins, boolean vertical, int startx, int starty ) {
		if (vertical) {
			for (int i = 0; i < numCoins; i++)
				coinPoints.add(new Point (startx, starty+20*i));
		}
		// Otherwise horizontal
		else {
			for (int i = 0; i < numCoins; i++)
				coinPoints.add(new Point (startx+20*i, starty));
			
		}
	}
	
	private void drawVerticalCoinLinePair(int numCoins, int x, int y) {
		drawCoinLine(numCoins, true, origin.x+x, origin.y+y);
		drawCoinLine(numCoins, true, rightOrigin.x-x, rightOrigin.y+y);
	}
	
	private void drawHorizontalCoinLinePair(int numCoins, int x, int y) {
		drawCoinLine(numCoins, false, origin.x+x, origin.y+y);
		drawCoinLine(numCoins, false, rightOrigin.x-x-(numCoins-1)*20, rightOrigin.y+y);
	}
	
	private void drawPowerupPair(int x, int y) {
		powerupPoints.add(new Point (origin.x+x, origin.y+y));
		powerupPoints.add(new Point (rightOrigin.x-x, origin.y+y));
	}
	
	
	public void draw(Graphics g) {
		if(debug) {
			g.setColor(Color.RED);
			g.drawRect(origin.x, origin.y, 5, 5);
			g.setColor(Color.YELLOW);
			g.drawRect(rightOrigin.x, origin.y, 5, 5);

			g.setColor(Color.RED);
			Iterator<TurnPoint> n = turnPoints.iterator();
			while (n.hasNext()) {
				n.next().draw(g);
			}
		}
		
		g.setColor(Color.YELLOW);
		for (int k = 0; k < coinPoints.size(); k++) {
			Point next = coinPoints.get(k); //k.next();
			g.fillOval(next.x, next.y, 5, 5);
		}
		
		g.setColor(Color.WHITE);
		state = (state+1)%16;
		Iterator<Point> l = powerupPoints.iterator();
		while (l.hasNext()) {
			Point next = l.next();
			if(state < 4)
				g.fillOval(next.x-5, next.y-5, 10, 10);
			else if(state < 8)
				g.fillOval(next.x-7, next.y-7, 14, 14);
			else if(state < 12)
				g.fillOval(next.x-9, next.y-9, 18, 18);
			else g.fillOval(next.x-10, next.y-10, 20, 20);
		}
	}
	
}
