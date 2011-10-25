import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/*
 * TurnPoint.java
 * Turn point object which Pacman and enemies use to navigate
 */

public class TurnPoint {

	boolean cangoUP, cangoDOWN, cangoRIGHT, cangoLEFT;
	Rectangle point;
	Point pos;
	
	TurnPoint(int x, int y, boolean UP, boolean RIGHT, boolean DOWN, boolean LEFT) {
		pos = new Point(x, y);
		this.cangoUP = UP;
		this.cangoDOWN = DOWN;
		this.cangoRIGHT = RIGHT;
		this.cangoLEFT = LEFT;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(pos.x, pos.y, 6, 6);
	}
	
}
