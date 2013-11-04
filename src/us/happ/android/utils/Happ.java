package us.happ.android.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class Happ {
	
	public final static boolean HAS_HARDWARE_ACCELERATION = Build.VERSION.SDK_INT >= 11;

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
		int separatorLength = separator.length();
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; ++i) {
			sb.append(arr[i]).append(separator);
		}
		
		return sb.substring(0, sb.length() - separatorLength);

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
	
	public static void startAnimationWithHardwareAcceleration(final View v, Animation anim){
		hardwareAccelerate(v);
		anim.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				Happ.stopHardwareAcceleration(v);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
        	
        });
		v.startAnimation(anim);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean hardwareAccelerate(View v){
		if (Happ.HAS_HARDWARE_ACCELERATION){
			v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			return true;
		}
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void stopHardwareAcceleration(View v){
		if (Happ.HAS_HARDWARE_ACCELERATION){
			v.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}

}
