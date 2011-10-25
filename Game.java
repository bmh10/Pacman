	import java.applet.Applet;
	import java.awt.*;
	import java.awt.event.KeyAdapter;
	import java.awt.event.KeyEvent;
	import java.awt.event.KeyListener;

	/*
	 * ***PACKMAN BY BEN HOMER - Game.java***
	 *  Main class which manages the game
	 */
	
	@SuppressWarnings("serial")
	public class Game extends Applet implements Runnable {
		
		Thread engine = null;
		Dimension winSize;
		Font scorefont, tinyfont, smallfont, largefont;
		Image dbimage;

		Image gameBackground;
		Image menuBackground;
		Image title;
		int menuBkInc;
		boolean menuDown;
		Pacman pacman;
		Enemy[] enemies;
		private int score, lives;
		Level level;
		Image heart;
		
		int countDownTimer; //c
		static final int INITIALTIME = 60;
		
		SoundManager backingTrack;
		long initialTime;
		
		//Player options
		boolean sound; // toggle sound ON/OFF
		int difficulty;  // 0=easy, 1=med, 2=hard
		boolean ghostView;
		boolean debug;
		
		boolean showStats;
		boolean paused, youWin;
		
		int state, prevState, menustate, optstate;
		static final int PLAYING = 0;
		static final int MAINMENU = 1;
		static final int OPTMENU = 2;
		static final int GAMEINFO = 3;
		static final int CHECK = 4;
		static final int GAMEOVER = 5;
		// Menu states
		static final int PLAY = 0;
		static final int OPTIONS = 1;
		static final int EXIT = 2;
		// Options states
		static final int OINFO = 0;
		static final int OSOUND = 1;
		static final int ODIFFICULTY = 2;
		static final int OGHOSTVIEW = 3;
		static final int ODEBUG = 4;
		static final int OEXIT = 5;

		public static final int easyPause = 40;
		public static final int medPause = 35;
		public static final int hardPause = 30;
		int pause;
		
		
		public String getAppletInfo() {
			return "Pacman by Ben Homer";
		}
		
		
		/*
		 * Initialises game
		 */
		public void init() {
			initialTime = System.currentTimeMillis();
			state = MAINMENU;
			prevState = MAINMENU;
			menustate = PLAY;
			optstate = OINFO;

			// Default settings
			paused = false;
			sound = true;
			showStats = false;
			debug = false;
			ghostView = false;
			
			
			
			difficulty = 1; // medium
			pause = easyPause;
			lives = 3;
			
			setBackground(Color.BLACK);
		
			gameBackground = getImage(getDocumentBase(),"pacmanbk.jpg");
			heart = getImage(getDocumentBase(),"beatingHeart.gif"); 
			title = getImage(getDocumentBase(),"pacmanLogo.jpg");
			menuBackground = getImage(getDocumentBase(),"darkBk.jpg");
			menuBkInc = 0;
			menuDown = true;
			
			
			Dimension d = winSize = Toolkit.getDefaultToolkit().getScreenSize();
			d.width -= 10; d.height -= 110; 
			this.setSize(d.width, d.height);
			this.setPreferredSize(new Dimension(d.width, d.height));
			
			
			enemies = new Enemy[4];
			level = new Level(this, 1);
			
			restart();
			countDownTimer = INITIALTIME;
			
			scorefont = new Font("Digital Dream", Font.BOLD, 30);
			smallfont = new Font("Atomic Clock Radio", Font.BOLD, 16);
			tinyfont = new Font("Arial", Font.BOLD, 10);
			largefont = new Font("Digital Dream", Font.BOLD, 48);
			
			dbimage = createImage(d.width, d.height);
			SoundManager.init();
			backingTrack = SoundManager.selectRandomBackgroundTrack();
			// add key/mouse listeners
			this.addKeyListener(keyListener);
		}


		/*
		 *  Starts a game
		 */
		@Override
		public void run() {
//			SoundManager.INTRO.play();
			while(true) {
				try {
					for(int i=0; i!=2; i++) //change to alter game speed
						step();
					repaint();
					Thread.currentThread();
					Thread.sleep(pause);  //or change pause to alter game speed
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		/*
		 * Single game step
		 */
		public void step() {
			// Maybe do background music in a separate thread
			if(state == PLAYING) {
				//backingTrack.startLoop();
				for (int i = 0; i < enemies.length; i++)
					enemies[i].move(this);
				pacman.move(this);
				checkCoins();
				checkPowerups();
				checkDeath();
				checkGameOver();
			}
		}
		
		/*
		 * Checks for game over
		 */
		private void checkGameOver() {
			if(level.coinPoints.size()==0) {
				youWin = true;
				state = GAMEOVER;
			}
			else if (lives == 0) {
				youWin = false;
				state = GAMEOVER;
			}
		}
		
		/*
		 * Checks for death
		 */
		private void checkDeath() {
			Rectangle pacRect = pacman.getOutline();
			for (int i = 0; i < enemies.length; i++) {
				Rectangle enemy = enemies[i].getOutline();
				if (pacRect.intersects(enemy)) {
					if(!enemies[i].scared) {
						SoundManager.DEATH.play();
						lives--;
						restart();
					}
					else {
						SoundManager.EATGHOST.play();
						score += 100; // change this
						int x = Level.origin.x;
						int y = Level.origin.y;
						switch(i) {
						case 0: enemies[i] = new Enemy(new Point(x+300, y+360), i, this); break;
						case 1: enemies[i] = new Enemy(new Point(x+370, y+270), i, this); break;
						case 2: enemies[i] = new Enemy(new Point(x+370, y+360), i, this); break;
						case 3: enemies[i] = new Enemy(new Point(x+440, y+360), i, this); break;
						}
						//enemies[i].slow = false;
					}
				}
			}
		}
		
		/*
		 * Checks for coins
		 */
		private void checkCoins() {
			Rectangle pacRect = pacman.getOutline();
			//Iterator<Point> i = level.coinPoints.iterator();
			//while (i.hasNext()) {
			// To avoid concurrent modification exception don't use iterator
			for (int i = 0; i < level.coinPoints.size(); i++)
			{
				Point next = level.coinPoints.get(i); // i.next();
				if (pacRect.contains(next)) {
					score += 5;
					level.coinPoints.remove(next);
				}
			}
		}
	
		/*
		 * Checks for coins
		 */
		private void checkPowerups() {
			Rectangle pacRect = pacman.getOutline();
			//Iterator<Point> i = level.powerupPoints.iterator();
			//while (i.hasNext()) {
				for (int i = 0; i < level.powerupPoints.size(); i++)
				{
				Point next = level.powerupPoints.get(i);
				if (pacRect.contains(next)) {
					for (int j = 0; j < enemies.length; j++) {
						enemies[j].makeScared();
					}
					level.powerupPoints.remove(next);
				}
			}
		}
		
		
		/*
		 * Restarts the current level (does not restore lives)
		 */
		private void restart() {
			countDownTimer = INITIALTIME;
			int x = Level.origin.x;
			int y = Level.origin.y;
			enemies[0] = new Enemy(new Point(x+300, y+360), 0, this);//Blue
			enemies[1] = new Enemy(new Point(x+370, y+270), 1, this);//Red
			enemies[2] = new Enemy(new Point(x+370, y+360), 2, this);//Pink
			enemies[3] = new Enemy(new Point(x+440, y+360), 3, this);//Orange
			pacman = new Pacman(this);
		}
		
		/*
		 * Restarts brand new game (setting score to zero and lives to max)
		 */
		private void startNewGame() {
			SoundManager.INTRO.play();
			level = new Level(this, 1);
			score = 0;
			lives = 3;
			restart();
		}
		
		
		/*
		 * Displays game's stats
		 */
		public void displayStats(Graphics g, int s) {
			g.setFont(tinyfont);
			g.setColor(Color.GREEN);
			FontMetrics fm = g.getFontMetrics();

			String time = Long.toString(System.currentTimeMillis() - initialTime);
			
			String px = null, py = null, pcx = null, pcy = null, pmin = null, pmax = null, pdi = null, pu = null, pr = null,
			pd = null, pl = null, pkey1 = null, pkey2 = null, pkey3 = null;
			if (pacman != null) {
				px = Integer.toString(pacman.getPos().x);
				py = Integer.toString(pacman.getPos().y);
				pcx = Double.toString(pacman.getOutline().getCenterX());
				pcy = Double.toString(pacman.getOutline().getCenterY());
				pmin = Double.toString(pacman.getOutline().getMinX());
				pmax = Double.toString(pacman.getOutline().getMaxY());
				pdi = pacman.getDirection();
				pu = Boolean.toString(pacman.cangoUp);
				pr  = Boolean.toString(pacman.cangoRight);
				pd = Boolean.toString(pacman.cangoDown);
				pl = Boolean.toString(pacman.cangoLeft);
				
				if(pacman.keyBuffer[0]==null)
					pkey1 = "NULL";
				else
					pkey1 = pacman.keyBuffer[0].toString();
				
				if(pacman.keyBuffer[1]==null)
					pkey2 = "NULL";
				else
					pkey2 = pacman.keyBuffer[1].toString();
				
				if(pacman.keyBuffer[2]==null)
					pkey3 = "NULL";
				else
					pkey3 = pacman.keyBuffer[2].toString();
			}

			//Enemy info
			String e0x = null, e0y = null, e0cx = null, e0cy = null, e0d = null;
			if(enemies[0] != null) {
				e0x = Integer.toString(enemies[0].getPos().x);
				e0y = Integer.toString(enemies[0].getPos().y);
				e0cx = Double.toString(enemies[0].getOutline().getCenterX());
				e0cy = Double.toString(enemies[0].getOutline().getCenterY());
				e0d = enemies[0].getDirection();
			}
			String e1x = null, e1y = null, e1cx = null, e1cy = null, e1d = null;
			if(enemies[1] != null) {
				e1x = Integer.toString(enemies[1].getPos().x);
				e1y = Integer.toString(enemies[1].getPos().y);
				e1cx = Double.toString(enemies[1].getOutline().getCenterX());
				e1cy = Double.toString(enemies[1].getOutline().getCenterY());
				e1d = enemies[1].getDirection();
			}
			String e2x = null, e2y = null, e2cx = null, e2cy = null, e2d = null;
			if(enemies[2] != null) {
				e2x = Integer.toString(enemies[2].getPos().x);
				e2y = Integer.toString(enemies[2].getPos().y);
				e2cx = Double.toString(enemies[2].getOutline().getCenterX());
				e2cy = Double.toString(enemies[2].getOutline().getCenterY());
				e2d = enemies[2].getDirection();
			}
			String e3x = null, e3y = null, e3cx = null, e3cy = null, e3d = null;
			if(enemies[3] != null) {
				e3x = Integer.toString(enemies[3].getPos().x);
				e3y = Integer.toString(enemies[3].getPos().y);
				e3cx = Double.toString(enemies[3].getOutline().getCenterX());
				e3cy = Double.toString(enemies[3].getOutline().getCenterY());
				e3d = enemies[3].getDirection();
			}
			
			String gsc = Integer.toString(enemies[0].scaredCount);
			
			
			String st = null;
			switch(state) {
			case 0: st = "PLAYING"; break;
			case 1: st = "MAINMENU"; break;
			case 2: st = "OPTMENU"; break;
			case 3: st = "GAMEINFO"; break;
			case 4: st = "CHECK"; break;
			}
			String snd = (sound) ? "ON" : "OFF";
			String diff = null;
			switch(difficulty) {
			case 0: diff = "EASY"; break;
			case 1: diff = "MEDIUM"; break;
			case 2: diff = "HARD"; break;
			}
			String gSpeed = Integer.toString(pause);
			String pause = (paused) ? "YES" : "NO";
			String cnttimer = Integer.toString(countDownTimer);
			
			rightString(g, fm, "Stats", s);
			rightString(g, fm, "Game run-time: " + time , s=space(s)+10);
			rightString(g, fm, "State: " + st, s=space(s));
			rightString(g, fm, "Paused: " + pause, s=space(s));
			rightString(g, fm, "Pacman coordinates: " + px + ", " + py, s=space(s)+10);
			rightString(g, fm, "Pacman centre-coordinates: " + pcx + ", " + pcy, s=space(s)+10);
			rightString(g, fm, "Pacman min/max coordinates: " + pmin + ", " + pmax, s=space(s)+10);
			rightString(g, fm, "Pacman range: " + Pacman.getRange()[0] + " to " + Pacman.getRange()[1], s=space(s)+10);
			rightString(g, fm, "Pacman direction: " + pdi, s=space(s));
			rightString(g, fm, "Can go Up: " + pu, s=space(s));
			rightString(g, fm, "Can go Right: " + pr, s=space(s));
			rightString(g, fm, "Can go Below: " + pd, s=space(s));
			rightString(g, fm, "Can go Left: " + pl, s=space(s));
			rightString(g, fm, "Key Buffer: " + pkey1 + ", " +  pkey2 + ", " +  pkey3 + ", ", s=space(s));
			
			rightString(g, fm, "Enemies", s=space(s)+10);
			rightString(g, fm, "Blue", s=space(s)+10);
			rightString(g, fm, "Blue coordinates: " + e0x + ", " + e0y, s=space(s));
			rightString(g, fm, "Blue centre-coordinates: " + e0cx + ", " + e0cy, s=space(s));
			rightString(g, fm, "Blue direction: " + e0d, s=space(s));
			
			rightString(g, fm, "Red", s=space(s)+10);
			rightString(g, fm, "Red coordinates: " + e1x + ", " + e1y, s=space(s));
			rightString(g, fm, "Red centre-coordinates: " + e1cx + ", " + e1cy, s=space(s));
			rightString(g, fm, "Red direction: " + e1d, s=space(s));
			
			rightString(g, fm, "Pink", s=space(s)+10);
			rightString(g, fm, "Pink coordinates: " + e2x + ", " + e2y, s=space(s));
			rightString(g, fm, "Pink centre-coordinates: " + e2cx + ", " + e2cy, s=space(s));
			rightString(g, fm, "Pink direction: " + e2d, s=space(s));
			
			rightString(g, fm, "Orange", s=space(s)+10);
			rightString(g, fm, "Orange coordinates: " + e3x + ", " + e3y, s=space(s));
			rightString(g, fm, "Orange centre-coordinates: " + e3cx + ", " + e3cy, s=space(s));
			rightString(g, fm, "Orange direction: " + e3d, s=space(s));
			
			rightString(g, fm, "Enemy scare count: " + gsc, s=space(s)+10);
			rightString(g, fm, "Player Options", s=space(s)+10);
			rightString(g, fm, "Sound: " + snd, s=space(s)+10);
			rightString(g, fm, "Difficulty: " + diff, s=space(s));
			rightString(g, fm, "Countdown Timer: " + cnttimer, s=space(s));
			rightString(g, fm, "Game Speed: " + gSpeed, s=space(s));
		}
		
		
		/*
		 * Draws and centers a string on the game screen at the specified y-position
		 */
		private void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
			g.drawString(str, (winSize.width - fm.stringWidth(str))/2, ypos);
		}
		
		/*
		 * Draws a string on the left side of the game screen at the specified y-position
		 */
		private void rightString(Graphics g, FontMetrics fm, String str, int ypos) {
			g.drawString(str, (int)(winSize.width*.80), ypos);
		}
		
		/*
		 * Allows for easy formatting of string in a vertical column
		 */
		private int space(int s) {
			s += 20;
			return s;
		}
		
		/*
		 * Draws game banner at the start of a new game
		 */
		public void drawBanner(Graphics g) {
			g.setFont(largefont);
			FontMetrics fm = g.getFontMetrics();
			g.setColor(Color.YELLOW);
			g.drawImage(title, (winSize.width-title.getWidth(null))/2, 50, null);
			g.setFont(scorefont);
			fm = g.getFontMetrics();
			centerString(g, fm, "by Ben Homer", 200);
			g.setFont(smallfont);
			fm = g.getFontMetrics();

			if(state == GAMEINFO)
				openGameInfo(g, fm, 400);

			else if(state == CHECK)
				openCheck(g, fm);

			else {
				centerString(g, fm, "Press ENTER to select", 260);

				if (state == MAINMENU) {
					openMainMenu(g, fm, 400);
				}
				else if (state == OPTMENU) {
					openOptionsMenu(g, fm, 400);
				}
			}

		}
		
		/*
		 * Draws game main menu
		 */
		public void openMainMenu(Graphics g, FontMetrics fm, int l) {
		
			if (menustate == PLAY) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Play", l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Play", l=space(l));
			}
			
			if (menustate == OPTIONS) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Options", l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Options", l=space(l));
			}
			
			if (menustate == EXIT) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Exit", l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Exit", l=space(l));
			}
			
		}
		
		/*
		 * Draws game options menu
		 */
		public void openOptionsMenu(Graphics g, FontMetrics fm, int l) {
			String s, d, v, db;
			if (sound) s = "ON";
			else s = "OFF";
			
			switch(difficulty) {
			case 0: d = "EASY"; break;
			case 2: d = "HARD"; break;
			default: d = "MEDIUM";
			}
			
			if (ghostView) v = "ON";
			else v = "OFF";
			
			if (debug) db = "ON";
			else db = "OFF";
			
			if (optstate == OINFO) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Game Info", l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Game Info", l=space(l));
			}
			
			if (optstate == OSOUND) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Sound  " + s , l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Sound  " + s , l=space(l));
			}
			
			if (optstate == ODIFFICULTY) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Difficulty  " + d , l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Difficulty  " + d , l=space(l));
			}
			
			if (optstate == OGHOSTVIEW) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Ghost view " + v, l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Ghost view  " + v, l=space(l));
			}
			
			if (optstate == ODEBUG) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm, "Debug Graphics  " + db, l=space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Debug Graphics  " + db, l=space(l));
			}
			
			
			if (optstate == OEXIT) {
				g.setColor(Color.YELLOW);
				g.fill3DRect((winSize.width-300)/2, l=space(l)-15, 300, 25, true);
				g.setColor(Color.BLACK);
				centerString(g, fm,"Back", space(l));
			}
			else {
				g.setColor(Color.YELLOW);
				centerString(g, fm, "Back", space(l));
			}

		}
		
		
		/*
		 * Draws game info
		 */
		public void openGameInfo(Graphics g, FontMetrics fm, int s) {
			centerString(g, fm, "An emulation of an old classic with a few extras", s);
			centerString(g, fm, "Go to the options menu to adjust game settings", s=space(s));
			centerString(g, fm, "Please send any questions or comments to bensblogx@gmail.com", s=space(s));
			centerString(g, fm, "Controls:", s=space(s)+20);
			centerString(g, fm, "Move pacman: Arrow keys", s=space(s));
			centerString(g, fm, "Pause: P", s=space(s));
			centerString(g, fm, "Press BACKSPACE at any time for game stats", s=space(s)+20);
			centerString(g, fm, "Press ENTER to go back", s=space(s)+20);
		}

		/*
		 * Draws checker screen (checks if user want to exit)
		 */
		public void openCheck(Graphics g, FontMetrics fm) {
			centerString(g, fm, "Are you sure?", 500);
			centerString(g, fm, "Y/N", 550);
		}
		

		/*
		 * Draws players score, next block to fall, number of lines and level
		 */
		public void drawIngameScores(Graphics g) {
			g.setFont(scorefont);
			g.setColor(Color.YELLOW);
			FontMetrics fm = g.getFontMetrics();
			String pscore = Integer.toString(score);
			g.setColor(Color.YELLOW);
			hudString(g, fm, "Score", 250);
			hudString(g, fm, pscore, 300);
			hudString(g, fm, "Lives", 400);
			drawLives(g, 450);
					
		}
		
		private void hudString(Graphics g, FontMetrics fm, String str, int ypos) {
			g.drawString(str, (winSize.width - fm.stringWidth(str))/16, ypos);
		}
		
		private void drawLives(Graphics g, int ypos) {
			for (int i = 0; i < lives; i++)
				g.drawImage(heart, 80+i*30, ypos,null);
		}
		
		/*
		 * Moves main menu background south-east as far as possible then moves it north-west back towards start
		 */
		private void moveBackground() {
			if(menuDown) {
				if(menuBkInc > -550)
					menuBkInc--;
				else menuDown = false;
			}
			else {
				if(menuBkInc < 0)
					menuBkInc++;
				else
					menuDown = true;
			}
			
		}
		
		/*
		 * Updates graphics every step
		 */
		public void update(Graphics realg) {
			Graphics g = dbimage.getGraphics();
			g.setColor(getBackground());
			g.fillRect(0, 0, winSize.width, winSize.height);
			
			if (state == GAMEOVER) {
				g.drawImage(gameBackground, (winSize.width-gameBackground.getWidth(null))/2, (winSize.height-gameBackground.getHeight(null))/2, null);
				drawIngameScores(g);
				g.setFont(largefont);
				g.setColor(Color.RED);
				if(youWin)
					centerString(g, g.getFontMetrics(),"LEVEL COMPLETE" , 400);
				else
					centerString(g, g.getFontMetrics(),"GAMEOVER" , 400);
				g.setFont(scorefont);
				centerString(g, g.getFontMetrics(),"Play again:" , 450);
				centerString(g, g.getFontMetrics(),"Y/N" , 490);
			}
			
			else if (state != PLAYING) {
				
				g.drawImage(menuBackground, menuBkInc, menuBkInc, null);
				moveBackground();
				g.setColor(getForeground());
				drawBanner(g);
			}

			// re-draw game screen
			else {
				g.drawImage(gameBackground, (winSize.width-gameBackground.getWidth(null))/2, (winSize.height-gameBackground.getHeight(null))/2, null);
				drawIngameScores(g);
				

				level.draw(g);
				pacman.draw(g);
				for (int i = 0; i < enemies.length; i++)
					enemies[i].draw(g);

				if(countDownTimer > 0) {
					Enemy.setWait(true);
					Pacman.setWait(true);
					countDown(g);
				}
				else {
					Enemy.setWait(false);
					Pacman.setWait(false);
				}
				
				if (paused) {
					g.setColor(Color.YELLOW);
					centerString(g, g.getFontMetrics(),"PAUSED" , 600);
				}
			}
			
			if (showStats)
				displayStats(g, 40);

			realg.drawImage(dbimage, 0, 0, this);
		}
		
		/*
		 * Draws countdown timer at start of a new game
		 */
		private void countDown(Graphics g) {
			if(countDownTimer>40)
			centerString(g,g.getFontMetrics(),"3", 485);
			else if(countDownTimer>20)
				centerString(g,g.getFontMetrics(),"2", 485);
			else if(countDownTimer>0)
				centerString(g,g.getFontMetrics(),"1", 485);
			countDownTimer--;
		}

		/*
		 * Starts a game thread
		 */
		public void start() {
			if(engine == null) {
				engine = new Thread(this);
				engine.start();
			}
		}
		
		/*
		 * Stops the current game thread
		 */
		@SuppressWarnings("deprecation")
		public void stop() {
			if (engine != null && engine.isAlive())
				engine.stop();
			engine = null;
		}


		/*
		 * Make thread sleep so that pause works correctly
		 */
		private void sleep() {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * Allows user to use keyboard as game controller
		*/
		KeyListener keyListener = new KeyAdapter() {
			
		@SuppressWarnings("deprecation")
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			
			if (key == KeyEvent.VK_BACK_SPACE) {
				if (showStats) showStats = false;
				else showStats = true;
			}
			
			switch(state) {
				case PLAYING:
					if (key == KeyEvent.VK_P) {
						if (paused) { paused = false; sleep(); engine.resume(); }
						else { paused = true;  sleep(); engine.suspend(); }
					}
					switch(key) {  
					case KeyEvent.VK_LEFT:
						if(pacman.direction != Pacman.dir.EAST)
							pacman.addToKeyBuffer(Pacman.dir.WEST);
						if (pacman.cangoLeft)
							pacman.setDirection(Pacman.dir.WEST);
						break;
					case KeyEvent.VK_RIGHT:
						if(pacman.direction != Pacman.dir.WEST)
							pacman.addToKeyBuffer(Pacman.dir.EAST);
						if (pacman.cangoRight)
							pacman.setDirection(Pacman.dir.EAST);
						break;
					case KeyEvent.VK_UP:
						if(pacman.direction != Pacman.dir.SOUTH)
							pacman.addToKeyBuffer(Pacman.dir.NORTH);
						if (pacman.cangoUp)
							pacman.setDirection(Pacman.dir.NORTH);
						break;
					case KeyEvent.VK_DOWN:
						if(pacman.direction != Pacman.dir.NORTH)
							pacman.addToKeyBuffer(Pacman.dir.SOUTH);
						if (pacman.cangoDown)
							pacman.setDirection(Pacman.dir.SOUTH);
						break;
					case KeyEvent.VK_ESCAPE:
						prevState = PLAYING;
						state = CHECK;
						break;
					}
					
					//currBlock.update();
				break;

				
				case MAINMENU:
					switch(key) {
					case KeyEvent.VK_UP:
						SoundManager.MENUCLICK.play();
						menustate = Math.abs((menustate-1+3)%3);
						break;
					case KeyEvent.VK_DOWN:
						SoundManager.MENUCLICK.play();
						menustate = (menustate+1)%3;
						break;
					case KeyEvent.VK_ENTER:
						SoundManager.MENUCLICK.play();
						switch(menustate) {
						case PLAY:
							startNewGame();
							state = PLAYING;
							break;
						case OPTIONS:
							state = OPTMENU;
							break;
						case EXIT:
							prevState = MAINMENU;
							state = CHECK;
							break;
						}
						break;
					case KeyEvent.VK_ESCAPE:
						SoundManager.MENUCLICK.play();
						prevState = MAINMENU;
						state = CHECK;
						break;
					}
				break;
				
					
				case OPTMENU:
					switch(key) {
					case KeyEvent.VK_UP:
						SoundManager.MENUCLICK.play();
						optstate = Math.abs((optstate-1+6)%6);
						break;
					case KeyEvent.VK_DOWN:
						SoundManager.MENUCLICK.play();
						optstate = (optstate+1)%6;
						break;
					case KeyEvent.VK_ENTER:
						SoundManager.MENUCLICK.play();
						switch(optstate) {
						case OINFO:
							state = GAMEINFO;
							break;
						case OSOUND:
							SoundManager.unmute();
							SoundManager.MENUCLICK.play();
							// toggle sound ON/OFF
							if (sound) {
								sound = false;
								SoundManager.mute();
							}
							else {
								sound = true;
								SoundManager.unmute();
							}
							break;
						case ODIFFICULTY:
							// toggle difficulty
							difficulty = (difficulty+1)%3;

							switch(difficulty) {
							case 0: pause = easyPause; break; //ball.setSpeed(1);
							case 1: pause = medPause; break;
							case 2: pause = hardPause; break;
							}
							break;
						case OGHOSTVIEW:
							// toggle ghost view ON/OFF
							if (ghostView) {
								ghostView = false;
								Enemy.showView(false);
							}
							else {
								ghostView = true;
								Enemy.showView(true);
							}
							break;
						case ODEBUG:
							// toggle debugging graphics ON/OFF
							if (debug) {
								debug = false;
								Pacman.showDebug(false);
								Enemy.showDebug(false);
								Level.showDebug(false);
							}
							else {
								debug = true;
								Pacman.showDebug(true);
								Enemy.showDebug(true);
								Level.showDebug(true);
							}
							break;
						case OEXIT:
							state = MAINMENU;
							break;
						}
						break;
					case KeyEvent.VK_ESCAPE:
						SoundManager.MENUCLICK.play();
						state = MAINMENU;
					}
						break;

						
				case GAMEINFO:
					switch(key) {
						case Event.ENTER:
							SoundManager.MENUCLICK.play();
							state = OPTMENU;
							break;
					}
				break;
				
				case CHECK:
					switch(key) {
						case 'Y': case 'y':
							SoundManager.MENUCLICK.play();
							if(prevState == PLAYING) {
								state = MAINMENU;
							}
							else {
								System.exit(0);
							}
							break;
						case 'N': case 'n':
							SoundManager.MENUCLICK.play();
							state = prevState;
							break;
					}
				break;
				
				case GAMEOVER:
					switch(key) {
						case 'Y': case 'y':
							SoundManager.MENUCLICK.play();
							startNewGame();
							state = PLAYING;
							break;
						case 'N': case 'n':
							SoundManager.MENUCLICK.play();
							state = MAINMENU;
							break;
					}
				break;
				}
			}
		};
	}


		



