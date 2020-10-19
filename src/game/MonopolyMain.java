package game;

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
	ArrayList<PlayerUi> playerUis = new ArrayList<PlayerUi>();
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
	PlayerUi player1Ui;
	PlayerUi player2Ui;
	PlayerUi player3Ui;
	PlayerUi player4Ui;
	GameManager gameManager;
	JPanel panelPlayer;
	JLabel panelPlayerTitle;
	Boolean doubleDiceForPlayer1 = false;
	Boolean doubleDiceForPlayer2 = false;
	Boolean doubleDiceForPlayer3 = false;
	static int nowPlaying = 0;

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

		player1 = new Player(1);
		players.add(player1);
		player1Ui = new PlayerUi(player1,Color.RED);
		playerUis.add(player1Ui);
		layeredPane.add(player1Ui, new Integer(2));

		player2 = new Player(2);
		players.add(player2);
		player2Ui = new PlayerUi(player2,Color.BLUE);
		playerUis.add(player2Ui);
		layeredPane.add(player2Ui, new Integer(2));

		player3 = new Player(3);
		players.add(player3);
		player3Ui = new PlayerUi(player3, Color.YELLOW);
		playerUis.add(player3Ui);
		layeredPane.add(player3Ui, new Integer(2));

		/* player4 = new Player(4);
		players.add(player4);
		player4Ui = new PlayerUi(player3, Color.GREEN);
		playerUis.add(player4Ui);
		layeredPane.add(player4Ui, new Integer(2));**/

		gameManager = new GameManager();

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
		panelPlayerTitle = new JLabel("PlayerUi 1 All Wealth");
		panelPlayerTitle.setForeground(Color.WHITE);
		panelPlayerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		panelPlayerTitle.setBounds(0, 6, 240, 16);
		panelPlayer.add(panelPlayerTitle);
		panelPlayer.setLayout(null);
		panelPlayerTextArea = new JTextArea();
		panelPlayerTextArea.setBounds(10, 34, 230, 149);
		panelPlayer.add(panelPlayerTextArea);
		playerAssetsPanel.add(panelPlayer, "1");

		updatePanelPlayerTextArea(player1, gameBoard, panelPlayerTextArea);

		infoConsole = new JTextArea();
		infoConsole.setColumns(20);
		infoConsole.setRows(5);
		infoConsole.setBounds(6, 6, 234, 56);
		test.add(infoConsole);
		infoConsole.setLineWrap(true);
		infoConsole.setText("PlayerUi 1 starts the game by clicking Roll Dice!");

		btnBuy = new JButton("Buy");
		btnBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//turnCounter--; // decrease because we increased at the end of the rolldice
				Player currentPlayer = players.get(nowPlaying);
				infoConsole.setText("You bought "+gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName() +
						" for " + gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getPrice() + " euros");
				currentPlayer.buyTitleDeed(currentPlayer.getCurrentSquareNumber());
				gameManager.setPropertyOwnership(gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName(), currentPlayer);
				int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getPrice();
				currentPlayer.withdrawFromWallet(withdrawAmount);
				btnBuy.setEnabled(false);
				updatePanelPlayerTextArea(currentPlayer, gameBoard, panelPlayerTextArea);

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
				Player ownerOfTheSquare = gameManager.getPropertyOwnership(gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName());
				int withdrawAmount = gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getRentPrice();
				infoConsole.setText("You paid " + withdrawAmount + " euros to PlayerUi "+ ownerOfTheSquare.getPlayerNumber());
				System.out.println(withdrawAmount);
				currentPlayer.withdrawFromWallet(withdrawAmount);
				ownerOfTheSquare.depositToWallet(withdrawAmount);
				updatePanelPlayerTextArea(currentPlayer, gameBoard, panelPlayerTextArea);
				btnNextTurn.setEnabled(true);
				btnPayRent.setEnabled(false);
				//currentPlayer.withdrawFromWallet(withdrawAmount);
				//turnCounter++;
				gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).setRentPaid(true);
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
					doubleDiceForPlayer1 = makePlay(player1, player1Ui, dice1, dice2);
					if(doubleDiceForPlayer1) {
						changeConsoleMessage("Double Dice Roll Have Another Turn PlayerUi 1", infoConsole);
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 2 To Roll Dice", infoConsole);
					}

				} else if (nowPlaying == 1){
					// player2's turn
					doubleDiceForPlayer2 = makePlay(player2, player2Ui, dice1, dice2);
					if(doubleDiceForPlayer2) {
						changeConsoleMessage("Double Dice Roll Have Another Turn PlayerUi 2", infoConsole);
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 3 To Roll Dice", infoConsole);
					}
				} else if (nowPlaying == 2) {
					// player3's turn
					doubleDiceForPlayer3 = makePlay(player3, player3Ui, dice1, dice2);
					if(doubleDiceForPlayer3) {
						changeConsoleMessage("Double Dice Roll Have Another Turn PlayerUi 3", infoConsole);
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 1 To Roll Dice", infoConsole);
					}
				}

				btnRollDice.setEnabled(false);



				// we have to add below 2 lines to avoid some GUI breakdowns.
				layeredPane.remove(gameBoard);
				layeredPane.add(gameBoard, new Integer(0));

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
							updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.BLUE, playerAssetsPanel, "2", "PlayerUi 2 Information", panelPlayerTextArea);
						}
						updatePanelPlayerTextArea(player2, gameBoard, panelPlayerTextArea);
					}
					else if(nowPlaying == 1){
						if(doubleDiceForPlayer2) {
							nowPlaying = 1;
						} else {
							nowPlaying = 2;
							changeConsoleMessage("It's now player 3's turn", infoConsole);
							updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.YELLOW, playerAssetsPanel,"3", "PlayerUi 3 Information", panelPlayerTextArea);
						}
						updatePanelPlayerTextArea(player3, gameBoard, panelPlayerTextArea);
					}
					else if(nowPlaying == 2) {
						if(doubleDiceForPlayer3) {
							nowPlaying = 2;
						} else {
							nowPlaying = 0;
							changeConsoleMessage("It's now player 1's turn", infoConsole);
							updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.RED, playerAssetsPanel, "1", "PlayerUi 1 Information", panelPlayerTextArea);
						}
						updatePanelPlayerTextArea(player1, gameBoard, panelPlayerTextArea);
					}
			}
		});
		
		btnNextTurn.setBounds(81, 519, 246, 53);
		rightPanel.add(btnNextTurn);
		btnNextTurn.setEnabled(false);
	}

	public void updatePanelPlayerTextArea(Player player, Board gameBoard, JTextArea panelPlayerTextArea) {
		// TODO Auto-generated method stub
		String result = "Player " + String.valueOf(player.getPlayerNumber()) + " Now Playing" + "\n";
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

	private boolean makePlay(Player player, PlayerUi playerUi ,Dice dice1, Dice dice2) {
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
		playerUi.move();

		if(player.ledger.containsKey(player.getCurrentSquareNumber()) // if bought by someone
				&& player.ledger.get(player.getCurrentSquareNumber()) != player.getPlayerNumber() // not by itself
		) {
			btnBuy.setEnabled(false);
			btnRollDice.setEnabled(false);
			btnNextTurn.setEnabled(false);
			btnPayRent.setEnabled(true);
		}
		if (player.ledger.containsKey(player.getCurrentSquareNumber()) // if bought by someone
				&& player.ledger.get(player.getCurrentSquareNumber()) == player.getPlayerNumber()) { // and by itself
			btnBuy.setEnabled(false);
			btnPayRent.setEnabled(false);
			btnNextTurn.setEnabled(true);
		}
		if(gameBoard.getUnbuyableSquares().contains(gameBoard.getAllSquares().get(player.getCurrentSquareNumber()))) {
			btnBuy.setEnabled(false);
			btnNextTurn.setEnabled(true);
		} else if (!player.ledger.containsKey(player.getCurrentSquareNumber())) { // if not bought by someone
			btnBuy.setEnabled(true);
			btnNextTurn.setEnabled(true);
			btnPayRent.setEnabled(false);
		}
		return doubleDiceForCurrentPlayer;
	}

}