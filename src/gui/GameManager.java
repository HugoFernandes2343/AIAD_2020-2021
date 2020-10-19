package gui;

import java.util.HashMap;

public class GameManager {

    private HashMap<String, Player> propertiesMap;

    public GameManager() {
        this.propertiesMap = new HashMap<>();
    }

    public void setPropertyOwnership(String propertyName, Player player) {
        this.propertiesMap.put(propertyName, player);
    }

    public Player getPropertyOwnership(String propertyName) {
        if(propertyName != null && !propertyName.equals("")){
            return this.propertiesMap.get(propertyName);
        }
        return null;
    }
}
