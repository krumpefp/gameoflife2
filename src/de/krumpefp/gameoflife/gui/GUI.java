package de.krumpefp.gameoflife.gui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.krumpefp.gameoflife.World;

public class GUI {
	
	private static final int speed = 10;
	private static final int refresh = 5;
	private static final int cellSize = 10;

	public static void main(String[] args) {

		World frame = new World(cellSize, refresh);
		frame.setSize(1920, 1080);
		frame.setTitle("Game of life 2");
		float[] hsb = Color.RGBtoHSB(0, 0, 0, null);
		frame.setBackground(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));

		// by default, an AWT Frame doesn't do anything when you click
		// the close button; this bit of code will terminate the program when
		// the window is asked to close
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		frame.setVisible(true);
		
		while (true) {
			frame.nextRound();
			
			try {
				Thread.sleep(1000 / GUI.speed);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
