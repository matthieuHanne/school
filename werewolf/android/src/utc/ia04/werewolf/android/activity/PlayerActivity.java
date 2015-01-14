package utc.ia04.werewolf.android.activity;


import jade.core.MicroRuntime;
import jade.gui.GuiEvent;
import jade.util.Logger;
import jade.util.ObjectManager;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utc.ia04.werewolf.android.utils.Constants;
import utc.ia04.werewolf.jade.Player.PlayerAgentInterface;
import utc.ia04.werewolf.utils.PlayerInfo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The Activity for the game 
 * 
 * To start, show an image to the agent role
 * And show the list of his behaviour
 * @author AudreyB
 *
 */
public class PlayerActivity extends Activity implements PropertyChangeListener {
	private Logger logger = Logger.getJADELogger(this.getClass().getName());
	private static Boolean registerList;
	
	private String playerName;
	
	private TextView playerRole;
	private ImageView playerRoleImg;
	private ImageView stateImg;
	
	private ListView playerList;
	private PlayerInfo.Role role;
	
	// TODO : TO CHANGE ! Just For Test ! 
	private boolean day = false;
	private boolean canVote = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_activity);
		
		Intent intent = this.getIntent();
		this.playerName = (String) intent.getExtras().getString(Constants.PLAYER_NAME);
		
		try {
			if(registerList == null){
				this.getAgent().addNewPropertyChangeListener(this);
				registerList = Boolean.TRUE;
				System.out.println("PlayerActivity - Registred as Listener");
			}
		} catch (StaleProxyException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (ControllerException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		
		this.role = PlayerInfo.Role.valueOf(intent.getExtras().getString(Constants.PLAYER_ROLE));
		
		this.playerRole = (TextView) findViewById(R.id.playerRole);
		this.playerRole.setText(this.role.toString());
		
		this.playerRoleImg = (ImageView) findViewById(R.id.roleImage);
		
		// TODO : Create an enum with role name and image id 
		switch(role){
		case Werewolf : 
			// WereWolf
			playerRoleImg.setImageResource(R.drawable.loupgarou);
			this.canVote = true;
			break;
		case Villager : 
			// Villager
			playerRoleImg.setImageResource(R.drawable.villageois);
			this.canVote = false;
			break;
		case Witch : 
			// Witch
			playerRoleImg.setImageResource(R.drawable.witch);
			this.canVote = false;
			break;
		case Hunter : 
			// Chasseur
			playerRoleImg.setImageResource(R.drawable.chassaur);
			this.canVote = false;
			break;
		default :
			// Others
			playerRoleImg.setImageResource(R.drawable.interr);
			break;
		}
		this.stateImg = (ImageView) findViewById(R.id.stateImage);
		
		String gameState = intent.getExtras().getString(Constants.GAME_STATE);
		if( gameState != null){
			if(gameState.equals("day")){
				System.out.println("PlayerActivity - C'est le jour");
				stateImg.setImageResource(R.drawable.soleil);
			} else if(gameState.equals("night")){
				System.out.println("PlayerActivity - C'est la nuit");
				stateImg.setImageResource(R.drawable.lune);
			} else if(gameState.equals("mort")){
				System.out.println("PlayerActivity - Mort");
				stateImg.setImageResource(R.drawable.interr);
			} else {
				System.err.println("PlayerActivity : Do not recognize game state "+gameState);
				stateImg.setImageResource(R.drawable.interr);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
	    if (requestCode == Constants.PLAYER_VOTE) {
	      // On vérifie aussi que l'opération s'est bien déroulée
	      if (resultCode == RESULT_OK) {
	    	  GuiEvent ev = new GuiEvent(this, Constants.AGENT_WW_VOTE);
	    	  ev.addParameter(data.getExtras().get(Constants.PLAYER_VOTE_NEW));
	    	  try {
				this.getAgent().fireOnGuiEvent(ev);
			} catch (StaleProxyException e) {
				System.err.println("PlayerActivity - Failed to send GUI Event to the Agent "+e.getMessage());
				e.printStackTrace();
			} catch (ControllerException e) {
				System.err.println("PlayerActivity - Failed to send GUI Event to the Agent "+e.getMessage());
				e.printStackTrace();
			}
	      }
	    }
	}
	
	public PlayerAgentInterface getAgent() throws StaleProxyException, ControllerException{
		return  (PlayerAgentInterface) MicroRuntime.getAgent("Agent"+playerName).getO2AInterface(PlayerAgentInterface.class);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		int propertyName = Integer.valueOf(event.getPropertyName());
		
		switch(propertyName){
			case Constants.ENV_NIGHT : 
				day = false;
				System.out.println("PlayerActivity - C'est la nuit");
				Intent intentNight = new Intent(PlayerActivity.this, PlayerActivity.class);
				intentNight.putExtra(Constants.PLAYER_ROLE, this.role.toString());
				intentNight.putExtra(Constants.GAME_STATE, "night");
				intentNight.putExtra(Constants.PLAYER_NAME, this.playerName);
				startActivity(intentNight);
				this.finish();
				break;
			case Constants.ENV_DAY : 
				day = true;
				System.out.println("PlayerActivity - C'est le jour");
				Intent newIntent = new Intent(PlayerActivity.this, PlayerActivity.class);
				newIntent.putExtra(Constants.PLAYER_ROLE, this.role.toString());
				newIntent.putExtra(Constants.GAME_STATE, "day");
				newIntent.putExtra(Constants.PLAYER_NAME, this.playerName);
				startActivity(newIntent);
				this.finish();
				break;
			case Constants.ENV_WW_VOTE : 
				System.out.println("PlayerActivity - Lancement du vote ");
				if(canVote && !day){
					displayPlayerList("ww",(String) event.getNewValue());
				}
				break;
			case Constants.ENV_WT_WAKE : 
				break;
			case Constants.ENV_WT_SAVE : 
				if(day == false){
					newIntent = new Intent(PlayerActivity.this, WitchActivity.class);
					newIntent.putExtra("Action", Constants.ENV_WT_SAVE);
					newIntent.putExtra(Constants.PLAYER_NAME, this.playerName);
					newIntent.putExtra(Constants.PLAYER_VOTE_OLD, (String) event.getNewValue());
					startActivity(newIntent);
				}
				break;
			case Constants.ENV_WT_KILL :
				ObjectMapper map = new ObjectMapper();
				
				if(day == false){
					newIntent = new Intent(PlayerActivity.this, WitchActivity.class);
					newIntent.putExtra("Action", Constants.ENV_WT_KILL);
					newIntent.putExtra(Constants.PLAYER_NAME, this.playerName);
					newIntent.putExtra(Constants.PLAYER_LIST, getListParam((List<String>) event.getNewValue()) );
					startActivity(newIntent);
				}
				break;
			case Constants.AGENT_HT_LIST : 
				ObjectMapper m = new ObjectMapper();
			List<String> list = new ArrayList<String>();
			try {
				list = m.readValue((String) event.getNewValue(), List.class);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				displayPlayerList("hunter",getListParam(list));
				break;
			case Constants.AGENT_GET_KILLED :
				newIntent = new Intent(PlayerActivity.this, PlayerActivity.class);
				newIntent.putExtra(Constants.PLAYER_ROLE, this.role.toString());
				newIntent.putExtra(Constants.GAME_STATE, "mort");
				newIntent.putExtra(Constants.PLAYER_NAME, this.playerName);
				startActivity(newIntent);
				this.finish();
				break;
			default: break;
		}
	}
	/**
	 * Display the liste of player : to allow the player to vote 
	 * @param newValue : An HashMap representing a list of player 
	 */
	private void displayPlayerList(String acteur, String values) {
		Intent secondIntent = new Intent(this, ListPlayerActivity.class);
		secondIntent.putExtra("Acteur", acteur);
		secondIntent.putExtra(Constants.PLAYER_LIST, values);
		secondIntent.putExtra(Constants.PLAYER_NAME, this.playerName);
		startActivityForResult(secondIntent, Constants.PLAYER_VOTE);
	}
	
	private String getListParam(List<String> list){
		ObjectMapper map =  new ObjectMapper();
		HashMap<String,Integer> hm = new HashMap<String, Integer>();
		for (String string : list) {
			hm.put(string, 0);
		}
		
		try {
			return map.writeValueAsString(hm);
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
		return "";
	}
}
