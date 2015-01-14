package utc.wolf.model.agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;

import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;
import utc.wolf.gui.MTWolfScene;
import utc.wolf.main.StartWolf;
import utc.wolf.model.Constants;
import utc.wolf.model.Player;
import utc.wolf.model.agent.action.SelectOptionAction;
import utc.wolf.model.menu.DefaultMenuModel;
import utc.wolf.model.menu.IMenuModel;
import utc.wolf.model.menu.MenuItem;

public class WolfGuiAgent extends GuiAgent {
	private static final long serialVersionUID = 1L;
	
	MTWolfScene scene;
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	int state = Constants.TO_DAY;
	ArrayList<Player> players = new ArrayList<Player>();
	int nbVote;
	Player selectedPlayer;
	private String deadPeople;

	@Override
	protected void setup() {
		super.setup();
		
		this.nbVote = 0;
		// Enregistrement DFService 
		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        ServiceDescription sd = new ServiceDescription();
        sd.setName("TableGui");
        sd.setType("Gui");
        dfd.addServices(sd);
        
        try {  
            DFService.register( this, dfd );  
        }
        catch (FIPAException fe) {
            fe.printStackTrace(); 
        }
		
		scene = (MTWolfScene) getArguments()[0];
		pcs.addPropertyChangeListener(scene);
		StartWolf.getInstance().setGuiagent(this);
		
		defaulParticipants();
		addBehaviour(new LaunchOptionBehaviour());
		addBehaviour(new WaitPlayerBehaviour());
		addBehaviour(new ChangeStateBehaviour());
		addBehaviour(new GameOver());
		
		System.out.println(getLocalName() + "--> Installed");
	}

	@Override
	protected void onGuiEvent(GuiEvent evt) {
		if (evt.getType() == Constants.TO_DAY) {
			state = Constants.TO_DAY;
			notifyView(Constants.IMAGE,	Constants.DAYLIGHT_IMAGE);
		}
		if (evt.getType() == Constants.TO_NIGHT) {
			state = Constants.TO_NIGHT;
			notifyView(Constants.IMAGE,	Constants.NIGHT_IMAGE);
		}
		if (evt.getType() == Constants.PLAYER_SELECTED) {
			Object value = evt.getParameter(0);
			if (value instanceof Player) {
				selectedPlayer = (Player) value;
				System.out.println("WolfGui - SelectedPlayer - "+selectedPlayer);
				// Send message to GameMaster 
				if(nbVote<players.size()){
					notifyGameMaster(Constants.PLAYER_SELECTED, selectedPlayer);
					nbVote++;
				} else {
					notifyView(Constants.HINT,"Tous les joueurs ont déjà voté, attente des résultats");
				}
				
				//notifyView(Constants.HINT,selectedPlayer + " selectionnÃ© ------------------ "+ "------------------- ");
			} else {
				notifyView(Constants.HINT," Ã©lÃ©ment inconnu");
				System.out.println(getLocalName() + " unknown element");
			}
		}
		if (evt.getType() == Constants.START_GAME){
			// TODO : Send message to TableAgent 
			ACLMessage informStart = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			AID gm = Utils.getMaster(this);
			informStart.addReceiver(gm);
			this.send(informStart);
		}
	}
	/**
	 * Notifier le GameMaster d'un vote pour un joueur 
	 * @param action : PlayerSelected
	 * @param selectedPlayer
	 */
    private void notifyGameMaster(int action, Player selectedPlayer) {
		if(action == Constants.PLAYER_SELECTED){
			// TODO : Vérifier que c'est le bon perfomatif et la bonne action
			ACLMessage notifyGM = new ACLMessage(ACLMessage.INFORM);
			utc.ia04.werewolf.utils.MessageFormat content = new utc.ia04.werewolf.utils.MessageFormat();
			content.setAction("PlayerSelected");
			content.addArg("playerName", selectedPlayer.getName());
			notifyGM.addReceiver(Utils.getMaster(this));
			//notifyGM.addReceiver(getReceiver("Agent","TableAgent"));
			notifyGM.setContent(content.toJSON());
			send(notifyGM);
		}
	}

	private void notifyView(String property,Object value) {
    	scene.getMTApplication().registerPreDrawAction(
				new SelectOptionAction(pcs, property,value));
    }
	private void defaulParticipants() {
		String[] participants = new String[] { };
		for (String s : participants) {
			players.add(new Player(s));
		}
	}

