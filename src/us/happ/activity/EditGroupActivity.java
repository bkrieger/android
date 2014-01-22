package us.happ.activity;

import us.happ.R;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;

public class EditGroupActivity extends ActionBarActivity {
	
	private EditText mSearchET;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editgroup);
		getWindow().setBackgroundDrawable(null); // optimization to reduce overdraw
		
		ActionBar actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Back");
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		
		
		mSearchET = (EditText) findViewById(R.id.search);		
		mSearchET.requestFocus();
	}
}
