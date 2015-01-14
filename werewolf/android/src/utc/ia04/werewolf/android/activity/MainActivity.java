package utc.ia04.werewolf.android.activity;

import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.gui.GuiEvent;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

import utc.ia04.werewolf.android.utils.Constants;
import utc.ia04.werewolf.jade.Player.PlayerAgent;
import utc.ia04.werewolf.jade.Player.PlayerAgentInterface;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity to launch the application 
 * 
 * 1) Enter the ip adress 
 * TODO : 
 * 2) RegisterActivity to enter his name 
 * 3) Start Jade
 * 4) Launch PlayerActivity
 * 
 * @author AudreyB
 *
 */
public class MainActivity extends Activity implements PropertyChangeListener, OnClickListener {
	
	private Logger logger = Logger.getJADELogger(this.getClass().getName());
	
	// Add in a constants file
	public static final int AGENT_ROLE = 0;
	private String playerName;
	private Button enterBtn;
	private TextView mainTxt;
	private EditText hostEdit;
	private TextView hostText;
	
	// Use Jade 
	private ServiceConnection serviceConnection;
	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
	private Context context;
	private String host;
	
	private boolean isRegister = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		context = getApplicationContext(); 
		
		// TODO : Add an if is registred ! 
		this.mainTxt = (TextView) findViewById(R.id.mainTxt);
		
