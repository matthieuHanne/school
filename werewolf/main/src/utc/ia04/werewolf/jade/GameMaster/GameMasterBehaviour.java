	package utc.ia04.werewolf.jade.GameMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import utc.ia04.werewolf.jade.Environment.EnvironmentAgent;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import jade.core.AID;
import utc.ia04.werewolf.utils.PlayerInfo;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

	
	public abstract class GameMasterBehaviour extends Behaviour{

		protected MessageTemplate requestListofSpecPlayer(PlayerInfo.Role role){
				ACLMessage askEnv = new ACLMessage(ACLMessage.QUERY_REF	); 
				MessageFormat askedcontent = new MessageFormat();
				askedcontent.setAction("getAID");
				askedcontent.addArg("roleFilter", role.toString());
				askEnv.setContent(askedcontent.toJSON());
				askEnv.addReceiver(Utils.getEnv(myAgent));
				askEnv.setConversationId(String.valueOf(((GameMasterAgent)myAgent).getConversationID()));
				String mirt = role.toString()+System.currentTimeMillis();
				askEnv.setReplyWith(mirt);
				MessageTemplate mt = MessageTemplate.and(
	                    MessageTemplate.MatchConversationId(String.valueOf(((GameMasterAgent)myAgent).getConversationID())),
	                    MessageTemplate.MatchInReplyTo(mirt));
				((GameMasterAgent)myAgent).increaseconversationID(); 
				myAgent.send(askEnv);
				return mt; 
		}
	
		protected MessageTemplate requestNicknames(){
			ACLMessage askEnv = new ACLMessage(ACLMessage.QUERY_REF	); 
			MessageFormat askedcontent = new MessageFormat();
			askedcontent.setAction("nicknames");
			askEnv.setContent(askedcontent.toJSON());
			askEnv.addReceiver(Utils.getEnv(myAgent));
			askEnv.setConversationId(String.valueOf(((GameMasterAgent)myAgent).getConversationID()));
			String mirt ="nicknames" +System.currentTimeMillis();
			askEnv.setReplyWith(mirt);
			MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchConversationId(String.valueOf(((GameMasterAgent)myAgent).getConversationID())),
                    MessageTemplate.MatchInReplyTo(mirt));
			((GameMasterAgent)myAgent).increaseconversationID(); 
			myAgent.send(askEnv);
			return mt;
	}
	
		
		protected ArrayList<AID> receiveListofSpecPlayer(MessageTemplate mt){
			ACLMessage messagefromEnv = myAgent.receive(mt);
			ArrayList<AID> specplayers=null;
			if(messagefromEnv!=null){
				MessageFormat listofplayer =MessageFormat.read(messagefromEnv.getContent());
				specplayers= (ArrayList<AID>) listofplayer.getArg("AIDplayers");
			}
			return specplayers;
	    }
		
		protected ArrayList<String> receiveListofNicknames(MessageTemplate mt){
			ArrayList<String> nicknames=null;
			ACLMessage messagefromEnv = myAgent.receive(mt);
			if(messagefromEnv!=null){
				MessageFormat listofplayer =MessageFormat.read(messagefromEnv.getContent());
				nicknames= (ArrayList<String>) listofplayer.getArg("nicknames");
			}
			return nicknames; 
	    }
		
		

}
