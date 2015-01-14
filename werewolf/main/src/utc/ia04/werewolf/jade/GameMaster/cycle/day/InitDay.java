package utc.ia04.werewolf.jade.GameMaster.cycle.day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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


public class InitDay extends SequentialBehaviour{

	GameMasterAgent gm;
	
	public InitDay(GameMasterAgent gm) {
		this.gm = gm;
		addSubBehaviour(new UpdateLocalEnvironmentBehaviour(0));
		addSubBehaviour(new NotifyDay());
	}
	
	
	private class NotifyDay extends OneShotBehaviour{

		@Override
		public void action() {
			// For the players
			ACLMessage wakeUp = new ACLMessage(ACLMessage.INFORM);
			
			for(AID player : gm.getplayers()){
				wakeUp.addReceiver(player);
			}
			
			MessageFormat content = new MessageFormat();
			content.setAction("setDay");
			wakeUp.setContent(content.toJSON());
			gm.send(wakeUp);
			
			// For the table
			ACLMessage tableDay = new ACLMessage(ACLMessage.REQUEST);
			tableDay.addReceiver(Utils.getTable(gm));
			tableDay.setContent(content.toJSON());
			gm.send(tableDay);
		}
		
	}
}

