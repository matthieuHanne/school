package utc.ia04.werewolf.jade.GameMaster.cycle.day;

import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 16/06/2014
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class KillSessionBehaviour extends SequentialBehaviour {
    GameMasterAgent gm;

    public KillSessionBehaviour(GameMasterAgent gm) {
        this.gm = gm;
        addSubBehaviour(new KillPeople(gm));

    }
}
