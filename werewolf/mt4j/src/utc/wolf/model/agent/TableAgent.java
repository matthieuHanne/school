package utc.wolf.model.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utc.ia04.werewolf.jade.behaviour.VoteDayBehaviourTest;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.wolf.model.Constants;

public class TableAgent extends Agent {

	@Override
	protected void setup() {
		super.setup();
		
		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        ServiceDescription sd = new ServiceDescription();
        sd.setName("TableAgent");
        sd.setType("Agent");
        dfd.addServices(sd);
        
        try {  
            DFService.register( this, dfd );  
        }
        catch (FIPAException fe) {
            fe.printStackTrace(); 
        }
        
		//addBehaviour(new WaitPlayerBehaviour());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addBehaviour(new VoteDayBehaviourTest());
		System.out.println(getLocalName() + "--> Installed");
	}
  private class WaitPlayerBehaviour extends CyclicBehaviour {

	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
		ACLMessage message = receive(mt);
		if (message != null) {
			String playername = message.getContent();
			MessageFormat mf = new MessageFormat();
			mf.setAction("add_player");
			mf.addArg("playerName",playername);
			ACLMessage propagate = new ACLMessage(ACLMessage.REQUEST);
			propagate.addReceiver(new AID(Constants.GUI_AGENT,AID.ISLOCALNAME));
			propagate.setContent(mf.toJSON());
			send(propagate);
		}
		else block();
	}
	  
  }
  
}