	private IMenuModel createListModel() {
		DefaultMenuModel listModel = new DefaultMenuModel();
		int i = 0;
		for (Player p : players) {
			MenuItem item = new MenuItem(i, p);
			listModel.add(item);
			i++;
		}
		return listModel;
	}

	private void showPlayerList() {
		scene.getMTApplication().registerPreDrawAction(
				new SelectOptionAction(pcs, Constants.OPEN_OPTION,
						createListModel()));
	}

	/**
	 * Lancement de la liste de joueur sur l'interface.
	 * @author AudreyB
	 *
	 */
	private class LaunchOptionBehaviour extends OneShotBehaviour {
		/**
		 * Ouvre le menu options
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			showPlayerList();
		}

	}

	/**
	 * Attente de la connexion d'un joueur
	 * Mise à jour de la liste des joueurs 
	 * 
	 * Attente réception message de TableAgent 
	 * REQUEST : action = add_player 
	 * @author AudreyB
	 *
	 */
	private class WaitPlayerBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
			if (message != null) {
				MessageFormat mf = MessageFormat.read(message.getContent());
				if (mf.getAction().equals("add_player")) {
					String playername = (String) mf.getArg("PlayerName");
					System.out.println("Receive PlayerName = "+playername);
					Player p = new Player(playername);
					players.add(p);
					showPlayerList();
				} else myAgent.putBack(message);
			} else block();
		}
	}
	
	/**
	 * TODO : Add the bahaviour
	 * @author AudreyB
	 *
	 */
	private class ChangeStateBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO : Vérifier que c'est le bon Performatif 
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage changeStateMsg = receive(mt);
			if(changeStateMsg != null){
				MessageFormat content = MessageFormat.read(changeStateMsg.getContent());
				// Verifier que ce soit la bonne action
				if(content.getAction().equals("setDay")){
					if(state != Constants.TO_DAY){
						nbVote = 0;
						// Passage au jour 
						state = Constants.TO_DAY;
						notifyView(Constants.IMAGE,	Constants.DAYLIGHT_IMAGE);
						
					} else {
						System.out.println("GuiTable, action setDay : It's already day ");
					}
				} else if(content.getAction().equals("setNight")){
					if(state != Constants.TO_NIGHT){
						// Passage au jour 
						state = Constants.TO_NIGHT;
						notifyView(Constants.IMAGE,	Constants.NIGHT_IMAGE);
						String str = "Joueur éliminé : \n";
						notifyView(Constants.HINT, str+deadPeople);
					} else {
						System.out.println("GuiTable, action setNight : It's already night");
					}
				} else if(content.getAction().equals("InitVotes")){
					// Passage aux votes 
					System.out.println("GUI TABLE Init Vote ");
					nbVote = 0;
					players.clear();
					HashMap<String,Integer> listNames = (HashMap<String,Integer>) content.getArg("ListPlayers");
					for (String name : listNames.keySet()) {
						players.add(new Player(name));
					}
					showPlayerList();
					
					myAgent.addBehaviour(new ResultVoteBehaviour());
				}
			} else {
				block();
			}
		}
	}
	
	public class ResultVoteBehaviour extends Behaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if(message != null){
				MessageFormat content = MessageFormat.read(message.getContent());
				if(content.getAction().equals("PlayerDead")){
					ArrayList<String> deadName = (ArrayList<String>) content.getArg("playerDeadList");
					deadPeople = "" ;
					for (String name : deadName) {
						deadPeople += name+"\n" ;
					}
					 
				} else if(content.getAction().equals("VoteAgain")){
					// Passage aux votes 
					nbVote = 0;
					players.clear();
					HashMap<String,Integer> listNames = (HashMap<String,Integer>) content.getArg("ListPlayers");
					for (String name : listNames.keySet()) {
						players.add(new Player(name));
					}
					showPlayerList();
					notifyView(Constants.HINT, " Aucune Majorité le vote recommence "+ "------------------- ");
				} else {
			//myAgent.putBack(message);
				}
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class GameOver extends CyclicBehaviour{

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = myAgent.receive(mt);
			if(message != null){
				MessageFormat content = MessageFormat.read(message.getContent());
				if(content.getAction().equals("gameOver")){
					notifyView(Constants.HINT, "L'equipe "+(String)content.getArg("winner")+" gagne la partie");
				} else {
					myAgent.putBack(message);
				}
			}
		}
		
	}
}
