package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;

import utc.ia04.werewolf.utils.Utils;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class WerewolfVoteBehaviour extends OneShotBehaviour {

	private String oldVote, newVote;
		public WerewolfVoteBehaviour(String oldVote, String newVote){
		this.oldVote = oldVote;
		this.newVote = newVote;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.addReceiver(Utils.getMaster(myAgent));
		MessageFormat content = new MessageFormat();
		content.setAction("voteWerewolf");
		content.addArg("oldVote", oldVote);
		content.addArg("newVote", newVote);
		message.setContent(content.toJSON());
		myAgent.send(message);
	}

}
