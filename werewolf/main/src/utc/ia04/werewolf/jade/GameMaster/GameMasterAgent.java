package utc.ia04.werewolf.jade.GameMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import utc.ia04.werewolf.jade.GameMaster.cycle.init.GameBeginBehaviour;
import utc.ia04.werewolf.jade.GameMaster.cycle.init.LogBehaviour;
import utc.ia04.werewolf.utils.MessageFormat;
import utc.ia04.werewolf.utils.PlayerInfo;
import utc.ia04.werewolf.utils.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class GameMasterAgent extends Agent {
	
	private DayCycleBehaviour cycle = null;
	
	//private ArrayList<AID> players; 
	private HashMap<String, AID> playersHm;
	private ArrayList<String> lovers;
	private String hunter;
    private String cupidon;
    private ArrayList<String> werewolves;
	private String witch=null;
	private boolean witchkilled=false; 
	private boolean witchsaved=false;
	private String witchSavedPlayer="";
	private String witchKilledPlayer="";

	private int conversationID=0;
	private boolean playing;
	private boolean gameover=false; 
	private PlayerInfo.Role winner=null;
	private ArrayList<String> killedPlayers=null;
	
	public boolean did_witchkill(){
		return witchkilled; 
	}
	
	public boolean did_witchsave(){
		return witchsaved; 
	}
	
	public void witchhaskilled(String player){
		System.out.println("Witch will kill "+player);
		markPlayerAsKilled(player);
		witchkilled=true; 
	}
	
	public void witchhassaved(){
		witchSavedPlayer = killedPlayers.get(0);
		System.out.println("Witch will save "+witchSavedPlayer);
		killedPlayers.clear();
		witchsaved=true;
	}
	
   public boolean is_gameover(){
	   return gameover; 
   }
	public void set_gamestate(boolean state){
		gameover=state; 
	}
	public PlayerInfo.Role getwinner(){
		return winner; 
	}
	public void setwinner(PlayerInfo.Role win){
		 winner=win; 
	}
	public void increaseconversationID(){
		++conversationID;
	}
	public int getConversationID(){
		return conversationID; 
	}
	
	public ArrayList<AID> getplayers(){
		ArrayList<AID> players = new ArrayList<AID>();
		for(AID player : this.playersHm.values()){
			players.add(player);
		}
		return players; 
	}
	
	public HashMap<String, AID> getPlayersHm() {
		return playersHm;
	}
    public String getCupidon(){
        return cupidon;
    }
	public void setPlayersHm(HashMap<String, AID> playersHm) {
		this.playersHm = playersHm;
	}
	public ArrayList<String> getLovers() {
		if(lovers!=null){
			return lovers;
		} else {
			return new ArrayList<String>();
		}
	}
	public void setLovers(ArrayList<String> lovers) {
		this.lovers = lovers;
	}
	public String getHunter() {
		return hunter;
	}
	public void setHunter(String hunter) {
		this.hunter = hunter;
	}
	public ArrayList<String> getWerewolves() {
		return werewolves;
	}
	public void setWerewolves(ArrayList<String> werewolves) {
		this.werewolves = werewolves;
	}
	public String getWitch() {
		return witch;
	}
	public void setWitch(String witch) {
		this.witch = witch;
	}
	
	public void addDayCycle(){
		this.cycle = new DayCycleBehaviour(this);
		this.addBehaviour(cycle);
	}
	
	public void gameOver(){
		if(gameover){
			System.out.println("GAME OVER - "+getwinner().toString()+" team wins!");
			ACLMessage notifyAll = new ACLMessage(ACLMessage.INFORM);
			for(AID player : this.getplayers()){
				notifyAll.addReceiver(player);
			}
			notifyAll.addReceiver(Utils.getTable(this));
			MessageFormat content = new MessageFormat();
			content.setAction("gameOver");
			content.addArg("winner", getwinner().toString());
			notifyAll.setContent(content.toJSON());
			this.send(notifyAll);
			
			this.removeBehaviour(cycle);
		}
	}
	
	
	public void setup(){
		testKill();
		playing=false;
		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );
        ServiceDescription sd = new ServiceDescription();
        sd.setName("GameMasterAgent");
        sd.setType("GM");
        dfd.addServices(sd);
        
        try {  
            DFService.register( this, dfd );  
        }
        catch (FIPAException fe) {
            fe.printStackTrace(); 
        }

        addBehaviour(new StartGameMasterBehaviour());
        testKill();

    }

    public void setCupidon(String cupidon) {
        this.cupidon = cupidon;
    }

    public class StartGameMasterBehaviour extends OneShotBehaviour{

		@Override
		public void action() {
			addBehaviour(new LogBehaviour());
	        addBehaviour(new GameBeginBehaviour((GameMasterAgent)myAgent));
		}
		
	}

	public void testKill(){
        System.out.println("testKill");

    }

	
	public void setPlaying(){
		playing = true;
	}
	
	public boolean is_playing(){
		return playing; 
	}
	
	public void resetRound(){
		witchKilledPlayer = "";
		witchSavedPlayer = "";
		killedPlayers = new ArrayList<String>();
	}
	
	public void markPlayerAsKilled(String player){
		killedPlayers.add(player);
	}
	
	
	public ArrayList<String> getMarkedPlayers(){
		return killedPlayers;
	}
	
}
		
		
