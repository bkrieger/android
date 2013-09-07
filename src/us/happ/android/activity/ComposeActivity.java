package us.happ.android.activity;

import us.happ.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ComposeActivity extends ActionBarActivity {
	
	private ActionBar actionbar;
	private EditText mComposeET;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		
		mComposeET = (EditText) findViewById(R.id.compose_message);
		
		actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Back");
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {    
    	switch (item.getItemId()) {        
          case android.R.id.home:
        	  finish();
        	  overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
        	  return true;
          case R.id.action_submit:
        	  submit();
        	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);    
    	}
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
	       finish();
	       overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
	       return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void onComposeSubmit(View v){
		submit();
	}
	
	public void submit(){
		
		String message = mComposeET.getText().toString();
		Intent intent = new Intent();
		
		if (message.length() > 0){	
			intent.putExtra("compose_msg", message);
			setResult(1, intent);
		} else {
			setResult(0, intent);
		}
		
		finish();
  	  	overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
	}
	
}
