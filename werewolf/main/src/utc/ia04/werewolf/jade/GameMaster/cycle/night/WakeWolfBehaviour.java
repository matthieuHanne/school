package utc.ia04.werewolf.jade.GameMaster.cycle.night;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
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

public class  WakeWolfBehaviour extends SequentialBehaviour{
	
	
	GameMasterAgent gm = null;
	ArrayList<AID> werewolves=null;
	ArrayList<String> nicknames=null; 
	HashMap<String, Integer> votes=null;
	
	
	public WakeWolfBehaviour(GameMasterAgent gm){
		this.gm = gm;
		addSubBehaviour(new WakeWolves());
		addSubBehaviour(new PauseBehaviour(myAgent));
	}
	
	/**
	 * Envoie de la nouvelles liste de joueur avec les votes mis à jour 
	 */
	private void updateVotes(){
		ACLMessage wolfvote = new ACLMessage(ACLMessage.INFORM); 
		MessageFormat contentforwolf = new MessageFormat(); 
		contentforwolf.setAction("voteWerewolfUpdate");
		contentforwolf.addArg("votes", votes);
		
		for(String id : nicknames){
			wolfvote.addReceiver(gm.getPlayersHm().get(id));
		}
		wolfvote.setContent(contentforwolf.toJSON());
		System.out.println("WakeWolves - Sending"+contentforwolf.toJSON());
		myAgent.send(wolfvote);
	}	
	
	/**
	 * Demande de la liste des AID à l'agent d'environnement
	 * => Filtre sur les Werewolf
	 * @author AudreyB
	 *
	 */
	private class RequestListAID extends OneShotBehaviour{

		@Override
		public void action() {
			System.out.println("WakeWolves - Request AIDs");
			ACLMessage request = new ACLMessage(ACLMessage.QUERY_REF);
			request.addReceiver(Utils.getEnv(myAgent));
			MessageFormat content = new MessageFormat();
			content.setAction("getAID");
			content.addArg("roleFilter", PlayerInfo.Role.Werewolf.toString());
			request.setContent(content.toJSON());
			myAgent.send(request);
		}
		
	}
	/**
	 * Réception de la liste des AID 
	 * => Avec le filtre Werewolf 
	 * Réponse RequestListAID
	 */
	private class ReceiveListAID extends Behaviour{

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
			ACLMessage answer = myAgent.receive(mt);
			if(answer != null){
				MessageFormat answContent = MessageFormat.read(answer.getContent());
				if(answContent.getAction().equals("AIDplayers")){
					werewolves = answContent.getAIDArgList("AIDplayers");
					System.out.println("WakeWolves - Received AIDs ("+werewolves.size()+")");
					System.err.println("IS DONE? "+(done()?"YES!!!":"NO"));
					
				} else {
					myAgent.putBack(answer);
				}
			}
		}
		
