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
import us.happ.android.utils.Media;
import us.happ.android.utils.SmoothInterpolator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
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
	private int getMeId = -1;
	
	// constants
	private final static int HIPPO_HEIGHT = 58; // 48 + 10
	
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
            	 final String number = ((Mood) adapterView.getItemAtPosition(position)).getNumber();   	
            	 Intent callIntent = new Intent(Intent.ACTION_VIEW);
                 callIntent.setData(Uri.parse("sms:" + number));
                 startActivity(callIntent);
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
        actionbar.setDisplayShowTitleEnabled(false);
        
        // Progress dialog
        mProgressDialog = new ProgressDialog(this);
        
        // Fetch
        mServiceHelper = ServiceHelper.getInstance();
        refreshing = true;
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_retrieve_contacts));
    	mProgressDialog.show();
        new fetchContactsTask().execute("");
        fetchMe();
	}
	
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
        	fetchMoods();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
        
	}   
	
	private void fetch(){
		fetchMoods();
		fetchMe();
	}
	
	private void fetchMoods(){
		Bundle extras = new Bundle();
        String[] numbers = mContactsManager.getAllContacts();
		extras.putStringArray("n", numbers);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        getMoodsId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
	}
	
	private void fetchMe(){        
		Bundle extras = new Bundle();
        String[] number = {mPhoneNumber};
		extras.putStringArray("n", number);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        getMeId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
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
		if (message == null){
			holder.meWrap.setVisibility(View.GONE);
			holder.tagLine.setVisibility(View.VISIBLE);
		} else {
			holder.message.setText(message);
			holder.tag.setImageBitmap(BitmapFactory.decodeResource(getResources(), Mood.resIdFromTag(tagId)));
			holder.meWrap.setVisibility(View.VISIBLE);
			holder.tagLine.setVisibility(View.GONE);
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		String results = resultData.getString(APIService.RESULTS);
		if (results == null) {
			resetHeaderPadding();
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
				JSONArray data = jResults.getJSONArray("data");
				
				if (data.length() == 0){
					stripView.setVisibility(View.GONE);
					sadHippoView.setVisibility(View.VISIBLE);
				} else {
					stripView.setVisibility(View.VISIBLE);
					sadHippoView.setVisibility(View.GONE);
				}
				
				JSONObject d;
				Mood m;
				for (int i = 0; i < data.length(); i++){
					d = (JSONObject) data.get(i);
					
					// TODO check if works
					// remote self from contacts
					if (d.getString("_id") == mPhoneNumber)
						continue;
					
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
			resetHeaderPadding();
		} else if (taskId == postMoodsId){
			
			try {
				JSONObject jResults = new JSONObject(results);
				if (jResults.getInt("status") == 200)
					Toast.makeText(this, getResources().getString(R.string.toast_post_success), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, getResources().getString(R.string.toast_post_error), Toast.LENGTH_SHORT).show();
			} catch (JSONException e){}
			
			postMoodsId = -1;
		} else if (taskId == getMeId){
			try {
				JSONObject jResults = new JSONObject(results);
				JSONArray data = jResults.getJSONArray("data");
				
				if (data.length() > 0){
					JSONObject d = (JSONObject) data.get(0);
					updateHeader(d.getString("message"), d.getInt("tag"));
				} else {
					updateHeader(null, 0);
				}
				
			} catch (JSONException e){}
			
			getMeId = -1;
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
	
	private void resetHeaderPadding() {

		 LayoutParams lp = (LayoutParams) mHeader.getLayoutParams();
		 int startPadding = lp.topMargin;
		 
		 if (startPadding == 0) return;
		 
		 if (startPadding <= hippoHeight){
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
		    	case MotionEvent.ACTION_UP:
		    		allowPullToRefresh = true;
					resetHeaderPadding();
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

}
