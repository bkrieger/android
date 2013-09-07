package us.happ.android.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import us.happ.android.adapter.HBAdapter;
import us.happ.android.model.Mood;
import us.happ.android.service.ServiceHelper;

import us.happ.android.service.APIService;

import us.happ.android.R;
import us.happ.android.service.ServiceReceiver;
import us.happ.android.utils.ContactsManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends ListActivity implements ServiceReceiver.Receiver{

	private static final int ID_COMPOSE = 1;
	
	// receiver flags
	private int getMoodsId = -1;
	private int postMoodsId = -1;
	
	private ServiceReceiver mReceiver;
	private ListView mListView;
	private HBAdapter mListAdapter;

	private ContactsManager mContactsManager;

	private Object mPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        // TODO runnable on different thread
        mContactsManager = new ContactsManager(this);
		
		// Set up lists
		mListView = getListView();
		mListAdapter = new HBAdapter(this, 0, mContactsManager); // TODO don't pass in contactsManager
		mListView.setAdapter(mListAdapter);
		
		// Setup receivers
        mReceiver = new ServiceReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        // Get self number
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = ContactsManager.clearnNumber(tMgr.getLine1Number());
        
        Bundle extras = new Bundle();
        String[] numbers = mContactsManager.getAllContacts();
		extras.putStringArray("n", numbers);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
        getMoodsId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
        
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
        	  Intent intent = new Intent(this, ComposeActivity.class);
        	  startActivityForResult(intent, ID_COMPOSE);
        	  overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
        	  return true;
          default:            
        	  return super.onOptionsItemSelected(item);    
    	}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == ID_COMPOSE && resultCode == 1){

			String msg = data.getStringExtra("compose_msg");
			
			Bundle extras = new Bundle();
			extras.putString("number", mPhoneNumber+"");
			extras.putString("msg", msg);
	        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
	        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
	        postMoodsId = mServiceHelper.startService(this, ServiceHelper.POST_MOODS, extras);
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		String results = resultData.getString(APIService.RESULTS);
		if (results == null) return;
		Log.i("results", results);
		
		int taskId = resultData.getInt(APIService.TASK_ID);
		
		if (taskId == getMoodsId){
			ArrayList<Mood> moods = new ArrayList<Mood>();
			
			try {
				JSONObject jResults = new JSONObject(results);
				JSONArray data = jResults.getJSONArray("data");
				
				JSONObject d;
				Mood m;
				for (int i = 0; i < data.length(); i++){
					d = (JSONObject) data.get(i);
					m = new Mood(d.getString("_id"), d.getString("message"));
					moods.add(m);
				}
				
			} catch (JSONException e){}

			mListAdapter.updateData(moods);

			getMoodsId = -1;
		} else if (taskId == postMoodsId){
			
			postMoodsId = -1;
		}
	}
	
	

}
