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
		this.setBounds(playerNumber*30, 33, 20, 28); // need to fix here for adjustable player numbers
		totalPlayers++;
	}

	public void withdrawFromWallet(int withdrawAmount) {
		//TODO add this to UI logs
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}


	int[] xLocationsOfPlayer1 = {31, 131, 231, 331, 431, 531,
			531, 531, 531, 531, 531,
			431, 331, 231, 131, 31,
			31, 31, 31, 31};

	int[] yLocationsOfPlayer1 = {33, 33, 33, 33, 33, 33,
			133, 233, 333, 433, 533,
			533, 533, 533, 533, 533,
			433, 333, 233, 133};
	
	int[] xLocationsOfPlayer2 = {61, 191, 291, 361, 461, 561,
			561, 561, 561, 561, 561,
			461, 361, 261, 161, 61,
			61, 61, 61, 61};

	int[] yLocationsOfPlayer2 = {33, 33, 33, 33, 33, 33,
			133, 233, 333, 433, 533,
			533, 533, 533, 533, 533,
			433, 333, 233, 133};

	//TODO Add locations of players 3 e 4

	public void move() {
		int targetSquare = player.getCurrentSquareNumber();
		if(MonopolyMain.nowPlaying == 0) {
			this.setLocation(xLocationsOfPlayer1[targetSquare], yLocationsOfPlayer1[targetSquare]);
		} else {
			this.setLocation(xLocationsOfPlayer2[targetSquare], yLocationsOfPlayer2[targetSquare]);
		}

		if(this.player.ledger.containsKey(targetSquare)) {
			MonopolyMain.infoConsole.setText("This property belongs to player "+this.player.ledger.get(targetSquare));
		}
		//ledger.put(this.getCurrentSquareNumber(), this.getPlayerNumber());
	}


}
