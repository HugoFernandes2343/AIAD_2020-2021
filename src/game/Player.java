package game;

import behaviors.PlayListeningBehaviour;
import jade.content.lang.Codec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import utils.ColorHelper;
import utils.MessageType;

import java.lang.reflect.Array;
import java.util.*;

public class Player extends Agent {

    private final int playerNumber;
    private final String colorOfHousesToBuy;

    //TODO change to private and create a method for it
    public HashMap<Integer, Integer> ledger = new HashMap<>();

    private int currentSquareNumber = 0; // where player is currently located on (0 - 19). initially zero
    private int currentTurnCounter;
    private int targetTurn;
    private int jailTurnCounter;
    private boolean isInJail;
    private ArrayList<Integer> titleDeeds = new ArrayList<>(); // squares that the player has
    private ArrayList<String> otherPlayersQueue;
    private ArrayList<String> generatedColorsList;
    private int wallet = 1500; // initial money

    private final Strategy strategy;

    public Player(int playerNumber, int strategy) {
        this.playerNumber = playerNumber;
        this.strategy = new Strategy(strategy);
        this.otherPlayersQueue = new ArrayList<>();
        this.isInJail = false;
        this.jailTurnCounter = 0;
        this.currentTurnCounter = 1;
        this.targetTurn = 0;
        this.colorOfHousesToBuy = "";
        this.generatedColorsList = generateColorSelection();
    }

