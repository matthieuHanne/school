package utc.ia04.werewolf.jade.GameMaster.cycle.day;

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

public class VoteDayBehaviour extends SequentialBehaviour {
	private static final long serialVersionUID = 1L;
	private HashMap<String,Integer> playerVoteList;
	private int nbVote;
	private int nbPlayer;
	private boolean voteDone = false;
	private GameMasterAgent gm;
	
	public VoteDayBehaviour(GameMasterAgent gm){
		this.gm = gm;
		// Get List of alive player : getnicknames
		//addSubBehaviour(new SendRequestEnvBehaviour());
		
		addSubBehaviour(new InitVoteBehaviour(gm));
		addSubBehaviour(new WaitVoteBehaviour());
	}
	
	/**
	 * INSERE DIRECTEMENT DANS LE INIT 
	 * Demande à l'agent d'environnement la liste des nicknames
	 * @author AudreyB
	 *
	 */
	/**public class SendRequestEnvBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage requestEnv = new ACLMessage(ACLMessage.QUERY_REF);
			MessageFormat content = new MessageFormat();
			content.setAction("getnicknames");
			requestEnv.setContent(content.toJSON());
			requestEnv.addReceiver(Utils.getEnv(myAgent));
			myAgent.send(requestEnv);

		}

	} */

	/**
	 * Requête et réception de la liste des nicknames
	 * @author AudreyB
	 *
	 */
	public class InitVoteBehaviour extends Behaviour{
		
		GameMasterAgent gm;
		
		public InitVoteBehaviour(GameMasterAgent gm){
			this.gm = gm;
		}
		
		private static final long serialVersionUID = 1L;
		@Override
		public void action() {
			playerVoteList = new HashMap<String, Integer>();
			for (String name : gm.getPlayersHm().keySet()) {
				playerVoteList.put(name, 0);
			}
			// Init environnement de vote
			nbVote = 0; // Démarrage des votes
			nbPlayer = gm.getplayers().size();
			
			
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			MessageFormat content = new MessageFormat();
			content.setAction("InitVotes");
			content.addArg("ListPlayers", playerVoteList);
			message.setContent(content.toJSON());
			message.addReceiver(Utils.getTable(myAgent));
			myAgent.send(message);
		}
	
		@Override
		public boolean done() {
			return playerVoteList != null;
		}
	}
	
	public class WaitVoteBehaviour extends Behaviour{
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msgReceived = myAgent.receive(mt);
			if(msgReceived!=null){
				MessageFormat content = MessageFormat.read(msgReceived.getContent());
				if(!content.getAction().equals("PlayerSelected")){
					myAgent.putBack(msgReceived);
				} else {
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
						// Un joueur majoritaire?
						HashMap<String, Integer> playerVoteListTmp = new HashMap<String, Integer>();
						ArrayList<String> playersMax = new ArrayList<String>();
						String majorityPlayer="";
						Integer max = 0;
						boolean majority = true;

						for (String name : playerVoteList.keySet()) {
							Integer vote = playerVoteList.get(name);
							if(vote>max){
								playersMax.clear();
								max = playerVoteList.get(name);
								playersMax.add(name);
							} else if(vote==max){
								playersMax.add(name);
							} 
							if(vote>0){
								playerVoteListTmp.put(name, vote);
							}
						}
						
						if(playersMax.size()==1){
							// Yes on player were killed !
							System.out.println("VoteDay Un joueur est mort "+playersMax.get(0));
							((GameMasterAgent)myAgent).markPlayerAsKilled(playersMax.get(0));
							voteDone = true;
						} else if(playersMax.size()>1) {
							System.out.println("VoteDay Un joueur est mort "+playersMax.get(0));
							((GameMasterAgent)myAgent).markPlayerAsKilled(playersMax.get(0));
							voteDone = true;
						} else if(playersMax.size()>1) {
							// Try Again !
							ACLMessage voteAgain = new ACLMessage(ACLMessage.INFORM);
							MessageFormat mf = new MessageFormat();
							content.setAction("VoteAgain");
							playerVoteList = playerVoteListTmp;
							nbVote = 0;
							nbPlayer = playerVoteList.size();
							content.addArg("ListPlayers", playerVoteList);
							voteAgain.setContent(mf.toJSON());
							myAgent.send(voteAgain);
						}
					}
				}
				
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return voteDone;
		}
		
	}

}
