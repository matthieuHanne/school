package utc.ia04.werewolf.jade.Player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import utc.ia04.werewolf.utils.PlayerInfo;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class PlayerAgent extends GuiAgent implements PlayerAgentInterface {

	private static final String AGENT_NAME = "agent_name";
	
	private boolean isNight = false;
	private HashMap<String, Integer> werewolvesVote = null;
	
	private PlayerInfo player = new PlayerInfo();
	private String playerName;
	private String playerVoteOld;
	private String playerVoteNew;
	private static final long serialVersionUID = 1L;
	private PropertyChangeSupport pcs;
	
	public void setup(){
		Object[] args = this.getArguments();
		
		this.pcs = new PropertyChangeSupport(this);
		if(args.length == 3){ // Android startup
			this.pcs.addPropertyChangeListener((PropertyChangeListener) args[1]);
			this.playerName = String.valueOf(args[2]);
		} else { // "Normal" startup
			Object[] arg = getArguments();
			this.playerName = (String)arg[0];
		}
		
		registerO2AInterface(PlayerAgentInterface.class, this);
		
		addBehaviour(new SubscribeBehaviour(playerName));
		addBehaviour(new NotifyEndGameBehaviour());
	}
	
	/**
	 * To send notification to android interface.
	 * @param propName : an string representing an integer
	 * @param value : A new value
	 */
	@Override
	public void fireOnChange(String propName, Object value){
		System.out.println("PlayerAgent - FireOnChange "+ propName+" - "+pcs.getPropertyChangeListeners());
		this.pcs.firePropertyChange(propName, null, value);
	}
	
	@Override
	protected void onGuiEvent(GuiEvent ev) {

	}

	@Override
	public void fireOnGuiEvent(GuiEvent ev) {
		System.out.println("PLAYER : Receive message "+ev.getParameter(0));
		if(ev.getType() == Integer.valueOf(PlayerInfo.AGENT_WW_VOTE).intValue()){
			// First Parameter : Old ; Second Parameter : New 
			this.playerVoteOld = this.playerVoteNew;
			this.playerVoteNew = (String) ev.getParameter(0);
			System.out.println("PlayerAgent Vote : old="+this.playerVoteOld+" new="+this.playerVoteNew);
			this.addBehaviour(new WerewolfVoteBehaviour(this.playerVoteOld, (String) ev.getParameter(0)));
		} else if (ev.getType() == Integer.valueOf(PlayerInfo.ENV_WT_SAVE).intValue()){
			String save = (String)ev.getParameter(0);
			System.out.println("PlayerAgent - Witch save ? "+save);
			this.addBehaviour(new WitchSaveBehaviour(Boolean.getBoolean(save)));
			// Envoyer oui ou non
		} else if(ev.getType() == Integer.valueOf(PlayerInfo.ENV_WT_KILL).intValue()){
			String peopleDead = (String) ev.getParameter(0);
			this.addBehaviour(new WitchKillBehaviour(peopleDead));
		} else if(ev.getType() == Integer.valueOf(PlayerInfo.AGENT_HT_LIST).intValue()){
			String peopleDead = (String) ev.getParameter(0);
			this.addBehaviour(new HunterKillBehaviour(peopleDead));
		}
		this.onGuiEvent(ev);
	}
	
	public boolean setRole(String roleName){
		boolean roleSeted = false;
		this.player.playerrole = PlayerInfo.Role.valueOf(roleName);
		switch(player.playerrole){
		case Werewolf:
			System.out.println("Player Agent - "+playerName+" is a Werewolf!");
			addBehaviour(new WerewolfBehaviour());
			roleSeted = true;
			break;
		case Cupidon:
            System.out.println("Player Agent - "+playerName+" is Cupidon!");
            addBehaviour(new CupidonBehaviour());
			break;
		case Witch:
			addBehaviour(new WitchBehaviour());
			break;
		case Thief:
			//addBehaviour(new WerewolfBehaviour());
			break;
		case Hunter:
            System.out.println("Player Agent - "+playerName+" is a Hunter!");
            addBehaviour(new HunterBehaviour());
			break;
		case Medium:
			//addBehaviour(new WerewolfBehaviour());
			break;
		default:
			addBehaviour(new VillagerBehaviour());
            roleSeted = true;
		}
		this.fireOnChange(PlayerInfo.AGENT_ROLE, this.player.playerrole.toString());
		return roleSeted;
	}
	
	public void setNight(boolean night){

		this.playerVoteOld = "";
		this.playerVoteNew = "";
		this.isNight = night;

        if(night){
		    fireOnChange(PlayerInfo.ENV_NIGHT, night);
        }
        else{
            fireOnChange(PlayerInfo.ENV_DAY, true);
        }
	}

    public void getKilled(){
        this.player.isAlive = false;
        fireOnChange(PlayerInfo.AGENT_GET_KILLED, true);
    }
	
	public void updateWerewolfVote(HashMap<String, Integer> votes){
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(sw, votes);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.werewolvesVote = votes;
		fireOnChange(PlayerInfo.ENV_WW_VOTE, sw.toString());
	}
	
	public void werewolfEndVotes(){
		fireOnChange(PlayerInfo.ENV_WW_ENDVOTE, null);
	}

	public void gameOver(String winner){
		fireOnChange(PlayerInfo.ENV_END, winner);
	}
	
	@Override
	public void addNewPropertyChangeListener(PropertyChangeListener pcl) {
		this.pcs.addPropertyChangeListener(pcl);
	}

    public void hunterTargetSelection(HashMap<String,AID> playerList){
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> list = new ArrayList<String>();
        for (String name : playerList.keySet()) {
			list.add(name);
		}
        try {
            mapper.writeValue(sw, list);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        fireOnChange(PlayerInfo.AGENT_HT_LIST, sw.toString());
    }

    public void cupidonLoversSelection(HashMap<String, AID> playersList) {
    	StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(sw, Arrays.asList(playersList.keySet()));
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        fireOnChange(PlayerInfo.AGENT_CD_LIST, sw.toString());    
    }
}