    public Player(int playerNumber, int strategy, int targetTurn) {
        this.playerNumber = playerNumber;
        this.strategy = new Strategy(strategy);
        this.otherPlayersQueue = new ArrayList<>();
        this.isInJail = false;
        this.jailTurnCounter = 0;
        this.currentTurnCounter = 1;
        this.targetTurn = targetTurn;
        this.colorOfHousesToBuy = "";
        this.generatedColorsList = generateColorSelection();
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

    private int generateRandomNumber(int arrayLength) {
        Random r = new Random();
        int randomNumber = r.nextInt(arrayLength);
        return randomNumber;
    }

    private ArrayList<String> generateColorSelection() {
        String[] colorArray = {"PINK", "CYAN", "MAGENTA", "ORANGE", "RED", "YELLOW", "GREEN", "BLUE"};
        ArrayList<String> colorGeneratedList = new ArrayList<>();
        int firstChoice = generateRandomNumber(colorArray.length);
        int secondChoice = generateRandomNumber(colorArray.length);
        if(firstChoice == secondChoice) {
            while(firstChoice == secondChoice) {
                secondChoice = generateRandomNumber(colorArray.length);
            }
        }
        colorGeneratedList.add(colorArray[firstChoice]);
        colorGeneratedList.add(colorArray[secondChoice]);
        return colorGeneratedList;
    }

    public boolean withdrawFromWallet(int withdrawAmount) {
        if (withdrawAmount > wallet) {
            System.out.println("Player_" + playerNumber + " went bankrupt!");
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
            if(this.otherPlayersQueue.isEmpty()){
                MonopolyMain.changeConsoleMessage("YOU WON PLAYER " + this.playerNumber);
                takeDown();
            }
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

        Thread.sleep(1000);

        ArrayList<Integer> diceResult = MonopolyMain.rollDiceUI();

        if(this.isInJail && this.jailTurnCounter < 2 && !diceResult.get(0).equals(diceResult.get(1))) {
            this.jailTurnCounter++;
            String nextPlayerNumber = getNextPlayerNumber();
            if (nextPlayerNumber != null) {
                MonopolyMain.changeConsoleMessage("Next Player's turn");
                Thread.sleep(1000);
                sendPlayMessage(nextPlayerNumber);
            }
        } else {
            this.jailTurnCounter = 0;
            this.isInJail = false;
        }


        int dicesTotal = diceResult.get(0) + diceResult.get(1);
        if (currentSquareNumber + dicesTotal > 35) {
            this.currentTurnCounter++;
            depositToWallet(200);
        }
        int targetSquare = (currentSquareNumber + dicesTotal) % 36;
        this.currentSquareNumber = targetSquare;

        if(
                (MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName().equals("Vá para a cadeia") ||
                MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName().equals("Prisão"))
                && this.currentTurnCounter > 1
        ) {
            this.isInJail = true;
            this.jailTurnCounter = this.currentTurnCounter;
            this.currentSquareNumber = 9;
        }

        MonopolyMain.makePlayUI(this);

        /*if(MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName().equals("Prisão")) {
            this.isInJail = true;
            this.turnCounterWhenPlayerWasArrested = this.currentTurnCounter;
        }*/

        MonopolyMain.changeConsoleMessage("Player " + playerNumber + " is at " + MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName());

        Thread.sleep(1000);

        //CASAS ESPECIAIS
        if(currentSquareNumber==11){
            withdrawFromWallet(200);
            MonopolyMain.changeConsoleMessage("Player " + playerNumber + " paid 200€ in taxes");
            Thread.sleep(1000);

        }else if(currentSquareNumber==25){
            withdrawFromWallet(100);
            MonopolyMain.changeConsoleMessage("Player " + playerNumber + " paid 100€ in taxes");
            Thread.sleep(1000);

        }else if( currentSquareNumber==6 || currentSquareNumber==20 || currentSquareNumber== 34) {
            Random random = new Random();
            boolean offset = random.nextBoolean();
            if(offset){
                currentSquareNumber += 2;

                if(currentSquareNumber==36){
                    currentSquareNumber=0;
                    this.currentTurnCounter++;
                    depositToWallet(200);
                }
                MonopolyMain.changeConsoleMessage("Player " + playerNumber + " moves 2 squares forward");
            }else{
                currentSquareNumber-=2;
                MonopolyMain.changeConsoleMessage("Player " + playerNumber + " moves 2 squares backwards");
            }

            MonopolyMain.makePlayUI(this);
            Thread.sleep(1000);

        }else if(currentSquareNumber==2 || currentSquareNumber==15 || currentSquareNumber== 30){
            Random random2 = new Random();
            boolean offset = random2.nextBoolean();
            int i = random2.nextInt(200 - 10) + 10;
            if(offset){
                MonopolyMain.changeConsoleMessage("Player " + playerNumber + " recieved " + i );
                depositToWallet(i);
            }else{
                MonopolyMain.changeConsoleMessage("Player " + playerNumber + " lost " + i);
                withdrawFromWallet(i);
            }
            Thread.sleep(1000);
        }


        if (ledger.containsKey(currentSquareNumber)) {
            if (ledger.get(currentSquareNumber) != playerNumber) {
                MonopolyMain.infoConsole.setText("This property belongs to player " + ledger.get(currentSquareNumber) + " you need to pay rent.");
                payRent("player_" + ledger.get(currentSquareNumber), MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getRentPrice());
            }
        }else {
            //Strategy is used to decide if the player buys the square or not
            //Output will be 1(Buy) or 0(Don't buy) or 255 if there is an error
            if (!isSquareUnbuyable(currentSquareNumber) && this.currentTurnCounter > 1) {
                int decision = strategy.strategize(
                        wallet,
                        MonopolyMain.priceOfPurchase(currentSquareNumber),
                        this.currentTurnCounter,
                        this.targetTurn,
                        MonopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getColor(),
                        this.generatedColorsList.get(0),
                        this.generatedColorsList.get(1));
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
            Thread.sleep(1000);
            move();
        } else {
            String nextPlayerNumber = getNextPlayerNumber();
            if (nextPlayerNumber != null) {
                MonopolyMain.changeConsoleMessage("Next Player's turn");
                Thread.sleep(1000);
                sendPlayMessage(nextPlayerNumber);
            }else{
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
