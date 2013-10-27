package us.happ.android.utils;

import java.util.Arrays;
import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
	private static final String PREFERENCES_FILE = "Happ";
	
	private static final String KEY_BLOCKED_NUMBERS = "blocked numbers";
	private static final String KEY_TOTAL_HAPPS = "total happs";
	
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
	
}
