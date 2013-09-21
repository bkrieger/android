package us.happ.android.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.happ.android.R;
import us.happ.android.adapter.HBAdapter;
import us.happ.android.model.Mood;
import us.happ.android.service.APIService;
import us.happ.android.service.ServiceHelper;
import us.happ.android.service.ServiceReceiver;
import us.happ.android.utils.ContactsManager;
import us.happ.android.utils.Happ;
import us.happ.android.utils.Media;
import us.happ.android.utils.SmoothInterpolator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ServiceReceiver.Receiver{
	
	// Activity results flags
	private static final int ID_COMPOSE = 1;
	
	// receiver flags
	private int getMoodsId = -1;
	private int postMoodsId = -1;
	
	// constants
	private final static int HIPPO_HEIGHT = 58; // 48 + 10
	private final static int FOOTER_HEIGHT = 48; 
	
	// flags
	private boolean INITIALIZED = false;
	private boolean refreshing = false;
	private boolean allowPullToRefresh = true;
	
	private ServiceReceiver mReceiver;
	private ListView mListView;
	private HBAdapter mListAdapter;
	private ServiceHelper mServiceHelper;
	private ContactsManager mContactsManager;
	private ActionBar actionbar;
	private ProgressDialog mProgressDialog;
	
	private String mPhoneNumber;
	
	// Views
	private View mHeader;
	private View mHippo;
	private int hippoHeight;
	private View hippoStatic;
	private View hippoDynamic;
	private View stripView;
	private View sadHippoView;
	
	private int footerHeight;

	private ActionBarDrawerToggle mDrawerToggle;

	private View mFooter;
	private TextView mFooterText;
	private View mFooterSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        mContactsManager = new ContactsManager(this);
		
        // Sad hippo
        stripView = findViewById(R.id.main_strip);
        sadHippoView = findViewById(R.id.main_sad_hippo);
        
		// Set up lists
		mListView = (ListView) findViewById(android.R.id.list);
		View header = getLayoutInflater().inflate(R.layout.list_header_board, null, true);
		mListView.addHeaderView(header, null, false);
		mHeader = header.findViewById(R.id.board_header);
		ViewHolder holder = new ViewHolder();
		holder.tag = (ImageView) mHeader.findViewById(R.id.board_tag);
		holder.message = (TextView) mHeader.findViewById(R.id.board_message);
		holder.meWrap = mHeader.findViewById(R.id.board_mewrap);
		holder.tagLine = mHeader.findViewById(R.id.board_tagline);
		mHeader.setTag(holder);
		
		// footer
		mFooter = findViewById(R.id.actionbar_footer);
		footerHeight = (int) Media.pxFromDp(this, FOOTER_HEIGHT);
		mFooterText = (TextView) mFooter.findViewById(R.id.actionbar_footer_text);
		mFooterSubmit = mFooter.findViewById(R.id.actionbar_footer_submit);
		// Absorbs touch events
		mFooter.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
        });
		
		mFooterSubmit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				final String number = Happ.implode(mListAdapter.getCheckedNumbers(), ",");  	
		   	 	Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("smsto:" + number));
		        startActivity(callIntent);
			}
			
		});
		
		// dancing hippo
		hippoStatic = header.findViewById(R.id.hippo_static);
		hippoDynamic = header.findViewById(R.id.hippo_dynamic);
		
		mHippo = header.findViewById(R.id.hippo);
		mListAdapter = new HBAdapter(this, 0, mContactsManager); // TODO don't pass in contactsManager
		mListView.setAdapter(mListAdapter);
		
		hippoHeight = (int) Media.pxFromDp(this, HIPPO_HEIGHT);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            	onBoardItemClick(position-1); // -1 for header
            }
        });
		
		// pull to refresh
		mListView.setOnTouchListener(pullToRefreshListener);
		
		// Setup receivers
        mReceiver = new ServiceReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        // Get self number
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = ContactsManager.clearnNumber(tMgr.getLine1Number());
        
        // Action Bar
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        
        // Navigation drawer
        View mDrawer = findViewById(R.id.drawer);
        mDrawer.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
        });
        
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            	
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Progress dialog
        mProgressDialog = new ProgressDialog(this);
        
        // Fetch
        mServiceHelper = ServiceHelper.getInstance();
        refreshing = true;
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_retrieve_contacts));
    	mProgressDialog.show();
        new fetchContactsTask().execute("");
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	// TODO onPostCreate instead?
	// Fetching contacts asynchronously
	private class fetchContactsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	mContactsManager.makeContactsMapping();
        	return "";
        }      

        @Override
        protected void onPostExecute(String results) {
        	mProgressDialog.setMessage(MainActivity.this.getResources().getString(R.string.dialog_retrieve_moods));
        	fetch();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
        
	}   
	
	private void fetch(){
		Bundle extras = new Bundle();
        String[] numbers = mContactsManager.getAllContacts();
		extras.putStringArray("n", numbers);
		extras.putString("me", mPhoneNumber);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        getMoodsId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
	}

	public void onResume(){
		super.onResume();
		if (INITIALIZED)
			fetch();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {   
		
		if (mDrawerToggle.onOptionsItemSelected(item))
          return true;
		
    	switch (item.getItemId()) {        
          case android.R.id.home:
        	  return true;
          case R.id.action_compose:
        	  compose();
        	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);    
    	}
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
			if (mListAdapter.isCheckboxShown()){
				mListAdapter.hideCheckbox();
				animateHideFooter();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void compose(){
		Intent intent = new Intent(this, ComposeActivity.class);
		startActivityForResult(intent, ID_COMPOSE);
		overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
	}
	
	public void onHeaderClick(View v){
		compose();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == ID_COMPOSE && resultCode == 1){

			Toast.makeText(this, "Sending..", Toast.LENGTH_SHORT).show();
			
			String msg = data.getStringExtra("compose_msg");
			int tag = data.getIntExtra("compose_tag", 1);
			String duration = data.getStringExtra("compose_duration");
			
			updateHeader(msg, tag);
			
			Bundle extras = new Bundle();
			extras.putString("number", mPhoneNumber+"");
			extras.putString("msg", msg);
			extras.putString("tag", tag+"");
			extras.putString("duration", duration);
	        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
	        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
	        postMoodsId = mServiceHelper.startService(this, ServiceHelper.POST_MOODS, extras);
		}
	}
	
	private void updateHeader(String message, int tagId){
		ViewHolder holder = (ViewHolder) mHeader.getTag();
		
		Happ.showViewIf(holder.tagLine, holder.meWrap, message == null);
		if (message != null){
			holder.message.setText(message);
			holder.tag.setImageBitmap(BitmapFactory.decodeResource(getResources(), Mood.resIdFromTag(tagId)));
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		String results = resultData.getString(APIService.RESULTS);
		if (results == null) {
			resetHeaderPadding(false);
			return;
		}
		Log.i("results", results);
		
		int taskId = resultData.getInt(APIService.TASK_ID);
		
		if (taskId == getMoodsId){
			INITIALIZED = true;
			refreshing = false;
			mProgressDialog.dismiss();
			ArrayList<Mood> moods = new ArrayList<Mood>();
			
			try {
				JSONObject jResults = new JSONObject(results);
				JSONObject data = jResults.getJSONObject("data");
				JSONObject me = data.getJSONObject("me");
				JSONArray contacts = data.getJSONArray("contacts");
				
				// Update me
				if (me.has("_id")){
					updateHeader(me.getString("message"), me.getInt("tag"));
				} else {
					updateHeader(null, 0);
				}
				
				Happ.showViewIf(sadHippoView, stripView, contacts.length() == 0);
				
				JSONObject d;
				Mood m;
				for (int i = 0; i < contacts.length(); i++){
					d = (JSONObject) contacts.get(i);
					
					m = new Mood(
							d.getString("_id"), 
							d.getString("message"), 
							d.getLong("timestamp"), 
							d.getInt("duration"),
							d.getInt("tag")
						);
					moods.add(m);
				}
				
			} catch (JSONException e){}

			mListAdapter.updateData(moods);

			getMoodsId = -1;
			resetHeaderPadding(false);
		} else if (taskId == postMoodsId){
			
			try {
				JSONObject jResults = new JSONObject(results);
				if (jResults.getInt("status") == 200)
					Toast.makeText(this, getResources().getString(R.string.toast_post_success), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, getResources().getString(R.string.toast_post_error), Toast.LENGTH_SHORT).show();
			} catch (JSONException e){}
			
			postMoodsId = -1;
		}
	}
	
	class ViewHolder {
		TextView message;
		ImageView tag;
		View meWrap;
		View tagLine;
	}
	
	// Pull to refresh stuffs
	private int mLastMotionY;
	private void applyHeaderPadding(MotionEvent ev) {
		// clever hack :D
		if (mListView.getFirstVisiblePosition() > 0 || mListView.getChildAt(0).getTop() != 0) return;
		
		int topPadding = (int) ((ev.getY() - mLastMotionY) / 2);
		
		if (topPadding < 0) topPadding = 0;
		
		LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
		lp.setMargins(0, topPadding, 0, 0);
		mHeader.setLayoutParams(lp);
		
		lp = (LayoutParams) mHippo.getLayoutParams();
		lp.setMargins(0, -1*hippoHeight + topPadding, 0, 0);
		mHippo.setLayoutParams(lp);

	}
	
	private void resetHeaderPadding(boolean refresh) {

		 LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
		 int startPadding = lp.topMargin;
		 
		 if (startPadding == 0) return;
		 
		 if (!refresh || startPadding < hippoHeight){
			 BounceAnimation a = new BounceAnimation(startPadding, 0);
			 a.setInterpolator(new SmoothInterpolator());
			 a.setDuration(500);
			 mHeader.startAnimation(a);
			 hippoDynamic.setVisibility(View.GONE);
			 hippoStatic.setVisibility(View.VISIBLE);
		 } else {
			 BounceAnimation a = new BounceAnimation(startPadding, hippoHeight);
			 a.setInterpolator(new SmoothInterpolator());
			 a.setDuration(500);
			 mHeader.startAnimation(a);
			 hippoDynamic.setVisibility(View.VISIBLE);
			 hippoStatic.setVisibility(View.GONE);
			 refreshing = true;
			 fetch();
		 }
	 }
	 
	 private class BounceAnimation extends Animation {
		 private int startPadding;
		 private int endPadding;
		 
		 public BounceAnimation(int startPadding, final int endPadding){
			 this.startPadding = startPadding;
			 this.endPadding = endPadding;
		 }
		 
		 @Override
		 protected void applyTransformation(float interpolatedTime, Transformation t) {
			 
			 int padding = (int) (endPadding + (startPadding-endPadding)*(1 - interpolatedTime));
			 
			 LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
			 lp.setMargins(0, padding, 0, 0);
			 mHeader.setLayoutParams(lp);
			 
			 lp = (LayoutParams) mHippo.getLayoutParams();
			 lp.setMargins(0, -1*hippoHeight + padding, 0, 0);
			 mHippo.setLayoutParams(lp);
	     }
		 
		 @Override
		 public boolean willChangeBounds(){
			 return true;
		 }
	}
	 
	OnTouchListener pullToRefreshListener = new OnTouchListener(){
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (refreshing) return false;
			
			final int y = (int) event.getY();
			
		    switch (event.getAction()) {
		    	case MotionEvent.ACTION_CANCEL:
		    		allowPullToRefresh = true;
					resetHeaderPadding(true);
					break;
		    	case MotionEvent.ACTION_UP:
		    		allowPullToRefresh = true;
					resetHeaderPadding(true);
					break;
	            case MotionEvent.ACTION_DOWN:
	                mLastMotionY = y;
	                allowPullToRefresh = mListView.getChildAt(0).getTop() == 0;
	                break;
	            case MotionEvent.ACTION_MOVE:
	            	if (allowPullToRefresh && mListView.getChildAt(0).getTop() < 0)
	            		allowPullToRefresh = false;
	            	if (allowPullToRefresh)
	            		applyHeaderPadding(event);
	                break;
		    }
			return false;
		}

	};
	
	public void onBoardItemClick(int position){
		boolean wasCheckShown = mListAdapter.isCheckboxShown();
    	mListAdapter.check(position);
    	if (!wasCheckShown && mListAdapter.isCheckboxShown()){
    		animateShowFooter();
    		mFooterSubmit.setEnabled(true);
    	} else if (wasCheckShown && !mListAdapter.isCheckboxShown()){
    		animateHideFooter();
    		mFooterSubmit.setEnabled(false);
    		
    	}
    	
    	mFooterText.setText("Text " + Happ.implode(mListAdapter.getCheckedNames(), ", "));
    	
	}
	
	public void animateShowFooter(){
		mFooter.setVisibility(View.VISIBLE);
		TranslateAnimation a = new TranslateAnimation (0, 0, footerHeight, 0);
		a.setFillEnabled(true);
		a.setFillAfter(true);
	 	a.setInterpolator(new SmoothInterpolator());
	 	a.setDuration(500);
	 	a.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				mListView.setPadding(0, 0, 0, footerHeight);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
	 		
	 	});
	 	mFooter.startAnimation(a);
	}
	
	public void animateHideFooter(){
		TranslateAnimation a = new TranslateAnimation (0, 0, 0, footerHeight);
		a.setInterpolator(new SmoothInterpolator());
		a.setFillEnabled(true);
		a.setFillAfter(true);
		a.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				mFooter.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}

		});
	 	a.setDuration(500);
	 	mFooter.startAnimation(a);
	 	mListView.setPadding(0, 0, 0, 0);
	}
}
