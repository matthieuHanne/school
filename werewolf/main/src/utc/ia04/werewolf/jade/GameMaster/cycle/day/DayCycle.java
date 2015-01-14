package utc.ia04.werewolf.jade.GameMaster.cycle.day;

import jade.core.behaviours.SequentialBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.UpdateLocalEnvironmentBehaviour;
import utc.ia04.werewolf.jade.GameMaster.cycle.night.InitNight;

public class DayCycle extends SequentialBehaviour{
	
	GameMasterAgent gm;
	
	public DayCycle(GameMasterAgent gm){
		System.out.println("--- DAY TIME ---");
		this.gm = gm;

	    addSubBehaviour(new InitDay(gm));
		
		addSubBehaviour(new KillPeople(gm));
		addSubBehaviour(new CheckEndGameCondition());
		
		addSubBehaviour(new UpdateLocalEnvironmentBehaviour(0));
		
	    addSubBehaviour(new VoteDayBehaviour(gm));
	    
	    addSubBehaviour(new KillPeople(gm));
		addSubBehaviour(new CheckEndGameCondition());
		
		addSubBehaviour(new EndOfDay(gm));
	}
}