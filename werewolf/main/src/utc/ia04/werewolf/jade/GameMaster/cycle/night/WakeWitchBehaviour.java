package utc.ia04.werewolf.jade.GameMaster.cycle.night;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.GameMasterBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.Utils;

public class  WakeWitchBehaviour extends SequentialBehaviour{
	
	
	GameMasterAgent gm = null;
	String nicknameWitch=null; 
	HashMap<String, AID> players=null;
	ArrayList<String> markedplayer=null; 
	
	public WakeWitchBehaviour(GameMasterAgent gm){
		this.gm = gm;
		this.addSubBehaviour(new WakeWitch());
	}
	

	/**
	 * Réveil de la sorcière
	 * Envoie inform à l'Agent dont le rôle est sorcière
	 * @author AudreyB
	 *
	 */
	private class WakeWitch extends OneShotBehaviour{
		@Override
		public void action(){
			players=gm.getPlayersHm(); 
			nicknameWitch=gm.getWitch(); 
			markedplayer =gm.getMarkedPlayers();
			boolean canKill = !gm.did_witchkill();
			boolean	canSave = !gm.did_witchsave();
			
			if(nicknameWitch!=null && (canKill || canSave)){
				if(canSave){
					addSubBehaviour(new SendKilledPlayer());
					addSubBehaviour(new WaitForSave());
				}
				if(canKill){
					addSubBehaviour(new SendAllPlayers());
					addSubBehaviour(new WaitForKill());
				}
			}
			
			
			System.out.println("WakeWitch - Wake the Witch...");
			ACLMessage informwitch = new ACLMessage(ACLMessage.INFORM); 
			MessageFormat contentforwitch = new MessageFormat(); 
			contentforwitch.setAction("wakeWitch");		
			informwitch.addReceiver(players.get(nicknameWitch));
			informwitch.setContent(contentforwitch.toJSON());
			gm.send(informwitch);
		}
	}
	
	private class SendKilledPlayer extends OneShotBehaviour{

		@Override
		public void action() {
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(players.get(nicknameWitch));
			MessageFormat content = new MessageFormat();
			content.setAction("witchSaveRequest");
			content.addArg("killedPlayer", gm.getMarkedPlayers().get(0));
			message.setContent(content.toJSON());
			gm.send(message);
		}
		
	}
	
	private class WaitForSave extends SimpleBehaviour{

		boolean messageReceived = false;
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage answer = gm.receive(mt);
			if(answer != null){
				MessageFormat content = MessageFormat.read(answer.getContent());
				if(content.getAction().equals("witchSaveAnswer")){
					messageReceived = true;
					if(content.getArg("savePlayer").equals("true")){
						gm.witchhassaved();
					}
				} else {
					gm.putBack(answer);
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return messageReceived;
		}
		
	}
	
	private class SendAllPlayers extends OneShotBehaviour{

		@Override
		public void action() {
			ArrayList<String> playersNick = new ArrayList<String>();
			for(String nick : players.keySet()){
				playersNick.add(nick);
			}
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.addReceiver(players.get(nicknameWitch));
			MessageFormat content = new MessageFormat();
			content.setAction("witchKillRequest");
			content.addArg("playerList", playersNick);
			message.setContent(content.toJSON());
			gm.send(message);
		}
		
	}
	
	private class WaitForKill extends SimpleBehaviour{

		boolean answerReceived = false;
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage answer = gm.receive(mt);
			if(answer != null){
				MessageFormat content = MessageFormat.read(answer.getContent());
				if(content.getAction().equals("witchKillAnswer")){
					answerReceived = true;
					if(!((String)content.getArg("killPlayer")).isEmpty()){
						gm.witchhaskilled((String)content.getArg("killPlayer"));
					}
				} else {
					gm.putBack(answer);
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return answerReceived;
		}
		
	}
}