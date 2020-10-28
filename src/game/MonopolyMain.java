package game;

import jade.core.Profile;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.StaleProxyException;
import utils.ColorHelper;

public class MonopolyMain extends JFrame{


	private Runtime runtimeInstance;
	private Profile profile;
	private ContainerController containerController;
	private JPanel contentIncluder;
	static JTextArea infoConsole;
	static JPanel playerAssetsPanel;
	CardLayout c1 = new CardLayout();
	static ArrayList<PlayerUi> playerUis = new ArrayList<PlayerUi>();
	ArrayList<Player> players = new ArrayList<Player>();
	static int turnCounter = 0;
	static JTextArea panelPlayerTextArea;
	static Board gameBoard;
	static Dice dice1;
	static Dice dice2;
	static JPanel panelPlayer;
	static JLabel panelPlayerTitle;
	static JLayeredPane layeredPane;
	static int nowPlaying = 0;

	private void createAgents() throws StaleProxyException {
		for(int i = 1; i < 5; i++) {
			String id = "player_" + i;
			Player player = new Player(i);
			players.add(player);
			PlayerUi playerUi = new PlayerUi(player, ColorHelper.getColor(i));
			playerUis.add(playerUi);
			AgentController agentController = this.containerController.acceptNewAgent(id, player);
			agentController.start();
		}
	}

	public MonopolyMain() throws StaleProxyException {
		this.runtimeInstance = Runtime.instance();
		this.profile = new ProfileImpl(true);
		this.containerController = runtimeInstance.createMainContainer(profile);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setSize(1080,720);
		contentIncluder = new JPanel();
		contentIncluder.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentIncluder);
		contentIncluder.setLayout(null);

		layeredPane = new JLayeredPane();
		layeredPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		layeredPane.setBounds(6, 6, 632, 630);
		contentIncluder.add(layeredPane);

		gameBoard = new Board(6,6,612,612);
		gameBoard.setBackground(new Color(51, 255, 153));
		layeredPane.add(gameBoard, new Integer(0));

		dice1 = new Dice(244, 406, 40, 40);
		layeredPane.add(dice1, new Integer(1));

		dice2 = new Dice(333, 406, 40, 40);
		layeredPane.add(dice2, new Integer(1));

		this.createAgents();

		for(PlayerUi playerUi : playerUis) {
			layeredPane.add(playerUi, new Integer(2));
		}

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

		updatePanelPlayerTextArea(players.get(0));

		infoConsole = new JTextArea();
		infoConsole.setColumns(20);
		infoConsole.setRows(5);
		infoConsole.setBounds(6, 6, 234, 56);
		test.add(infoConsole);
		infoConsole.setLineWrap(true);
		infoConsole.setText("Player 1 starts the game!");

