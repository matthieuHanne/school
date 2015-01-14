package utc.ia04.werewolf.jade.GameMaster.cycle.night;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import utc.ia04.werewolf.jade.GameMaster.CupidonSelectionBehaviour;
import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.utils.MessageFormat;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 16/06/2014
 * Time: 19:43
 * To change this template use File | Settings | File Templates.
 */
public class FirstNight extends OneShotBehaviour {
    GameMasterAgent gm;
    public FirstNight(GameMasterAgent gm) {
        this.gm = gm;

    }

    @Override
    public void action() {
        HashMap<String,AID> players = gm.getPlayersHm();
        ((SequentialBehaviour)getParent()).addSubBehaviour(new CupidonSelectionBehaviour());
        String cupidon = ((GameMasterAgent)myAgent).getCupidon();
        System.out.println(" Send playerList for cupidon");
        ACLMessage cupidonPlayersList = new ACLMessage(ACLMessage.INFORM);
        cupidonPlayersList.addReceiver(players.get(cupidon));
        MessageFormat playersList = new MessageFormat();
        playersList.setAction("setLovers");
        playersList.addArg("playersList", (HashMap<String,AID>)players);
        cupidonPlayersList.setContent(playersList.toJSON());
        myAgent.send(cupidonPlayersList);    }
}
