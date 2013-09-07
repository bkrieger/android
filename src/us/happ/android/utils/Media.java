package us.happ.android.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import us.happ.android.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.PictureDrawable;

public class Media {

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap, float decay) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	    
	    final int borderPx = (int) pxFromDp(context, 5);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pxFromDp(context, 80);

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    
	    // draw border
	    paint.setColor(context.getResources().getColor(R.color.white));
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth((float) borderPx);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    
	    // draw time left	   
	    paint.setColor(context.getResources().getColor(R.color.happ_purple));
	    paint.setAlpha(255);
	    int start = (int) (270 - decay*360);
	    int end = (int) (decay*360);
	    canvas.drawArc(rectF, start, end, false, paint);
	    
	    return output;
	  }
	
	private static float dpFromPx(Context context, float px) {
	    return px / context.getResources().getDisplayMetrics().density;
	}


	private static float pxFromDp(Context context, float dp) {
	    return dp * context.getResources().getDisplayMetrics().density;
	}
}
