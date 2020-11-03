package game;

import behaviors.PlayListeningBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import utils.ColorHelper;
import utils.MessageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player extends Agent {

    private final int playerNumber;

    //TODO change to private and create a method for it
    public HashMap<Integer, Integer> ledger = new HashMap<>();

    private int currentSquareNumber = 0; // where player is currently located on (0 - 19). initially zero

    private ArrayList<Integer> titleDeeds = new ArrayList<>(); // squares that the player has
    private int wallet = 3200; // initial money

    private final Strategy strategy;

    public Player(int playerNumber, int strategy) {
        this.playerNumber = playerNumber;
        this.strategy = new Strategy(strategy);
    }

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("player");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new DFSubscriptionInit(this, dfd));
        addBehaviour(new PlayListeningBehaviour(this));
    }


    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName() + ": done working.");
    }

    protected ArrayList<String> searchForPlayers() {
        ArrayList<String> players = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("player");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfAgentDescription : result) {
                String playerName = dfAgentDescription.getName().getName();
                System.out.println("Found " + playerName);
                String[] splitInformation = playerName.split("@");
                if (!splitInformation[0].equals("player_" + this.getPlayerNumber())) {
                    players.add(splitInformation[0]);
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        if (players.size() == 0) {
            MonopolyMain.changeConsoleMessage("YOU WON PLAYER " + this.playerNumber);
            takeDown();
        }

        return players;
    }


    public ArrayList<Integer> getTitleDeeds() {
        return titleDeeds;
    }

    public int getWallet() {
        return wallet;
    }

    public void withdrawFromWallet(int withdrawAmount) {
        if (withdrawAmount > wallet) {
            System.out.println("PlayerUi " + playerNumber + " went bankrupt!");
            //TODO Send bust message
            takeDown();
        } else {
            wallet -= withdrawAmount;
        }
    }

    public void depositToWallet(int depositAmount) {
        wallet += depositAmount;
        System.out.println("Payday for player " + getPlayerNumber() + ". You earned $" + depositAmount + "!");
    }

    public int getCurrentSquareNumber() {
        return currentSquareNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean hasTitleDeed(int squareNumber) {
        return titleDeeds.contains(squareNumber) ? true : false;
    }

    public void registerTransactionInLedger(int squareNumber, int playerNumber) {
        this.ledger.put(squareNumber, playerNumber);
    }

    private boolean isSquareUnbuyable(int squareNumber) {
        for (Square square : MonopolyMain.gameBoard.getUnbuyableSquares()) {
            if (square.getName().equalsIgnoreCase(MonopolyMain.gameBoard.getSquareAtIndex(squareNumber).getName())) {
                return true;
            }
        }
        return false;
    }

    private void buySquare(int squareNumber, int playerNumber) {
        int price = MonopolyMain.priceOfPurchase(squareNumber);
        withdrawFromWallet(price);
        titleDeeds.add(squareNumber);
        this.registerTransactionInLedger(squareNumber, playerNumber);  // everytime a player buys a title deed, it is written in ledger, for example square 1 belongs to player 2

        ArrayList<String> players = searchForPlayers();
        for (String player : players) {
            sendBuyMessage(player, squareNumber, playerNumber);
        }
    }

    private String getNextPlayerNumber() {
        String nextPlayer = "player_";
        if (this.getPlayerNumber() != 4) {
            return nextPlayer + (this.getPlayerNumber() + 1);
        }
        return nextPlayer + 1;
    }

    public void move() throws InterruptedException {
        //Update the panel
        MonopolyMain.changeConsoleMessage("Player " + playerNumber + " is at " + MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName());
        MonopolyMain.updatePlayerPanel(ColorHelper.getColor(this.getPlayerNumber()), this.getPlayerNumber());
        MonopolyMain.updatePanelPlayerTextArea(this);

        Thread.sleep(2000);

        ArrayList<Integer> diceResult = MonopolyMain.rollDiceUI();
        int dicesTotal = diceResult.get(0) + diceResult.get(1);
        if (currentSquareNumber + dicesTotal > 35) {
            depositToWallet(200);
        }
        int targetSquare = (currentSquareNumber + dicesTotal) % 36;
        this.currentSquareNumber = targetSquare;
        MonopolyMain.makePlayUI(this);

        MonopolyMain.changeConsoleMessage("Player " + playerNumber + " is at " + MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName());

        Thread.sleep(2000);

        //Verificar se um square ja tem dono
        if (ledger.containsKey(targetSquare)) {
            if (ledger.get(targetSquare) != playerNumber) {
                MonopolyMain.infoConsole.setText("This property belongs to player " + ledger.get(targetSquare) + " you need to pay rent.");
                //Todo pagar renda
            }
        } else {
            //Strategy is used to decide if the player buys the square or not
            //Output will be 1(Buy) or 0(Don't buy) or 255 if there is an error
            if (!isSquareUnbuyable(currentSquareNumber)) {
                int decision = strategy.relentlessStrategy(wallet, MonopolyMain.priceOfPurchase(currentSquareNumber));
                if (decision == 1) {
                    buySquare(currentSquareNumber, this.getPlayerNumber());
                    //TODO method to buy and then send the buy message to the other players
                }
            }
        }

        MonopolyMain.updatePlayerPanel(ColorHelper.getColor(this.getPlayerNumber()), this.getPlayerNumber());
        MonopolyMain.updatePanelPlayerTextArea(this);

        if (diceResult.get(0).equals(diceResult.get(1))) {
            MonopolyMain.changeConsoleMessage("Double Dice Roll Have Another Turn Player " + this.getPlayerNumber());
            Thread.sleep(2000);
            move();
        } else {
            String nextPlayerNumber = getNextPlayerNumber();
            MonopolyMain.changeConsoleMessage("Next Player's turn");
            Thread.sleep(2000);
            sendPlayMessage(nextPlayerNumber);
        }
    }

    private void sendBuyMessage(String player, int squareNumber, int originalPlayerNumber) {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("MESSAGE_TYPE", MessageType.BUY.toString());
        msg.addReceiver(new AID(player, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent(originalPlayerNumber+ "/" + squareNumber);
        this.send(msg);
    }

    public void sendPlayMessage(String nextPlayerNumber) {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("MESSAGE_TYPE", MessageType.PLAY.toString());
        msg.addReceiver(new AID(nextPlayerNumber, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent(this.getLocalName());
        this.send(msg);
    }


    class DFSubscriptionInit extends SubscriptionInitiator {

        DFSubscriptionInit(Agent agent, DFAgentDescription dfad) {
            super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
        }

        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
                for (int i = 0; i < dfds.length; i++) {
                    AID agent = dfds[i].getName();
                    System.out.println("New agent in town: " + agent.getLocalName());
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

    }
}
