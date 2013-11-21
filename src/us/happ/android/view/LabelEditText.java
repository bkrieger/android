package us.happ.android.view;

import us.happ.android.R;
import us.happ.android.utils.Media;
import us.happ.android.utils.SmoothInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

public class LabelEditText extends EditText {

	private String placeholder;
	private int labelHeight;
	private Paint labelPaint;
	private int animOffset = 0;
	private boolean labelShown = false;
	private int colorPurple;
	private int colorGray;

	public LabelEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		placeholder = (String) getHint();
		if (placeholder == null) placeholder = "";
		
		labelPaint = new Paint();
		colorPurple = context.getResources().getColor(R.color.happ_purple);
		colorGray = context.getResources().getColor(R.color.gray_solid);
		labelPaint.setColor(colorPurple);
		labelPaint.setAntiAlias(true);
		labelPaint.setTypeface(Typeface.DEFAULT_BOLD);
		int textSize = (int) Media.pxFromDp(context, 12);
		labelPaint.setTextSize(textSize);
		labelPaint.setAlpha(0);
		FontMetrics fm = labelPaint.getFontMetrics();
	    labelHeight = (int) (fm.bottom - fm.top);
	    
		setPadding(getPaddingLeft(), getPaddingTop() + labelHeight, getPaddingRight(), getPaddingBottom());
		
		final ShowLabelAnimation anim = new ShowLabelAnimation();
		anim.setDuration(500);
		anim.setInterpolator(new SmoothInterpolator());
		addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0 && !labelShown){
					anim.reset();
					startAnimation(anim);
					labelShown = true;
				} else if (s.length() == 0 && labelShown){
					labelPaint.setAlpha(0);
					LabelEditText.this.clearAnimation();
					invalidate();
					labelShown = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});
		
		setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					labelPaint.setColor(colorPurple);
				} else {
					labelPaint.setColor(colorGray);
				}
				if (labelShown) {
					labelPaint.setAlpha(255);
				} else {
					labelPaint.setAlpha(0);
				}
				invalidate();
			}
			
		});
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		canvas.drawText(placeholder, getPaddingLeft(), getPaddingTop() + animOffset - labelPaint.descent(), labelPaint);
	}
	
	private class ShowLabelAnimation extends Animation {
		
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			animOffset = (int) (labelHeight/2 * (1 - interpolatedTime));
			labelPaint.setAlpha((int) Math.ceil(255*(interpolatedTime)));
			invalidate();
	    }
	}

}
