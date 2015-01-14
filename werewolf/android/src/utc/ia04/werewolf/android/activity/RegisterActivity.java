package utc.ia04.werewolf.android.activity;


import utc.ia04.werewolf.android.utils.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity the register the player 
 * The player need to enter his name to start the application
 * @author AudreyB
 *
 */
public class RegisterActivity extends Activity {
	
	private Button registerBtn;
	private EditText name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		
		name = (EditText) findViewById(R.id.playerName);
		
		registerBtn = (Button) findViewById(R.id.registerButton);
		registerBtn.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  // Return his name to the main activity, and termine
		    	  Intent result = new Intent();
		          result.putExtra(Constants.PLAYER_NAME,name.getText().toString());
		          setResult(RESULT_OK, result);
		          finish();
		      }
		});
	}
}
