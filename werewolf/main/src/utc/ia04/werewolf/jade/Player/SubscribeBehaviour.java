package utc.ia04.werewolf.jade.Player;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.Utils;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class SubscribeBehaviour extends SequentialBehaviour{

	private String name;

	//Where context is the field where the agent stored the ApplicationContext received as startup argument
	// We need Android librairie
	
	public SubscribeBehaviour(String name){
		this.name = name;
		System.out.println("Adding subBehaviours");
		addSubBehaviour(new RequestBehaviour());
		addSubBehaviour(new WaitBehaviour());
	}

	public class RequestBehaviour extends OneShotBehaviour{

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
			message.addReceiver(Utils.getMaster(myAgent));
			MessageFormat msgData;
			msgData = new MessageFormat();
			msgData.setAction("subscribe");
			msgData.addArg("nickname", name);
			msgData.addArg("age", 21);
			System.out.println(msgData.toJSON());
			message.setContent(msgData.toJSON());
			myAgent.send(message);
		}
		
	}
	
	public class WaitBehaviour extends Behaviour{
		
		private boolean roleReceived = false;

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			ACLMessage msg = myAgent.receive(template);
			if(msg != null){
				System.out.println("Connection confirmed, waiting for role");
				((PlayerAgent)myAgent).fireOnChange(PlayerInfo.WAITING_ROLE, null);
			}
			template = MessageTemplate.MatchPerformative(ACLMessage.FAILURE);
			msg = myAgent.receive(template);
			if(msg != null){
				System.err.println("Connection refused by host");
				/*
				 * AFFICHER CODE ANDROID POUR MESSAGE D'ERREUR
				 */
			}
			
			template = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			msg = myAgent.receive(template);
			if(msg != null){
				System.err.println("SubscribeBehaviour : Role received ("+msg.getContent()+")");
				MessageFormat message = MessageFormat.read(msg.getContent());
				if(message.getAction().equals("setRole")){
					System.out.println("SubscribeBehaviour - Role : "+message.getArg("roleName"));
					roleReceived = true; 
					((PlayerAgent)myAgent).setRole((String)message.getArg("roleName"));
				}
			}
		}
		
		@Override
		public boolean done(){
			return roleReceived;
		}
		
	}
}