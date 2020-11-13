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
import java.util.Map;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.StaleProxyException;
import utils.ColorHelper;

public class MonopolyMain extends JFrame{


	private Runtime runtimeInstance;
	private Profile profile;
	private JScrollPane scrollPane;
	private ContainerController containerController;
	private JPanel contentIncluder;
	static JTextArea infoConsole;
	static JPanel playerAssetsPanel;
	CardLayout c1 = new CardLayout();
	static ArrayList<PlayerUi> playerUis = new ArrayList<PlayerUi>();
	ArrayList<Player> players = new ArrayList<Player>();
	static int turnCounter = 0;
	static JTextArea panelPlayerTextArea;
	JButton btnStart;
	static Board gameBoard;
	static Dice dice1;
	static Dice dice2;
	static JPanel panelPlayer;
	static JLabel panelPlayerTitle;
	static JLayeredPane layeredPane;
	static int nowPlaying = 0;

	public MonopolyMain() throws StaleProxyException {
		this.runtimeInstance = Runtime.instance();
		this.profile = new ProfileImpl(true);
		this.containerController = runtimeInstance.createMainContainer(profile);


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1030, 1030);
		setSize(1600,1080);
		contentIncluder = new JPanel();
		contentIncluder.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentIncluder);
		contentIncluder.setLayout(null);

		layeredPane = new JLayeredPane();
		layeredPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		layeredPane.setBounds(6, 6, 1024, 1022);
		contentIncluder.add(layeredPane);

		gameBoard = new Board(6,6,1012,1012);
		gameBoard.setBackground(new Color(51, 255, 153));
		layeredPane.add(gameBoard, new Integer(0));

		dice1 = new Dice(420, 706, 80, 80);
		layeredPane.add(dice1, new Integer(1));

		dice2 = new Dice(515, 706, 80, 80);
		layeredPane.add(dice2, new Integer(1));

		this.createAgents();

		for(PlayerUi playerUi : playerUis) {
			layeredPane.add(playerUi, new Integer(2));
		}

		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.LIGHT_GRAY);
		rightPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		rightPanel.setBounds(1100, 6, 419, 600);
		contentIncluder.add(rightPanel);
		rightPanel.setLayout(null);

		JPanel test = new JPanel();
		test.setBounds(81, 312, 246, 68);
		rightPanel.add(test);
		test.setLayout(null);

		playerAssetsPanel = new JPanel();
		playerAssetsPanel.setBounds(81, 28, 246, 250);
		rightPanel.add(playerAssetsPanel);
		playerAssetsPanel.setLayout(c1);

		panelPlayer = new JPanel();
		panelPlayer.setBackground(Color.RED);
		panelPlayerTitle = new JLabel("Player 1 Information");
		panelPlayerTitle.setForeground(Color.WHITE);
		panelPlayerTitle.setHorizontalAlignment(SwingConstants.CENTER);
		panelPlayerTitle.setBounds(0, 6, 240, 25);
		panelPlayer.add(panelPlayerTitle);
		panelPlayer.setLayout(null);
		panelPlayerTextArea = new JTextArea();
		panelPlayerTextArea.setBounds(10, 34, 230, 200);
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


		btnStart = new JButton("start");
		btnStart.setBounds(81, 478, 117, 29);
		rightPanel.add(btnStart);
		btnStart.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 try {
					 btnStart.setEnabled(false);
					 rightPanel.remove(btnStart);
					 rightPanel.updateUI();
					 layeredPane.remove(gameBoard);
					 layeredPane.add(gameBoard, new Integer(0));
					 players.get(0).move();
				 } catch (InterruptedException interruptedException) {
					 interruptedException.printStackTrace();
				 }
			 }
		 });
	}

	public static double squareEfficiency(int squareNumber) {
		return gameBoard.getSquareAtIndex(squareNumber).getEfficiency();
	}

	private void createAgents() throws StaleProxyException {
		for(int i = 1; i < 5; i++) {
			//estrategias tao todas como a 1 currently
			String id = "player_" + i;
			Player player = new Player(i,1);

			players.add(player);

			PlayerUi playerUi = new PlayerUi(player, ColorHelper.getColor(i));
			playerUis.add(playerUi);

			AgentController agentController = this.containerController.acceptNewAgent(id, player);
			agentController.start();
		}
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

	public static int priceOfPurchase(int squareNumber) {
		int price;
		price = gameBoard.getSquareAtIndex(squareNumber).getPrice();
		return price;
	}
	public static int priceOfRent(int squareNumber) {
		int rent;
		rent = gameBoard.getSquareAtIndex(squareNumber).getRentPrice();
		return rent;
	}

	// TODO: Remove From UI
	public static void removeFromUI(Player player) {
		for(PlayerUi playerUi : playerUis) {
			if(playerUi.getPlayer().getPlayerNumber() == player.getPlayerNumber()) {
				layeredPane.remove(playerUi);
			}
		}
	}
}