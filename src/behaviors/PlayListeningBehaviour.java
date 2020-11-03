package behaviors;

import game.Player;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class PlayListeningBehaviour extends Behaviour{
    Player player;

    public PlayListeningBehaviour(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        ACLMessage msg = this.player.receive();

        if (msg != null && !msg.getContent().equals("Got your message!")) {
            Object tmp = msg.getAllUserDefinedParameters().get("MESSAGE_TYPE");
            if (tmp != null) {
                switch (tmp.toString()) {
                    case "PLAY":
                        try {
                            this.player.move();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "BUY":
                        String[] splitInformation = msg.getContent().split("/");
                        if(splitInformation.length == 2) {
                            this.player.registerTransactionInLedger(Integer.parseInt(splitInformation[1]), Integer.parseInt(splitInformation[0]));
                        }
                        break;
                    case "PAYMENT":
                        this.player.receiveRentPayment(Integer.parseInt(msg.getContent()));
                        break;
                    case  "BUST":
                        this.player.removePlayerFromQueue(msg.getContent());
                        break;
                    default:
                        System.out.println("PlayListeningBehavior - ERROR: message type " + tmp.toString() + " unknown");
                }
            }
        } else {
            block();
        }

    }

    @Override
    public boolean done() {
        return false;
    }
}
