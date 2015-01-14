package utc.ia04.werewolf.jade.Environment;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.HashMap;

import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.PlayerInfo.Role;

public class EnvironmentAgent extends Agent {
	public HashMap<String, PlayerInfo> players = new HashMap<String, PlayerInfo>();
	public void setup(){
		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        ServiceDescription sd = new ServiceDescription();
        sd.setName("Environment");
        sd.setType("ENV");
        dfd.addServices(sd);
        
        try {  
            DFService.register( this, dfd );  
        }
        catch (FIPAException fe) {
            fe.printStackTrace(); 
        }
        
		addBehaviour(new AddPlayerBehaviour());
		addBehaviour(new SetPlayerRoleBehaviour());
        addBehaviour(new KillPlayerBehaviour());
        //addBehaviour(new SetInfoBehaviour());
        addBehaviour(new EnvDataBehaviour());
	}

	
	public boolean addPlayer(String nickname, AID playerid){
		if(!players.containsKey(nickname)){
			PlayerInfo player = new PlayerInfo();
			player.agentID = playerid;
			players.put(nickname, player);
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<AID> getAIDplayers(PlayerInfo.Role filter){
		ArrayList<AID> playerList = new ArrayList<AID>();
		for(PlayerInfo player : players.values()){
			if((player.playerrole.equals(filter) || filter.equals(PlayerInfo.Role.Player))
					&& player.isAlive){
				playerList.add(player.agentID);
			}
		}
		return playerList;
	}
	
	public ArrayList<String> getPlayersNick(){
		ArrayList<String> playerList = new ArrayList<String>();
		for(String player : players.keySet()){
			if(players.get(player).isAlive){
				playerList.add(player);
			}
		}
		return playerList;
	}
	
	public ArrayList<String> getPlayersNick(Role playerRole){
		ArrayList<String> playerList = new ArrayList<String>();
		for(String player : players.keySet()){
			if(players.get(player).isAlive){
				if(playerRole != null && players.get(player).playerrole.equals(playerRole)){
					playerList.add(player);
				}
			}
		}
		return playerList;
	}
	
	public ArrayList<String> getLoversNick(){
		ArrayList<String> lovers = new ArrayList<String>();
		for(String player : players.keySet()){
			if(players.get(player).isAlive && players.get(player).isInLove){
				lovers.add(player);
			}
		}
		return lovers;
	}
	
	public boolean setPlayerRole(AID player, String role){
		for(String playerName : players.keySet()){
			if(players.get(playerName).agentID.equals(player)){
				players.get(playerName).playerrole = PlayerInfo.Role.valueOf(role);
				return true;
			}
		}
		return false;
	}

    public boolean killPlayer(String nickname){
        if(players.containsKey(nickname)){
            PlayerInfo player = players.get(nickname);
            player.isAlive = false;
            players.put(nickname, player);
            return true;
        } else {
            return false;
        }
    }
    
    public AID getPlayerFromNick(String nickname){
    	return players.get(nickname).agentID;
    }
}
