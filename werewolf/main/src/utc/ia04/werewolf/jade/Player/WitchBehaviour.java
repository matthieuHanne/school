package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WitchBehaviour extends CyclicBehaviour{

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message != null){
            System.out.println("WitchBehaviour - Received INFORM Message for Witch");
            if(message.getContent() != null){
                MessageFormat msgContent = MessageFormat.read(message.getContent());
                if(msgContent.getAction().equals("wakeWitch")){
                	((PlayerAgent)myAgent).fireOnChange(PlayerInfo.ENV_WT_WAKE, null);
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
        
        mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        message = myAgent.receive(mt);
        if(message != null){
            System.out.println("WitchBehaviour - Received REQUEST Message for Witch");
        	MessageFormat content = MessageFormat.read(message.getContent());
        	if(content.getAction().equals("witchSaveRequest")){
        		((PlayerAgent)myAgent).fireOnChange(PlayerInfo.ENV_WT_SAVE, content.getArg("killedPlayer"));
        	} else if(content.getAction().equals("witchKillRequest")){
        		((PlayerAgent)myAgent).fireOnChange(PlayerInfo.ENV_WT_KILL, content.getArg("playerList"));
        	} else {
        		myAgent.putBack(message);
        	}
        }
	}
}
