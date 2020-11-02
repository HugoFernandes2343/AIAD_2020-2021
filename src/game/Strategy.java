package game;

public class Strategy {

    private int strategyFlag;

    public Strategy(int flag){
        this.strategyFlag = flag;
    }

    public int strategize(int wallet, int squareNumber){
        try {
            if(strategyFlag==1){
                return relentlessStrategy(wallet,squareNumber);
            }else if (strategyFlag==2){
                return secondStrategy();
            }else if (strategyFlag==3){
                return thirdStrategy();
            }else if (strategyFlag==4){
                return forthStrategy();
            }else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 255;
    }

    public int relentlessStrategy(int wallet, int squarePrice){
        if(wallet > squarePrice) {
            return 1;
        }
        return 0;
    };
    public int secondStrategy(){return 0;}
    public int thirdStrategy(){return 0;}
    public int forthStrategy(){return 0;}



}
