package utc.ia04.werewolf.jade.GameMaster.cycle.night;

import utc.ia04.werewolf.jade.GameMaster.DayCycleBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.cycle.day.DayCycle;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

/**
 * This class is necessary to get the DayCycle inside the right Behaviour
 * @author aliaume
 *
 */

public class EndOfNight extends OneShotBehaviour {

	GameMasterAgent gm;
	
	public EndOfNight(GameMasterAgent gm) {
		this.gm = gm;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		((SequentialBehaviour)getParent()).addSubBehaviour(new DayCycle(gm));
	}

}
