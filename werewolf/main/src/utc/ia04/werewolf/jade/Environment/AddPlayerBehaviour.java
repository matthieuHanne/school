package utc.ia04.werewolf.jade.Environment;

import utc.ia04.werewolf.utils.MessageFormat;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AddPlayerBehaviour extends CyclicBehaviour {

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
		ACLMessage message = myAgent.receive(mt);
		if(message != null){
			System.out.println("AddPlayerBehaviour - Received message "+message.getContent());
			MessageFormat content = MessageFormat.read(message.getContent());
			if(content.getAction().equals("addPlayer")){
				System.out.println("AddPlayerBehaviour - Received new player");
				String nickname = (String)content.getArg("nickname");
				AID playerID = content.getAIDArg("AID");
				boolean success = ((EnvironmentAgent)myAgent).addPlayer(nickname, playerID);
				
				
				if(success){
					System.out.println("AddPlayerBahaviour - New Player added! ("+((EnvironmentAgent)myAgent).players.size()+")");
					ACLMessage reply = message.createReply();
					reply.setPerformative(ACLMessage.CONFIRM);
					
					MessageFormat replyContent = new MessageFormat();
					replyContent.setAction("addPlayerConfirm");
					replyContent.addArg("AID", playerID);
					replyContent.addArg("PlayerName", nickname);
					
					reply.setContent(replyContent.toJSON());
					myAgent.send(reply);
				} else {
					System.out.println("Failed adding new player!");
					ACLMessage reply = message.createReply();
					reply.setPerformative(ACLMessage.FAILURE);
					
					MessageFormat replyContent = new MessageFormat();
					replyContent.setAction("addPlayerFailure");
					replyContent.addArg("AID", playerID);
					
					reply.setContent(replyContent.toJSON());
					myAgent.send(reply);
				}
			}
		}		
	}

}
