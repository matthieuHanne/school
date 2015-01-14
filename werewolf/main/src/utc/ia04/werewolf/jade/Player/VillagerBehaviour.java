package utc.ia04.werewolf.jade.Player;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.utils.MessageFormat;

import java.util.HashMap;

public class VillagerBehaviour extends Behaviour {

    public VillagerBehaviour(){
        System.out.println("VillagerBehaviour created");
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            System.out.println("VillagerBehaviour - Received Message for Villager");
            if(message.getContent() != null){
                MessageFormat msgContent = MessageFormat.read(message.getContent());
                if(msgContent.getAction().equals("wakeVillager")){

                } else if(msgContent.getAction().equals("voteVillagerUpdate")){
                    //((PlayerAgent)myAgent).updateVillagerVote((HashMap<String, Integer>)msgContent.getArg("votes"));
                } else if(msgContent.getAction().equals("getKilled")){
                    System.out.println("VillagerBehaviour - Get killed");
                    ((PlayerAgent)myAgent).getKilled();
                } else if(msgContent.getAction().equals("setDay")){
                    System.out.println("VillagerBehaviour - Day notification");
                    ((PlayerAgent)myAgent).setNight(false);
                } else if(msgContent.getAction().equals("setNight")){
                    System.out.println("VillagerBehaviour - Night notification");
                    ((PlayerAgent)myAgent).setNight(true);
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
