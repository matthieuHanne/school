package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class WitchSaveBehaviour extends OneShotBehaviour {

	boolean savePlayer;
	
	public WitchSaveBehaviour(boolean savePlayer) {
		this.savePlayer = savePlayer;
	}
	
	@Override
	public void action() {
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.addReceiver(Utils.getMaster(myAgent));
		MessageFormat content = new MessageFormat();
		content.setAction("witchSaveAnswer");
		content.addArg("savePlayer", savePlayer?"true":"false");
		message.setContent(content.toJSON());
		myAgent.send(message);
	}

}
