package us.happ.view;

import us.happ.R;
import us.happ.utils.Media;
import us.happ.utils.SmoothInterpolator;
import us.happ.view.AvatarView.DecayAnimation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class DurationProgressView extends View {
	
	private float decay; 
	private float tempDecay;
	private float startDecay;
	private Context mContext;
	private Paint paint;
	private DecayAnimation mAnimation;
	private int stroke;

	public DurationProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init(){
    	paint = new Paint();
    	paint.setAntiAlias(true);
    	paint.setColor(mContext.getResources().getColor(R.color.happ_purple));
    	paint.setStyle(Paint.Style.STROKE);
    	stroke = (int) Media.pxFromDp(mContext, 3); // 3dp
    	paint.setStrokeWidth(stroke);
	    
	    // Animation
	    mAnimation = new DecayAnimation();
	    mAnimation.setInterpolator(new SmoothInterpolator());
	    mAnimation.setDuration(1500);
	    
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.drawLine(0, stroke/2, canvas.getWidth()*tempDecay, stroke/2, paint);
	}
	
	public void setDecay(float decay){
		this.decay = decay;
		tempDecay = decay;
		invalidate();
	}
	
	class DecayAnimation extends Animation {

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			tempDecay = startDecay - (startDecay-decay)*(interpolatedTime);
			invalidate();
		}
	}
	
	public float getDecay(){
		return decay;
	}
	
	public void animateDecay(float startDecay, float endDecay){
		this.startDecay = startDecay;
		decay = endDecay;
		tempDecay = endDecay;
		mAnimation.reset();
		startAnimation(mAnimation);
	}

}
