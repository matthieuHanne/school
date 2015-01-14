package utc.ia04.werewolf.android.activity;

import jade.core.MicroRuntime;
import jade.gui.GuiEvent;
import jade.util.ObjectManager;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utc.ia04.werewolf.android.utils.Constants;
import utc.ia04.werewolf.jade.Player.PlayerAgentInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WitchActivity extends Activity {
	
	private TextView question;
	private TextView peopleDead;
	private Button  saveBtn;
	private Button killBtn;
	private String playerName;
	private String playerList;
	private static Boolean registerList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.witch_activity);
		
		Intent intent = getIntent();
		playerName = (String) intent.getExtras().get(Constants.PLAYER_NAME);
		
		/**try {
			if(registerList == null){
				this.getAgent().addNewPropertyChangeListener(this);
				registerList = Boolean.TRUE;
				System.out.println("PlayerActivity - Registred as Listener");
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}*/
		
		this.saveBtn = (Button) findViewById(R.id.saveBtn);
		this.killBtn = (Button) findViewById(R.id.killBtn);
		this.question = (TextView) findViewById(R.id.question);
		this.peopleDead = (TextView) findViewById(R.id.peopleDead);
		
		if(intent.getExtras().get("Action").equals(Constants.ENV_WT_SAVE)){
			setSavePotion(intent.getExtras().getString(Constants.PLAYER_VOTE_OLD));
		} else {
			playerList = (String) intent.getExtras().get(Constants.PLAYER_LIST);
			setKillPotion(); 
		}
	}
	
	public void setSavePotion(String deadPeople){
			this.question.setVisibility(View.VISIBLE);
			this.question.setText("Voulez-vous sauver cette personne ?");
			this.peopleDead.setVisibility(View.VISIBLE);
			this.peopleDead.setText(deadPeople);
			this.saveBtn.setVisibility(View.VISIBLE);
			this.saveBtn.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View v) {
	    	  GuiEvent ev = new GuiEvent(this, Constants.ENV_WT_SAVE);
	    	  ev.addParameter("true");
	    	  finish();
	    	  try {
				((PlayerAgentInterface)getAgent()).fireOnGuiEvent(ev);
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	});
			this.killBtn.setVisibility(View.VISIBLE);
		this.killBtn.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  GuiEvent ev = new GuiEvent(this, Constants.ENV_WT_SAVE);
		    	  ev.addParameter("");
		    	  finish();
		    	  try {
					((PlayerAgentInterface)getAgent()).fireOnGuiEvent(ev);
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		});
	}
	
	private void setKillPotion(){
		this.peopleDead.setVisibility(View.INVISIBLE);
		this.question.setVisibility(View.VISIBLE);
		this.question.setText("Voulez-vous tuer une personne ?");
		this.saveBtn.setVisibility(View.VISIBLE);
		this.saveBtn.setOnClickListener(new View.OnClickListener() {
		  @Override
		   public void onClick(View v) {
		    	Intent secondIntent = new Intent( v.getContext() , ListPlayerActivity.class);
		    	secondIntent.putExtra("Acteur", "witch");
		  		secondIntent.putExtra(Constants.PLAYER_LIST, playerList);
		  		secondIntent.putExtra(Constants.PLAYER_NAME, getPlayerName());
		  		startActivityForResult(secondIntent, Constants.WITCH_KILL);  
		  		finish();
		      }
		});
		this.killBtn.setVisibility(View.VISIBLE);
		this.killBtn.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  GuiEvent ev = new GuiEvent(this, Constants.ENV_WT_KILL);
		    	  ev.addParameter("false");
		    	  ev.addParameter("");
		    	  try {
					((PlayerAgentInterface)getAgent()).fireOnGuiEvent(ev);
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		});
	}
	
	public PlayerAgentInterface getAgent() throws StaleProxyException, ControllerException{
		return  (PlayerAgentInterface) MicroRuntime.getAgent("Agent"+playerName).getO2AInterface(PlayerAgentInterface.class);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
	    if (requestCode == Constants.WITCH_KILL) {
	      // On vérifie aussi que l'opération s'est bien déroulée
	      if (resultCode == RESULT_OK) {
	    	  GuiEvent ev = new GuiEvent(this, Constants.ENV_WT_KILL);
	    	  ev.addParameter("true");
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
	
	private String getPlayerName(){ return this.playerName; }

}
