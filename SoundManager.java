import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

	/*
	 * SoundManager.java
	 * Manages game sound effects and background music
	 */

	public enum SoundManager {
		SONAR("sonar1.wav"),
		INTRO("pacmanIntro.wav"),
		MENUCLICK("menuSelect.wav"),
		DEATH("pacmanDeath.wav"),	
		EATGHOST("pacmanEatGhost.wav"),
		BACK1("back1.wav"),
		BACK2("back2.wav"),
		BACK3("back3.wav");

		private static int volume = 3;
		private Clip clip;
		
		 // Constructor to construct each element of the enum with its own sound file.
		   SoundManager(String soundFileName) {
		      try {
		         // Use URL (instead of File) to read from disk and JAR.
		         URL url = this.getClass().getClassLoader().getResource(soundFileName);
		         // Set up an audio input stream piped from the sound file.
		         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
		         // Get a clip resource.
		         clip = AudioSystem.getClip();
		         // Open audio clip and load samples from the audio input stream.
		         clip.open(audioInputStream);
		      } catch (UnsupportedAudioFileException e) {
		         e.printStackTrace();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (LineUnavailableException e) {
		       e.printStackTrace();
		      }
		   }

		public void play() {
			if (volume != 0) {
				if (clip.isRunning())
					return;//clip.stop();   // If it is still running continue and don't play new sound
				clip.setFramePosition(0); // rewind to the beginning
				clip.start();     // Start playing
			}
		}

		public void startLoop() {
			if (volume != 0) {
				if (clip.isRunning())
					return;//clip.stop();   // If it is still running continue and don't play new sound
				clip.setFramePosition(0); // rewind to the beginning
				clip.start();     // Start playing
//					while(clip.isActive()) {
//						//wait
//					}
//					startLoop();
				
			}
		}
		
		public static SoundManager selectRandomBackgroundTrack() {
			int track = (int) Math.round(Math.random()*3);
			switch(track) {
			case 0: return BACK1;
			case 1: return BACK2;
			case 2: return BACK3;
			default: return BACK1;
			
			}
		}
		public void stopLoop() {
				if (clip.isRunning())
					clip.stop();
		}
		
		public static void setVolume(int volume) {
			SoundManager.volume = volume;
		}
		
		public static void mute() {
			volume = 0;
		}
		
		public static void unmute() {
			volume = 3;
		}

		// Optional static method to pre-load all the sound files.
		static void init() {
			values(); // calls the constructor for all the elements
		}
	}

