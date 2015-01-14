package utc.ia04.werewolf.jade.Player;

import java.util.HashMap;

import utc.ia04.werewolf.utils.MessageFormat;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * 
 * @author AudreyB
 *
 */
public class WerewolfBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;

	public WerewolfBehaviour(){
		System.out.println("WereWolfBehav created");
	}

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage message = myAgent.receive(mt);
		if(message != null){
			System.out.println("WerewolfBehaviour - Received Message for Werewolf");
			if(message.getContent() != null){
				MessageFormat msgContent = MessageFormat.read(message.getContent());
				if(msgContent.getAction().equals("wakeWerewolf")){
					((PlayerAgent)myAgent).setNight(true);
				} else if(msgContent.getAction().equals("voteWerewolfUpdate")){
					 ((PlayerAgent)myAgent).updateWerewolfVote((HashMap<String, Integer>)msgContent.getArg("votes"));
				} else if(msgContent.getAction().equals("voteWerewolfEnd")){
					((PlayerAgent)myAgent).werewolfEndVotes();
				}else if(msgContent.getAction().equals("getKilled")){
                    System.out.println("WerewolfeBehaviour - Get killed");
                    ((PlayerAgent)myAgent).getKilled();
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
		}
	}

}
