package game;

import com.sun.xml.internal.bind.v2.TODO;
import jade.core.Agent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Agent{

    private int playerNumber;

    //TODO Setup register

    //TODO change to private and create a method for it
    public HashMap<Integer, Integer> ledger= new HashMap<>();

    private int currentSquareNumber = 0; // where player is currently located on (0 - 19). initially zero

    private ArrayList<Integer> titleDeeds = new ArrayList<Integer>(); // squares that the player has
    private int wallet = 3200; // initial money

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;

    }

    public ArrayList<Integer> getTitleDeeds() {
        return titleDeeds;
    }

    public int getWallet() {
        return wallet;
    }

    public void withdrawFromWallet(int withdrawAmount) {
        if(withdrawAmount > wallet) {
            System.out.println("PlayerUi "+ playerNumber + " went bankrupt!");
        } else {
            wallet -= withdrawAmount;
        }
    }

    public void depositToWallet(int depositAmount) {
        wallet += depositAmount;
        System.out.println("Payday for player "+getPlayerNumber()+". You earned $" + depositAmount +"!");
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
        if(ledger.containsKey(squareNumber)) {
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

}
