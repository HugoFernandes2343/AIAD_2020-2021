package game;

import java.util.Random;

public class Strategy {

    private int strategyFlag;

    public Strategy(int flag){
        this.strategyFlag = flag;
    }

    public int strategize(int wallet, int squarePrice, int currentTurn, int targetTurn, String squareColor, String desiredColor1, String desiredColor2){
        try {
            if(getStrategyFlag() ==1){
                return relentlessStrategy(wallet, squarePrice);
            }else if (getStrategyFlag() ==2){
                return buyOnlyAfterHavingDoneTargetAmountOfTurnsStrategy(currentTurn, targetTurn, wallet, squarePrice);
            }else if (getStrategyFlag() ==3){
                return buyBlocksOfTwoColorHousesStrategy(desiredColor1, desiredColor2, squareColor, wallet, squarePrice);
            }else if (getStrategyFlag() ==4){
                //TODO: Implement fourth strategy
            }else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 255;
    }

    private int relentlessStrategy(int wallet, int squarePrice){
        if(wallet > squarePrice) {
            return 1;
        }
        return 0;
    };

    private int buyOnlyAfterHavingDoneTargetAmountOfTurnsStrategy(int currentTurn, int targetTurn, int wallet, int squarePrice){
        if(currentTurn < targetTurn) {
            return 0;
        }
        return relentlessStrategy(wallet, squarePrice);
    }

    public int buyBlocksOfTwoColorHousesStrategy(String desiredColor1, String desiredColor2, String squareColor, int wallet, int squarePrice){
        if((desiredColor1.equals(squareColor) || desiredColor2.equals(squareColor)) && relentlessStrategy(wallet, squarePrice) == 1) {
            return 1;
        }
        return 0;
    }

    //TODO: Implement fourth strategy
    public int fourthStrategy(int squareNumber, int wallet, int squarePrice){
       // Random r = new Random;




        return 0;
    }

    public int getStrategyFlag() {
        return strategyFlag;
    }
}
