package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Dice extends JPanel {
	
	Random rnd = new Random();
	int faceValue = 1;
	
	public Dice(int xCoord, int yCoord, int width, int height) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(xCoord, yCoord, width, height);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(faceValue == 1) {
			g.fillOval(getWidth()/2 - 10/2, getHeight()/2 - 10/2, 10, 10);
		} else if(faceValue == 2) {
			g.fillOval(getWidth()/2 - 20, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 - 20,  10, 10);
		} else if(faceValue == 3) {
			g.fillOval(getWidth()/2 - 20, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 - 10/2, getHeight()/2 - 10/2,  10, 10);
		} else if(faceValue == 4) {
			g.fillOval(getWidth()/2 - 20, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 - 20, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 + 15,  10, 10);
		} else if(faceValue == 5) {
			g.fillOval(getWidth()/2 - 20, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 - 20, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 - 10/2, getHeight()/2 - 10/2,  10, 10);
		} else {
			g.fillOval(getWidth()/2 - 20, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 - 20, getHeight()/2 - 20,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 + 15,  10, 10);
			g.fillOval(getWidth()/2 - 20, getHeight()/2 - 10/2,  10, 10);
			g.fillOval(getWidth()/2 + 15, getHeight()/2 - 10/2,  10, 10);
		}
		
	}
	

	public void rollDice(){
		faceValue = rnd.nextInt(6) + 1;
		repaint();
	}
	
	public int getFaceValue(){
		return faceValue;
	}
	
	public Dice(int xCoord, int yCoord, int width, int height, String labelString) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(xCoord, yCoord, width, height);
		
	}
	
	

}
