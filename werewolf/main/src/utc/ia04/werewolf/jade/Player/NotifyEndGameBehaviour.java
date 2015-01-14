package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class NotifyEndGameBehaviour extends CyclicBehaviour {

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage gameOver = myAgent.receive(mt);
		if(gameOver != null){
			MessageFormat content = MessageFormat.read(gameOver.getContent());
			if(content.getAction().equals("gameOver")){
				((PlayerAgent)myAgent).gameOver((String)content.getArg("winner"));
			} else {
				myAgent.putBack(gameOver);
			}
		}

	}

}
