package us.happ.android.activity;

import us.happ.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
		
		mComposeET = (EditText) findViewById(R.id.compose_message);

		setTag(Tag.CHILL);
		setDuration(Duration.TWO_HOURS);
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
		final Tag[] tags = Tag.values();
		String[] tagLabels = new String[tags.length];
		for (int i = 0; i < tags.length; i++) {
			tagLabels[i] = tags[i].label;
		}
		builder.setTitle(getResources().getString(R.string.mood_title))
				.setItems(tagLabels, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setTag(tags[which]);
					}
				});
		builder.create().show();
	}

	public void onClickDuration(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final Duration[] durations = Duration.values();
		String[] durationLabels = new String[durations.length];
		for (int i = 0; i < durations.length; i++) {
			durationLabels[i] = durations[i].label;
		}
		builder.setTitle(getResources().getString(R.string.duration_title))
				.setItems(durationLabels, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setDuration(durations[which]);
					}
				});
		builder.create().show();
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
