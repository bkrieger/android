package us.happ.android.view;

import us.happ.android.R;
import us.happ.android.utils.Media;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ListView;

public class ContactsListView extends ListView {

	private static final String sections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private Paint textPaint;
	private int topMargin;
	private int paddingRight;
	private int paddingTop;
	private int rightMargin;
	private int height;
	private int width;
	private int indexSize;
	
	public ContactsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		textPaint = new Paint();
	    textPaint.setColor(context.getResources().getColor(R.color.happ_purple));
	    textPaint.setAntiAlias(true);
	    
	    topMargin = (int) Media.pxFromDp(context, 20);
	    rightMargin = (int) Media.pxFromDp(context, 15); // 15dp

		paddingRight = getPaddingRight();
	    paddingTop = getPaddingTop();
	    textPaint.setTextSize(paddingRight / 4);
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		
		height = MeasureSpec.getSize(heightMeasureSpec);
		width = (int) MeasureSpec.getSize(widthMeasureSpec);
		indexSize = (height - 2*topMargin) / sections.length();
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
	 
	    for (int i = 0; i < sections.length(); i++){
	    	String s = Character.toString(sections.charAt(i));
	    	
	        canvas.drawText(
	        		s,
	        		width - rightMargin - textPaint.measureText(s)/2, 
	                paddingTop + topMargin + indexSize * (i + 1), 
	                textPaint);
	    } 
	     
	}
	

}
