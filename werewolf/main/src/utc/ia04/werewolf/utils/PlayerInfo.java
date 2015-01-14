package utc.ia04.werewolf.utils;

import jade.core.AID;

public class PlayerInfo {

	public enum Role{
		Villager,
		Werewolf,
		Cupidon,
		Witch,
		Thief,
		Hunter,
		Medium,
		Player,
		Lovers
	}
	
	public boolean isAlive = true;
	public boolean isInLove = false;
	public Role playerrole = Role.Villager;
	public AID agentID = null;
	
	// ANDROID 
	public static final String WAITING_ROLE = "0";
	public static final String AGENT_ROLE = "1";
	public static final String ENV_NIGHT = "2";
	public static final String ENV_WW_VOTE = "3";
	public static final String AGENT_WW_VOTE = "4";
	public static final String ENV_WW_ENDVOTE = "5";
    public static final String ENV_DAY = "6";
    public static final String AGENT_GET_KILLED = "7";
    public static final String AGENT_HT_LIST = "8";
    public static final String ENV_END = "9";
	
	public static final String ENV_WT_WAKE = "10";
    public static final String ENV_WT_SAVE = "11";
    public static final String ENV_WT_KILL = "12";
	public static final String AGENT_CD_LIST = "13";
	public PlayerInfo (){}
	
	public static final long WEREWOLF_VOTE_TIMEOUT = 2000;

}

