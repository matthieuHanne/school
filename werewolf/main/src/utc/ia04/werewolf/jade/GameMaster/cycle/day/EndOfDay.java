package utc.ia04.werewolf.jade.GameMaster.cycle.day;

import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.cycle.night.NightCycle;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class EndOfDay extends OneShotBehaviour {

	GameMasterAgent gm;
	
	public EndOfDay(GameMasterAgent gm) {
		this.gm = gm;
	}
	
	@Override
	public void action() {
		((SequentialBehaviour)getParent()).addSubBehaviour(new NightCycle(gm));
	}

}
