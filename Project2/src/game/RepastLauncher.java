package game;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;
import utils.ColorHelper;

import java.util.ArrayList;

public class RepastLauncher extends Repast3Launcher {
    private Runtime runtimeInstance;
    private Profile profile;
    private ContainerController containerController;
    ArrayList<Player> players = new ArrayList<>();
    int numberOfAgents;

    @Override
    protected void launchJADE() {
        this.runtimeInstance = Runtime.instance();
        this.profile = new ProfileImpl(true);
        this.containerController = runtimeInstance.createMainContainer(profile);
        System.out.println("a minha vida é triste");
        players.clear();
        launchAgents();

        MonopolyMain frame = null;
        frame = new MonopolyMain(players);
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
    public int getNumberOfAgents() {
        return numberOfAgents;
    }
    @Override
    public String[] getInitParam() {
        return new String[]{"numberOfAgents"};
    }

    @Override
    public String getName() {
        return "192.168.56.1:1099/JADE";
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(3);   // works only in batch mode
        init.loadModel(new RepastLauncher(), null, false);
	}
}