		@Override
		public boolean done(){
			return (werewolves != null);
		}
		
	}
	
	/**
	 * Demande de la liste des noms des joueurs à l'agent environnement 
	 * (Pour initialiser la liste des vote de nuit des loup garou)
	 */
	private class RequestNickNames extends OneShotBehaviour{
		@Override
		public void action(){
			System.out.println("WakeWolves - Request Nicknames");
			ACLMessage request = new ACLMessage(ACLMessage.QUERY_REF);
			request.addReceiver(Utils.getEnv(myAgent));
			MessageFormat content = new MessageFormat();
			content.setAction("getnicknames");
			request.setContent(content.toJSON());
			myAgent.send(request);
		}
	}
	
	/**
	 * Réception de la liste des noms des joueurs 
	 * @author AudreyB
	 *
	 */
	private class ReceiveNicknames extends Behaviour{

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
			ACLMessage answer = myAgent.receive(mt);
			if(answer != null){
				System.out.println("WakeWolves - Receive Nicknames");
				MessageFormat answContent = MessageFormat.read(answer.getContent());
				if(answContent.getAction().equals("nicknames")){
					nicknames = (ArrayList<String>)answContent.getArg("nicknames");
					
				} else {
					myAgent.putBack(answer);
				}
			}
		}
		
		@Override
		public boolean done(){
			return (nicknames != null);
		}
		
	}
	
	/**
	 * Réveil de tous les loup garou 
	 * Env oie inform à tous les Agent dont le rôle est loup garou
	 * Nous avions récupéré la liste des AID grâce au ReqListAID 
	 * @author AudreyB
	 *
	 */
	private class WakeWolves extends OneShotBehaviour{
		@Override
		public void action(){
			System.out.println("WakeWolves - Wake the Wolves...");
			nicknames = gm.getWerewolves();
			ACLMessage informwolf = new ACLMessage(ACLMessage.INFORM); 
			MessageFormat contentforwolf = new MessageFormat(); 
			contentforwolf.setAction("wakeWerewolf");
			HashMap<String, AID> players = gm.getPlayersHm();
			for(String nick : nicknames){
				informwolf.addReceiver(players.get(nick));
			}
			informwolf.setContent(contentforwolf.toJSON());
			myAgent.send(informwolf);
			
			// Also, init votes
			votes = new HashMap<String, Integer>();
			for(String nickname : gm.getPlayersHm().keySet()){
				System.out.println("Adding "+nickname);
				votes.put(nickname, 0);
			}
		}
	}
	
	/**
	 * Au bout de 5 secondes on lance les votes des loup garou
	 * @author AudreyB
	 *
	 */
	private class PauseBehaviour extends WakerBehaviour{
		public PauseBehaviour(Agent a){
			super(a, 5000);
		}
		
		public void onWake(){
			((SequentialBehaviour) getParent()).addSubBehaviour(new VoteProcess());
			((SequentialBehaviour) getParent()).addSubBehaviour(new NotifyEndVote());
			((SequentialBehaviour) getParent()).addSubBehaviour(new SetForElimination());
		}
	}
	
	private class VoteProcess extends ParallelBehaviour{
		public VoteProcess(){
			super(WHEN_ANY); // Terminates if one or the other Behaviour is done
			updateVotes();
			addSubBehaviour(new VotetimeEx(myAgent, PlayerInfo.WEREWOLF_VOTE_TIMEOUT));
			addSubBehaviour(new VoteBehaviour());
		}
		
	}
	
	/**
	 * Pour limiter la durée du vote à timeOut minutes 
	 * 
	 * => A voir 
	 * @author AudreyB
	 *
	 */
	private class  VotetimeEx extends WakerBehaviour{

		public VotetimeEx(Agent a, long timeout) {
			super(a, timeout);
		}
		
		public void onWake(){}
	}
	
	/**
	 * Gestion du vote des loups garou durant la nuit
	 * 
	 * Attente d'un message de type REQUEST et d'action = voteWerewolf
	 * Contenant l'ancien et le nouveau vote de la part d'une joueur (role = WW)
	 * et mise à jour des votes 
	 *
	 */
	private class VoteBehaviour extends Behaviour{
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = myAgent.receive(mt);
			if(message != null){
				MessageFormat content = MessageFormat.read(message.getContent());
				if(content.getAction().equals("voteWerewolf")){
					System.out.println("WakeWolves - Received Vote!");
					String oldVote = (String)content.getArg("oldVote");
					String newVote = (String)content.getArg("newVote");
					if(!oldVote.isEmpty()) votes.put(oldVote, votes.get(oldVote) -1);
					votes.put(newVote, votes.get(newVote) +1);
					updateVotes();
				}
			}
		}

		@Override
		public boolean done() {
			int max = 0;
			int maxHit = 0;
			int total = 0;
			for(Integer nbVotes : votes.values()){
				if(nbVotes > max){
					maxHit = 1;
					max = nbVotes;
				} else if(nbVotes == max){
					maxHit++;
				}
				total += nbVotes;
			}
			return (total == nicknames.size() && maxHit == 1);
		}
		
	}
	
	/**
	 * Envoie à tous les loups garou, la notification de fin de vote
	 * Message de type Inform et action = voteWerewolfEnd
	 * @author AudreyB
	 *
	 */
	private class NotifyEndVote extends OneShotBehaviour{
		@Override
		public void action(){
			ACLMessage endVote = new ACLMessage(ACLMessage.INFORM);
			for(String werewolf: nicknames){
				endVote.addReceiver(gm.getPlayersHm().get(werewolf));
			}
			MessageFormat content = new MessageFormat();
			content.setAction("voteWerewolfEnd");
			endVote.setContent(content.toJSON());
			myAgent.send(endVote);
		}
	}
	
	/**
	 * Lancé une fois que les votes sont terminés
	 * Analyse des votes
	 * Elimination d'un joueur => marquer un joueur comme mort 
	 * @author AudreyB
	 *
	 */
	private class SetForElimination extends OneShotBehaviour{

		@Override
		public void action() {
			int max = 0;
			ArrayList<String> eliminated = new ArrayList<String>();
			for(String player : votes.keySet()){
				int nbVotes = votes.get(player);
				if(nbVotes > max){
					eliminated.clear();
					eliminated.add(player);
					max = nbVotes;
				} else if(nbVotes == max){
					eliminated.add(player);
				}
			}
			int selectRandom = (int)(Math.random()*eliminated.size());
			gm.markPlayerAsKilled(eliminated.get(selectRandom));
			System.err.println("Chose to kill "+eliminated.get(selectRandom));
		}
		
	}
}