		try {
			Thread.sleep(20000);
			this.players.get(0).move();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		/*btnBuy = new JButton("Buy");
		btnBuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//turnCounter--; // decrease because we increased at the end of the rolldice
				Player currentPlayer = players.get(nowPlaying);
				infoConsole.setText("You bought "+gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getName() +
						" for " + gameBoard.getAllSquares().get(currentPlayer.getCurrentSquareNumber()).getPrice() + " euros");
				currentPlayer.buyTitleDeed(currentPlayer.getCurrentSquareNumber());
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
				Player ownerOfTheSquare = null;
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

		btnRollDice = new JButton("Roll Dice");
		btnRollDice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(nowPlaying == 0) {
					// player1's turn
					doubleDiceForPlayer1 = makePlayUI(player1, player1Ui, dice1, dice2);
					if(doubleDiceForPlayer1) {
						changeConsoleMessage("Double Dice Roll Have Another Turn PlayerUi 1");
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 2 To Roll Dice");
					}
				} else if (nowPlaying == 1){
					// player2's turn
					doubleDiceForPlayer2 = makePlayUI(player2, player2Ui, dice1, dice2);
					if(doubleDiceForPlayer2) {
						changeConsoleMessage("Double Dice Roll Have Another Turn PlayerUi 2");
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 3 To Roll Dice");
					}
				} else if (nowPlaying == 2) {
					// player3's turn
					doubleDiceForPlayer3 = makePlayUI(player3, player3Ui, dice1, dice2);
					if(doubleDiceForPlayer3) {
						changeConsoleMessage("Double Dice Roll Have Another Turn PlayerUi 3");
					}
					else {
						changeConsoleMessage("Click Next Turn To Allow Player 1 To Roll Dice");
					}
				}

				btnRollDice.setEnabled(false);





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
							changeConsoleMessage("It's now player 2's turn");
							updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.BLUE, playerAssetsPanel, "2", "PlayerUi 2 Information", panelPlayerTextArea);
						}
						updatePanelPlayerTextArea(player2, gameBoard, panelPlayerTextArea);
					}
					else if(nowPlaying == 1){
						if(doubleDiceForPlayer2) {
							nowPlaying = 1;
						} else {
							nowPlaying = 2;
							changeConsoleMessage("It's now player 3's turn");
							updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.YELLOW, playerAssetsPanel,"3", "PlayerUi 3 Information", panelPlayerTextArea);
						}
						updatePanelPlayerTextArea(player3, gameBoard, panelPlayerTextArea);
					}
					else if(nowPlaying == 2) {
						if(doubleDiceForPlayer3) {
							nowPlaying = 2;
						} else {
							nowPlaying = 0;
							changeConsoleMessage("It's now player 1's turn");
							updatePlayerPanel(panelPlayer, panelPlayerTitle, Color.RED, playerAssetsPanel, "1", "PlayerUi 1 Information", panelPlayerTextArea);
						}
						updatePanelPlayerTextArea(player1, gameBoard, panelPlayerTextArea);
					}
			}
		});
		
		btnNextTurn.setBounds(81, 519, 246, 53);
		rightPanel.add(btnNextTurn);
		btnNextTurn.setEnabled(false);*/
	}

	public static void updatePanelPlayerTextArea(Player player) {
		// TODO Auto-generated method stub
		String result = "Player " + String.valueOf(player.getPlayerNumber()) + " Now Playing" + "\n";
		result += "Current Balance: "+player.getWallet()+"\n";

		result += "Title Deeds: \n";
		for(int i = 0; i < player.getTitleDeeds().size(); i++) {
			result += " - "+gameBoard.getAllSquares().get(player.getTitleDeeds().get(i)).getName()+"\n";
		}

		panelPlayerTextArea.setText(result);
	}

	public static void updatePlayerPanel(Color color, int playerNumber) {
		panelPlayer.setBackground(color);
		panelPlayer.setLayout(null);
		panelPlayerTitle.setText("Player " + playerNumber + " Information");
		panelPlayerTitle.setForeground(Color.WHITE);
		panelPlayerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		panelPlayerTitle.setBounds(0, 6, 240, 16);
		panelPlayer.add(panelPlayerTitle);
		playerAssetsPanel.add(panelPlayer, String.valueOf(playerNumber));
	}

	public static void changeConsoleMessage(String message) {
		infoConsole.setText(message);
	}
	
	public static void errorBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.ERROR_MESSAGE);
	}


	public static void main(String[] args) throws StaleProxyException {

		MonopolyMain frame = new MonopolyMain();
		frame.setVisible(true);

	}

	public static ArrayList<Integer> rollDiceUI() {
		ArrayList<Integer> res = new ArrayList<>();
		dice1.rollDice();
		dice2.rollDice();
		res.add(dice1.getFaceValue());
		res.add(dice2.getFaceValue());
		return res;
	}

	private static PlayerUi getPlayerFromPlayerUI(Player player) {
		for(PlayerUi playerUi: playerUis) {
			if(playerUi.getPlayer().getPlayerNumber() == player.getPlayerNumber()) {
				return playerUi;
			}
		}
		return null;
	}

	public static void makePlayUI(Player player) {
		PlayerUi playerUi = getPlayerFromPlayerUI(player);
		nowPlaying = player.getPlayerNumber() - 1;
		if(playerUi != null) {
			playerUi.move();
		}
		layeredPane.remove(gameBoard);
		layeredPane.add(gameBoard, new Integer(0));
	}

}