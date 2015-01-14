package utc.ia04.werewolf.android.utils;

/**
 * Utils constant to run the application
 * @author AudreyB
 *
 */
public class Constants {
	
	public static final String SettingsFileName = "settingsfile";
	//public static final String host = "192.168.1.16"; // Maison
	//public static final String host = "172.26.134.104"; // Table
	public static final String agentName="PlayerAgent";
	public static final String AGENT_INTERFACE = "agent_interface";

	public static final String BUTTON ="button";
	public static final String REGISTER = "register";
	public static final String PLAYER_ROLE = "player_role";
	public static final String PLAYER_NAME = "player_name";
	public static final int REGISTRED = 1;
	public static final int NO_REGISTRED = 0;
	
	
	public static final String ROLE_RECEIVED = "role_received";

	public static final String GAME_STATE =  "game_state";
	public static final String PLAYER_LIST = "player_list";
	
	// Intent Request (for startActivityForResult)
	public static final int REGISTER_BTN = 1;
	public static final int PLAYER_VOTE = 2;
	public static final int WITCH_KILL =  3;
	
	// PropertyChange (Comm with agent)
	public static final int WAITING_ROLE = 0;
	public static final int AGENT_ROLE = 1;
	public static final int ENV_NIGHT = 2;
	public static final int ENV_WW_VOTE = 3;
	public static final int AGENT_WW_VOTE = 4;
	public static final int ENV_WW_ENDVOTE = 5;
	public static final int ENV_DAY = 6;
	public static final int AGENT_GET_KILLED = 7;
	public static final int AGENT_HT_LIST = 8;
	public static final int ENV_END = 9;
	public static final int ENV_WT_WAKE = 10;
	public static final int ENV_WT_SAVE = 11;
	public static final int ENV_WT_KILL = 12;
	
	// To send vote for AgentPlayer
	public static final String PLAYER_VOTE_OLD ="player_vote_old";
	public static final String PLAYER_VOTE_NEW ="player_vote_new";
	

}
