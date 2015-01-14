package utc.ia04.werewolf.jade.GameMaster;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 14/06/2014
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
public class HunterEliminationBehaviour extends Behaviour {
	private boolean msgReceived = false;
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            MessageFormat content = MessageFormat.read(message.getContent());
            if(content.getAction().equals("hunterElimination")){
                String target = (String)content.getArg("target");
                System.out.println("HungerEliminationBehaviour - The hunter killed "+target);
                ((GameMasterAgent)myAgent).markPlayerAsKilled(target);
                msgReceived = true;
            }
        }

    }

    @Override
    public boolean done() {
        return msgReceived;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
