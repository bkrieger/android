package us.happ.android.view;

import us.happ.android.utils.Media;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

public class GifWebView extends WebView {

	public GifWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int px = (int) Media.pxFromDp(context, 48);
		if (px <= 48){
			loadUrl("file:///android_asset/hippo_mhdpi.gif");
		} else if (px == 72){
			loadUrl("file:///android_asset/hippo_hdpi.gif");
		} else if (px == 96){
			loadUrl("file:///android_asset/hippo_xhdpi.gif");
		} else {
			loadUrl("file:///android_asset/hippo_xxhdpi.gif");
		}
		
		setInitialScale(100);
		setBackgroundColor(0x00000000);
	}

}
