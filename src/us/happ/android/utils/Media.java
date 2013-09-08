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
		"#0404AA",
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
		
		Bitmap output;
		
		int profileColor = 0;
		
		if (noProfile) {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_avatar);
			output = Bitmap.createBitmap((int) pxFromDp(context, 80f), (int) pxFromDp(context, 80f), Config.ARGB_8888);
			profileColor = generateColor(phoneNumber);
		}else {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		}
		
		Canvas canvas = new Canvas(output);
	    
	    final int borderPx = (int) pxFromDp(context, 5);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    Rect rect;
	    RectF rectF;
	    final Rect bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    int padding = (int) pxFromDp(context, 5f);
	    
	    if (noProfile){
	    	int bitmapWidth = (int) pxFromDp(context, 60f);
	    	
	    	rect = new Rect(padding, padding, bitmap.getWidth() + padding, bitmap.getHeight() + padding);
	    	rectF = new RectF(rect);
	    	
	    	padding = bitmap.getWidth() - bitmapWidth;
	    	rect = new Rect(padding, padding, bitmapWidth + padding, bitmapWidth + padding);
	    } else {
	    	rect = bitmapRect;
	    	rectF = new RectF(bitmapRect);
	    }
	    
	    final float roundPx = pxFromDp(context, 80);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    
	    if (noProfile){
	    	paint.setColor(profileColor);
	    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    }
	    
	    paint.setColor(color);
	    
	    if (!noProfile){
		    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    }
		
	    canvas.drawBitmap(bitmap, bitmapRect, rect, paint);
	    
	    
//	    // draw border
	    if (noProfile){
	    	rect = new Rect(borderPx/2, borderPx/2, bitmap.getWidth() + borderPx*3/2, bitmap.getHeight() + borderPx*3/2);
	    	rectF = new RectF(rect);
	    	paint.setColor(profileColor);
	    	paint.setAlpha(50);
	    } else {
	    	paint.setColor(context.getResources().getColor(R.color.light_purple));
	    }
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth((float) borderPx);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    
	    // draw time left	   
	    if (noProfile){
	    	rect = new Rect(borderPx, borderPx, bitmap.getWidth() + borderPx, bitmap.getHeight() + borderPx);
	    	rectF = new RectF(rect);
	    	paint.setColor(profileColor);
		    paint.setAlpha(255);
		    paint.setStrokeWidth((float) borderPx*3/2 + pxFromDp(context, 3));
	    } else {
	    	paint.setColor(context.getResources().getColor(R.color.happ_purple));
		    paint.setAlpha(255);
	    }
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
