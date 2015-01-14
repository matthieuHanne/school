package utc.ia04.werewolf.jade.GameMaster.cycle.day;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import utc.ia04.werewolf.jade.GameMaster.GameMasterAgent;
import utc.ia04.werewolf.jade.GameMaster.HunterEliminationBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.Utils;

public class KillPeople extends OneShotBehaviour{

	GameMasterAgent gm;
	ArrayList<AID> peopleToKill = null;
	
	public KillPeople(GameMasterAgent gm){
		this.gm = gm;	
	}




    @Override
	public void action() {

        HashMap<String,AID> players = gm.getPlayersHm();
        String hunter= gm.getHunter();

        //Lovers Detection
        boolean loverKilled = false;
        for(String deadPerson :gm.getMarkedPlayers()){
            for(String lover : gm.getLovers()){
                if(lover.equals(deadPerson)){
                    loverKilled = true;
                    if(gm.getLovers().get(0).equals(lover)){
                        gm.markPlayerAsKilled(gm.getLovers().get(1));
                    }else{
                        gm.markPlayerAsKilled(gm.getLovers().get(0));
                    }
                }
            }
        }

        for(String deadPerson :gm.getMarkedPlayers()){
            if(deadPerson.equals(hunter)){
                System.out.println("DayCycleBehaviour - The hunter have been killed");
                ACLMessage hunterYouBeDead = new ACLMessage(ACLMessage.INFORM);
                //Get HunterAid
                hunterYouBeDead.addReceiver(players.get(hunter));
                MessageFormat hunterNotification = new MessageFormat();
                hunterNotification.setAction("hunterNotification");
                hunterNotification.addArg("playersList", (HashMap<String,AID>)players);
                hunterYouBeDead.setContent(hunterNotification.toJSON());
                myAgent.send(hunterYouBeDead);
                System.err.println("Parent is"+getParent().getClass().getSimpleName());
                ((SequentialBehaviour)getParent()).addSubBehaviour(new HunterEliminationBehaviour());
                ((SequentialBehaviour)getParent()).addSubBehaviour(new KillPeople(gm));
            }
         }


		System.out.println("KillPeople - About to kill someone...");
		// Informer l'environnement que les gens sont morts
		for(String deadPerson : gm.getMarkedPlayers()){
			ACLMessage killItWithFire = new ACLMessage(ACLMessage.REQUEST);
			killItWithFire.addReceiver(Utils.getEnv(myAgent));
			MessageFormat beforeItLaysEggs = new MessageFormat();
			beforeItLaysEggs.setAction("killPlayer");
			beforeItLaysEggs.addArg("nickname", deadPerson);
			killItWithFire.setContent(beforeItLaysEggs.toJSON());
			myAgent.send(killItWithFire);
			System.err.println("Killed "+deadPerson+"...");
		}

		// Informer les joueurs de leur mort
		ACLMessage youBeDead = new ACLMessage(ACLMessage.INFORM);
		for(String deadPerson : gm.getMarkedPlayers()){
			youBeDead.addReceiver(gm.getPlayersHm().get(deadPerson));
		}
		MessageFormat killingPeopleContent = new MessageFormat("getKilled", new HashMap<String, Object>());
		youBeDead.setContent(killingPeopleContent.toJSON());
		myAgent.send(youBeDead);


		// Informer la table de la liste des morts
		ACLMessage toTable = new ACLMessage(ACLMessage.INFORM);
		MessageFormat contentTotable = new MessageFormat();
		contentTotable.setAction("PlayerDead");
		contentTotable.addArg("playerDeadList", gm.getMarkedPlayers());
		toTable.addReceiver(Utils.getTable(myAgent));
		toTable.setContent(contentTotable.toJSON());
		myAgent.send(toTable);

		((GameMasterAgent)myAgent).resetRound();
	
	}
}