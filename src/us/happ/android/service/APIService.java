package us.happ.android.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * All the intents are put in a queue, so no need for locking
 */
public class APIService extends IntentService {
	public static final String TAG = "APIService";
	public static final String NAME = "APIService";
	
	public static final String RESULTS = "results";
	public static final String TASK_ID = "task_id";

	public APIService() {
		super(NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!intent.hasExtra(ServiceHelper.ACTION))
			return;

		int action = intent.getIntExtra(ServiceHelper.ACTION, 0);
		int taskId = intent.getIntExtra(ServiceHelper.TASK_ID, 0);
		
		ServiceHelper mServiceHelper;
		String results;
		Bundle bundle = new Bundle();
		
		switch (action) {
		case ServiceHelper.GET_MOODS:
			Log.i(TAG, "GET MOODS");
			
			String[] numbers = intent.getStringArrayExtra("n");
			String params = "";
			// TODO building url
			for (int i = 0; i < numbers.length; i++){
				if (i == 0) params += "?";
				params += "n[]=" + numbers[i];
				if (i + 1 < numbers.length)
					params += "&";
			}
			
			results = HttpCaller.getRequest(this, "/moods" + params);
			
			bundle.putString(RESULTS, results);
			
			mServiceHelper = ServiceHelper.getInstance();
			mServiceHelper.onReceive(ServiceHelper.SUCCESS, taskId, bundle);

			break;
		case ServiceHelper.POST_MOODS:
			Log.i(TAG, "POST MOODS");
			
			String number = intent.getStringExtra("number");
			String msg = "";
			try {
				msg = URLEncoder.encode(intent.getStringExtra("msg"), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			params = "?id="+ number + "&msg=" + msg + "&tags[]=lol&timestamp=3&duration=1000";
			
			Log.i("url", params);
			results = HttpCaller.postRequest(this, "/moods" + params);
			
			bundle.putString(RESULTS, results);
			
			mServiceHelper = ServiceHelper.getInstance();
			mServiceHelper.onReceive(ServiceHelper.SUCCESS, taskId, bundle);

			break;
		default:
			// No intent specified
			break;
		}

	}

}
