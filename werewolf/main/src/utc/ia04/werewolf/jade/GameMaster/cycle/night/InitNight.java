package utc.ia04.werewolf.jade.GameMaster.cycle.night;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CompositeBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import utc.ia04.werewolf.jade.GameMaster.CupidonSelectionBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.HunterEliminationBehaviour;
import utc.ia04.werewolf.jade.GameMaster.UpdateLocalEnvironmentBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.Utils;

public class InitNight extends SequentialBehaviour{
	
	private ArrayList<AID> allPlayers = null;

	public InitNight() {
		this.addSubBehaviour(new UpdateLocalEnvironmentBehaviour(1));
		this.addSubBehaviour(new NotifyAndReset());


	}
	
	/**
	 * R�initialise la partie et envoie notification de nuit � la table 
	 * @author AudreyB
	 *
	 */
	private class NotifyAndReset extends OneShotBehaviour{
		@Override
		public void action(){
			System.out.println("INIT NIGHT : Notify And Reset");
			
			((GameMasterAgent)myAgent).resetRound();
			
			// 
			ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			MessageFormat content = new MessageFormat();
			content.setAction("setNight");
			message.setContent(content.toJSON());
			message.addReceiver(Utils.getTable(myAgent));
			for(AID player : ((GameMasterAgent)myAgent).getplayers()){
				message.addReceiver(player);
			}
			myAgent.send(message);
			// For the table
			ACLMessage tableNight = new ACLMessage(ACLMessage.REQUEST);
			tableNight.addReceiver(Utils.getTable(myAgent));
			tableNight.setContent(content.toJSON());
			myAgent.send(tableNight);
		}
	}
}