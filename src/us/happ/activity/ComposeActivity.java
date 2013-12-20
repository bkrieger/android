package us.happ.activity;

import us.happ.adapter.DurationAdapter;
import us.happ.adapter.GroupAdapter;
import us.happ.adapter.TagsAdapter;
import us.happ.R;
import us.happ.model.Duration;
import us.happ.model.Group;
import us.happ.model.Mood;
import us.happ.model.Tag;
import us.happ.utils.Happ;
import us.happ.utils.Storage;
import us.happ.utils.Happ.KeyboardListener;
import us.happ.view.PickerListView;
import android.annotation.SuppressLint;
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
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
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
	private Group group;
	private int chosen_tag_position;
	private int chosen_duration_position;
	private int chosen_group_position;
	
	private ImageView mMoodIconView;
	private TextView mDurationTextView;
	private TextView mGroupTextView;

	private PickerListView mListView;

	private TagsAdapter mTagsAdapter;
	private DurationAdapter mDurationAdapter;
	private GroupAdapter mGroupAdapter;

	private static final int PICKER_MOOD = 0x01;
	private static final int PICKER_DURATION = 0x02;
	private static final int PICKER_GROUP = 0x03;
	
	private int pickerId;

	private MenuItem mActionSubmit;

	private View optionsView;
	private TextView mCounterView;
	
	private boolean newlineDetected = false;
	private String replacedText;
	private int nextCursorPosition;

	private int mKeyboardHeight;
	
	private static final int MAX_LENGTH = 50;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		getWindow().setBackgroundDrawable(null); // optimization to reduce overdraw
		
		mContext = this;

		actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle("Back");
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		
		mComposeET = (EditText) findViewById(R.id.compose_message);
		
		// Delaying the keyboard (lags on older devices)
		mComposeET.setFocusable(false);
		mComposeET.setFocusableInTouchMode(false);
		mComposeET.setEnabled(false);
		
		// Delay view initialization until animation is over
		Handler handler = new Handler();
		Runnable r = new Runnable(){
			@Override
			public void run() {
				initView();	
			}
		};
		handler.postDelayed(r, getResources().getInteger(android.R.integer.config_longAnimTime));
	}
	
	private void initView(){
		
		final int colorWhite = getResources().getColor(R.color.white);
		final int colorBlack = getResources().getColor(R.color.black);
		final int colorPurple = getResources().getColor(R.color.happ_purple);
		final int colorGray = getResources().getColor(R.color.gray);
		
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
		
		// Prevents pasting of newline characters
		mComposeET.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				mActionSubmit.setEnabled(s.length() > 0);
				if (s.length() < MAX_LENGTH){
					mCounterView.setTextColor(colorGray);
				} else {
					mCounterView.setTextColor(colorPurple);
				}
				mCounterView.setText((MAX_LENGTH - s.length()) + "");
				if (newlineDetected){
					newlineDetected = false;
					mComposeET.setText(replacedText);
					mComposeET.setSelection(nextCursorPosition);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains("\n")){
					newlineDetected = true;
					replacedText = s.toString().replaceAll("\n", " ");
					nextCursorPosition = start + count;
				}
			}
			
		});
		
		final View moodWrapper = findViewById(R.id.mood_wrapper);
		final View durationWrapper = findViewById(R.id.duration_wrapper);
		final View groupWrapper = findViewById(R.id.group_wrapper);
		
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
					// TODO cache all the drawables?
					mMoodIconView.setImageDrawable(getResources().getDrawable(Mood.resIdFromTag(tag.valueForPost, true)));
				} else {
					moodWrapper.setBackgroundResource(R.drawable.list_selector);
					// TODO cache all the drawables?
					mMoodIconView.setImageDrawable(getResources().getDrawable(Mood.resIdFromTag(tag.valueForPost, false)));
				}
			}
			
		});
		
		durationWrapper.setOnFocusChangeListener(new OnFocusChangeListener(){

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
					durationWrapper.setBackgroundColor(colorPurple);
				} else {
					mDurationTextView.setTextColor(colorBlack);
					durationWrapper.setBackgroundResource(R.drawable.list_selector);
				}
			}
			
		});
		
		groupWrapper.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mListView.setVisibility(View.VISIBLE);
					if (pickerId != PICKER_GROUP){
						mListView.setAdapter(mGroupAdapter);
						mListView.setChosen(chosen_group_position);
						pickerId = PICKER_GROUP;
					}
					mGroupTextView.setTextColor(colorWhite);
					groupWrapper.setBackgroundColor(colorPurple);
				} else {
					mGroupTextView.setTextColor(colorBlack);
					groupWrapper.setBackgroundResource(R.drawable.list_selector);
				}
			}
			
		});
		
		// Option buttons
		optionsView = findViewById(R.id.compose_options);
	
		mCounterView = (TextView) findViewById(R.id.compose_counter);
		mMoodIconView = (ImageView) findViewById(R.id.mood_icon);
		mDurationTextView = (TextView) findViewById(R.id.duration_value);	
		mGroupTextView = (TextView) findViewById(R.id.group_value);
		
		mListView = (PickerListView) findViewById(android.R.id.list);
		mTagsAdapter = new TagsAdapter(this, 0, Tag.values());
		mDurationAdapter = new DurationAdapter(this, 0, Duration.values());
		mGroupAdapter = new GroupAdapter(this, 0, Group.values());

		// Should have a better way of doing this
		setTag(Tag.CHILL, false);
		chosen_tag_position = 0;
		setDuration(Duration.FOUR_HOURS);
		chosen_duration_position = 4;
		setGroup(Group.FRIENDS);
		chosen_group_position = 0;
		
		// Show keyboard
		mComposeET.setFocusable(true);
		mComposeET.setFocusableInTouchMode(true);
		mComposeET.setEnabled(true);
		mComposeET.requestFocus();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mComposeET, 0);
		
		mKeyboardHeight = Storage.getKeyboardHeight(mContext);
		if (mKeyboardHeight != 0){
			keyboardMeasured();
		} else {
			Happ.setKeyboardMeasurer(this, new KeyboardListener(){

				@Override
				public void onKeyboardMeasured(int keyboardHeight) {
					mKeyboardHeight = keyboardHeight;
					keyboardMeasured();
				}
				
			});
		}
	}
	
	private void keyboardMeasured(){
		int actionbarHeight = Happ.getActionBarHeight(this);
		int statusbarHeight = Happ.getStatusBarHeight(this);
		
		View contentView = findViewById(android.R.id.content);
		
		View v = contentView.findViewById(R.id.activity_compose);
        
        int contentHeight = contentView.getRootView().getHeight() - statusbarHeight - actionbarHeight - mKeyboardHeight;
		
        int width = v.getWidth() - v.getPaddingLeft()*2;
        
        int marginTop = contentHeight;
    
        mListView.setDimen(width,  mKeyboardHeight - v.getPaddingTop());
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
        
        // TODO improve performance (high process time of animation)
        // TODO explore options other than alpha animation + hardware Acceleration
		// Maybe paint a white layer on top of the view to cover it?
        Animation anim = new AlphaAnimation(0.00f, 1.00f);
        anim.setDuration(500);
        Happ.startAnimationWithHardwareAcceleration(optionsView, anim);
        
        // Setting height of EditText
        mComposeET.setLayoutParams(new LayoutParams(width, marginTop - optionsHeight));
        
        // Counter
        mCounterView.setLayoutParams(new LayoutParams(width, marginTop - optionsHeight));
        mCounterView.setVisibility(View.VISIBLE);
    
	}
	
	@Override
	public void onResume(){
		super.onResume();
		// TODO
		// Case when notification bar is dragged down before keyboard comes up
		if (mKeyboardHeight == 0){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mComposeET, 0);
		}
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
		} else if (pickerId == PICKER_GROUP){
			setGroup(Group.values()[position]);
			chosen_group_position = position;
		}
	}
	
	// Initial tag is purple instead of white
	public void setTag(Tag tag, boolean inverse){
		this.tag = tag;
		if (mMoodIconView != null){
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
	
	public void setGroup(Group group){
		this.group = group;
		if (mGroupTextView != null)
			mGroupTextView.setText(group.label);
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
