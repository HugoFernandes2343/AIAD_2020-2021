package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Square extends JPanel {

	int number;
	private String name;
	String description;
	JLabel nameLabel;
	static int totalSquares = 0;
	private int price;
	private int rentPrice;
	
	public void setRentPrice(int rentPrice) {
		this.rentPrice = rentPrice;
	}
	
	public int getRentPrice() {
		return rentPrice;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getPrice() {
		return price;
	}
	
	public String getName() {
		return name;
	}
	
	
	public Square(int xCoord, int yCoord, int width, int height, String labelString, int rotationDegrees) {
		number = totalSquares;
		totalSquares++;
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(xCoord, yCoord, width, height);
		name = labelString;
		this.setLayout(null);

		nameLabel = new JLabel(labelString);
		nameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel.setBounds(0,0,this.getWidth(),this.getHeight());
		this.add(nameLabel);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(this.number == 1 || this.number == 3 || this.number == 4) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.PINK);
			g.fillRect(0, 0, this.getWidth(), 20);
		}
		if(this.number == 5 || this.number == 7 || this.number == 8) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.CYAN);
			g.fillRect(0, 0, this.getWidth(), 20);
		}


		if(this.number == 10 || this.number == 12 || this.number == 13) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.MAGENTA);
			g.fillRect(0, 0, this.getWidth(), 20);
		}
		if(this.number == 14 || this.number == 16 || this.number == 17) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.ORANGE);
			g.fillRect(0, 0, this.getWidth(), 20);
		}

		if(this.number == 19 || this.number == 21 || this.number == 22) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.RED);
			g.fillRect(0, 0, this.getWidth(), 20);
		}
		if(this.number == 23 || this.number == 24 || this.number == 26) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.YELLOW);
			g.fillRect(0, 0, this.getWidth(), 20);
		}

		if(this.number == 28 || this.number == 29 || this.number == 31) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, this.getWidth(), 20);
		}
		if(this.number == 32 || this.number == 33 || this.number == 35) {
			g.drawRect(0, 0, this.getWidth(), 20);
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, this.getWidth(), 20);
		}

	}

	private boolean isRentPaid = false;
	public boolean isRentPaid() {
		return isRentPaid;
	}
	public void setRentPaid(boolean pay) {
		isRentPaid = pay;
	}

}
