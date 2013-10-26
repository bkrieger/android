package us.happ.android.view;


import us.happ.android.R;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Drawer extends ViewGroup {

	private int height;
	private int width;

	public Drawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.drawer, this);
		
		try {
			String versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			((TextView) findViewById(R.id.copyright)).setText("v" + versionCode + " " + context.getResources().getString(R.string.copyright));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

		height = MeasureSpec.getSize(heightMeasureSpec);
		width = (int) MeasureSpec.getSize(widthMeasureSpec);
		
		setMeasuredDimension(width, height);
		
		View child;
		for(int i = 0; i < getChildCount(); i++){	
			child = getChildAt(i);
			measureChild(child, 
					MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {	
		int childTop = 0;
		
		for(int i = 0; i < getChildCount(); i++){
			View child = getChildAt(i);
			
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
		
			child.layout(0, childTop, childWidth, childHeight);
			childTop += childHeight;
		}
	}

}
