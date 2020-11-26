package game;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;

import java.util.ArrayList;

import static java.lang.System.out;

public class RepastLauncher extends Repast3Launcher {
    private Runtime runtimeInstance;
    private Profile profile;
    private ContainerController containerController;
    ArrayList<Player> players = new ArrayList<>();
    //Variables for graphs
    int numberOfAgents;

    int walletPlayer1;
    int walletPlayer2;
    int walletPlayer3;
    int walletPlayer4;

    int walletPercent1;
    int walletPercent2;
    int walletPercent3;
    int walletPercent4;

    int player1Turn = 0;
    int player2Turn = 0;
    int player3Turn = 0;
    int player4Turn = 0;

    int purchasesPlayer1;
    int purchasesPlayer2;
    int purchasesPlayer3;
    int purchasesPlayer4;

    @Override
    protected void launchJADE() {
        this.runtimeInstance = Runtime.instance();
        this.profile = new ProfileImpl(true);
        this.containerController = runtimeInstance.createMainContainer(profile);
        System.out.println("a minha vida é triste");
        players.clear();
        launchAgents();

        MonopolyMain frame = null;
        frame = new MonopolyMain(players, this);
        frame.setVisible(true);
        MonopolyMain finalFrame = frame;
        Thread thread = new Thread("New Thread") {
            public void run(){
                finalFrame.start();
            }
        };

        thread.start();
    }

    private void launchAgents() {
        try {

            Player player1 = new Player(1,1);
            Player player2 = new Player(2,2,3);
            Player player3 = new Player(3,3);
            Player player4 = new Player(4,4);

            players.add(player1);
            players.add(player2);
            players.add(player3);
            players.add(player4);

            setNumberOfAgents(4);

            for(int i = 1; i < 5; i++) {
                String id = "player_" + i;
                AgentController agentController = this.containerController.acceptNewAgent(id, players.get(i-1));
                agentController.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    public void setNumberOfAgents(int numberOfAgents) {
        this.numberOfAgents = numberOfAgents;
    }

    public void decrementNumberOfAgents(){
        int newNumber = this.numberOfAgents - 1;
        setNumberOfAgents(newNumber);
    }

    public int getNumberOfAgents() {
        return numberOfAgents;
    }

    @Override
    public String[] getInitParam() {
        return new String[]{"numberOfAgents"};
    }

    @Override
    public String getName() {
        return "Monopoly";
    }

    public void stopCurrentSim() {
        this.fireStopSim();
        this.fireEndSim();
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
      //  init.setNumRuns(4);   // works only in batch mode
        init.loadModel(new RepastLauncher(), null, false);
	}

	public void setWalletPlayer(int playerNumber,int wallet){
        switch (playerNumber) {
            case 1:
                this.walletPlayer1 = wallet;
                break;
            case 2:
                this.walletPlayer2 = wallet;
                break;
            case 3:
                this.walletPlayer3 = wallet;
                break;
            case 4:
                this.walletPlayer4 = wallet;
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public void setWalletPercentage(int playerNumber){
        int totalWallet= walletPlayer1+walletPlayer2+walletPlayer3+walletPlayer4;
        switch (playerNumber) {
            case 1:
                walletPercent1 = (walletPlayer1*100)/totalWallet;
                break;
            case 2:
                walletPercent2 = (walletPlayer2*100)/totalWallet;
                break;
            case 3:
                walletPercent3 = (walletPlayer3*100)/totalWallet;
                break;
            case 4:
                walletPercent4 = (walletPlayer4*100)/totalWallet;
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public void setPlayerTurn(int playerNumber){
        switch (playerNumber) {
            case 1:
                player1Turn= player1Turn++;
                break;
            case 2:
                player2Turn= player2Turn++;
                break;
            case 3:
                player3Turn= player3Turn++;
                break;
            case 4:
                player4Turn= player4Turn++;
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public void setPlayerPurchases(int playerNumber){
        switch (playerNumber) {
            case 1:
                purchasesPlayer1= purchasesPlayer1++;
                break;
            case 2:
                purchasesPlayer2= purchasesPlayer2++;
                break;
            case 3:
                purchasesPlayer3= purchasesPlayer3++;
                break;
            case 4:
                purchasesPlayer4= purchasesPlayer4++;
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public int getWalletPlayer1() {
        return walletPlayer1;
    }

    public int getWalletPlayer2() {
        return walletPlayer2;
    }

    public int getWalletPlayer3() {
        return walletPlayer3;
    }

    public int getWalletPlayer4() {
        return walletPlayer4;
    }

    public int getWalletPercent1() {
        return walletPercent1;
    }

    public int getWalletPercent2() {
        return walletPercent2;
    }

    public int getWalletPercent3() {
        return walletPercent3;
    }

    public int getWalletPercent4() {
        return walletPercent4;
    }

    public int getPlayer1Turn() {
        return player1Turn;
    }

    public int getPlayer2Turn() {
        return player2Turn;
    }

    public int getPlayer3Turn() {
        return player3Turn;
    }

    public int getPlayer4Turn() {
        return player4Turn;
    }

    public int getPurchasesPlayer1() {
        return purchasesPlayer1;
    }

    public int getPurchasesPlayer2() {
        return purchasesPlayer2;
    }

    public int getPurchasesPlayer3() {
        return purchasesPlayer3;
    }

    public int getPurchasesPlayer4() {
        return purchasesPlayer4;
    }

}
