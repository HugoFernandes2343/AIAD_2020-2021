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

import java.util.*;

public class Player extends Agent {

    private final int playerNumber;

    //TODO change to private and create a method for it
    public HashMap<Integer, Integer> ledger = new HashMap<>();

    private int currentSquareNumber = 0; // where player is currently located on (0 - 19). initially zero
    private int currentTurnCounter;
    private ArrayList<Integer> titleDeeds = new ArrayList<>(); // squares that the player has
    private ArrayList<String> otherPlayersQueue;
    private int wallet = 3200; // initial money

    private final Strategy strategy;

    public Player(int playerNumber, int strategy) {
        this.playerNumber = playerNumber;
        this.strategy = new Strategy(strategy);
        this.otherPlayersQueue = new ArrayList<>();
        this.currentTurnCounter = 1;
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

    protected void searchForPlayers() {
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
                    this.otherPlayersQueue.add(splitInformation[0]);
                }
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        Collections.sort(this.otherPlayersQueue);
        /*if (players.size() == 0) {
            MonopolyMain.changeConsoleMessage("YOU WON PLAYER " + this.playerNumber);
            takeDown();
        }

        return players;*/
    }

    public ArrayList<Integer> getTitleDeeds() {
        return titleDeeds;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public boolean withdrawFromWallet(int withdrawAmount) {
        if (withdrawAmount > wallet) {
            System.out.println("PlayerUi " + playerNumber + " went bankrupt!");
            //TODO Send bust message
            sendBustToOtherPlayers("player_" + this.getPlayerNumber());
            return false;
        } else {
            wallet -= withdrawAmount;
        }
        return true;
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
        if (!withdrawFromWallet(price)) {
            return;
        }
        titleDeeds.add(squareNumber);
        this.registerTransactionInLedger(squareNumber, playerNumber);  // everytime a player buys a title deed, it is written in ledger, for example square 1 belongs to player 2

        for (String player : this.otherPlayersQueue) {
            sendBuyMessage(player, squareNumber, playerNumber);
        }
    }

    private String getNextPlayerNumber() {
        if (this.otherPlayersQueue.isEmpty() && this.currentTurnCounter == 1) {
            this.searchForPlayers();
        }
        if (this.otherPlayersQueue.isEmpty() && this.currentTurnCounter > 1) {
            return null;
        }
        if (this.getPlayerNumber() != 4) {
            for (int i = 0; i < otherPlayersQueue.size(); i++) {
                String[] splitInformation = otherPlayersQueue.get(i).split("_");
                if (this.getPlayerNumber() < Integer.parseInt(splitInformation[1])) {
                    return otherPlayersQueue.get(i);
                }
            }
        }
        return this.otherPlayersQueue.get(0);
    }

    private void removeBustedPlayerPropertiesFromLedger(String player) {
        String[] splitInformation = player.split("_");
        this.ledger.values().remove(Integer.parseInt(splitInformation[1]));
    }

    public void removePlayerFromQueue(String player) {
        this.removeBustedPlayerPropertiesFromLedger(player);
        if (this.otherPlayersQueue.contains(player)) {
            this.otherPlayersQueue.remove(player);
        }
    }

    public void receiveRentPayment(int rentValue) {
        this.depositToWallet(rentValue);
    }

    private void payRent(String squareOwner, int rentValue) {
        this.withdrawFromWallet(rentValue);
        this.sendPaymentMessage(squareOwner, rentValue);
    }

    private void sendBustToOtherPlayers(String originalPlayer) {
        MonopolyMain.removeFromUI(this);
        for (String player : otherPlayersQueue) {
            sendBustMessage(player, originalPlayer);
        }
        takeDown();
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
            this.currentTurnCounter++;
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
                payRent("player_" + ledger.get(targetSquare), MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getRentPrice());
            }
        } else {
            //Strategy is used to decide if the player buys the square or not
            //Output will be 1(Buy) or 0(Don't buy) or 255 if there is an error
            if (!isSquareUnbuyable(currentSquareNumber) && this.currentTurnCounter > 1) {
                int decision = strategy.strategize(wallet, currentSquareNumber, MonopolyMain.priceOfPurchase(currentSquareNumber));
                if (decision == 1) {
                    MonopolyMain.infoConsole.setText("The property " + MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName() + " was bought by Player " + this.getPlayerNumber() + ".");
                    buySquare(currentSquareNumber, this.getPlayerNumber());
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
            if (nextPlayerNumber != null) {
                MonopolyMain.changeConsoleMessage("Next Player's turn");
                Thread.sleep(2000);
                sendPlayMessage(nextPlayerNumber);
            } else {
                MonopolyMain.changeConsoleMessage("YOU WON PLAYER " + this.playerNumber);
                takeDown();
            }
        }
    }

    private void sendBuyMessage(String player, int squareNumber, int originalPlayerNumber) {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("MESSAGE_TYPE", MessageType.BUY.toString());
        msg.addReceiver(new AID(player, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent(originalPlayerNumber + "/" + squareNumber);
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

    private void sendPaymentMessage(String squareOwner, int rentValue) {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("MESSAGE_TYPE", MessageType.PAYMENT.toString());
        msg.addReceiver(new AID(squareOwner, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent(String.valueOf(rentValue));
        this.send(msg);
    }

    private void sendBustMessage(String player, String originalPlayer) {
        jade.lang.acl.ACLMessage msg = new jade.lang.acl.ACLMessage(ACLMessage.INFORM);
        msg.addUserDefinedParameter("MESSAGE_TYPE", MessageType.BUST.toString());
        msg.addReceiver(new AID(player, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setContent(originalPlayer);
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
