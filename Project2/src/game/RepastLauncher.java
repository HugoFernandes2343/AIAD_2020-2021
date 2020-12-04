package game;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.Histogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.SimInit;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

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

    int maxPurchasesPlayer1 = 0;
    int maxPurchasesPlayer2 = 0;
    int maxPurchasesPlayer3 = 0;
    int maxPurchasesPlayer4 = 0;

    private ArrayList<Integer> recordOfPlayer1Wallets = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer2Wallets = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer3Wallets = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer4Wallets = new ArrayList<>();

    private ArrayList<Integer> averageOfPlayer1Wallets = new ArrayList<>();
    private ArrayList<Integer> averageOfPlayer2Wallets = new ArrayList<>();
    private ArrayList<Integer> averageOfPlayer3Wallets = new ArrayList<>();
    private ArrayList<Integer> averageOfPlayer4Wallets = new ArrayList<>();

    private ArrayList<Integer> recordOfPlayer1Results = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer2Results = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer3Results = new ArrayList<>();
    private ArrayList<Integer> recordOfPlayer4Results = new ArrayList<>();

    private int player1TotalScore = 0;
    private int player2TotalScore = 0;
    private int player3TotalScore = 0;
    private int player4TotalScore = 0;

    private Histogram player1WalletAverageHistogram = new Histogram("P1 Wallet Average Distribution", 4, 0,
            4500);
    private Histogram player2WalletAverageHistogram = new Histogram("P2 Wallet Average Distribution", 4, 0,
            4500);
    private Histogram player3WalletAverageHistogram = new Histogram("P3 Wallet Average Distribution", 4, 0,
            4500);
    private Histogram player4WalletAverageHistogram = new Histogram("P4 Wallet Average Distribution", 4, 0,
            4500);

    private Histogram numberOfTimesHousesWereBoughtByWinningPlayerHistogram = new Histogram("Number Of Times A House Was Bought", 36, 0 ,36);

    private OpenSequenceGraph plotTotalPointsPlayer1 = new OpenSequenceGraph("Total Points By Player 1", this);
    private OpenSequenceGraph plotTotalPointsPlayer2 = new OpenSequenceGraph("Total Points By Player 2", this);
    private OpenSequenceGraph plotTotalPointsPlayer3 = new OpenSequenceGraph("Total Points By Player 3", this);
    private OpenSequenceGraph plotTotalPointsPlayer4 = new OpenSequenceGraph("Total Points By Player 4", this);

    private OpenSequenceGraph plotPlayer1Turns = new OpenSequenceGraph("Total Turns By Player 1", this);
    private OpenSequenceGraph plotPlayer2Turns = new OpenSequenceGraph("Total Turns By Player 2", this);
    private OpenSequenceGraph plotPlayer3Turns = new OpenSequenceGraph("Total Turns By Player 3", this);
    private OpenSequenceGraph plotPlayer4Turns = new OpenSequenceGraph("Total Turns By Player 4", this);

    private OpenSequenceGraph plotTotalPlayTimeByRun = new OpenSequenceGraph("Total Time Of Play By Run", this);
    private OpenSequenceGraph plotMaxPurchasesByPlayer = new OpenSequenceGraph("Max Purchases By Player", this);

    private long runTotalTime = 0;

    private long startTime = System.currentTimeMillis();

    private static int numRuns = 1;
    private ArrayList<Integer> listOfNumberOfTimesHouseWasBought = new ArrayList<>();

    @Override
    protected void launchJADE() {
        this.runtimeInstance = Runtime.instance();
        this.profile = new ProfileImpl(true);
        this.containerController = runtimeInstance.createMainContainer(profile);
        players.clear();
        launchAgents();

        recordOfPlayer1Wallets.add(1500);
        recordOfPlayer2Wallets.add(1500);
        recordOfPlayer3Wallets.add(1500);
        recordOfPlayer4Wallets.add(1500);

        RepastLauncher r = this;
        ContainerController c =this.containerController;


        makeHistograms();
        //makeTotalTimePlot();
        //makeMaxPurchasesPlot();
        //makePlayerTurnsPlot();
        //makeTotalPointsPlot();

        Thread thread = new Thread("New Thread") {
            public void run() {
                MonopolyMain frame = null;
                frame = new MonopolyMain(players, r);
                frame.setVisible(true);
                frame.start();
                String numberRun = String.valueOf(numRuns);
                long startTime = Instant.now().toEpochMilli();;
                long elapsedTime,currentTime;
                System.out.println("IM HERE cunt"+" run number: "+ numberRun);
                boolean cleanup=false;
                do{
                    if(!frame.isVisible()){
                        System.out.println("i KMS cuz of visible");
                        break;
                    }
                    if(!frame.isEnabled()){
                        System.out.println("i KMS cuz of enabled");
                        break;
                    }
                    currentTime = Instant.now().toEpochMilli();
                    elapsedTime = currentTime - startTime;

                    if(elapsedTime>=110000){
                        System.out.println("i KMS cuz of time");
                        cleanup = true;
                    }

                }while(elapsedTime < 110000);

                System.out.println("afterhours" + " run number: "+ numberRun);
                numRuns++;
                resetTimeStamps();
                resetPlayerTurns();

                if(cleanup){
                    System.out.println("Starting the cleanup");
                    boolean playtime = false;
                    for (int i = 0; i < players.size() ; i++) {
                        if(players.get(i).isAlive()){
                            players.get(i).takeDown();

                            if(!playtime){
                                r.setTotalPlayerPlayTime(System.currentTimeMillis());
                                playtime=true;
                            }
                        }

                    }
                    frame.setVisible(false);
                    frame.dispose();
                }else{
                    System.out.println("No need to cleanup");
                }
                try {
                    c.getPlatformController().kill();
                } catch (ControllerException e) {
                    e.printStackTrace();
                }
                r.stopSimulation();


            }
        };

        thread.start();
    }

    private void launchAgents() {
        try {

            Player player1 = new Player(1, 1);
            Player player2 = new Player(2, 2, 3);
            Player player3 = new Player(3, 3);
            Player player4 = new Player(4, 4);

            players.add(player1);
            players.add(player2);
            players.add(player3);
            players.add(player4);
            setWalletPlayer(1, 1500);
            setWalletPlayer(2, 1500);
            setWalletPlayer(3, 1500);
            setWalletPlayer(4, 1500);
            setWalletPercentage(1);
            setWalletPercentage(2);
            setWalletPercentage(3);
            setWalletPercentage(4);
            setNumberOfAgents(4);

            for (int i = 1; i < 5; i++) {
                String id = "player_" + i;
                AgentController agentController = this.containerController.acceptNewAgent(id, players.get(i - 1));
                agentController.start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    public void setNumberOfAgents(int numberOfAgents) {
        this.numberOfAgents = numberOfAgents;
    }

    public void decrementNumberOfAgents() {
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
        init.setNumRuns(50);   // works only in batch mode
        init.loadModel(new RepastLauncher(), null, true);
        numRuns = 1;
    }

    private void resetPlayerTurns() {
        this.player1Turn = 0;
        this.player2Turn = 0;
        this.player3Turn = 0;
        this.player4Turn = 0;
    }

    private void makeHistograms() {
/*        makePlayer1WalletAverageHistogram();
        player1WalletAverageHistogram.display();
        makePlayer2WalletAverageHistogram();
        player2WalletAverageHistogram.display();
        makePlayer3WalletAverageHistogram();
        player3WalletAverageHistogram.display();
        makePlayer4WalletAverageHistogram();
        player4WalletAverageHistogram.display();*/
        makeHousesBoughtHistogram();
        numberOfTimesHousesWereBoughtByWinningPlayerHistogram.display();
    }

    private void makeTotalPointsPlot() {
        plotTotalPointsPlayer1.setXRange(0, 1000);
        plotTotalPointsPlayer1.setYRange(0, 1000);
        plotTotalPointsPlayer2.setXRange(0, 1000);
        plotTotalPointsPlayer2.setYRange(0, 1000);
        plotTotalPointsPlayer3.setXRange(0, 1000);
        plotTotalPointsPlayer3.setYRange(0, 1000);
        plotTotalPointsPlayer4.setXRange(0, 1000);
        plotTotalPointsPlayer4.setYRange(0, 1000);
        if (numRuns == 1) {
            plotTotalPointsPlayer1.addSequence("P1", new Sequence() {
                public double getSValue() {
                    return recordOfPlayer1Wallets.get(recordOfPlayer1Wallets.size() - 1);
                }
            }, Color.RED);
            plotTotalPointsPlayer2.addSequence("P2", new Sequence() {
                public double getSValue() {
                    return recordOfPlayer2Wallets.get(recordOfPlayer2Wallets.size() - 1);
                }
            }, Color.BLUE);
            plotTotalPointsPlayer3.addSequence("P3", new Sequence() {
                public double getSValue() {
                    return recordOfPlayer3Wallets.get(recordOfPlayer3Wallets.size() - 1);
                }
            }, Color.YELLOW);
            plotTotalPointsPlayer4.addSequence("P4", new Sequence() {
                public double getSValue() {
                    return recordOfPlayer4Wallets.get(recordOfPlayer4Wallets.size() - 1);
                }
            }, Color.GREEN);
        }
        plotTotalPointsPlayer1.display();
        plotTotalPointsPlayer2.display();
        plotTotalPointsPlayer3.display();
        plotTotalPointsPlayer4.display();

        getSchedule().scheduleActionAtInterval(1, plotTotalPointsPlayer1, "step");
        getSchedule().scheduleActionAtInterval(1, plotTotalPointsPlayer2, "step");
        getSchedule().scheduleActionAtInterval(1, plotTotalPointsPlayer3, "step");
        getSchedule().scheduleActionAtInterval(1, plotTotalPointsPlayer4, "step");
    }

    private void makePlayerTurnsPlot() {
        plotPlayer1Turns.setXRange(0, 1000);
        plotPlayer1Turns.setYRange(0, 1000);
        plotPlayer2Turns.setXRange(0, 1000);
        plotPlayer2Turns.setYRange(0, 1000);
        plotPlayer3Turns.setXRange(0, 1000);
        plotPlayer3Turns.setYRange(0, 1000);
        plotPlayer4Turns.setXRange(0, 1000);
        plotPlayer4Turns.setYRange(0, 1000);
        if (numRuns == 1) {
            plotPlayer1Turns.addSequence("P1", new Sequence() {
                public double getSValue() {
                    return player1Turn;
                }
            }, Color.RED);
            plotPlayer2Turns.addSequence("P2", new Sequence() {
                public double getSValue() {
                    return player2Turn;
                }
            }, Color.BLUE);
            plotPlayer3Turns.addSequence("P3", new Sequence() {
                public double getSValue() {
                    return player3Turn;
                }
            }, Color.YELLOW);
            plotPlayer4Turns.addSequence("P4", new Sequence() {
                public double getSValue() {
                    return player4Turn;
                }
            }, Color.GREEN);
        }
        plotPlayer1Turns.display();
        plotPlayer2Turns.display();
        plotPlayer3Turns.display();
        plotPlayer4Turns.display();

        getSchedule().scheduleActionAtInterval(1, plotPlayer1Turns, "step");
        getSchedule().scheduleActionAtInterval(1, plotPlayer2Turns, "step");
        getSchedule().scheduleActionAtInterval(1, plotPlayer3Turns, "step");
        getSchedule().scheduleActionAtInterval(1, plotPlayer4Turns, "step");
    }

    private void makeMaxPurchasesPlot() {
        plotMaxPurchasesByPlayer.setXRange(0, 1);

        plotMaxPurchasesByPlayer.setYRange(0, 1);
        if (numRuns == 1) {
            plotMaxPurchasesByPlayer.addSequence("P1", new Sequence() {
                public double getSValue() {
                    return maxPurchasesPlayer1;
                }
            }, Color.RED);
            plotMaxPurchasesByPlayer.addSequence("P2", new Sequence() {
                public double getSValue() {
                    return maxPurchasesPlayer2;
                }
            }, Color.BLUE);
            plotMaxPurchasesByPlayer.addSequence("P3", new Sequence() {
                public double getSValue() {
                    return maxPurchasesPlayer3;
                }
            }, Color.YELLOW);
            plotMaxPurchasesByPlayer.addSequence("P4", new Sequence() {
                public double getSValue() {
                    return maxPurchasesPlayer4;
                }
            }, Color.GREEN);
        }
        plotMaxPurchasesByPlayer.display();

        getSchedule().scheduleActionAtInterval(1, plotMaxPurchasesByPlayer, "step");
    }

    public void resetTimeStamps() {
        this.runTotalTime = 0;
    }

    public void setWalletPlayer(int playerNumber, int wallet) {
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

    public void setNumberOfTimesHouseWasBoughtByWinningPlayer(ArrayList<Integer> housesBoughtByWinningPlayer){
        this.listOfNumberOfTimesHouseWasBought.addAll(housesBoughtByWinningPlayer);
    }

    public void setWalletPercentage(int playerNumber) {
        int totalWallet = walletPlayer1 + walletPlayer2 + walletPlayer3 + walletPlayer4;
        switch (playerNumber) {
            case 1:
                if (walletPlayer1 == 0) {
                    walletPercent1 = 0;
                } else {
                    walletPercent1 = (walletPlayer1 * 100) / totalWallet;
                }
                break;
            case 2:
                if (walletPlayer2 == 0) {
                    walletPercent2 = 0;
                } else {
                    walletPercent2 = (walletPlayer2 * 100) / totalWallet;
                }
                break;
            case 3:
                if (walletPlayer3 == 0) {
                    walletPercent3 = 0;
                } else {
                    walletPercent3 = (walletPlayer3 * 100) / totalWallet;
                }
                break;
            case 4:
                if (walletPlayer4 == 0) {
                    walletPercent4 = 0;
                } else {
                    walletPercent4 = (walletPlayer4 * 100) / totalWallet;
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

    public void setPlayerTurn(int playerNumber) {
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

    public void setRecordPlayerWallets(int playerNumber, int walletValue) {
        switch (playerNumber) {
            case 1:
                this.recordOfPlayer1Wallets.add(walletValue);
                break;
            case 2:
                this.recordOfPlayer2Wallets.add(walletValue);
                break;
            case 3:
                this.recordOfPlayer3Wallets.add(walletValue);
                break;
            case 4:
                this.recordOfPlayer4Wallets.add(walletValue);
                break;
            default:
                out.println("The number was wrong");
        }
    }

    public void setMaxPlayerPurchases(int playerNumber, int numberOfPurchases) {
        switch (playerNumber) {
            case 1:
                //if(numberOfPurchases > this.maxPurchasesPlayer1) {
                    this.maxPurchasesPlayer1 = numberOfPurchases;
                //}
                break;
            case 2:
                //if(numberOfPurchases > this.maxPurchasesPlayer2) {
                    this.maxPurchasesPlayer2 = numberOfPurchases;
                //}
                break;
            case 3:
                //if(numberOfPurchases > this.maxPurchasesPlayer3) {
                    this.maxPurchasesPlayer3 = numberOfPurchases;
                //}
                break;
            case 4:
                //if(numberOfPurchases > this.maxPurchasesPlayer3) {
                    this.maxPurchasesPlayer4 = numberOfPurchases;
                //}
                break;
            default:
                out.println("The number was wrong");
        }
    }

    private int returnPlayerAverage(ArrayList<Integer> list) {
        int sum = 0;
        for (Integer recordedWalletValue : list) {
            if (recordedWalletValue != 0) {
                sum += recordedWalletValue;
            }
        }
        return sum / list.size() - 1;
    }

    public void setAverageOfPlayerWallets(int playerNumber) {
        switch (playerNumber) {
            case 1:
                averageOfPlayer1Wallets.add(returnPlayerAverage(this.recordOfPlayer1Wallets));
                break;
            case 2:
                averageOfPlayer2Wallets.add(returnPlayerAverage(this.recordOfPlayer2Wallets));
                break;
            case 3:
                averageOfPlayer3Wallets.add(returnPlayerAverage(this.recordOfPlayer3Wallets));
                break;
            case 4:
                averageOfPlayer4Wallets.add(returnPlayerAverage(this.recordOfPlayer4Wallets));
                break;
            default:
                out.println("The number was wrong");
        }
    }

    private void makeTotalTimePlot() {
        plotTotalPlayTimeByRun.setXRange(0, 1000);
        plotTotalPlayTimeByRun.setYRange(0, 10000);
        
        if (numRuns == 1) {
            plotTotalPlayTimeByRun.addSequence("Run", new Sequence() {
                public double getSValue() {
                    return runTotalTime;
                }
            }, Color.BLACK);
        }
        plotTotalPlayTimeByRun.display();

        getSchedule().scheduleActionAtInterval(1, plotTotalPlayTimeByRun, "step");
    }

    /*
     * Creates a histogram of the degree distribution.
     */
    private void makePlayer1WalletAverageHistogram() {

        BinDataSource source1 = new BinDataSource() {
            @Override
            public double getBinValue(Object o) {
                return Double.parseDouble(o.toString());
            }
        };

        player1WalletAverageHistogram.setXRange(100, 300);
        player1WalletAverageHistogram.setYRange(10, 3000);

        if (numRuns == 1) {
            player1WalletAverageHistogram.createHistogramItem("P1", this.averageOfPlayer1Wallets, source1);
        }

        getSchedule().scheduleActionAtInterval(1, player1WalletAverageHistogram, "step");
    }

    /*
     * Creates a histogram of the degree distribution.
     */
    private void makePlayer2WalletAverageHistogram() {

        BinDataSource source1 = new BinDataSource() {
            @Override
            public double getBinValue(Object o) {
                return Double.parseDouble(o.toString());
            }
        };

        player2WalletAverageHistogram.setXRange(100, 300);
        player2WalletAverageHistogram.setYRange(10, 3000);

        if (numRuns == 1) {
            player2WalletAverageHistogram.createHistogramItem("P2", this.averageOfPlayer2Wallets, source1);
        }

        getSchedule().scheduleActionAtInterval(1, player2WalletAverageHistogram, "step");
    }

    /*
     * Creates a histogram of the degree distribution.
     */
    private void makePlayer3WalletAverageHistogram() {

        BinDataSource source1 = new BinDataSource() {
            @Override
            public double getBinValue(Object o) {
                return Double.parseDouble(o.toString());
            }
        };

        player3WalletAverageHistogram.setXRange(100, 300);
        player3WalletAverageHistogram.setYRange(10, 3000);

        if (numRuns == 1) {
            player3WalletAverageHistogram.createHistogramItem("P3", this.averageOfPlayer3Wallets, source1);
        }

        getSchedule().scheduleActionAtInterval(1, player3WalletAverageHistogram, "step");
    }

    /*
     * Creates a histogram of the degree distribution.
     */
    private void makePlayer4WalletAverageHistogram() {

        BinDataSource source1 = new BinDataSource() {
            @Override
            public double getBinValue(Object o) {
                return Double.parseDouble(o.toString());
            }
        };

        player4WalletAverageHistogram.setXRange(100, 300);
        player4WalletAverageHistogram.setYRange(10, 3000);
        player4WalletAverageHistogram.setAxisTitles("Houses", "");

        if (numRuns == 1) {
            player4WalletAverageHistogram.createHistogramItem("P3", this.averageOfPlayer4Wallets, source1);
        }

        getSchedule().scheduleActionAtInterval(1, player4WalletAverageHistogram, "step");
    }

    /*
     * Creates a histogram of the degree distribution.
     */
    private void makeHousesBoughtHistogram() {
        BinDataSource source1 = new BinDataSource() {
            @Override
            public double getBinValue(Object o) {
                return Double.parseDouble(o.toString());
            }
        };

        numberOfTimesHousesWereBoughtByWinningPlayerHistogram.setAxisTitles("Houses By Number", "Number Of Times Winning Player Bought It");

        if (numRuns == 1) {
            numberOfTimesHousesWereBoughtByWinningPlayerHistogram.createHistogramItem("", this.listOfNumberOfTimesHouseWasBought, source1);
        }

        getSchedule().scheduleActionAtInterval(1, numberOfTimesHousesWereBoughtByWinningPlayerHistogram, "step");
    }

    public void setTotalPlayerPlayTime(long finishTime) {
        this.runTotalTime = finishTime - this.getStartTime();
    }

    public long getStartTime() {
        return startTime;
    }
}
