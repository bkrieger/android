package us.happ.android.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

public class Happ {

	public static void showViewIf(View viewShown, View viewHidden,
			boolean condition) {
		if (condition) {
			viewShown.setVisibility(View.VISIBLE);
			viewHidden.setVisibility(View.GONE);
		} else {
			viewShown.setVisibility(View.GONE);
			viewHidden.setVisibility(View.VISIBLE);
		}
	}

	public static String implode(String[] arr, String separator) {

		if (arr.length == 0) return "";
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; ++i) {
			sb.append(arr[i]).append(separator);
		}
		
		return sb.substring(0, sb.length() - 1);

	}
	
	public static int getActionBarHeight(Context context){
		int result = 0;
		TypedValue tv = new TypedValue();
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
			result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
		}
		return result;
	}
	
	public static int getStatusBarHeight(Context context){
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
