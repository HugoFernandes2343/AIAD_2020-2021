package game;

import behaviors.GameManagerBuyBehaviour;
import behaviors.GameManagerPlayBehaviour;
import jade.core.Agent;

import java.util.HashMap;

public class GameManager extends Agent {

    private HashMap<String, Player> propertiesMap;
    private int playerToPlayId;

    public GameManager() {
        this.propertiesMap = new HashMap<>();
        this.playerToPlayId = 1;
    }

    public void setup() {
        addBehaviour(new GameManagerPlayBehaviour());
        addBehaviour(new GameManagerBuyBehaviour());
        System.out.println(getLocalName() + ": starting to work!");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

    public void setPropertyOwnership(String propertyName, Player player) {
        this.propertiesMap.put(propertyName, player);
    }

    public Player getPropertyOwnership(String propertyName) {
        if (propertyName != null && !propertyName.equals("")) {
            return this.propertiesMap.get(propertyName);
        }
        return null;
    }

    public int getPlayerToPlayId() {
        return this.playerToPlayId;
    }
}
