package us.happ.android.activity;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import us.happ.android.R;
import us.happ.android.fragment.BoardFragment;
import us.happ.android.fragment.ContactsFragment;
import us.happ.android.fragment.FeedbackFragment;
import us.happ.android.fragment.HappFragment;
import us.happ.android.gcm.GcmIntentService;
import us.happ.android.service.APIService;
import us.happ.android.service.ServiceHelper;
import us.happ.android.service.ServiceReceiver;
import us.happ.android.utils.ContactsManager;
import us.happ.android.utils.Storage;
import us.happ.android.view.Drawer;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ServiceReceiver.Receiver {
	private final static String TAG = "MainActivity";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private String SENDER_ID = "676728144551";
	private GoogleCloudMessaging gcm;
	private String regid;
	
	private boolean activityDestroyed = false;
	
	// Activity results flags
	private static final int ACTIVITY_COMPOSE = 0x01;
	
	// receiver flags
	private int getMoodsId = -1;
	private int postMoodsId = -1;
	private int postGcmRegisterId = -1;
	private int postFeedbackId = -1;
	
	// TODO should I keep a copy of all fragments? leak?
	// Fragments
	private BoardFragment mBoardFragment;
	private ContactsFragment mFriendsFragment;
	private FeedbackFragment mFeedbackFragment;
	
	// Fragment IDs
	private static final int FRAGMENT_BOARD = 0x01;
	private static final int FRAGMENT_FRIENDS = 0x02;
	private static final int FRAGMENT_FEEDBACK = 0x03;
	private int fragmentId;
	private HappFragment mFragment;
	
	private ServiceReceiver mReceiver;
	private String mPhoneNumber;
	private ActionBar actionbar;
	private Drawer mDrawer;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ProgressDialog mProgressDialog;
	private ContactsManager mContactsManager;
	private ServiceHelper mServiceHelper;

	private FragmentTransaction mFragmentTransaction;
	
	private TextView selectedMenuItem;
	private TextView menuHappening;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawable(null); // optimization to reduce overdraw
		
		mContactsManager = ContactsManager.getInstance(this);
		
		mFragmentTransaction = getSupportFragmentManager().beginTransaction();
		
		// Default view = happening board
		menuHappening = (TextView) findViewById(R.id.menu_happening);
		selectMenuItem(menuHappening);
		mBoardFragment = new BoardFragment();
		fragmentId = FRAGMENT_BOARD;
		mFragment = mBoardFragment;
		mFragmentTransaction.add(R.id.content_frame, mBoardFragment);
		mFragmentTransaction.commit();
		
		// Setup receivers
        mReceiver = new ServiceReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        // Service
        mServiceHelper = ServiceHelper.getInstance();
        
        // Get self number
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = ContactsManager.cleanNumber(tMgr.getLine1Number());
        
        // Action Bar
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        
        // GCM
        // Check device for Play Services APK.
	    if (checkPlayServices()) {
	        // If this check succeeds, proceed with normal processing.
	        // Otherwise, prompt user to get valid Play Services APK.
	    	gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId();
            
            if (regid.isEmpty()) {
                registerInBackground();
            } else if (!Storage.getGcmIdUpToDate(this)){
            	// If phone is registered with GCM but not with Happ Server
            	sendRegistrationIdToBackend(regid);
            }
	    }
        
        // Navigation drawer
        mDrawer = (Drawer) findViewById(R.id.drawer);
        mDrawer.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
        });
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            	closeKeyboard();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Progress dialog
        mProgressDialog = new ProgressDialog(this);
		
        // Previously done in async task
        if (!mContactsManager.hasFetchedContacts()){
	        new fetchContactsTask().execute("");
	        mProgressDialog.setMessage(getResources().getString(R.string.dialog_retrieve_contacts));
	    	mProgressDialog.show();
        }
        
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
	public void onResume(){
		super.onResume();
		checkPlayServices();
	}
	
	private class fetchContactsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	mContactsManager.makeContactsMapping();
        	return "";
        }      

        @Override
        protected void onPostExecute(String results) {
        	if (fragmentId != FRAGMENT_BOARD)
        		mProgressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
        
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {   
		
		if (mDrawerToggle.onOptionsItemSelected(item))
          return true;
		
    	switch (item.getItemId()) {        
          case android.R.id.home:
        	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);
    	}
    }
	
	@Override
	public void onBackPressed(){
		if (fragmentId != FRAGMENT_BOARD){
			switchFragment(FRAGMENT_BOARD);
			selectMenuItem(menuHappening);
			return;
		}
		if (mFragment.onBackPressed()) return;
		super.onBackPressed();
	}
	
	public void compose(){
		mBoardFragment.closeFooter(false);
		Intent intent = new Intent(this, ComposeActivity.class);
		startActivityForResult(intent, ACTIVITY_COMPOSE);
		overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == ACTIVITY_COMPOSE && resultCode == 1){
			
			String msg = data.getStringExtra("compose_msg");
			int tag = data.getIntExtra("compose_tag", 1);
			String duration = data.getStringExtra("compose_duration");
			
			startPostService(msg, tag, duration);
		}
	}
	
	public void startPostService(String msg, int tag, String duration){
		mBoardFragment.setHeader(msg, tag, 0, Integer.parseInt(duration), true);
		Bundle extras = new Bundle();
		String[] numbers = mContactsManager.getAllFriends();
		extras.putStringArray("n", numbers);
		extras.putString("number", mPhoneNumber+"");
		extras.putString("msg", msg);
		extras.putString("tag", tag+"");
		extras.putString("duration", duration);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
        postMoodsId = mServiceHelper.startService(this, ServiceHelper.POST_MOODS, extras);
	}
    	
	public void startFetchService(){
		Bundle extras = new Bundle();
		String[] numbers = mContactsManager.getAllFriends();
		extras.putStringArray("n", numbers);
		extras.putString("me", mPhoneNumber);
		extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
		getMoodsId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
	}
	
	public void startFeedbackService(String name, String email, String feedback){
		Bundle extras = new Bundle();
		extras.putString("name", name);
		extras.putString("number", mPhoneNumber);
		extras.putString("email", email);
		extras.putString("feedback", feedback);
		extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
		postFeedbackId = mServiceHelper.startService(this, ServiceHelper.POST_FEEDBACK, extras);
		Toast.makeText(this, getResources().getString(R.string.toast_feedback_thanks), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (activityDestroyed) return;
		
		String results = resultData.getString(APIService.RESULTS);
		int taskId = resultData.getInt(APIService.TASK_ID);
		
		if (taskId == getMoodsId){
			if (fragmentId == FRAGMENT_BOARD)
				mBoardFragment.onFetchResult(results);
			mProgressDialog.dismiss();
			getMoodsId = -1;
		} else if (taskId == postMoodsId){
			try {
				if (results != null) {
					JSONObject jResults = new JSONObject(results);
					if (jResults.getInt("status") == 200){
						// TODO remove from string
	//					Toast.makeText(this, getResources().getString(R.string.toast_post_success), Toast.LENGTH_SHORT).show();
						Storage.incTotalHapps(this);
						mDrawer.updateTotalHapps();
						JSONObject data = jResults.getJSONObject("data");
						long timestamp = data.getLong("timestamp");
						if (mBoardFragment != null){
							mBoardFragment.onPostSuccess(timestamp);
						}
					}
				} else {
					if (mBoardFragment != null){
						mBoardFragment.onPostError();
					}
					Toast.makeText(this, getResources().getString(R.string.toast_post_error), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e){}
			
			postMoodsId = -1;
		} else if (taskId == postGcmRegisterId){
			try{
				if (results != null){
					JSONObject jResults = new JSONObject(results);
					if (jResults.getInt("status") == 200){
						Storage.setGcmIdUpToDate(this, true);
					}
				}
			} catch (JSONException e){}
			
			postGcmRegisterId = -1;
		} else if (taskId == postFeedbackId){
			try{
				if (results != null){
					JSONObject jResults = new JSONObject(results);
					if (jResults.getInt("status") == 200){
						Storage.setFeedbackText(this, "");
					}
				} else {
					Toast.makeText(this, getResources().getString(R.string.toast_feedback_error), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e){}
			postFeedbackId = -1;
		}
		
		hideSpinner();
		
		if (results != null) Log.i("results", results);
	}
	
	public void setProgressDialog(String msg){
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	
	@Override
	public void onDestroy(){
		activityDestroyed = true;
		super.onDestroy();
	}
	
	/**
	 * Menu clicks
	 */
	public void switchFragment(int id){
    	if (fragmentId == id) {
    		mDrawerLayout.closeDrawers();
    		return;
    	}
    	
    	mFragmentTransaction = getSupportFragmentManager().beginTransaction();
    	
    	if (fragmentId == FRAGMENT_BOARD){
    		mFragmentTransaction.detach(mFragment);
    	} else {
    		mFragmentTransaction.remove(mFragment);
    	}
    	
    	switch (id) {
    		case FRAGMENT_BOARD:
    			if (mBoardFragment == null)
    				mBoardFragment = new BoardFragment();
    			mFragment = mBoardFragment;
    			break;
    		case FRAGMENT_FRIENDS:
    			if (mFriendsFragment == null)
    				mFriendsFragment = new ContactsFragment();
    			mFragment = mFriendsFragment;
    			break;
    		case FRAGMENT_FEEDBACK:
    			if (mFeedbackFragment == null)
    				mFeedbackFragment = new FeedbackFragment();
    			mFragment = mFeedbackFragment;
    			break;
    		default:
    			return;
    	}
    	
    	if (id == FRAGMENT_BOARD){
    		mFragmentTransaction.attach(mFragment);
    	} else {
    		mFragmentTransaction.add(R.id.content_frame, mFragment);
    	}
    	
    	fragmentId = id;
    	if (mDrawerLayout.isDrawerOpen(mDrawer)){
    		scheduleSwitchFragment();
    	} else {
    		mFragmentTransaction.commit();
    	}
	}
	
	private final Handler drawerHandler = new Handler();
	
	private void scheduleSwitchFragment(){
		drawerHandler.removeCallbacksAndMessages(null);
		drawerHandler.postDelayed(new Runnable(){
			@Override
			public void run(){
				mFragmentTransaction.commit();
			}
		}, 250);
		mDrawerLayout.closeDrawers();
	}
	
	// Menus
	private void selectMenuItem(TextView v){
		if (selectedMenuItem != null){
			selectedMenuItem.setTextColor(getResources().getColor(R.color.white));
		}
		selectedMenuItem = v;
		v.setTextColor(getResources().getColor(R.color.happ_purple_highlight));
	}
	
	public void showSpinner(){
        setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
	}
	
	public void hideSpinner(){
        setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
	}
	
	public void returnToBoard(){
		closeKeyboard();
		switchFragment(FRAGMENT_BOARD);
		selectMenuItem(menuHappening);
	}
	
	public void onClickBoard(View v){
		returnToBoard();
	}
	
	public void onClickFriends(View v){
		switchFragment(FRAGMENT_FRIENDS);
		selectMenuItem((TextView) v);
	}
	
	public void onClickPrivacy(View v){
		String url = "http://www.happ.us/terms#privacy";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	public void onClickTerms(View v){
		String url = "http://www.happ.us/terms";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	public void onClickFeedback(View v){
		switchFragment(FRAGMENT_FEEDBACK);
		selectMenuItem((TextView) v);
	}
	
	private void closeKeyboard(){
		InputMethodManager inputManager = (InputMethodManager)            
            	getSystemService(Context.INPUT_METHOD_SERVICE); 
            	inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),      
            			    InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	// GCM
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	private String getRegistrationId() {
	    String registrationId = Storage.getRegistrationId(this);
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = Storage.getStoredAppVersion(this);
	    int currentVersion = getAppVersion(this);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	private void storeRegistrationId(String regId) {
	    Storage.setRegistrationId(this, regId);
	    int appVersion = getAppVersion(this);
	    Storage.setStoredAppVersion(this, appVersion);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new RegisterTask().execute(null, null, null);
	}
	
	class RegisterTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }
                regid = gcm.register(SENDER_ID);

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                
                Storage.setGcmIdUpToDate(MainActivity.this, false);
                sendRegistrationIdToBackend(regid);

                // Persist the regID - no need to register again.
                storeRegistrationId(regid);
            } catch (IOException ex) {
            	// TODO
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
			return null;
		}
		
	}

    private void sendRegistrationIdToBackend(String regid) {
    	Log.i(TAG, regid);
    	Bundle extras = new Bundle();
		extras.putString("number", mPhoneNumber);
		extras.putString("regid", regid);
		extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
		postGcmRegisterId = mServiceHelper.startService(this, ServiceHelper.POST_GCM_REGISTER, extras);
    }
}
