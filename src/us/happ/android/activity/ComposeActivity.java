package us.happ.android.activity;

import us.happ.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
	}

	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {    
    	switch (item.getItemId()) {        
          case android.R.id.home:
        	  finish();
        	  overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
        	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);    
    	}
    }
	
	public void onComposeSubmit(View v){
		Intent intent = new Intent();
		intent.putExtra("compose_msg", mComposeET.getText().toString());
		Log.i("text", mComposeET.getText().toString());
		setResult(1, intent);
		finish();
  	  	overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
	}
	
}
