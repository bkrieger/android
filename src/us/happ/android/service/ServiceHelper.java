package us.happ.android.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;

/**
 * A singleton class that:
 *  - Prepare and send the Service request
	Ð Check if the method is already pending
	Ð Create the request Intent
	Ð Add the operation type and a unique request id
	Ð Add the method specific parameters
	Ð Add the binder callback
	Ð Call startService(Intent)
	Ð Return the request id
	- Handle the callback from the service
	Ð Dispatch callbacks to the user interface listeners
 */
public class ServiceHelper{
	public static final String TAG = "ServiceHelper";
	
	public static final String ACTION = "action";
	public static final String TASK_ID = "taskid";
	
	// Result Code
	public static final int SUCCESS = 1;
	public static final int ERROR = 2;
	
	//Status
	public static final String STATUS_OK = "ok";
	
	// Operations available
	public static final int POST_GCM_REGISTER = 0x00;
	public static final int GET_MOODS = 0x01;
	public static final int POST_MOODS = 0x02;
	
	private int idCount = 0;
	
	private SparseArray<ResultReceiver> receivers;
	
	private static ServiceHelper instance;
	
	public ServiceHelper(){
		receivers = new SparseArray<ResultReceiver>();
	}
		
	public static synchronized ServiceHelper getInstance(){
		if (instance == null){
			instance = new ServiceHelper();
		}
		return instance;
	}
	
	public int startService(Context context, int action){
		return startService(context, action, null);
	}

	/**
	 * Starts Service based on Operation ID and returns a task ID.
	 * If a similar process is already in the queue, returns the task id of that process.
	 * @param context
	 * @param action - Operation ID
	 * @param extras - Extra information needed for operation
	 * @return taskId - unique identifier for a task that the UI thread can listen or check status of
	 */
	public int startService(Context context, int action, Bundle extras){
		// TODO
		// Build a task identifier, ie action + keys in extra = make sure the same operation isn't called twice
		// Object/map to store id, status, Broadcast receiver (to UI)

		// Generate task identifier (string)
		// See if task already operating
		// Add task id to intent
		// Generate int id for that task,
		// Store id, operation and receiver
		int taskId;
		int size = receivers.size();
		
//		if (size == 0) taskId = 0;
//		else taskId = receivers.keyAt(size - 1) + 1;
		taskId = idCount;
		idCount += 1;
		
	    ResultReceiver receiver = extras.getParcelable(ServiceReceiver.NAME);
	    extras.remove(ServiceReceiver.NAME);
		receivers.put(taskId, receiver);
		
		Intent serviceIntent = new Intent(context, APIService.class);
	    serviceIntent.putExtra(ACTION, action);
	    serviceIntent.putExtra(TASK_ID, taskId);
	    
	    if (extras != null)
	    	serviceIntent.putExtras(extras);
	    
	    context.startService(serviceIntent);
	    
	    return taskId;
	}
	
	/**
	 * Receives callback from APIService. Prepares callback to UI thread.
	 * @param resultCode - Success or Error
	 * @param taskIdentifier - Unique identifier for an operation. ie GET_TASK, id = 3
	 */
	public void onReceive(int resultCode, int taskId, Bundle data) {
		if (resultCode != SUCCESS) {
			// should still do something even if it failed
			//return;
			Log.i(TAG, "result failed");
		} else {
			Log.i(TAG, "result successful");
		}
		
		data.putInt(APIService.TASK_ID, taskId);
		
		ResultReceiver receiver = receivers.get(taskId);
		receiver.send(resultCode, data);
		receivers.remove(taskId);
	}

}
