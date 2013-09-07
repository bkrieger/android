package us.happ.android.activity;

import us.happ.android.service.ServiceHelper;

import us.happ.android.service.APIService;

import us.happ.android.R;
import us.happ.android.service.ServiceReceiver;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends ListActivity implements ServiceReceiver.Receiver{

	private ServiceReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Setup receivers
        mReceiver = new ServiceReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        Bundle extras = new Bundle();
        String[] numbers = {"6969696969"};
		extras.putStringArray("n", numbers);
        extras.putParcelable(ServiceReceiver.NAME, (Parcelable) mReceiver);
        ServiceHelper mServiceHelper = ServiceHelper.getInstance();
    	int taskId = mServiceHelper.startService(this, ServiceHelper.GET_MOODS, extras);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		String results = resultData.getString(APIService.RESULTS);
		if (results == null) return;
		
		int taskId = resultData.getInt(APIService.TASK_ID);
		
		Log.i("results", results);
	}

}
