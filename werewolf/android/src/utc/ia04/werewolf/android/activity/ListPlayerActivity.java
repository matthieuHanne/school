package utc.ia04.werewolf.android.activity;

import jade.core.MicroRuntime;
import jade.gui.GuiEvent;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import utc.ia04.werewolf.android.utils.Constants;
import utc.ia04.werewolf.jade.Player.PlayerAgentInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ListPlayerActivity extends Activity implements PropertyChangeListener {
	
	private Intent intent;
	private ListView listPlayer;
	public String newVote;
	private String playerName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listplayer_activity);
        
        try {
			getAgent().addNewPropertyChangeListener(this);
		} catch (StaleProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ControllerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        ObjectMapper mapper = new ObjectMapper();
        
        // Get ListView object from xml
        this.listPlayer = (ListView) findViewById(R.id.listPlayer);
        
        // Get intent args
        this.intent = this.getIntent();
        this.playerName = (String) intent.getExtras().get(Constants.PLAYER_NAME);
        String hmPlayer = (String) intent.getExtras().get(Constants.PLAYER_LIST);
        HashMap<String,Integer> hm = null;
        if(hmPlayer!=null){
	        try {
				 hm = mapper.readValue(hmPlayer, new TypeReference<HashMap<String, Integer>>(){});
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
        } else {
        	hm = new HashMap<String,Integer>();
        	System.err.println("ListPlayerActivity - Impossible to read arguments PLAYER_LIST");
        }

        List<PlayerRow> listPlayerRow = new ArrayList<PlayerRow>();
        for (Entry<String, Integer> playerRow : hm.entrySet()) {
        	listPlayerRow.add(new PlayerRow(playerRow.getKey(), Integer.valueOf(playerRow.getValue())));
		}
        // Define a new Adapter
        // adapter = new MyArrayAdapter(getApplicationContext(), Arrays.asList(values));
        // TODO : TO TEST 
        MyArrayAdapter adapter = new MyArrayAdapter(getApplicationContext(), listPlayerRow);

        // Assign adapter to ListView
        this.listPlayer.setAdapter(adapter); 
        
        // ListView Item Click Listener
        this.listPlayer.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
               // ListView Clicked item index
               int itemPosition = position;
               
               // ListView Clicked item value
               PlayerRow itemValue = (PlayerRow)listPlayer.getItemAtPosition(position);
                  
                // Show Alert
                Toast.makeText(getApplicationContext(), "Position :"+itemPosition+"  ListItem : " +itemValue.name , Toast.LENGTH_LONG).show();
                newVote = itemValue.name;
                // Envoyer un message à PlayerAgent 
                GuiEvent ev = null;
                if(intent.getExtras().get("Acteur").equals("witch")){
                	 ev = new GuiEvent(this, Constants.ENV_WT_KILL);
                } else if(intent.getExtras().get("Acteur").equals("ww")) {
                	ev = new GuiEvent(this, Constants.AGENT_WW_VOTE);
                } else if(intent.getExtras().get("Acteur").equals("hunter")) {
                	ev = new GuiEvent(this, Constants.AGENT_HT_LIST);
                }
	  	    	ev.addParameter(newVote);
	  	    	  try {
	  				getAgent().fireOnGuiEvent(ev);
	  			} catch (StaleProxyException e) {
	  				System.err.println("PlayerActivity - Failed to send GUI Event to the Agent "+e.getMessage());
	  				e.printStackTrace();
	  			} catch (ControllerException e) {
	  				System.err.println("PlayerActivity - Failed to send GUI Event to the Agent "+e.getMessage());
	  				e.printStackTrace();
	  			}
		        finish();
              }
         }); 
    }
    
	public PlayerAgentInterface getAgent() throws StaleProxyException, ControllerException{
		return  (PlayerAgentInterface) MicroRuntime.getAgent("Agent"+playerName).getO2AInterface(PlayerAgentInterface.class);
	}
    
    //////////////////////////////////////////////
    ////// SHOW PLAYER LIST /////////////////////
    ////////////////////////////////////////////
    
    public class PlayerRow {
    	public String name;
    	public String vote;

    	PlayerRow(String name, Integer vote){
    		this.name = name;
    		this.vote = String.valueOf(vote);
    	}
    }
    public class MyArrayAdapter extends BaseAdapter {

    	private final Context context;

    	private final List<PlayerRow> list;
    	//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    	private LayoutInflater inflater;

    	public MyArrayAdapter(Context context, List<PlayerRow> list) {
    		this.context = context;
    		this.list = list;
    		this.inflater = LayoutInflater.from(context);
    	}

    	public View getView(int position, View convertView, ViewGroup parent) {

    		LinearLayout layoutItem;
    		//1) : Réutilisation des layouts
    		if (convertView == null) {
    			//Initialisation de notre item à partir du  layout XML "personne_layout.xml"
    			layoutItem = (LinearLayout) inflater.inflate(R.layout.row_layout, parent, false);
    		} else {
    			layoutItem = (LinearLayout) convertView;
    		}

    		// 2) : Recuperation des TextView de row_layout
    		TextView  name = (TextView) layoutItem.findViewById(R.id.name);
    		TextView voteView = (TextView) layoutItem.findViewById(R.id.vote);
    		// 3) Récupération  des valeurs 
    		name.setText(list.get(position).name);
    		voteView.setText(list.get(position).vote);

    		//On retourne l'item créé.
    		return layoutItem;
    	}

    	@Override
    	public int getCount() {
    		return list.size();
    	}

    	@Override
    	public Object getItem(int position) {
    		return list.get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		return position;
    	}

    }
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		int propertyName = Integer.valueOf(event.getPropertyName());
		switch(propertyName){
			case Constants.ENV_WW_ENDVOTE : 
				this.finish();
				break;
			default : break;
		}
	}
}
