package utc.ia04.werewolf.jade.GameMaster.cycle.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import utc.ia04.werewolf.jade.GameMaster.DayCycleBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.UpdateLocalEnvironmentBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.Utils;
import utc.ia04.werewolf.utils.PlayerInfo.Role;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GameBeginBehaviour extends SequentialBehaviour{

	private ArrayList<AID> players = null;
	private GameMasterAgent gm;
	
	public GameBeginBehaviour(GameMasterAgent gm) {
		this.gm = gm;

		addSubBehaviour(new WaitForTable());
		//addSubBehaviour(new QueryPlayers());
		//addSubBehaviour(new ReceivePlayers());
		/**this.players = new ArrayList<AID>();
		HashMap<String, AID> hm = ((GameMasterAgent)myAgent).getPlayersHm();
		for (String nickname : hm.keySet()) {
			this.players.add(hm.get(nickname));
		}*/

		addSubBehaviour(new UpdateLocalEnvironmentBehaviour(1));
		addSubBehaviour(new SetRoles());
	}
	private class WaitForTable extends Behaviour{

		boolean messageReceived = false;
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage message = myAgent.receive(mt);
			if(message != null){
				((GameMasterAgent)myAgent).setPlaying();
				((GameMasterAgent)myAgent).resetRound();
				messageReceived = true;
				System.out.println("==== Game Has Started! ====");
			}
		}

		@Override
		public boolean done() {
			return messageReceived;
		}
		
	}
	
	private class QueryPlayers extends OneShotBehaviour{
		public void action() {
			ACLMessage request = new ACLMessage(ACLMessage.QUERY_REF);
			request.addReceiver(Utils.getEnv(gm));
			MessageFormat content = new MessageFormat();
			content.setAction("getAllPlayers");
			//content.addArg("roleFilter", PlayerInfo.Role.Player.toString());
			request.setContent(content.toJSON());
			gm.send(request);
		}
	}
	
	private class ReceivePlayers extends Behaviour{
		boolean messageReceived = false;
		
		

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
			ACLMessage answer = myAgent.receive(mt);
			if(answer != null){
				MessageFormat answContent = MessageFormat.read(answer.getContent());
				
				System.err.println(answer.getContent());
				if(answContent.getAction().equals("sendAllPlayers")){

					players = new ArrayList<AID>();
					//((GameMasterAgent)myAgent).setPlayers(players);
					System.out.println("Got List of AIDs : "+players.size());
					messageReceived = true;
				} else {
					myAgent.putBack(answer);
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return messageReceived;
		}
	
	}
	private class SetRoles extends OneShotBehaviour{

		private int werewolves = 0;
		private boolean witch = false, cupidon = false, hunter = false, medium = false;
		private HashMap<AID, PlayerInfo.Role> roles = new LinkedHashMap<AID, PlayerInfo.Role>();
		
		@Override
		public void action() {
			// TO test ! 
			HashMap<String, AID> hm = gm.getPlayersHm();
			AID audreyAID = hm.get("Audrey");
			System.err.println("Audrey = "+audreyAID);
			
			players = gm.getplayers();
			System.out.println("Setting roles !");
			if(players.size() < 5){
				werewolves = 2;
				witch = false;
			} else {
				werewolves = 2;
				witch = true;
				cupidon = true;
				hunter = true;
			}
			
			
			for(int i = werewolves ; i > 0 ; i--){
				AID p = getPlayerAtRandom();
				if(p.equals(audreyAID)){
					roles.put(p, Role.Hunter);
					System.err.println("Hunter added for audrey");
				} else {
					roles.put(p, Role.Werewolf);
				}
			}
			AID player = getPlayerAtRandom();
			/**if(witch && player != null){
				roles.put(player, Role.Witch);
				player = getPlayerAtRandom();
			}*/

            /**if(hunter){
                roles.put(getPlayerAtRandom(), Role.Hunter);
            }

            if(cupidon){
                roles.put(getPlayerAtRandom(), Role.Cupidon);
            }*/

			while(player != null){
				
				if(player.equals(audreyAID)){
					System.out.println("Adding Hunter!");
					roles.put(player, Role.Hunter);
				} else {
					System.out.println("Adding Villager!");
					roles.put(player, Role.Villager);
				}
				player = getPlayerAtRandom();
			}
			
			// sending roles!
			AID env = Utils.getEnv(myAgent);
			for(AID agent : roles.keySet()){
				System.err.println("Is Agent null?"+agent);
				System.out.println("Setting role "+roles.get(agent).toString());
				ACLMessage toEnv = new ACLMessage(ACLMessage.PROPAGATE);
				toEnv.addReceiver(env);
				MessageFormat toEnvContent = new MessageFormat();
				toEnvContent.setAction("setPlayerRole");
				toEnvContent.addArg("role", roles.get(agent).toString());
				toEnvContent.addArg("AID", agent);
				toEnv.setContent(toEnvContent.toJSON());
				myAgent.send(toEnv);
				
				ACLMessage toPlayer = new ACLMessage(ACLMessage.PROPAGATE);
				toPlayer.addReceiver(agent);
				MessageFormat toPlayerContent = new MessageFormat();
				toPlayerContent.setAction("setRole");
				toPlayerContent.addArg("roleName", roles.get(agent).toString());
				toPlayer.setContent(toPlayerContent.toJSON());
				myAgent.send(toPlayer);
			}
			System.out.println("Roles sent!");
			
			// ADDING NEXT PHASE OF THE GAME
			gm.addDayCycle();
		}
		
		private AID getPlayerAtRandom(){
			if(players.size() > 0){
				int position = (int)(Math.random()*players.size());
				return players.remove(position);
			} else {
				return null;
			}
		}
	}
}
