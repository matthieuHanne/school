package utc.ia04.werewolf.jade.GameMaster.cycle.night;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import utc.ia04.werewolf.jade.GameMaster.CupidonSelectionBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import jade.core.behaviours.SequentialBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;

import java.util.HashMap;

public class NightCycle extends SequentialBehaviour{
    static public boolean firstNight = true;

    public NightCycle(GameMasterAgent gm) {


		System.out.println("--- NIGHT TIME ---");
		this.addSubBehaviour(new InitNight());
        /**if(firstNight){
            this.addSubBehaviour(new FirstNight(gm));
        }*/
		this.addSubBehaviour(new WakeWolfBehaviour(gm));
		this.addSubBehaviour(new WakeWitchBehaviour(gm));
		this.addSubBehaviour(new EndOfNight(gm));
        firstNight = false;

	}
}