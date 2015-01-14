package utc.ia04.werewolf.jade.Environment;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 09/06/2014
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */
public class SetInfoBehaviour extends Behaviour {
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            System.out.println("Received message "+message.getContent());
            MessageFormat content = MessageFormat.read(message.getContent());
            String nickname = (String)content.getArg("nickname");

            if(content.getAction().equals("setInLove")){
                Boolean val = (Boolean)content.getArg("InLove");
                ((EnvironmentAgent)myAgent).players.get("nickname").isInLove=val;

            }
            else if(content.getAction().equals("setAlive")){
                Boolean val = (Boolean)content.getArg("Alive");
                ((EnvironmentAgent)myAgent).players.get("nickname").isAlive=val;
            }
            else if(content.getAction().equals("setRole")){
                PlayerInfo.Role val = (PlayerInfo.Role)content.getArg("Role");
                ((EnvironmentAgent)myAgent).players.get("nickname").playerrole=val;
            }
            else
                System.out.println("Error on SetInfo conditionals");
        }
    }

    @Override
    public boolean done() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
