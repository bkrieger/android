package us.happ.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import us.happ.activity.MainActivity;
import us.happ.R;
import us.happ.model.Mood;
import us.happ.utils.ContactsManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                
                // Post notification of received message.
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void sendNotification(Bundle extra) {
		
		String msg = "wants to hang out.";
		String sender = "Someone";
		if (extra.containsKey("sender")){
			String senderNumber = extra.getString("sender");
			// Get name from phone number
//			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderNumber));
//			ContentResolver resolver = getApplicationContext().getContentResolver();
//			Cursor cursor = resolver.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, "where " + PhoneLookup.NORMALIZED_NUMBER + "=?", null, null);
			// TODO inefficient
			ContactsManager manager = ContactsManager.getInstance(this);
			if (!manager.hasFetchedContacts()){
				manager.makeContactsMapping();
			}
			String name = manager.getName(senderNumber);
			if (name != null){ 
				sender = name;
			} else {
				// A chance someone tried to notify himself.
		        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		        if (senderNumber.equals(ContactsManager.cleanNumber(tMgr.getLine1Number()))){
		        	return;
		        }
			}
		}
		if (extra.containsKey("tag")){
			int tag = Integer.parseInt(extra.getString("tag"));		
			msg = Mood.getNotificationText(tag);
		}
		msg = sender + " " + msg;
		
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Happ")
        .setTicker(msg)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
