package us.happ.android.utils;

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
			if (i == 0) {
				sb.append(arr[i]);
			} else {
				sb.append(separator).append(arr[i]);
			}
		}
		
		return sb.toString();

	}

}
