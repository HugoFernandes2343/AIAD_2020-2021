package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class PlayerUi extends JPanel{

	private Player player;

	JLabel lblPlayerNumber;
	static int totalPlayers = 0; // we might need this number later on


	public PlayerUi(int xCoord, int yCoord, int width, int height) {
		setBorder(new LineBorder(new Color(0, 0, 0)));
		setBounds(xCoord, yCoord, 20, 20);
		this.setLayout(null);
	}

	public PlayerUi(Player player, Color color) {
		this.player = player;
		int playerNumber = this.player.getPlayerNumber();
		this.setBackground(color);
		lblPlayerNumber = new JLabel(""+playerNumber);
		lblPlayerNumber.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblPlayerNumber.setForeground(Color.WHITE);
		this.add(lblPlayerNumber); 
		this.setBounds(playerNumber*20, 33, 20, 28); // need to fix here for adjustable player numbers
		totalPlayers++;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void withdrawFromWallet(int withdrawAmount) {
		//TODO add this to UI logs
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}


	int[] xLocationsOfPlayer1 = {15, 115, 215, 315, 415, 515,
			515, 515, 515, 515, 515,
			415, 315, 215, 115, 15,
			15, 15, 15, 15};

	int[] yLocationsOfPlayer1 = {33, 33, 33, 33, 33, 33,
			133, 233, 333, 433, 533,
			533, 533, 533, 533, 533,
			433, 333, 233, 133};
	
	int[] xLocationsOfPlayer2 = {40, 140, 240, 340, 440, 540,
			540, 540, 540, 540, 540,
			440, 340, 240, 140, 40,
			40, 40, 40, 40};

	int[] yLocationsOfPlayer2 = {33, 33, 33, 33, 33, 33,
			133, 233, 333, 433, 533,
			533, 533, 533, 533, 533,
			433, 333, 233, 133};

	int[] xLocationsOfPlayer3 = {65, 165, 265, 365, 465, 565,
			565, 565, 565, 565, 565,
			465, 365, 265, 165, 65,
			65, 65, 65, 65};

	int[] yLocationsOfPlayer3 = {33, 33, 33, 33, 33, 33,
			133, 233, 333, 433, 533,
			533, 533, 533, 533, 533,
			433, 333, 233, 133};

	int[] xLocationsOfPlayer4 = {90, 190, 290, 390, 490, 590,
			590, 590, 590, 590, 590,
			490, 390, 290, 190, 90,
			90, 90, 90, 90};

	int[] yLocationsOfPlayer4 = {33, 33, 33, 33, 33, 33,
			133, 233, 333, 433, 533,
			533, 533, 533, 533, 533,
			433, 333, 233, 133};

	public void move() {
		int targetSquare = player.getCurrentSquareNumber();
		if(MonopolyMain.nowPlaying == 0) {
			this.setLocation(xLocationsOfPlayer1[targetSquare], yLocationsOfPlayer1[targetSquare]);
		} else if(MonopolyMain.nowPlaying == 1) {
			this.setLocation(xLocationsOfPlayer2[targetSquare], yLocationsOfPlayer2[targetSquare]);
		} else if(MonopolyMain.nowPlaying == 2) {
			this.setLocation(xLocationsOfPlayer3[targetSquare], yLocationsOfPlayer3[targetSquare]);
		} else if(MonopolyMain.nowPlaying == 3) {
			this.setLocation(xLocationsOfPlayer4[targetSquare], yLocationsOfPlayer4[targetSquare]);
		}


		if(this.player.ledger.containsKey(targetSquare)) {
			MonopolyMain.infoConsole.setText("This property belongs to player "+this.player.ledger.get(targetSquare));
		}
		//ledger.put(this.getCurrentSquareNumber(), this.getPlayerNumber());
	}


}
