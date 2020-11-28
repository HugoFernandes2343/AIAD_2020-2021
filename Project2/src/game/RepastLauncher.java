package game;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.Histogram;
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

    int player1Turn;
    int player2Turn;
    int player3Turn;
    int player4Turn;

    int purchasesPlayer1;
    int purchasesPlayer2;
    int purchasesPlayer3;
    int purchasesPlayer4;

    private ArrayList<Integer> recordOfPlayer1Results = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer2Results = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer3Results = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer4Results = new ArrayList<>();

    private int player1TotalScore = 0;
    private int player2TotalScore = 0;
    private int player3TotalScore = 0;
    private int player4TotalScore = 0;

    private Histogram degreeDist;

    @Override
    protected void launchJADE() {
        this.runtimeInstance = Runtime.instance();
        this.profile = new ProfileImpl(true);
        this.containerController = runtimeInstance.createMainContainer(profile);
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
        degreeDist = null;
        makeHistogram();
        degreeDist.display();
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

            this.player1Turn = 0;
            this.player2Turn = 0;
            this.player3Turn = 0;
            this.player4Turn = 0;
            this.purchasesPlayer1 = 0;
            this.purchasesPlayer2 = 0;
            this.purchasesPlayer3 = 0;
            this.purchasesPlayer4 = 0;
            setWalletPlayer(1, 1500);
            setWalletPlayer(2, 1500);
            setWalletPlayer(3, 1500);
            setWalletPlayer(4, 1500);
            setWalletPercentage(1);
            setWalletPercentage(2);
            setWalletPercentage(3);
            setWalletPercentage(4);
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

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(7);   // works only in batch mode
        init.loadModel(new RepastLauncher(), null, true);
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
                if(walletPlayer1 == 0) {
                    walletPercent1 = 0;
                }else {
                    walletPercent1 = (walletPlayer1*100)/totalWallet;
                }
                break;
            case 2:
                if(walletPlayer2 == 0) {
                    walletPercent2 = 0;
                }else {
                    walletPercent2 = (walletPlayer2 * 100) / totalWallet;
                }
                break;
            case 3:
                if(walletPlayer3 == 0) {
                    walletPercent3 = 0;
                }else {
                    walletPercent3 = (walletPlayer3*100)/totalWallet;
                }
                break;
            case 4:
                if(walletPlayer4 == 0) {
                    walletPercent4 = 0;
                }else {
                    walletPercent4 = (walletPlayer4*100)/totalWallet;
                }
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public void setRecordResultsArrayList(int playerNumber, int position) {
        switch (playerNumber) {
            case 1:
                this.recordOfPlayer1Results.add(position);
                break;
            case 2:
                this.recordOfPlayer2Results.add(position);
                break;
            case 3:
                this.recordOfPlayer3Results.add(position);
                break;
            case 4:
                this.recordOfPlayer4Results.add(position);
                break;
            default:
                out.println("The number was wrong");
        }
    }

    private int returnPlayerScoreInRun(ArrayList<Integer> scoreResultList) {
        switch (scoreResultList.get(scoreResultList.size() - 1)) {
            case 1:
                return 4;
            case 2:
                return 3;
            case 3:
                return 2;
            case 4:
                return 1;
            default:
                out.println("The number was wrong");
                return 0;
        }
    }

    public void setPlayersTotalScore() {
        player1TotalScore += returnPlayerScoreInRun(recordOfPlayer1Results);
        player2TotalScore += returnPlayerScoreInRun(recordOfPlayer2Results);
        player3TotalScore += returnPlayerScoreInRun(recordOfPlayer3Results);
        player4TotalScore += returnPlayerScoreInRun(recordOfPlayer4Results);
    }

    public void setPlayerTurn(int playerNumber){
        switch (playerNumber) {
            case 1:
                this.player1Turn++;
                break;
            case 2:
                this.player2Turn++;
                break;
            case 3:
                this.player3Turn++;
                break;
            case 4:
                this.player4Turn++;
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public void setPlayerPurchases(int playerNumber){
        switch (playerNumber) {
            case 1:
                this.purchasesPlayer1++;
                break;
            case 2:
                this.purchasesPlayer2++;
                break;
            case 3:
                this.purchasesPlayer3++;
                break;
            case 4:
                this.purchasesPlayer4++;
                break;
            default:
                out.println("The number was wrong");
        }
    }

    /*
     * Creates a histogram of the degree distribution.
     */
    private void makeHistogram() {

        degreeDist = new Histogram("Degree Distribution", 10, 0,
                10000, this);

        degreeDist.createHistogramItem("P1", this.players,
                "getWallet");
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

    public ArrayList<Integer> getRecordOfPlayer1Results() {
        return recordOfPlayer1Results;
    }

    public ArrayList<Integer> getRecordOfPlayer2Results() {
        return recordOfPlayer2Results;
    }

    public ArrayList<Integer> getRecordOfPlayer3Results() {
        return recordOfPlayer3Results;
    }

    public ArrayList<Integer> getRecordOfPlayer4Results() {
        return recordOfPlayer4Results;
    }

    public int getPlayer1TotalScore() {
        return player1TotalScore;
    }

    public int getPlayer2TotalScore() {
        return player2TotalScore;
    }

    public int getPlayer3TotalScore() {
        return player3TotalScore;
    }

    public int getPlayer4TotalScore() {
        return player4TotalScore;
    }
}
