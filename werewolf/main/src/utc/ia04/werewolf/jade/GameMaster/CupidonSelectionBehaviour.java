package utc.ia04.werewolf.jade.GameMaster;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 16/06/2014
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class CupidonSelectionBehaviour extends Behaviour {
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROXY);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            MessageFormat content = MessageFormat.read(message.getContent());
            if(content.getAction().equals("loversSelection")){
                System.out.println("CupidonSelectionBehaviour - set lovers");
                ((GameMasterAgent)myAgent).setLovers((ArrayList<String>)content.getArg("lovers"));
            }
        }

    }

    @Override
    public boolean done() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
