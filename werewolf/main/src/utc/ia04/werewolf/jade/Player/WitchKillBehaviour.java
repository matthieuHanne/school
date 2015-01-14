package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class WitchKillBehaviour extends OneShotBehaviour {

	String killPlayer;
	
	public WitchKillBehaviour(String player) {
		killPlayer = player;
	}
	
	@Override
	public void action() {
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(Utils.getMaster(myAgent));
		MessageFormat content = new MessageFormat();
		content.setAction("witchKillAnswer");
		content.addArg("killPlayer", killPlayer);
		message.setContent(content.toJSON());
		myAgent.send(message);
	}

}
