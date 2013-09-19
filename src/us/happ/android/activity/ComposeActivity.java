package us.happ.android.activity;

import us.happ.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import us.happ.android.adapter.TagsAdapter;
import us.happ.android.model.Tag;
import us.happ.android.model.Duration;

public class ComposeActivity extends ActionBarActivity {

	private ActionBar actionbar;
	private EditText mComposeET;

	private Tag tag;
	private Duration duration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);

		actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Back");
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		
//		final View contentView = findViewById(android.R.id.content);
//		contentView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//		    @Override
//		    public void onGlobalLayout() {
//		        int heightDiff = contentView.getRootView().getHeight() - contentView.getHeight();
//		        
//		        if (heightDiff > 300) { // if more than 300 pixels, its probably a keyboard...
//		            Log.i("keyboard", "shown");
//		            
//		            Display display = getWindowManager().getDefaultDisplay(); 
//		            int width = display.getWidth();  // deprecated
//		            int height = display.getHeight();  // deprecated
//		            
//		            int marginTop = height - heightDiff - contentView.getHeight();
//		            
//		            Log.i("marginTop", contentView.getHeight()+"");
//
//		            
//		        }
//		     }
//		});
		
		mComposeET = (EditText) findViewById(R.id.compose_message);
		mComposeET.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event != null && event.getAction() != KeyEvent.ACTION_DOWN){
					return false;
				} else if (actionId == EditorInfo.IME_ACTION_SEND) { 
					submit();
					return true;
				}
				return true;
			}
			
		});

		setTag(Tag.CHILL);
		setDuration(Duration.FOUR_HOURS);
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
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onClickMood(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_tags, null);
		builder.setView(dialogView);
		ListView mListView = (ListView) dialogView.findViewById(android.R.id.list);
		TagsAdapter adapter = new TagsAdapter(this, 0, Tag.values());
		mListView.setAdapter(adapter);
		
		final AlertDialog dialog = builder.create();
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				setTag(Tag.values()[position]);
				dialog.dismiss();
			}
			
		});

		dialog.show();
	}

	public void onClickDuration(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_tags, null);
		builder.setView(dialogView);
		ListView mListView = (ListView) dialogView.findViewById(android.R.id.list);
		((TextView) dialogView.findViewById(R.id.dialog_header)).setText(getResources().getString(R.string.duration_title));
		
		String[] durations = new String[Duration.values().length];
		for (int i = 0; i < Duration.values().length; i++){
			durations[i] = Duration.values()[i].label;
		}
		mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_dialog_duration, durations));
		
		final AlertDialog dialog = builder.create();
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				setDuration(Duration.values()[position]);
				dialog.dismiss();
			}
			
		});

		dialog.show();

	}

	private void setTag(Tag tag) {
		this.tag = tag;
		Button button = (Button) findViewById(R.id.mood_value);
		button.setText(tag.label);
	}
	
	private void setDuration(Duration duration) {
		this.duration = duration;
		TextView textView = (TextView) findViewById(R.id.duration_value);
		textView.setText(duration.label);
	}
	
	public void submit() {

		String message = mComposeET.getText().toString();
		Intent intent = new Intent();

		if (message.length() > 0) {
			intent.putExtra("compose_msg", message);
			intent.putExtra("compose_tag", tag.valueForPost);
			intent.putExtra("compose_duration", duration.valueForPost);
			setResult(1, intent);
		} else {
			setResult(0, intent);
		}

		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
	}

}
