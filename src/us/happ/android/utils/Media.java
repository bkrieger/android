package us.happ.android.utils;

import us.happ.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class Media {
	
	static String[] colors = {
		"#F22C2C",
		"#006699",
		"#04AA2B",
		"#AA2804",
		"#DED11F",
		"#1FC4DE",
		"#7D7D7D",
		"#FA7500",
	};

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float decay, long phoneNumber) {
		boolean noProfile = bitmap == null;
		
		Bitmap output = Bitmap.createBitmap((int) pxFromDp(context, 70f), (int) pxFromDp(context, 70f), Config.ARGB_8888);
		
		int profileColor = generateColor(phoneNumber);
		
		int bitmapWidth = (int) pxFromDp(context, 60f);
		
		if (noProfile) {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_avatar);
		}
		
		Canvas canvas = new Canvas(output);
	    
	    final int borderPx = (int) pxFromDp(context, 5);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    
	    Rect bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    Rect rect = new Rect(borderPx, borderPx, bitmapWidth + borderPx, bitmapWidth + borderPx);
	    RectF rectF = new RectF(rect);
	    
	    final float roundPx = pxFromDp(context, 70f);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    
    	paint.setColor(profileColor);
    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    
	    paint.setColor(color);
	    
	    if (!noProfile){
		    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    }
		
	    canvas.drawBitmap(bitmap, bitmapRect, rect, paint);  
	    paint.setXfermode(null);
	    
	    // draw border
    	rect = new Rect(borderPx/2, borderPx/2, bitmapWidth + borderPx*3/2, bitmapWidth + borderPx*3/2);
    	rectF = new RectF(rect);
    	paint.setColor(profileColor);
    	paint.setAlpha(50);

	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth((float) borderPx);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    
	    int onedp;
	    // draw time left
	    if (noProfile){
	    	onedp = (int) pxFromDp(context, 1);
	    } else {
	    	onedp = 0;
	    }
	    
	    rect = new Rect(borderPx/2 + onedp, borderPx/2 + onedp, bitmapWidth + borderPx*3/2 - onedp, bitmapWidth + borderPx*3/2 - onedp);
    	rectF = new RectF(rect);
    	paint.setColor(profileColor);
	    paint.setAlpha(255);
	    paint.setStrokeWidth((float) borderPx + onedp);

	    int start = (int) (270 - decay*360);
	    int end = (int) (decay*360);
	    canvas.drawArc(rectF, start, end, false, paint);
	    
	    return output;
	  }
	
	public static float dpFromPx(Context context, float px) {
	    return px / context.getResources().getDisplayMetrics().density;
	}


	public static float pxFromDp(Context context, float dp) {
	    return dp * context.getResources().getDisplayMetrics().density;
	}
	
	private static int generateColor(long id){
		int i = (int) (id % colors.length);
		return Color.parseColor(colors[i]);
	}
	
}
