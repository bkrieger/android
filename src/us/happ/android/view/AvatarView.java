package us.happ.android.view;

import us.happ.android.utils.Media;
import us.happ.android.utils.SmoothInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

public class AvatarView extends ImageView {

	private Context mContext;
	private RectF rectF;
	private Paint paint;
	private int decay;
	private float tempDecay;
	private DecayAnimation mAnimation;
	private boolean hasProfile = false;
	private int startDecay;

	public AvatarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init(){
		int profileColor = Color.parseColor("#006699");
		
    	paint = new Paint();
    	paint.setAntiAlias(true);
    	paint.setColor(profileColor);
    	paint.setStyle(Paint.Style.STROKE);
	    
	    // Animation
	    mAnimation = new DecayAnimation();
	    mAnimation.setInterpolator(new SmoothInterpolator());
	    mAnimation.setDuration(1000);
	    
	    getRect();
	}
	
	private void getRect(){
		final int borderPx = (int) Media.pxFromDp(mContext, 5);
		int bitmapWidth = (int) Media.pxFromDp(mContext, 60f);

		int onedp = hasProfile ? 0 : (int) Media.pxFromDp(mContext, 1);
		
	    Rect rect = new Rect(borderPx/2 + onedp, borderPx/2 + onedp, bitmapWidth + borderPx*3/2 - onedp, bitmapWidth + borderPx*3/2 - onedp);
    	rectF = new RectF(rect);
    	
    	paint.setStrokeWidth((float) borderPx + onedp);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
	    canvas.drawArc(rectF, 270 - tempDecay, tempDecay, false, paint);
	}
	
	public void setNumber(long number){
		paint.setColor(Media.generateColor(number));
	}
	
	public void setDecay(int decay){
		this.decay = decay;
		tempDecay = decay;
	}
	
	public void setHasProfile(boolean hasProfile){
		if (hasProfile != this.hasProfile){
			this.hasProfile = hasProfile;
			getRect();
		}
	}
	
	class DecayAnimation extends Animation {

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			tempDecay = startDecay - (startDecay-decay)*(interpolatedTime);
			invalidate();
		}
	}
	
	public void animateDecay(int startDecay){
		this.startDecay = startDecay;
		mAnimation.reset();
		startAnimation(mAnimation);
	}

}
