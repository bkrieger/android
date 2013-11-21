package us.happ.android.utils;


import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class Happ {
	
    public static boolean hasFroyo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    public static boolean hasGingerbread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    public static boolean hasHoneycomb = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static boolean hasHoneycombMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    public static boolean hasJellyBean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

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
	
	public static boolean hasSmsService(Context context){
	    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:123456789"));
	    PackageManager pm = context.getPackageManager();
	    List<ResolveInfo> res = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    if(res.size() > 0) return true;
	    return false;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static boolean hardwareAccelerate(View v){
		if (Happ.hasHoneycomb){
			v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
			return true;
		}
		return false;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void stopHardwareAcceleration(View v){
		if (Happ.hasHoneycomb){
			v.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (hasGingerbread) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (hasHoneycomb) {
                threadPolicyBuilder.penaltyFlashScreen();
                // TODO uncomment
//                vmPolicyBuilder
//                        .setClassInstanceLimit(ImageGridActivity.class, 1)
//                        .setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
}
