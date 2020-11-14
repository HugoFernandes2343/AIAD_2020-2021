package game;

import java.util.Random;

public class Strategy {

    private int strategyFlag;
    private int targetTurn;
    private String desiredColor1;
    private String desiredColor2;
    private String desiredColor3;

    public Strategy(int flag){
        this.strategyFlag = flag;
    }
    public Strategy(int flag, int targetTurn){
        this.strategyFlag = flag;
        this.targetTurn = targetTurn;
    }
    public Strategy(int flag, String desiredColor1, String desiredColor2,String desiredColor3){
        this.strategyFlag = flag;
        this.desiredColor1 = desiredColor1;
        this.desiredColor2 = desiredColor2;
        this.desiredColor3 = desiredColor3;
    }

    public void setStrategyFlag(int strategyFlag) {
        this.strategyFlag = strategyFlag;
    }

    public int strategize(int squareNumber, int wallet, int squarePrice, int currentTurn, String squareColor){
        try {
            if(getStrategyFlag() ==1){
                return relentlessStrategy(wallet, squarePrice);
            }else if (getStrategyFlag() ==2){
                return buyOnlyAfterHavingDoneTargetAmountOfTurnsStrategy(currentTurn, targetTurn, wallet, squarePrice);
            }else if (getStrategyFlag() ==3){
                return buyBlocksOfThreeColorHousesStrategy(desiredColor1, desiredColor2,desiredColor3, squareColor, wallet, squarePrice);
            }else if (getStrategyFlag() ==4){
                return efficiencyStrategy(squareNumber,wallet,squarePrice);
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
    }

    private int buyOnlyAfterHavingDoneTargetAmountOfTurnsStrategy(int currentTurn, int targetTurn, int wallet, int squarePrice){
        if(currentTurn < targetTurn) {
            return 0;
        }
        return relentlessStrategy(wallet, squarePrice);
    }

    public int buyBlocksOfThreeColorHousesStrategy(String desiredColor1, String desiredColor2,String desiredColor3, String squareColor, int wallet, int squarePrice){
        if((desiredColor1.equals(squareColor) || desiredColor2.equals(squareColor) || desiredColor3.equals(squareColor) )&& relentlessStrategy(wallet, squarePrice) == 1) {
            return 1;
        }
        return 0;
    }

    public int efficiencyStrategy(int squareNumber, int wallet, int squarePrice){
        double efficiency = MonopolyMain.squareEfficiency(squareNumber);
        int percent = (int) (efficiency*100);
        Random r = new Random();
        if(r.nextInt(100)<percent){
            return 1;
        }else {
            return 0;
        }
    }

    public int getStrategyFlag() {
        return strategyFlag;
    }
}
