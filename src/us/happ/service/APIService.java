package us.happ.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import us.happ.utils.Happ;

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
		
		ServiceHelper mServiceHelper = ServiceHelper.getInstance();
		String results;
		String params;
		Bundle bundle = new Bundle();
		
		if (!Happ.isNetworkAvailable(getApplicationContext())){
			mServiceHelper.onReceive(ServiceHelper.ERROR, taskId, bundle);
			return;
		}
		
		switch (action) {
		case ServiceHelper.GET_MOODS:
			Log.i(TAG, "GET MOODS");
			
			String me = intent.getStringExtra("me");
			String[] numbers = intent.getStringArrayExtra("n");
			params = "?me=" + me + "&";
			// TODO building url
			for (int i = 0; i < numbers.length; i++){
				params += "n[]=" + numbers[i];
				if (i + 1 < numbers.length)
					params += "&";
			}
			
			results = HttpCaller.getRequest(this, "/moods" + params);
			
			bundle.putString(RESULTS, results);
			
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
			String tag = intent.getStringExtra("tag");
			String duration = intent.getStringExtra("duration");
			params = "?id="+ number + "&msg=" + msg + "&tag="+ tag + "&duration=" + duration;
			
			String[] numbers2 = intent.getStringArrayExtra("n");
			// TODO building url
			for (int i = 0; i < numbers2.length; i++){
				params += "&n[]=" + numbers2[i];
			}
			
			Log.i("url", params);
			results = HttpCaller.postRequest(this, "/moods" + params);
			
			bundle.putString(RESULTS, results);
			
			mServiceHelper.onReceive(ServiceHelper.SUCCESS, taskId, bundle);

			break;
		case ServiceHelper.POST_GCM_REGISTER:
			Log.i(TAG, "POST GCM REGISTER");
			String number1 = intent.getStringExtra("number");
			String regid = intent.getStringExtra("regid");
			params = "?me=" + number1 + "&os=1&token=" + regid;
			
			results = HttpCaller.postRequest(this,  "/registerpush" + params);
			bundle.putString(RESULTS, results);
			
			mServiceHelper.onReceive(ServiceHelper.SUCCESS, taskId, bundle);
			
			break;
		case ServiceHelper.POST_FEEDBACK:
			Log.i(TAG, "POST FEEDBACK");
			
			params = "";
			try {
				String feedback_name = URLEncoder.encode(intent.getStringExtra("name"), "UTF-8");
				String feedback_email = URLEncoder.encode(intent.getStringExtra("email"), "UTF-8");
				String feedback_phone = URLEncoder.encode(intent.getStringExtra("number"), "UTF-8");
				String feedback_text = URLEncoder.encode(intent.getStringExtra("feedback"), "UTF-8");
				params = "?name=" + feedback_name + "&email=" + feedback_email + "&phone=" + feedback_phone + "&feedback=" + feedback_text
						+ "&os=android&version=" + URLEncoder.encode(Happ.getVersionCode(getApplicationContext()), "UTF-8");;
			} catch (UnsupportedEncodingException e1) {}
			
			results = HttpCaller.postRequest(this,  "/feedback" + params);
			bundle.putString(RESULTS, results);
			
			mServiceHelper.onReceive(ServiceHelper.SUCCESS, taskId, bundle);
			
			break;
		case ServiceHelper.POST_FRIENDS:
			Log.i(TAG, "POST FRIENDS");
			
			String me1 = intent.getStringExtra("number");
			String[] numbers1 = intent.getStringArrayExtra("n");
			params = "?me=" + me1 + "&";
			// TODO building url
			for (int i = 0; i < numbers1.length; i++){
				params += "n[]=" + numbers1[i];
				if (i + 1 < numbers1.length)
					params += "&";
			}
			
			results = HttpCaller.postRequest(this, "/friends" + params);
			bundle.putString(RESULTS, results);
			
			mServiceHelper.onReceive(ServiceHelper.SUCCESS, taskId, bundle);
			
			break;
		default:
			// No intent specified
			break;
		}

	}

}
