package game;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import sajas.core.Runtime;
import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import uchicago.src.sim.engine.SimInit;

public class RepastLauncher extends Repast3Launcher {
    private Runtime runtimeInstance;
    private Profile profile;
    private ContainerController containerController;

    @Override
    protected void launchJADE() {
        this.runtimeInstance = Runtime.instance();
        this.profile = new ProfileImpl(true);
        this.containerController = runtimeInstance.createMainContainer(profile);

        MonopolyMain frame = null;
        try {
            frame = new MonopolyMain(containerController);
            frame.setVisible(true);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getInitParam() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Monopoly Main -- SAJaS Repast3 Implementation";
    }

    public static void main(String[] args) {
        SimInit init = new SimInit();
        init.setNumRuns(1);   // works only in batch mode
        init.loadModel(new RepastLauncher(), null, true);
	}
}
