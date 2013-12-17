package us.happ.utils;

import java.util.Arrays;
import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
	private static final String PREFERENCES_FILE = "Happ";
	
	private static final String KEY_ALLOW_CONTACTS_ACCESS = "allow_contacts_access";
	
	private static final String KEY_GCM_ID = "gcm id";
	private static final String KEY_GCMID_UPTODATE = "gcm uptodate";
	private static final String KEY_APP_VERSION = "app version";
	
	private static final String KEY_BLOCKED_NUMBERS = "blocked numbers";
	private static final String KEY_TOTAL_HAPPS = "total happs";
	
	private static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";
	
	private static final String KEY_FEEDBACK_NAME = "feedback_name";
	private static final String KEY_FEEDBACK_EMAIL = "feedback_email";
	private static final String KEY_FEEDBACK_TEXT = "feedback_text";
	
	private static final String KEY_RETRY_UPDATE_FRIENDS = "retry_update_friends";
	
	// Settings
	private static final String KEY_HINT_CONTACTS = "hint_contacts";
	
	public static boolean allowContactsAccess(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_ALLOW_CONTACTS_ACCESS, false);
	}
	
	public static void setAllowContactsAccess(Context context, boolean allowAccess){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putBoolean(KEY_ALLOW_CONTACTS_ACCESS, allowAccess).commit();
	}
	
	public static String getRegistrationId(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getString(KEY_GCM_ID, "");
	}
	
	public static void setRegistrationId(Context context, String id){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(KEY_GCM_ID, id).commit();
	}
	
	public static int getStoredAppVersion(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getInt(KEY_APP_VERSION, Integer.MIN_VALUE);
	}
	
	public static void setStoredAppVersion(Context context, int version){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putInt(KEY_APP_VERSION, version).commit();
	}
	
	public static boolean getGcmIdUpToDate(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_GCMID_UPTODATE, false);
	}
	
	public static void setGcmIdUpToDate(Context context, boolean upToDate){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putBoolean(KEY_GCMID_UPTODATE, upToDate).commit();
	}
	
	public static HashSet<String> getBlockedNumbers(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		String str = sp.getString(KEY_BLOCKED_NUMBERS, "");
		if (str.length() == 0)
			return new HashSet<String>();
		String[] values = str.split(",");
		HashSet<String> set = new HashSet<String>(Arrays.asList(values));
		return set;
	}

	public static void setBlockedNumbers(Context context, HashSet<String> set){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		StringBuilder sb = new StringBuilder();
		for (String s: set){
			sb.append(s);
			sb.append(',');
		}
		String str;
		if (sb.length() > 0){
			str = sb.substring(0, sb.length() - 1);
		} else {
			str = "";
		}
		sp.edit().putString(KEY_BLOCKED_NUMBERS, str).commit();

        ContactsManager.getInstance(context).setFriendsDirty(true);

	}
	
	public static int getTotalHapps(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getInt(KEY_TOTAL_HAPPS, 0);
	}
	
	public static void incTotalHapps(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		int totalHapps = sp.getInt(KEY_TOTAL_HAPPS, 0);
		sp.edit().putInt(KEY_TOTAL_HAPPS, totalHapps + 1).commit();
	}
	
	public static int getKeyboardHeight(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getInt(KEY_KEYBOARD_HEIGHT, 0);
	}
	
	public static void setKeyboardHeight(Context context, int height){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putInt(KEY_KEYBOARD_HEIGHT, height).commit();
	}
	
	// FEEDBACK
	public static String getFeedbackName(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getString(KEY_FEEDBACK_NAME, "");
	}
	
	public static void setFeedbackName(Context context, String feedbackName){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(KEY_FEEDBACK_NAME, feedbackName).commit();
	}
	
	public static String getFeedbackEmail(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getString(KEY_FEEDBACK_EMAIL, "");
	}
	
	public static void setFeedbackEmail(Context context, String feedbackEmail){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(KEY_FEEDBACK_EMAIL, feedbackEmail).commit();
	}
	
	public static String getFeedbackText(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getString(KEY_FEEDBACK_TEXT, "");
	}
	
	public static void setFeedbackText(Context context, String feedbackText){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putString(KEY_FEEDBACK_TEXT, feedbackText).commit();
	}
	
	// Auto-retry service
	public static boolean getRetryUpdateFriends(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_RETRY_UPDATE_FRIENDS, false);
	}
	
	public static void setRetryServiceUpdateFriends(Context context, boolean autoRetry){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putBoolean(KEY_RETRY_UPDATE_FRIENDS, autoRetry).commit();
	}
	
	// SETTINGS
	public static boolean getHintContacts(Context context){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_HINT_CONTACTS, true);
	}
	public static void setHintContacts(Context context, boolean enabled){
		SharedPreferences sp = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
		sp.edit().putBoolean(KEY_HINT_CONTACTS, false).commit();
	}
	
}
