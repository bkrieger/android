package us.happ.android.utils;

import java.util.Arrays;
import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
	private static final String PREFERENCES_FILE = "Happ";
	
	private static final String KEY_GCM_ID = "gcm id";
	private static final String KEY_GCMID_UPTODATE = "gcm uptodate";
	private static final String KEY_APP_VERSION = "app version";
	
	private static final String KEY_BLOCKED_NUMBERS = "blocked numbers";
	private static final String KEY_TOTAL_HAPPS = "total happs";
	
	private static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";
	
	// Settings
	private static final String KEY_HINT_CONTACTS = "hint_contacts";
	
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
