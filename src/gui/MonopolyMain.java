package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MonopolyMain extends JFrame{

	private JPanel contentIncluder;
	static JTextArea infoConsole;
	JPanel playerAssetsPanel;
	CardLayout c1 = new CardLayout();
	ArrayList<Player> players = new ArrayList<Player>();
	static int turnCounter = 0;
	JButton btnNextTurn;
	JButton btnRollDice;
	JButton btnPayRent;
	JButton btnBuy;
	JTextArea panelPlayerTextArea;
	Board gameBoard;
	Player player1;
	Player player2;
	Player player3;
	Player player4;
	JPanel panelPlayer;
	JLabel panelPlayerTitle;
	Boolean doubleDiceForPlayer1 = false;
	Boolean doubleDiceForPlayer2 = false;
	Boolean doubleDiceForPlayer3 = false;
	static int nowPlaying = 0;

	private boolean makePlay(Player player, Dice dice1, Dice dice2) {
		boolean doubleDiceForCurrentPlayer = false;
		int dice1OldValue = dice1.getFaceValue();
		int dice2OldValue = dice2.getFaceValue();
		dice1.rollDice();
		dice2.rollDice();
		int dicesTotal = dice1.getFaceValue() + dice2.getFaceValue();
		if(dice1.getFaceValue() == dice2.getFaceValue()) {
			doubleDiceForCurrentPlayer = true;
		}
		player.move(dicesTotal);
		if(Player.ledger.containsKey(player.getCurrentSquareNumber()) // if bought by someone
				&& Player.ledger.get(player.getCurrentSquareNumber()) != player.getPlayerNumber() // not by itself
		) {
			btnBuy.setEnabled(false);
			btnRollDice.setEnabled(false);
			btnNextTurn.setEnabled(false);
			btnPayRent.setEnabled(true);
		}
		if (Player.ledger.containsKey(player.getCurrentSquareNumber()) // if bought by someone
				&& Player.ledger.get(player.getCurrentSquareNumber()) == player.getPlayerNumber()) { // and by itself
			btnBuy.setEnabled(false);
			btnPayRent.setEnabled(false);
			btnNextTurn.setEnabled(true);
		}
		if(gameBoard.getUnbuyableSquares().contains(gameBoard.getAllSquares().get(player.getCurrentSquareNumber()))) {
			btnBuy.setEnabled(false);
			btnNextTurn.setEnabled(true);
		} else if (!Player.ledger.containsKey(player.getCurrentSquareNumber())) { // if not bought by someone
			btnBuy.setEnabled(true);
			btnNextTurn.setEnabled(true);
			btnPayRent.setEnabled(false);
		}
		return doubleDiceForCurrentPlayer;
	}
	
	public MonopolyMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1080,720);
		contentIncluder = new JPanel();
		contentIncluder.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentIncluder);
		contentIncluder.setLayout(null);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		layeredPane.setBounds(6, 6, 632, 630);
		contentIncluder.add(layeredPane);

		gameBoard = new Board(6,6,612,612);
		gameBoard.setBackground(new Color(51, 255, 153));
		layeredPane.add(gameBoard, new Integer(0));

		player1 = new Player(1, Color.RED);
		players.add(player1);
		layeredPane.add(player1, new Integer(2));

		player2 = new Player(2, Color.BLUE);
		players.add(player2);
		layeredPane.add(player2, new Integer(2));

		player3 = new Player(3, Color.YELLOW);
		players.add(player3);
		layeredPane.add(player3, new Integer(2));

		//player4 = new Player(4, Color.GREEN);
		//players.add(player4);
		//layeredPane.add(player4, new Integer(2));

		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.LIGHT_GRAY);
		rightPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		rightPanel.setBounds(634, 6, 419, 600);
		contentIncluder.add(rightPanel);
		rightPanel.setLayout(null);

		JPanel test = new JPanel();
		test.setBounds(81, 312, 246, 68);
		rightPanel.add(test);
		test.setLayout(null);

		playerAssetsPanel = new JPanel();
		playerAssetsPanel.setBounds(81, 28, 246, 189);
		rightPanel.add(playerAssetsPanel);
		playerAssetsPanel.setLayout(c1);

		panelPlayer = new JPanel();
		panelPlayer.setBackground(Color.RED);
		panelPlayerTitle = new JLabel("Player 1 All Wealth");
		panelPlayerTitle.setForeground(Color.WHITE);
		panelPlayerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		panelPlayerTitle.setBounds(0, 6, 240, 16);
		panelPlayer.add(panelPlayerTitle);
		panelPlayer.setLayout(null);
		panelPlayerTextArea = new JTextArea();
		panelPlayerTextArea.setBounds(10, 34, 230, 149);
		panelPlayer.add(panelPlayerTextArea);
		playerAssetsPanel.add(panelPlayer, "1");

		updatePanelPlayerTextArea("1", player1, gameBoard, panelPlayerTextArea);

		infoConsole = new JTextArea();
		infoConsole.setColumns(20);
		infoConsole.setRows(5);
		infoConsole.setBounds(6, 6, 234, 56);
		test.add(infoConsole);
		infoConsole.setLineWrap(true);
		infoConsole.setText("Player 1 starts the game by clicking Roll Dice!");

		btnBuy = new JButton("Buy");
		btnBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//turnCounter--; // decrease because we increased at the end of the rolldice
				Player currentPlayer = players.get(nowPlaying);
				infoConsole.setText("You bought "+gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName());
				currentPlayer.buyTitleDeed(currentPlayer.getCurrentSquareNumber());
				int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getPrice();
				currentPlayer.withdrawFromWallet(withdrawAmount);
				btnBuy.setEnabled(false);
				if(currentPlayer.getPlayerNumber() == 1) {
					updatePanelPlayerTextArea("1", player1, gameBoard, panelPlayerTextArea);
				} else if(currentPlayer.getPlayerNumber() == 2) {
					updatePanelPlayerTextArea("2", player2, gameBoard, panelPlayerTextArea);
				} else {
					updatePanelPlayerTextArea("3", player3, gameBoard, panelPlayerTextArea);
				}
			}
		});
		btnBuy.setBounds(81, 478, 117, 29);
		rightPanel.add(btnBuy);
		btnBuy.setEnabled(false);

		btnPayRent = new JButton("Pay Rent");
		btnPayRent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// turnCounter--;
				Player currentPlayer = players.get(nowPlaying);
				Player ownerOfTheSquare = players.get((Player.ledger.get(currentPlayer.getCurrentSquareNumber()))==1?0:1);
				infoConsole.setText("You paid to the player "+ownerOfTheSquare.getPlayerNumber());

				int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getRentPrice();
				System.out.println(withdrawAmount);
				currentPlayer.withdrawFromWallet(withdrawAmount);
				ownerOfTheSquare.depositToWallet(withdrawAmount);
				
				btnNextTurn.setEnabled(true);
				btnPayRent.setEnabled(false);
				//currentPlayer.withdrawFromWallet(withdrawAmount);
				//updatePanelPlayer1TextArea();
				//updatePanelPlayer2TextArea();
				//updatePanelPlayer3TextArea();
				//turnCounter++;
				//gameBoard.getAllSquares().get(player1.getCurrentSquareNumber()).setRentPaid(true);
			}

		});
		btnPayRent.setBounds(210, 478, 117, 29);
		rightPanel.add(btnPayRent);
		btnPayRent.setEnabled(false);

		Dice dice1 = new Dice(244, 406, 40, 40);
		layeredPane.add(dice1, new Integer(1));

		Dice dice2 = new Dice(333, 406, 40, 40);
		layeredPane.add(dice2, new Integer(1));

		btnRollDice = new JButton("Roll Dice");
		btnRollDice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(nowPlaying == 0) {
					// player1's turn
					doubleDiceForPlayer1 = makePlay(player1, dice1, dice2);
					if(doubleDiceForPlayer1) {
						changeConsoleMessage("Double Dice Roll Have Another Turn Player 1", infoConsole);
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 2 To Roll Dice", infoConsole);
					}

				} else if (nowPlaying == 1){
					// player2's turn
					doubleDiceForPlayer2 = makePlay(player2, dice1, dice2);
					if(doubleDiceForPlayer2) {
						changeConsoleMessage("Double Dice Roll Have Another Turn Player 2", infoConsole);
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 3 To Roll Dice", infoConsole);
					}
				} else if (nowPlaying == 2) {
					// player3's turn
					doubleDiceForPlayer3 = makePlay(player3, dice1, dice2);
					if(doubleDiceForPlayer3) {
						changeConsoleMessage("Double Dice Roll Have Another Turn Player 3", infoConsole);
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 1 To Roll Dice", infoConsole);
					}
				}

				btnRollDice.setEnabled(false);



				// we have to add below 2 lines to avoid some GUI breakdowns.
				layeredPane.remove(gameBoard);
				layeredPane.add(gameBoard, new Integer(0));
				
				//updatePanelPlayer1TextArea();
				//updatePanelPlayer2TextArea();
				//updatePanelPlayer3TextArea();
			}
		});
		btnRollDice.setBounds(81, 413, 246, 53);
		rightPanel.add(btnRollDice);

		btnNextTurn = new JButton("Next Turn");
		btnNextTurn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				btnRollDice.setEnabled(true);
				btnBuy.setEnabled(false);
				btnPayRent.setEnabled(false);
				btnNextTurn.setEnabled(false);

				String text = "";
					if(nowPlaying == 0) {
						if(doubleDiceForPlayer1 ) {
							nowPlaying = 0;
						} else {
							nowPlaying = 1;
							changeConsoleMessage("It's now player 2's turn", infoConsole);
						}
						updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.BLUE, playerAssetsPanel, "2", "Player 2 Information", panelPlayerTextArea);
						updatePanelPlayerTextArea("2", player2, gameBoard, panelPlayerTextArea);
					}
					else if(nowPlaying == 1){
						if(doubleDiceForPlayer2) {
							nowPlaying = 1;
						} else {
							nowPlaying = 2;
							changeConsoleMessage("It's now player 3's turn", infoConsole);
						}
						updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.YELLOW, playerAssetsPanel,"3", "Player 3 Information", panelPlayerTextArea);
						updatePanelPlayerTextArea("3", player3, gameBoard, panelPlayerTextArea);
					}
					else if(nowPlaying == 2) {
						if(doubleDiceForPlayer3) {
							nowPlaying = 2;
						} else {
							nowPlaying = 0;
							changeConsoleMessage("It's now player 1's turn", infoConsole);
						}
						updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.RED, playerAssetsPanel, "1", "Player 1 Information", panelPlayerTextArea);
						updatePanelPlayerTextArea("1", player1, gameBoard, panelPlayerTextArea);
					}

				//c1.show(playerAssetsPanel, ""+ text); // maps 0 to 1 and 1 to 2
				//updatePanelPlayer1TextArea();
				//updatePanelPlayer2TextArea();
				//updatePanelPlayer3TextArea();

			}

		

		});
		
		btnNextTurn.setBounds(81, 519, 246, 53);
		rightPanel.add(btnNextTurn);
		btnNextTurn.setEnabled(false);
	}

	public void updatePanelPlayerTextArea(String playerNumber, Player player, Board gameBoard, JTextArea panelPlayerTextArea) {
		// TODO Auto-generated method stub
		String result = "Player " + playerNumber + " Now Playing" + "\n";
		result += "Current Balance: "+player.getWallet()+"\n";

		result += "Title Deeds: \n";
		for(int i = 0; i < player.getTitleDeeds().size(); i++) {
			result += " - "+gameBoard.getAllSquares().get(player.getTitleDeeds().get(i)).getName()+"\n";
		}

		panelPlayerTextArea.setText(result);
	}

	public void updatePlayerPanel(JPanel panelPlayer, JLabel panelPlayerTitle, Color color, JPanel playerAssetsPanel ,String constraintMessage, String labelMessage, JTextArea panelPlayerTextArea) {
		panelPlayer.setBackground(color);
		panelPlayer.setLayout(null);
		panelPlayerTitle.setText(labelMessage);
		panelPlayerTitle.setForeground(Color.WHITE);
		panelPlayerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		panelPlayerTitle.setBounds(0, 6, 240, 16);
		panelPlayer.add(panelPlayerTitle);
		//panelPlayerTextArea = new JTextArea();
		//panelPlayerTextArea.setBounds(10, 34, 230, 149);
		//panelPlayer.add(panelPlayerTextArea);
		playerAssetsPanel.add(panelPlayer, constraintMessage);
	}

	public static void changeConsoleMessage(String message, JTextArea console) {
		console.setText(message);
	}
	
	public static void errorBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.ERROR_MESSAGE);
	}


	public static void main(String[] args) {

		MonopolyMain frame = new MonopolyMain();
		frame.setVisible(true);

	}

}