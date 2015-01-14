package utc.ia04.werewolf.jade.GameMaster.cycle.init;

import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.GameMasterBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LogBehaviour extends GameMasterBehaviour{

	private static final long serialVersionUID = 1L;
	int step=0; 
    AID currentSubscribtion=null; 

	MessageTemplate mt = null;
	 
	@Override
	public void action() {
				switch(step){
				case 0 : 
					MessageTemplate mtt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
	    			ACLMessage messagefromplayer = myAgent.receive(mtt);
	    			if(messagefromplayer != null){
	    				if(((GameMasterAgent)myAgent).is_playing()){
	    					ACLMessage replytoplayer = messagefromplayer.createReply();
	    					replytoplayer.setPerformative(ACLMessage.FAILURE);
	    					MessageFormat contentforplayer = new MessageFormat();
	    					contentforplayer.setAction("informError");
	    					contentforplayer.addArg("informError", "game is playing, please wait for next part.");
	    					replytoplayer.setContent(contentforplayer.toJSON());
	    					myAgent.send(replytoplayer);
	    				}	else{
	    					currentSubscribtion= messagefromplayer.getSender();
	    					ACLMessage informEnv = new ACLMessage(ACLMessage.INFORM_REF); 
	    					informEnv.setConversationId(String.valueOf(((GameMasterAgent)myAgent).getConversationID()));
	    					String mirt = "confirm"+System.currentTimeMillis();
	    					informEnv.setReplyWith(mirt);
	    					MessageFormat contentforEnv = MessageFormat.read(messagefromplayer.getContent());
	    					contentforEnv.setAction("addPlayer");
	    					contentforEnv.addArg("AID", currentSubscribtion);
	    					informEnv.setContent(contentforEnv.toJSON());
	    					informEnv.addReceiver(Utils.getEnv(myAgent));
	    					myAgent.send(informEnv);
	    					mt = MessageTemplate.and(
	    		                    MessageTemplate.MatchConversationId(String.valueOf(((GameMasterAgent)myAgent).getConversationID())),
	    		                    MessageTemplate.MatchInReplyTo(mirt));
	    					((GameMasterAgent)myAgent).increaseconversationID();
	    					step=1; 
	    				}
	    				
	    			}else{
	    				block(); 
	    			}	
					break; 
				case 1 :
					ACLMessage messagefromEnv = myAgent.receive(mt);
					if(messagefromEnv !=null){
						if(messagefromEnv.getPerformative()==ACLMessage.CONFIRM){
							// Inform Table AgentAdded
	   						ACLMessage informTable = new ACLMessage(ACLMessage.INFORM);
	   						MessageFormat content = MessageFormat.read(messagefromEnv.getContent());
	   						content.setAction("add_player");
	   						
	   						System.out.println("LogBehav send "+content.toJSON());
	   					    informTable.setContent(content.toJSON());
	   						informTable.addReceiver(Utils.getTable(myAgent));
		    				myAgent.send(informTable);
		    				
		    				// Confirm to Player 
	    					ACLMessage confirmplayer = new ACLMessage(ACLMessage.CONFIRM);
	    					confirmplayer.addReceiver(currentSubscribtion); 
	    					MessageFormat contentconfirmplayer = new MessageFormat();
	    					contentconfirmplayer.setAction("subscribeok");
	    					contentconfirmplayer.addArg("subscribeok", "you have been added in game.");
	    					confirmplayer.setContent(contentconfirmplayer.toJSON());
	    					myAgent.send(confirmplayer);
	    					currentSubscribtion=null; 
						} 
						step=0;
					}else{
						block();
					}
			    break; 
				}
	}

	@Override
	public boolean done() {
		
		return false;
	}
}



