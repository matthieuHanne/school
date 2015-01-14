package utc.ia04.werewolf.jade.behaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class VoteDayBehaviourTest extends SequentialBehaviour {
	private static final long serialVersionUID = 1L;
	private HashMap<String,Integer> playerVoteList;
	private int nbVote;
	private int nbPlayer;
	
	public VoteDayBehaviourTest(){
		// Get List of alive player : getnicknames
		addSubBehaviour(new SendRequestEnvBehaviour());
		addSubBehaviour(new InitVoteBehaviour());
		addSubBehaviour(new WaitVoteBehaviour());
	}
	
	public class SendRequestEnvBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			/**ACLMessage requestEnv = new ACLMessage(ACLMessage.QUERY_REF);
			MessageFormat content = new MessageFormat();
			content.setAction("getnicknames");
			requestEnv.setContent(content.toJSON());
			requestEnv.addReceiver(Utils.getEnv(myAgent));
			myAgent.send(requestEnv); */
			playerVoteList = new HashMap<String, Integer>();
			playerVoteList.put("Audrey", 0);
			playerVoteList.put("Aliaume", 0);
			playerVoteList.put("Cecile", 0);
			playerVoteList.put("Autre", 0);
			

		}

	}

	public class InitVoteBehaviour extends Behaviour{
		private static final long serialVersionUID = 1L;
		private boolean ok = false;
		@Override
		public void action() {
			//MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
			//ACLMessage message = myAgent.receive(mt);
			//if(message != null){
				//MessageFormat content = MessageFormat.read(message.getContent());
				//if(content.getAction().equals("nicknames")){
					//playerVoteList = new HashMap<String, Integer>();
					//ArrayList<String> playerList = (ArrayList<String>) content.getArg("nicknames");
					Set<String> playerList = playerVoteList.keySet();
					for (String name : playerList) {
						playerVoteList.put(name, 0);
					}
					// Init environnement de vote
					nbVote = 0; // Démarrage des votes
					nbPlayer = playerList.size();
					
					
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					MessageFormat content = new MessageFormat();
					content.setAction("InitVotes");
					content.addArg("ListPlayers", playerVoteList);
					message.setContent(content.toJSON());
					message.addReceiver(Utils.getTable(myAgent));
					myAgent.send(message);
					ok = true;
				//}
			//} 
		}
		@Override
		public boolean done() {
			return ok;
		}
	}
	
	public class WaitVoteBehaviour extends CyclicBehaviour{
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msgReceived = myAgent.receive(mt);
			if(msgReceived!=null){
				MessageFormat content = MessageFormat.read(msgReceived.getContent());
				if(content.getAction().equals("PlayerSelected")){
					if(nbVote<nbPlayer){
						String playerName = (String) content.getArg("playerName");
						System.out.println("VoteDay- Received "+playerName);
						Integer vote = playerVoteList.get(playerName);
						vote++;
						playerVoteList.put(playerName, vote);
						nbVote++;
						
					} else {
						// Tous les joueurs ont déjà voté
					}
					
					if(nbVote==nbPlayer){
						// Fin des votes
						ACLMessage toTable = new ACLMessage(ACLMessage.CONFIRM);
						//content.setAction("EndAction");
						
						// Un joueur majoritaire?
						HashMap<String, Integer> playerVoteListTmp = new HashMap<String, Integer>();
						String majorityPlayer="";
						Integer max = 0;
						boolean majority = true;

						for (String name : playerVoteList.keySet()) {
							Integer vote = playerVoteList.get(name);
							if(vote>max){
								max = playerVoteList.get(name);
								majorityPlayer = name;
							} else if(vote==max){
								majority = false;
							} 
							if(vote>0){
								playerVoteListTmp.put(name, vote);
							}
						}
						
						if(majority){
							// Yes on player were killed !
							//((GameMasterAgent)myAgent).markPlayerAsKilled(majorityPlayer);
							System.out.println("VoteDayTest - A player Was Killed "+majorityPlayer);
						} else {
							// Try Again !
							playerVoteList = playerVoteListTmp;
							nbVote = 0;
							nbPlayer = playerVoteList.size();
							content.setAction("VoteAgain");
							content.addArg("ListPlayers", playerVoteList);
							toTable.setContent(content.toJSON());
							toTable.addReceiver(Utils.getTable(myAgent));
							myAgent.send(toTable);
						}
					}
				}
				
			}
			
		}
		
	}

}
