package utc.ia04.werewolf.jade.GameMaster.cycle.day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.GameMasterBehaviour;
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


public class CheckEndGameCondition extends SequentialBehaviour {

	ArrayList<AID> players=null;
	MessageTemplate mt_players=null;
	ArrayList<AID> werewolf=null; 
	MessageTemplate mt_werewolf=null;
	ArrayList<AID> lovers=null;
	MessageTemplate mt_lovers=null;
	
	public CheckEndGameCondition(){
		addSubBehaviour(new UpdateLocalEnvironmentBehaviour(0));
		addSubBehaviour(new EvalEndCondition()); 
	}
	
	private class  EvalEndCondition  extends OneShotBehaviour{

		@Override
		public void action() {
			GameMasterAgent gm = (GameMasterAgent)myAgent;
			if(gm.getLovers().size()==2 && gm.getPlayersHm().size() == 2){
				gm.setwinner(PlayerInfo.Role.Lovers);
				gm.set_gamestate(true);
			} else if(gm.getWerewolves().size()==0){
				gm.setwinner(PlayerInfo.Role.Villager);
				gm.set_gamestate(true); 
			} else if(gm.getWerewolves().size()==gm.getPlayersHm().size()){
					gm.setwinner(PlayerInfo.Role.Werewolf);
					gm.set_gamestate(true);
			} else {
				System.out.println("Check state : not done yet (WW : "+gm.getWerewolves().toString()+")");
				gm.set_gamestate(false); 
			}
			
			if(gm.is_gameover()){
				gm.gameOver();
			}
		}
	}
	
}
