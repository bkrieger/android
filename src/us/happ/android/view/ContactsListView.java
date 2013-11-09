package us.happ.android.view;

import us.happ.android.R;
import us.happ.android.adapter.ContactsAdapter;
import us.happ.android.utils.Media;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class ContactsListView extends ListView {

	private Paint textPaint;
	private int topMargin;
	private int paddingRight;
	private int paddingTop;
	private int rightMargin;
	private int height;
	private int width;
	private int indexSize;
	
	// Overlay
	private Paint overlayPaint;
	private Paint overlayTextPaint;
	private int overlayPaddingLeft;
	private int overlayHeight;
	private int overlayRightMargin;
	private Paint overlayBoarderPaint;
	private boolean overlayShow;
	private String overlayText;
	private int overlayTop;
	private int onedp;
	
	public ContactsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		textPaint = new Paint();
	    textPaint.setColor(context.getResources().getColor(R.color.happ_purple));
	    textPaint.setAntiAlias(true);
	    
	    overlayPaint = new Paint();
	    overlayPaint.setColor(context.getResources().getColor(R.color.white));
	    overlayPaint.setAntiAlias(true);
	    
	    overlayTextPaint = new Paint();
	    overlayTextPaint.setColor(context.getResources().getColor(R.color.happ_purple));
	    overlayTextPaint.setAntiAlias(true);
	    int textSize = (int) Media.pxFromDp(context, 20);
	    overlayTextPaint.setTextSize(textSize);

	    onedp = (int) Media.pxFromDp(context, 1);
	    FontMetrics fm = overlayTextPaint.getFontMetrics();
	    overlayHeight = (int) (fm.bottom - fm.top) + onedp;
	    overlayPaddingLeft = (int) Media.pxFromDp(context, 10);
	    overlayRightMargin = (int) Media.pxFromDp(context, 50);
	    
	    overlayBoarderPaint = new Paint();
	    overlayBoarderPaint.setColor(context.getResources().getColor(R.color.happ_purple));
	    overlayBoarderPaint.setAntiAlias(true);
	    overlayBoarderPaint.setStrokeWidth(onedp);
	    
	    topMargin = (int) Media.pxFromDp(context, 20);
	    rightMargin = (int) Media.pxFromDp(context, 15); // 15dp

		paddingRight = getPaddingRight();
	    paddingTop = getPaddingTop();
	    textPaint.setTextSize(paddingRight / 4);
	    
	    // default
	    overlayShow = false;
	    overlayText = "";

	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		
		height = MeasureSpec.getSize(heightMeasureSpec);
		width = (int) MeasureSpec.getSize(widthMeasureSpec);
		
		indexSize = (height - 2*topMargin) / ContactsAdapter.alphabet.length();
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas){
		// Under children
		super.dispatchDraw(canvas);
		// Above children
		if (overlayShow){
			canvas.drawRect(overlayPaddingLeft, overlayTop, width - overlayRightMargin, overlayTop + overlayHeight, overlayPaint);
			canvas.drawLine(overlayPaddingLeft, overlayTop + overlayHeight, width - overlayRightMargin, overlayTop + overlayHeight, overlayBoarderPaint);
			canvas.drawText(overlayText, overlayPaddingLeft*2 , overlayTop + overlayHeight - overlayTextPaint.descent() - onedp, overlayTextPaint);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
	 
	    for (int i = 0; i < ContactsAdapter.alphabet.length(); i++){
	    	String s = Character.toString(ContactsAdapter.alphabet.charAt(i));
	    	
	        canvas.drawText(
	        		s,
	        		width - rightMargin - textPaint.measureText(s)/2, 
	                paddingTop + topMargin + indexSize * (i + 1), 
	                textPaint);
	    }
	     
	}
	
	public void enableOverlay(boolean enable){
		overlayShow = enable;
	}
	
	public void setSectionText(String text){
		overlayText = text;
	}
	
	public void setSectionTop(int top){
		overlayTop = top;
	}
	
	public int getSectionHeight(){
		return overlayHeight;
	}

}
