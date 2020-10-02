import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ListeningAgent extends Agent {
    public void setup(){
        addBehaviour(new ListeningBehaviour());
        System.out.println("Hello world! I'm an agent!" + " My local name is " + getAID().getLocalName() + "\n" + "My GUID is " + getAID().getName() + "\n" + "My addresses are " + String.join(",", getAID().getAddressesArray()));
    }

    public void takeDown(){
        System.out.println(getLocalName() + ": done working.");
    }

    class ListeningBehaviour extends CyclicBehaviour {
        public void action(){
            ACLMessage msg = receive();
            if(msg != null) {
                System.out.println(msg);
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Got Your Message!");
                send(reply);
            }else{
                block();
            }
        }
    }

}

