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
import android.content.Context;
import android.content.Intent;
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

	private static final int ID_COMPOSE = 1;
	
	// receiver flags
	private int getMoodsId = -1;
	private int postMoodsId = -1;
	private int getMeId = -1;
	
	private ServiceReceiver mReceiver;
	private ListView mListView;
	private HBAdapter mListAdapter;

	private ContactsManager mContactsManager;

	private String mPhoneNumber;

	private ActionBar actionbar;

	private View mHeader;
	private View mHippo;
	private int hippoHeight;
	
	private boolean refreshing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        // TODO runnable on different thread
        mContactsManager = new ContactsManager(this);
		
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
		
		mHippo = header.findViewById(R.id.hippo);
		mListAdapter = new HBAdapter(this, 0, mContactsManager); // TODO don't pass in contactsManager
		mListView.setAdapter(mListAdapter);
		
		hippoHeight = (int) Media.pxFromDp(this, 58);
		
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
		mListView.setOnTouchListener(new OnTouchListener(){
			 
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (refreshing) return false;
				
				final int y = (int) event.getY();
				
			    switch (event.getAction()) {
			    	case MotionEvent.ACTION_UP:
						resetHeaderPadding();
						break;
		            case MotionEvent.ACTION_DOWN:
		                mLastMotionY = y;
		                break;
		            case MotionEvent.ACTION_MOVE:
		                applyHeaderPadding(event);
		                break;
			    }
				return false;
			}
		
		});
		
		// Setup receivers
        mReceiver = new ServiceReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        // Get self number
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = ContactsManager.clearnNumber(tMgr.getLine1Number());
        
        // Action Bar
        actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        
        // Fetch
        refreshing = true;
        new fetchContactsTask().execute("");
	}
	
	private class fetchContactsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	mContactsManager.makeContactsMapping();;
        	return "";
        }      

        @Override
        protected void onPostExecute(String results) {
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
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
        getMoodsId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
        
        extras = new Bundle();
        String[] number = {mPhoneNumber};
		extras.putStringArray("n", number);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        getMeId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
	}
	
	public void onResume(){
		super.onResume();
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
			String tag = data.getStringExtra("compose_tag");
			String duration = data.getStringExtra("compose_duration");
			
			ViewHolder holder = (ViewHolder) mHeader.getTag();
			holder.message.setText(msg);
			holder.tag.setImageBitmap(BitmapFactory.decodeResource(getResources(), Mood.resIdFromTag(Integer.parseInt(tag))));
			holder.meWrap.setVisibility(View.VISIBLE);
			holder.tagLine.setVisibility(View.GONE);
			
			Bundle extras = new Bundle();
			extras.putString("number", mPhoneNumber+"");
			extras.putString("msg", msg);
			extras.putString("tag", tag);
			extras.putString("duration", duration);
	        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
	        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
	        postMoodsId = mServiceHelper.startService(this, ServiceHelper.POST_MOODS, extras);
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
			this.findViewById(R.id.splash).setVisibility(View.GONE);
			refreshing = false;
			ArrayList<Mood> moods = new ArrayList<Mood>();
			
			try {
				JSONObject jResults = new JSONObject(results);
				JSONArray data = jResults.getJSONArray("data");
				
				JSONObject d;
				Mood m;
				for (int i = 0; i < data.length(); i++){
					d = (JSONObject) data.get(i);
					
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
					Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "Error submitting. Try again.", Toast.LENGTH_SHORT).show();
			} catch (JSONException e){}
			
			postMoodsId = -1;
		} else if (taskId == getMeId){
			try {
				JSONObject jResults = new JSONObject(results);
				JSONArray data = jResults.getJSONArray("data");
				
				ViewHolder holder = (ViewHolder) mHeader.getTag();
				
				if (data.length() > 0){
					JSONObject d = (JSONObject) data.get(0);
					holder.message.setText(d.getString("message"));
					holder.tag.setImageBitmap(BitmapFactory.decodeResource(getResources(), Mood.resIdFromTag(d.getInt("tag"))));
					holder.meWrap.setVisibility(View.VISIBLE);
					holder.tagLine.setVisibility(View.GONE);
				} else {
					holder.meWrap.setVisibility(View.GONE);
					holder.tagLine.setVisibility(View.VISIBLE);
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
	
	private int mLastMotionY;
	private void applyHeaderPadding(MotionEvent ev) {
		// clever hack :D
		if (mListView.getFirstVisiblePosition() > 0 || mListView.getChildAt(0).getTop() != 0) return;
		
		int topPadding = (int) ((ev.getY() - mLastMotionY) / 4);
		
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
		 } else {
			 BounceAnimation a = new BounceAnimation(startPadding, hippoHeight);
			 a.setInterpolator(new SmoothInterpolator());
			 a.setDuration(500);
			 mHeader.startAnimation(a);
			 refreshing = true;
			 fetch();
		 }
	 }
	 
	 private class BounceAnimation extends Animation {
		 private int startPadding;
		 private int endPadding;
		 
		 public BounceAnimation(int startPadding, int endPadding){
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
	 
	

}
