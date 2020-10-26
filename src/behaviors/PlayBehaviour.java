package behaviors;

import game.Player;
import jade.core.behaviours.Behaviour;

public class PlayBehaviour extends Behaviour{
    Player player;

    public PlayBehaviour(Player player) {
        this.player = player;
    }

    @Override
    public void action() {

    }

    @Override
    public boolean done() {
        return false;
    }
}
