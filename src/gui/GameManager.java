package gui;

import java.util.HashMap;

public class GameManager {

    private static final int AMOUNT_TO_ADD = 200;
    HashMap<String, Player> propertiesMap;

    public GameManager() {
        this.propertiesMap = new HashMap<>();
    }

    public void depositGoSquareMoneyToPlayerAccount(Player player) {
        player.depositToWallet(AMOUNT_TO_ADD);
    }

    public void setPropertyOwnership(String propertyName, Player player) {
        this.propertiesMap.put(propertyName, player);
    }

    public int getPropertyOwnership(String propertyName) {
        if(propertyName != null && !propertyName.equals("")){
            return this.propertiesMap.get(propertyName).getPlayerNumber();
        }
        return 0;
    }
}
