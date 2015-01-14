package utc.ia04.werewolf.jade.GameMaster;

import java.util.ArrayList;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.Utils;
import utc.ia04.werewolf.utils.PlayerInfo.Role;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class UpdateLocalEnvironmentBehaviour extends SequentialBehaviour {

	private int state;
	private int cpt = 0;
	public UpdateLocalEnvironmentBehaviour(int state) {
		this.state = state;
		switch(state){
		case 0 : // Jour
			
			addSubBehaviour(new SendRequestDay());
			
			break;
		case 1 : // Nuit
			addSubBehaviour(new SendRequestNight());
			break;
		default : 
			break;
		}

	}
	
	public class SendRequestDay extends OneShotBehaviour{
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.QUERY_REF);
			MessageFormat content = new MessageFormat();
			message.addReceiver(Utils.getEnv(myAgent));
			
			// Get all players request 
			content.setAction("getAllPlayers");
			message.setContent(content.toJSON());
			myAgent.send(message);
			
			//Get werewolves request
			content.setAction("getnicknames");
			content.addArg("roleFilter", Role.Werewolf);
			message.setContent(content.toJSON());
			myAgent.send(message);
			
			
			// get lovers request 
			content.setAction("getLovers");
			message.setContent(content.toJSON());
			myAgent.send(message);
			
			// get hunter request 
			content.setAction("getnicknames");
			content.addArg("roleFilter", PlayerInfo.Role.Hunter);
			message.setContent(content.toJSON());
			myAgent.send(message);
			
			cpt = 0;
			addSubBehaviour(new ReceivedResponseDay());
		}

	}
	
	public class ReceivedResponseDay extends Behaviour{
		private static final long serialVersionUID = 1L;
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
			ACLMessage message = myAgent.receive(mt);
			if(message != null){
				MessageFormat mf = MessageFormat.read(message.getContent());
				if(mf.getAction().equals("sendAllPlayers")){
					cpt++;
					((GameMasterAgent)myAgent).setPlayersHm(mf.getPlayers("players"));
				} else if(mf.getAction().equals("sendLovers")){
					cpt++;
					ArrayList<String> list = (ArrayList<String>) mf.getArg("nicknames");
					if(list != null && list.size()>0){
						((GameMasterAgent)myAgent).setLovers((ArrayList<String>) mf.getArg("lovers"));
					} else {
						((GameMasterAgent)myAgent).setLovers(new ArrayList<String>());
						System.out.println("There is no lovers ("+(done()?"done)":"not done)"));
					}
				}
				
				if(mf.getAction().equals("nicknames") && mf.getArg("rolePlayer").equals("Werewolf")){
					cpt++;
					ArrayList<String> list = (ArrayList<String>) mf.getArg("nicknames");
					if(list != null && list.size()>0){
						((GameMasterAgent)myAgent).setWerewolves((ArrayList<String>) mf.getArg("nicknames"));
					} else {
						System.out.println("There is no werewolves ("+(done()?"done)":"not done)"));
						((GameMasterAgent)myAgent).setWerewolves(new ArrayList<String>());
					}
				}
				
				else if(mf.getAction().equals("nicknames")){
					cpt++;
					ArrayList<String> list = (ArrayList<String>) mf.getArg("nicknames");
					if(list != null && list.size()>0){
						((GameMasterAgent)myAgent).setHunter(list.get(0));
					} else {
						System.out.println("There is no hunter ("+(done()?"done)":"not done)"));
						((GameMasterAgent)myAgent).setHunter(null);
					}
					
				}
			}
		}

		@Override
		public boolean done() {
			return cpt == 4;
		}
		
	}
	
	public class SendRequestNight extends OneShotBehaviour{

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.QUERY_REF);
			MessageFormat content = new MessageFormat();
			// Get all players request 
			content.setAction("getAllPlayers");
			message.addReceiver(Utils.getEnv(myAgent));
			message.setContent(content.toJSON());
			myAgent.send(message);
			
			//Get werewolves 
			content.setAction("getnicknames");
			content.addArg("roleFilter", Role.Werewolf);
			message.setContent(content.toJSON());
			myAgent.send(message);
			
			//Get witch
			content.setAction("getnicknames");
			content.addArg("roleFilter", Role.Witch);
			message.setContent(content.toJSON());
			myAgent.send(message);

            //Get cupidon
            content.setAction("getnicknames");
            content.addArg("roleFilter", Role.Cupidon);
            message.setContent(content.toJSON());
            myAgent.send(message);
			
			cpt = 0;
			addSubBehaviour(new ReceiveResponseNight());
			
		}
	}
	
	public class ReceiveResponseNight extends Behaviour{
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
			ACLMessage message = myAgent.receive(mt);
			if(message != null){
				MessageFormat mf = MessageFormat.read(message.getContent());
				if(mf.getAction().equals("sendAllPlayers")){
					cpt++;
					System.out.println("UPDATELOCALENVIRONMENT -- Received "+mf.getPlayers("players").size()+" players");
					((GameMasterAgent)myAgent).setPlayersHm(mf.getPlayers("players"));
				} if(mf.getAction().equals("nicknames") && mf.getArg("rolePlayer").equals("Werewolf")){
					cpt++;
					ArrayList<String> list = (ArrayList<String>) mf.getArg("nicknames");
					if(list != null && list.size()>0){
						((GameMasterAgent)myAgent).setWerewolves((ArrayList<String>) mf.getArg("nicknames"));
					} else {
						System.out.println("There is no werewolves ("+(done()?"done)":"not done)"));
					}
				} if(mf.getAction().equals("nicknames") && mf.getArg("rolePlayer").equals("Witch")){
					cpt++;
					ArrayList<String> list = (ArrayList<String>) mf.getArg("nicknames");
					if(list != null && list.size()>0){
						((GameMasterAgent)myAgent).setWitch(((ArrayList<String>) mf.getArg("nicknames")).get(0));
					} else {
						System.out.println("There is no witch ("+(done()?"done)":"not done)"));
					}
					
				} if(mf.getAction().equals("nicknames") && mf.getArg("rolePlayer").equals("Cupidon")){
                    cpt++;
                    ArrayList<String> list = (ArrayList<String>) mf.getArg("nicknames");
                    if(list != null && list.size()>0){
                        ((GameMasterAgent)myAgent).setCupidon(((ArrayList<String>) mf.getArg("nicknames")).get(0));
                    } else {
                        System.out.println("There is no cupidon ("+(done()?"done)":"not done)"));
                    }

                }
			}
			
		}

		@Override
		public boolean done() {
			return cpt == 4;
		}
		
	}

}
