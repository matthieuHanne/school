package utc.ia04.werewolf.jade.Environment;

import utc.ia04.werewolf.utils.MessageFormat;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SetPlayerRoleBehaviour extends CyclicBehaviour {

	@Override
	public void action() {
		// TODO Auto-generated method stub
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
		ACLMessage msg = myAgent.receive(mt);
		if(msg != null){
			MessageFormat content = MessageFormat.read(msg.getContent());
			System.err.println("SetRoleBehaviour content : "+ msg.getContent());
			if(((EnvironmentAgent)myAgent).setPlayerRole(content.getAIDArg("AID"), (String)content.getArg("role"))){
				System.out.println("Correctly set player role");
			} else {
				System.out.println("Player wasn't found, no role set!");
			}
		} else {
			block();
		}
	}

}
