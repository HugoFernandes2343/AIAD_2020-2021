package game;

import behaviors.PlayListeningBehaviour;
import jade.wrapper.ControllerException;
import sajas.core.AID;
import sajas.core.Agent;
import sajas.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import sajas.proto.SubscriptionInitiator;
import utils.ColorHelper;
import utils.MessageType;

import java.util.*;

public class Player extends Agent {

    private final int playerNumber;

    public MonopolyMain monopolyMain;


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
    private static final String PREFIX = "player_";
    private final transient Strategy strategy;
    private Random r = new Random();
    private RepastLauncher impl;
    private boolean isAlive;

    public Player(int playerNumber, int strategy) {
        this.playerNumber = playerNumber;
        if(strategy == 3){
            this.generatedColorsList = generateColorSelection();
            this.strategy = new Strategy(strategy,generatedColorsList.get(0),generatedColorsList.get(1),generatedColorsList.get(2));
        }else{
            this.strategy = new Strategy(strategy);
        }
        this.otherPlayersQueue = new ArrayList<>();
        this.isInJail = false;
        this.jailTurnCounter = 0;
        this.currentTurnCounter = 1;
        this.targetTurn = 0;
        this.isAlive = true;
    }

    public Player(int playerNumber, int strategy, int targetTurn) {
        this.playerNumber = playerNumber;
        this.strategy = new Strategy(strategy,targetTurn);
        this.otherPlayersQueue = new ArrayList<>();
        this.isInJail = false;
        this.jailTurnCounter = 0;
        this.currentTurnCounter = 1;
        this.targetTurn = targetTurn;
        this.isAlive = true;
    }

    public void setFrame(MonopolyMain monopolyMain){
        this.monopolyMain = monopolyMain;
    }

    @Override
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

