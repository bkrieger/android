package us.happ.activity;

import us.happ.R;
import us.happ.utils.Storage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		getWindow().setBackgroundDrawable(null); // optimization to reduce overdraw
		
		
	}
	
	public void onAccessClick(View v){
		Storage.setAllowContactsAccess(this, true);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
}
