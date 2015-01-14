package utc.ia04.werewolf.jade.Player;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 14/06/2014
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class HunterBehaviour extends Behaviour {

    public HunterBehaviour(){
        System.out.println("HunterBehaviour created");
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            if(message.getContent() != null){
                MessageFormat msgContent = MessageFormat.read(message.getContent());
                System.out.println("HunterBehaviour - Received Message for Hunter "+msgContent.getAction());
                if(msgContent.getAction().equals("hunterNotification")){
                    System.out.println("HunterBehaviour - Get killed");
                    ((PlayerAgent)myAgent).getKilled();
                    ((PlayerAgent)myAgent).hunterTargetSelection((HashMap<String, AID>)msgContent.getArg("playersList"));
                    System.out.println("HunterBehaviour - Waiting for target");
                } else if(msgContent.getAction().equals("setDay")){
                    ((PlayerAgent)myAgent).setNight(false);
                } else if(msgContent.getAction().equals("setNight")){
                    ((PlayerAgent)myAgent).setNight(true);
                } else if(msgContent.getAction().equals("getKilled")){
                	// DO NOTHING, THROW IT IN THE GARBAGE
                } else {
                    myAgent.putBack(message);
                }
            } else {
                myAgent.putBack(message);
            }
        }
    }

    @Override
    public boolean done() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
