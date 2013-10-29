package us.happ.android.activity;

import us.happ.android.R;
import us.happ.android.adapter.DurationAdapter;
import us.happ.android.adapter.TagsAdapter;
import us.happ.android.model.Duration;
import us.happ.android.model.Mood;
import us.happ.android.model.Tag;
import us.happ.android.utils.Happ;
import us.happ.android.view.PickerListView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ComposeActivity extends ActionBarActivity {

	private Context mContext;
	
	private ActionBar actionbar;
	private EditText mComposeET;

	private Tag tag;
	private Duration duration;
	private int chosen_tag_position;
	private int chosen_duration_position;
	
	private ImageView mMoodIconView;
	private TextView mMoodTextView;
	private TextView mDurationTextView;

	private PickerListView mListView;

	private TagsAdapter mTagsAdapter;
	private DurationAdapter mDurationAdapter;
	
	// flags
	private boolean keyboardInitialized = false;

	private int actionbarHeight;
	private int statusbarHeight;

	private static final int PICKER_MOOD = 0x01;
	private static final int PICKER_DURATION = 0x02;
	
	private int pickerId;

	private MenuItem mActionSubmit;

	private View optionsView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		
		mContext = this;

		actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Back");
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		
		
		actionbarHeight = Happ.getActionBarHeight(this);
		statusbarHeight = Happ.getStatusBarHeight(this);
		
		final int colorWhite = getResources().getColor(R.color.white);
		final int colorBlack = getResources().getColor(R.color.black);
		final int colorPurple = getResources().getColor(R.color.happ_purple);
		
		final View contentView = findViewById(android.R.id.content);
		contentView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	if (keyboardInitialized) return;
		    	
		        int heightDiff = contentView.getRootView().getHeight() - statusbarHeight - actionbarHeight - contentView.getHeight();
		        
		        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
		            Log.i("keyboard", "shown");
		            keyboardInitialized = true; 
		            
		            View v = contentView.findViewById(R.id.activity_compose);
		            
		            int width = v.getWidth() - v.getPaddingLeft()*2;
		            
		            int marginTop = contentView.getHeight();
		        
		            mListView.setDimen(width,  heightDiff - v.getPaddingTop());
		            LayoutParams params = (LayoutParams) mListView.getLayoutParams();
		            params.topMargin = marginTop-mListView.getTop();
		            mListView.setLayoutParams(params);
		            
		            // Positioning options
		            optionsView.setVisibility(View.VISIBLE);
		            optionsView.measure(
		            		MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
		            		MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		            int optionsHeight = optionsView.getMeasuredHeight();
		            params = (LayoutParams) optionsView.getLayoutParams();
		            params.topMargin = marginTop - optionsHeight;
		            optionsView.setLayoutParams(params);
		            Animation anim = new AlphaAnimation(0.00f, 1.00f);
		            anim.setDuration(500);
		            optionsView.startAnimation(anim);
		            
		            // Setting height of EditText
		            mComposeET.setLayoutParams(new LayoutParams(width, marginTop - optionsHeight));
		        }
		     }
		});
		
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
		
		mComposeET.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mComposeET.getWindowToken(), 0);	
				} else {
					mListView.setVisibility(View.GONE);
				}
			}
			
		});
		
		mComposeET.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				mActionSubmit.setEnabled(s.length() > 0);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});

		final View moodWrapper = findViewById(R.id.mood_wrapper);
		mMoodIconView = (ImageView) findViewById(R.id.mood_icon);
		mMoodTextView = (TextView) findViewById(R.id.mood_value);
		mDurationTextView = (TextView) findViewById(R.id.duration_value);
		
		moodWrapper.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mListView.setVisibility(View.VISIBLE);
					if (pickerId != PICKER_MOOD){
						mListView.setAdapter(mTagsAdapter);
						mListView.setChosen(chosen_tag_position);
						pickerId = PICKER_MOOD;
					}
					moodWrapper.setBackgroundColor(colorPurple);
					mMoodTextView.setTextColor(colorWhite);
					// TODO cache all the drawables?
					mMoodIconView.setImageDrawable(getResources().getDrawable(Mood.resIdFromTag(tag.valueForPost, true)));
				} else {
					moodWrapper.setBackgroundColor(colorWhite);
					mMoodTextView.setTextColor(colorBlack);
					// TODO cache all the drawables?
					mMoodIconView.setImageDrawable(getResources().getDrawable(Mood.resIdFromTag(tag.valueForPost, false)));
				}
			}
			
		});
		
		mDurationTextView.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mListView.setVisibility(View.VISIBLE);
					if (pickerId != PICKER_DURATION){
						mListView.setAdapter(mDurationAdapter);
						mListView.setChosen(chosen_duration_position);
						pickerId = PICKER_DURATION;
					}
					mDurationTextView.setTextColor(colorWhite);
					mDurationTextView.setBackgroundColor(colorPurple);
				} else {
					mDurationTextView.setTextColor(colorBlack);
					mDurationTextView.setBackgroundColor(colorWhite);
				}
			}
			
		});
		
		// Option buttons
		optionsView = findViewById(R.id.compose_options);
		
		mListView = (PickerListView) findViewById(android.R.id.list);
		mTagsAdapter = new TagsAdapter(this, 0, Tag.values());
		mDurationAdapter = new DurationAdapter(this, 0, Duration.values());
		
		// Should have a better way of doing this
		setTag(Tag.CHILL, false);
		chosen_tag_position = 0;
		setDuration(Duration.FOUR_HOURS);
		chosen_duration_position = 4;
		
		// Delaying the keyboard (lags on older devices)
		mComposeET.setFocusable(false);
		mComposeET.setFocusableInTouchMode(false);
		mComposeET.setEnabled(false);
		
		Handler handler = new Handler();
		Runnable r = new Runnable(){
			@Override
			public void run() {
				mComposeET.setFocusable(true);
				mComposeET.setFocusableInTouchMode(true);
				mComposeET.setEnabled(true);
				mComposeET.requestFocus();
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mComposeET, 0);	
			}
		};
		handler.postDelayed(r, getResources().getInteger(android.R.integer.config_longAnimTime));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		mActionSubmit = menu.findItem(R.id.action_submit);
		mActionSubmit.setEnabled(false);
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
	
	public void setPicker(int position){
		if (pickerId == PICKER_MOOD){
			setTag(Tag.values()[position]);
			chosen_tag_position = position;
		} else if (pickerId == PICKER_DURATION){
			setDuration(Duration.values()[position]);
			chosen_duration_position = position;
		}
	}
	
	// Initial tag is purple instead of white
	public void setTag(Tag tag, boolean inverse){
		this.tag = tag;
		if (mMoodTextView != null){
			mMoodTextView.setText(tag.label);
			mMoodIconView.setImageDrawable(getResources().getDrawable(Mood.resIdFromTag(tag.valueForPost, inverse)));
		}
	}

	public void setTag(Tag tag) {
		setTag(tag, true);
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
		if (mDurationTextView != null)
			mDurationTextView.setText(duration.label);
	}
	
	public void submit() {
	
		String message = mComposeET.getText().toString();
		
		if (message.length() == 0){
			Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
			return;
		}
		
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
