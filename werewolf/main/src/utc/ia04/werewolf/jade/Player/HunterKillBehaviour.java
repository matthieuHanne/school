package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class HunterKillBehaviour extends OneShotBehaviour {

	private String killPlayer;
	
	public HunterKillBehaviour(String killPlayer) {
		this.killPlayer = killPlayer;
	}
	@Override
	public void action() {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(Utils.getMaster(myAgent));
            MessageFormat content = new MessageFormat();
            content.setAction("hunterElimination");
            content.addArg("target",killPlayer);
            message.setContent(content.toJSON());
            myAgent.send(message);
	}

}
