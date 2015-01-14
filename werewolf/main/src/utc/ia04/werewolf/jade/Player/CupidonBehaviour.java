package utc.ia04.werewolf.jade.Player;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 16/06/2014
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class CupidonBehaviour extends Behaviour {

    public CupidonBehaviour(){
        System.out.println("CupidonBehaviour created");
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            System.out.println("HunterBehaviour - Received Message for Hunter");
            if(message.getContent() != null){
                MessageFormat msgContent = MessageFormat.read(message.getContent());
                if(msgContent.getAction().equals("setLovers")){
                    System.out.println("CupidonBehaviour - Received playersList for setting lovers");
                    ((PlayerAgent)myAgent).cupidonLoversSelection((HashMap<String, AID>) msgContent.getArg("playersList"));
                } else if(msgContent.getAction().equals("loversSelection")){
                    ACLMessage loversSelection = new ACLMessage(ACLMessage.PROXY);
                    message.addReceiver(Utils.getMaster(myAgent));
                    MessageFormat content = new MessageFormat();
                    content.setAction("loversSelection");
                    content.addArg("lovers", (ArrayList<String>)msgContent.getArg("lovers"));
                    loversSelection.setContent(content.toJSON());
                    myAgent.send(loversSelection);
                }  else if(msgContent.getAction().equals("setDay")){
                    ((PlayerAgent)myAgent).setNight(false);
                } else if(msgContent.getAction().equals("setNight")){
                    ((PlayerAgent)myAgent).setNight(true);
                } else {
                    myAgent.putBack(message);
                }
            } else {
                myAgent.putBack(message);
            }
        }      }

    @Override
    public boolean done() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
