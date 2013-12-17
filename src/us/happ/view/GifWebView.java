package us.happ.view;

import us.happ.utils.Media;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GifWebView extends WebView {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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

		if (Build.VERSION.SDK_INT >= 11){
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		
		setWebViewClient(new WebViewClient() {

		   public void onPageFinished(WebView view, String url) {
		        setVisibility(View.GONE);
		    }
		});
		
		setInitialScale(100);
		setBackgroundColor(0x00000000);
	}

}