    private void getPlayerPosition() {
        if(this.otherPlayersQueue.size() == 3) {
            this.getImpl().setRecordResultsArrayList(this.playerNumber, 4);
        }
        else if(this.otherPlayersQueue.size() == 2) {
            this.getImpl().setRecordResultsArrayList(this.playerNumber, 3);
        }
        else if(this.otherPlayersQueue.size() == 1) {
            this.getImpl().setRecordResultsArrayList(this.playerNumber, 2);
        }
        else {
            this.getImpl().setRecordResultsArrayList(this.playerNumber, 1);
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            getPlayerPosition();
            impl.decrementNumberOfAgents();
            impl.setAverageOfPlayerWallets(this.playerNumber);
            impl.setMaxPlayerPurchases(this.playerNumber, this.getTitleDeeds().size());
            this.getContainerController().removeLocalAgent(this);
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
                System.out.println("I'm player_" + playerNumber +" I found " + playerName);
                String[] splitInformation = playerName.split("@");
                if (!splitInformation[0].equals(PREFIX + this.getPlayerNumber())) {
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

    private int generateRandomNumber(int arrayLength) {
        int randomNumber = r.nextInt(arrayLength);
        return randomNumber;
    }

    private ArrayList<String> generateColorSelection() {
        String[] colorArray = {"PINK", "CYAN", "MAGENTA", "ORANGE", "RED", "YELLOW", "GREEN", "BLUE"};
        ArrayList<String> colorGeneratedList = new ArrayList<>();
        int firstChoice = generateRandomNumber(colorArray.length);
        int secondChoice = generateRandomNumber(colorArray.length);
        int thirdChoice = generateRandomNumber(colorArray.length);
        if(firstChoice == secondChoice) {
            while(firstChoice == secondChoice) {
                secondChoice = generateRandomNumber(colorArray.length);
            }
        }
        if(thirdChoice == firstChoice || thirdChoice==secondChoice){
            while(thirdChoice == secondChoice||thirdChoice==firstChoice) {
                thirdChoice = generateRandomNumber(colorArray.length);
            }
        }
        colorGeneratedList.add(colorArray[firstChoice]);
        colorGeneratedList.add(colorArray[secondChoice]);
        colorGeneratedList.add(colorArray[thirdChoice]);
        return colorGeneratedList;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public boolean withdrawFromWallet(int withdrawAmount) {
        if (withdrawAmount > wallet) {
            System.out.println("Player_" + playerNumber + " went bankrupt!");
            for(int i =0; i<titleDeeds.size();i++){
                Square s = monopolyMain.gameBoard.getSquareAtIndex(titleDeeds.get(i));
                s.resetSquare();
            }
            wallet = 0;
            this.getImpl().setWalletPlayer(playerNumber, wallet);
            this.getImpl().setWalletPercentage(playerNumber);
            this.getImpl().setRecordPlayerWallets(playerNumber, wallet);
            sendBustToOtherPlayers(PREFIX + this.getPlayerNumber());
            return false;
        } else {
            wallet = wallet - withdrawAmount;
            this.getImpl().setWalletPlayer(playerNumber, wallet);
            this.getImpl().setWalletPercentage(playerNumber);
            this.getImpl().setRecordPlayerWallets(playerNumber, wallet);
            System.out.println("Player_" + playerNumber + " paid " + withdrawAmount + " at square " + currentSquareNumber);
        }

        return true;
    }

    public void depositToWallet(int depositAmount) {
        wallet = wallet + depositAmount;
        this.getImpl().setWalletPlayer(playerNumber, wallet);
        this.getImpl().setWalletPercentage(playerNumber);
        this.getImpl().setRecordPlayerWallets(playerNumber, wallet);
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
        for (Square square : monopolyMain.gameBoard.getUnbuyableSquares()) {
            if (square.getName().equalsIgnoreCase(monopolyMain.gameBoard.getSquareAtIndex(squareNumber).getName())) {
                return true;
            }
        }
        return false;
    }

    private void buySquare(int squareNumber, int playerNumber) {
        int price = monopolyMain.priceOfPurchase(squareNumber);
        if (!withdrawFromWallet(price)) {
            return;
        }
        titleDeeds.add(squareNumber);
        this.registerTransactionInLedger(squareNumber, playerNumber);  // everytime a player buys a title deed, it is written in ledger, for example square 1 belongs to player 2
        System.out.println(PREFIX+playerNumber + " bought square number " + squareNumber);

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
                monopolyMain.changeConsoleMessage("YOU WON PLAYER " + this.playerNumber);
                monopolyMain.updatePlayerPanel(ColorHelper.getColor(this.getPlayerNumber()), this.getPlayerNumber());
                monopolyMain.updatePanelPlayerTextArea(this);
                System.out.println("YOU WON PLAYER " + this.playerNumber);
                shutdown();
            }else if(this.otherPlayersQueue.size()==1 && strategy.getStrategyFlag()==3){
                strategy.setStrategyFlag(1);
            }
        }
    }

    public void receiveRentPayment(int rentValue) {
        this.depositToWallet(rentValue);
    }

    private boolean payRent(String squareOwner, int rentValue) {
        boolean b = this.withdrawFromWallet(rentValue);
        System.out.println(PREFIX + playerNumber + " pays player " + squareOwner + " $" + rentValue );
        this.sendPaymentMessage(squareOwner, rentValue);
        return b;
    }

    private void sendBustToOtherPlayers(String originalPlayer) {
        monopolyMain.removeFromUI(playerNumber);

        for (String player : otherPlayersQueue) {
            sendBustMessage(player, originalPlayer);
        }
    }

    public void move() throws InterruptedException {
        //Update the panel
        monopolyMain.changeConsoleMessage("Player " + playerNumber + " is at " + monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName());
        monopolyMain.updatePlayerPanel(ColorHelper.getColor(this.getPlayerNumber()), this.getPlayerNumber());
        monopolyMain.updatePanelPlayerTextArea(this);

        Thread.sleep(150);

        ArrayList<Integer> diceResult = monopolyMain.rollDiceUI();

        if(this.isInJail && this.jailTurnCounter < 2 && !diceResult.get(0).equals(diceResult.get(1))) {
            this.jailTurnCounter++;
            String nextPlayerNumber = getNextPlayerNumber();
            if (nextPlayerNumber != null) {
                monopolyMain.changeConsoleMessage("Next Player's turn");
                Thread.sleep(150);
                sendPlayMessage(nextPlayerNumber);
            }
        } else {
            this.jailTurnCounter = 0;
            this.isInJail = false;
        }

        this.getImpl().setPlayerTurn(playerNumber);
        int dicesTotal = diceResult.get(0) + diceResult.get(1);
        if (currentSquareNumber + dicesTotal > 35) {
            this.currentTurnCounter++;
            System.out.println(PREFIX+playerNumber + " passed by Partida gain 200$");
            depositToWallet(200);
        }
        int targetSquare = (currentSquareNumber + dicesTotal) % 36;
        this.currentSquareNumber = targetSquare;

        if(
                (monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName().equals("Vá para a cadeia") ||
                monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName().equals("Prisão"))
                && this.currentTurnCounter > 1
        ) {
            this.isInJail = true;
            this.jailTurnCounter = this.currentTurnCounter;
            this.currentSquareNumber = 9;
        }

        monopolyMain.makePlayUI(this);

        monopolyMain.changeConsoleMessage("Player " + playerNumber + " is at " + monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName());

       // Thread.sleep(150);

        isAlive=true;

        //CASAS ESPECIAIS
        if(currentSquareNumber==11){//imposto capitais
            isAlive = withdrawFromWallet(200);
            monopolyMain.changeConsoleMessage("Player " + playerNumber + " paid 200€ in taxes");
            //Thread.sleep(150);

        }else if(currentSquareNumber==25){//imposto luxo
            isAlive = withdrawFromWallet(100);
            monopolyMain.changeConsoleMessage("Player " + playerNumber + " paid 100€ in taxes");
            //Thread.sleep(150);

        }else if( currentSquareNumber==6 || currentSquareNumber==20 || currentSquareNumber== 34) {//sorte
            boolean offset = r.nextBoolean();
            if(offset){
                currentSquareNumber += 2;

                if(currentSquareNumber==36){
                    currentSquareNumber=0;
                    this.currentTurnCounter++;
                    depositToWallet(200);
                }
                monopolyMain.changeConsoleMessage("Player " + playerNumber + " moves 2 squares forward");
            }else{
                currentSquareNumber-=2;
                monopolyMain.changeConsoleMessage("Player " + playerNumber + " moves 2 squares backwards");
            }

            monopolyMain.makePlayUI(this);
            Thread.sleep(150);

        }else if(currentSquareNumber==2 || currentSquareNumber==15 || currentSquareNumber== 30){//caixa comunidade
            boolean offset = r.nextBoolean();
            int i = r.nextInt(200 - 10) + 10;
            if(offset){
                monopolyMain.changeConsoleMessage("Player " + playerNumber + " recieved " + i );
                System.out.println(PREFIX + playerNumber + " recieves " + i + "$ from the comunity");
                depositToWallet(i);
            }else{
                monopolyMain.changeConsoleMessage("Player " + playerNumber + " lost " + i);
                System.out.println(PREFIX + playerNumber + " pays " + i + "$ to the comunity");
                isAlive = withdrawFromWallet(i);
            }
            Thread.sleep(150);
        }

        if(!isAlive){
            return;
        }

        if (ledger.containsKey(currentSquareNumber)) {
            if (ledger.get(currentSquareNumber) != playerNumber) {
                monopolyMain.infoConsole.setText("This property belongs to player " + ledger.get(currentSquareNumber) + " you need to pay rent.");
                isAlive = payRent(PREFIX + ledger.get(currentSquareNumber), monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getRentPrice());
            }else{
                Square currentSquare = monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber);
                if(wallet >currentSquare.getHousePrice()*3.5 && currentSquare.getHouseCounter()<4){
                    double efficiency = currentSquare.getEfficiency();
                    int percent = (int) (efficiency*100);
                    if(r.nextInt(100)<percent){
                       isAlive = withdrawFromWallet(currentSquare.getHousePrice());
                        currentSquare.addHouse();
                    }
                }
            }
        }else {
            //Strategy is used to decide if the player buys the square or not
            //Output will be 1(Buy) or 0(Don't buy) or 255 if there is an error
            if (!isSquareUnbuyable(currentSquareNumber) && this.currentTurnCounter > 1) {
                int priceOfPurchase = monopolyMain.priceOfPurchase(currentSquareNumber);
                String squareColor = monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getColor();
                double squareEfficiency = monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getEfficiency();
                int decision = strategy.strategize(currentSquareNumber , wallet, priceOfPurchase, this.currentTurnCounter, squareColor,squareEfficiency);
                if (decision == 1) {
                    monopolyMain.infoConsole.setText("The property " + monopolyMain.gameBoard.getSquareAtIndex(currentSquareNumber).getName() + " was bought by Player " + this.getPlayerNumber() + ".");
                    buySquare(currentSquareNumber, this.getPlayerNumber());
                }
            }
        }

        if(!isAlive){
            String nextPlayerNumber = getNextPlayerNumber();
            if (nextPlayerNumber != null) {
                monopolyMain.changeConsoleMessage("Next Player's turn");
                //Thread.sleep(150);
                sendPlayMessage(nextPlayerNumber);
                takeDown();
            }
            return;
        }else {
            monopolyMain.updatePlayerPanel(ColorHelper.getColor(this.getPlayerNumber()), this.getPlayerNumber());
            monopolyMain.updatePanelPlayerTextArea(this);
        }

        if (diceResult.get(0).equals(diceResult.get(1))) {
            monopolyMain.changeConsoleMessage("Double Dice Roll Have Another Turn Player " + this.getPlayerNumber());
            //Thread.sleep(150);
            move();
        } else {
            String nextPlayerNumber = getNextPlayerNumber();
            if (nextPlayerNumber != null) {
                monopolyMain.changeConsoleMessage("Next Player's turn");
                Thread.sleep(150);
                sendPlayMessage(nextPlayerNumber);
            }/*else{
                monopolyMain.changeConsoleMessage("YOU WON PLAYER " + this.playerNumber);
                takeDown();
            }*/
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

    public void shutdown(){
        // shutdown
        try {

            takeDown();

            impl.setPlayersTotalScore();
            impl.setTotalPlayerPlayTime(System.currentTimeMillis());
            impl.setNumberOfTimesHouseWasBoughtByWinningPlayer(this.getTitleDeeds());

            Thread.sleep(1000);

            impl.updatePositions();

            monopolyMain.setVisible(false);

            Thread.sleep(100);
            monopolyMain.setEnabled(false);
            monopolyMain.dispose();




        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MonopolyMain getMonopoly() {
        return monopolyMain;
    }

    public RepastLauncher getImpl() {
        return impl;
    }

    public void setImpl(RepastLauncher impl) {
        this.impl = impl;
    }

    public int getWallet() { return wallet; }

    class DFSubscriptionInit extends SubscriptionInitiator {

        DFSubscriptionInit(Agent agent, DFAgentDescription dfad) {
            super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
                for (int i = 0; i < dfds.length; i++) {
                    if(!(dfds[i] ==null)){
                        System.out.println("Agent: " + dfds[i].getName().getLocalName());
                    }
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }
}