		enterBtn = (Button) findViewById(R.id.enterBtn);
		enterBtn.setOnClickListener(this);
	}

	/**
	 * Lors de la réponse de l'intent 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
	    if (requestCode == Constants.REGISTER_BTN) {
	      // On vérifie aussi que l'opération s'est bien déroulée
	      if (resultCode == RESULT_OK) {
	    	 // Informer Agent du click
	    	  System.out.println("click register ");
				//GuiEvent evt = new GuiEvent(this, 0);
	    	  this.hostText = (TextView) findViewById(R.id.ipTxt);
				this.playerName = data.getExtras().get(Constants.PLAYER_NAME).toString();
				this.mainTxt.setText("Attente d'un rôle");
				this.hostText.setVisibility(View.INVISIBLE);
				this.hostEdit.setVisibility(View.INVISIBLE);
				this.enterBtn.setVisibility(View.INVISIBLE);
				
				// A changer faire saisir l'adresse IP ou la déplacer dans un dossier.
				startJade("Agent"+this.playerName, this.host, "2058", agentStartupCallback);
	      }
	    }
	  }
	
	// To send changed to the Agent 
	private void fireOnGuiEvent(GuiEvent event) {
		try {
			getAgent().fireOnGuiEvent(event);
			return;
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}catch (NullPointerException e){
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "ProblÃ¨me de connection", Toast.LENGTH_SHORT).show();

	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		System.out.println("MainActivity - propertyChange");
		switch(Integer.parseInt(event.getPropertyName())){
			case Constants.WAITING_ROLE : 
				System.out.println("MainActivity - Waiting For a role");
				//this.mainTxt.setText("Attente des autres joueurs pour démarrer la partie");
				//TODO : Affichage bar d'attente 
			break;
			case Constants.AGENT_ROLE : 
				// ROLE AGENT 
				logger.log(Level.INFO, "MainActivity - Role Received : "+(String) event.getNewValue());
				this.isRegister = true;
				Intent intent = new Intent(MainActivity.this, PlayerActivity.class); 
				intent.putExtra(Constants.PLAYER_ROLE, (String)event.getNewValue());
				intent.putExtra(Constants.PLAYER_NAME, this.playerName);
				this.startActivity(intent);
				break;
			default : 
				System.out.println("Unreconnized property name in property change of MainActivity, propertyName = "+ event.getPropertyName());
				logger.log(Level.WARNING, "Unreconnized property name in property change of MainActivity, propertyName = "+ event.getPropertyName());
				break;
		}
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.enterBtn){
			System.out.println("Btn click");
	    	  // Lancement de la page d'enregistrement
	    	  if(!isRegister){
	    		  this.hostEdit = (EditText) findViewById(R.id.ipEdit);
	    		  this.host = hostEdit.getText().toString();
	    		  
	    		  Intent secondActivity = new Intent(MainActivity.this, RegisterActivity.class);
	    		  startActivityForResult(secondActivity, Constants.REGISTER_BTN);
	    	  }
		}
		
	}

	/**
	 * START JADE 
	 */
	
	private PlayerAgentInterface getAgent() throws StaleProxyException, ControllerException{
		return  (PlayerAgentInterface) MicroRuntime.getAgent("Agent"+playerName).getO2AInterface(PlayerAgentInterface.class);
	}
	
	private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
		@Override
		public void onSuccess(AgentController agent) {
		}

		@Override
		public void onFailure(Throwable throwable) {
		}
	};
	
	public void startJade(final String nickname, final String host, final String port, final RuntimeCallback<AgentController> agentStartupCallback) {

		final Properties profile = new Properties();
		profile.setProperty(Profile.MAIN_HOST, host);
		profile.setProperty(Profile.MAIN_PORT, port);
		profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
		profile.setProperty(Profile.JVM, Profile.ANDROID);

		if (AndroidHelper.isEmulator()) {
			// Emulator: this is needed to work with emulated devices
			profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
		} else {
			profile.setProperty(Profile.LOCAL_HOST,
					AndroidHelper.getLocalIPAddress());
		}
		// Emulator: this is not really needed on a real device
		profile.setProperty(Profile.LOCAL_PORT, "1099");

		if (microRuntimeServiceBinder == null) {
			serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
					logger.log(Level.INFO, "Gateway successfully bound to MicroRuntimeService");
					startContainer(nickname, profile, agentStartupCallback);
				};

				public void onServiceDisconnected(ComponentName className) {
					microRuntimeServiceBinder = null;
					logger.log(Level.INFO, "Gateway unbound from MicroRuntimeService");
				}
			};
			logger.log(Level.INFO, "Binding Gateway to MicroRuntimeService...");
			bindService(new Intent(getApplicationContext(),
					MicroRuntimeService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			logger.log(Level.INFO, "MicroRumtimeGateway already binded to service");
			startContainer(nickname, profile, agentStartupCallback);
		}
	}

	private void startContainer(final String nickname, Properties profile,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		if (!MicroRuntime.isRunning()) {
			microRuntimeServiceBinder.startAgentContainer(profile,
					new RuntimeCallback<Void>() {
						@Override
						public void onSuccess(Void thisIsNull) {
							logger.log(Level.INFO, "Successfully start of the container...");
							startAgent(nickname, agentStartupCallback);
						}

						@Override
						public void onFailure(Throwable throwable) {
							logger.log(Level.SEVERE, "Failed to start the container...");
						}
					});
		} else {
			startAgent(nickname, agentStartupCallback);
		}
	}

	private void startAgent(final String nickname, final RuntimeCallback<AgentController> agentStartupCallback) {
		microRuntimeServiceBinder.startAgent(nickname,PlayerAgent.class.getName(),
				new Object[] { getApplicationContext(), this, this.playerName },
				new RuntimeCallback<Void>() {
					@Override
					public void onSuccess(Void thisIsNull) {
						logger.log(Level.INFO, "Successfully start of the "
								+ PlayerAgent.class.getName() + "...");
						try {
							agentStartupCallback.onSuccess(MicroRuntime
									.getAgent(nickname));
						} catch (ControllerException e) {
							// Should never happen
							agentStartupCallback.onFailure(e);
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						logger.log(Level.SEVERE, "Failed to start the "
								+ PlayerAgent.class.getName() + "...");
						agentStartupCallback.onFailure(throwable);
					}
				});
	}
	
	/**
	 * END START JADE
	 */

}
