package game;

import behaviors.BuyBehaviour;
import behaviors.PayBehaviour;
import behaviors.PlayBehaviour;
import behaviors.ReceiveBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Agent {

    private int playerNumber;

    //TODO change to private and create a method for it
    public HashMap<Integer, Integer> ledger = new HashMap<>();

    private int currentSquareNumber = 0; // where player is currently located on (0 - 19). initially zero

    private ArrayList<Integer> titleDeeds = new ArrayList<Integer>(); // squares that the player has
    private int wallet = 3200; // initial money

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;

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
        addBehaviour(new PlayBehaviour(this));
        addBehaviour(new PayBehaviour());
        addBehaviour(new ReceiveBehaviour());
        addBehaviour(new BuyBehaviour());
    }


    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName() + ": done working.");
    }

    protected void searchAgents() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("player");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (int i = 0; i < result.length; ++i) {
                System.out.println("Found " + result[i].getName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
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

    public void buyTitleDeed(int squareNumber) {
        if (ledger.containsKey(squareNumber)) {
            System.out.println("It's already bought by someone. You cannot buy here.");
        } else {
            titleDeeds.add(this.getCurrentSquareNumber());
            ledger.put(squareNumber, this.getPlayerNumber()); // everytime a player buys a title deed, it is written in ledger, for example square 1 belongs to player 2

        }
    }

    public void move(int dicesTotal) {
        if (currentSquareNumber + dicesTotal > 19) {
            depositToWallet(200);
        }
        int targetSquare = (currentSquareNumber + dicesTotal) % 20;
        this.currentSquareNumber = targetSquare;
    }

    class DFSubscriptionInit extends SubscriptionInitiator {

        DFSubscriptionInit(Agent agent, DFAgentDescription dfad) {
            super(agent, DFService.createSubscriptionMessage(agent, getDefaultDF(), dfad, null));
        }

        protected void handleInform(ACLMessage inform) {
            try {
                DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
                for(int i=0; i<dfds.length; i++) {
                    AID agent = dfds[i].getName();
                    System.out.println("New agent in town: " + agent.getLocalName());
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

    }
}
