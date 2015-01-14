package utc.ia04.werewolf.jade.GameMaster;

import utc.ia04.werewolf.jade.GameMaster.cycle.night.NightCycle;
import jade.core.behaviours.SequentialBehaviour;

public class DayCycleBehaviour extends SequentialBehaviour {
	private GameMasterAgent gm;
	public DayCycleBehaviour(GameMasterAgent gm){
		this.gm = gm;
		addSubBehaviour(new NightCycle(gm));
	}
	
}
