package us.happ.view;


import us.happ.R;
import us.happ.utils.Happ;
import us.happ.utils.Storage;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Drawer extends ViewGroup {

	private Context mContext;
	private TextView totalHappsView;
	private int height;
	private int width;
	private TextView totalHappsTextView;

	public Drawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.drawer, this);
		
		totalHappsView = (TextView) findViewById(R.id.menu_total_happs_count);
		totalHappsTextView = (TextView) findViewById(R.id.menu_total_happs);
		
		String versionCode = Happ.getVersionCode(context);
		((TextView) findViewById(R.id.copyright)).setText("v" + versionCode + " " + context.getResources().getString(R.string.copyright));	
		
		updateTotalHapps();
	}
	
	public void updateTotalHapps(){
		int totalHapps = Storage.getTotalHapps(mContext);
		if (totalHapps == 1){
			totalHappsTextView.setText(mContext.getResources().getString(R.string.total_happs_s));
		} else {
			totalHappsTextView.setText(mContext.getResources().getString(R.string.total_happs_pl));
		}
		totalHappsView.setText(Storage.getTotalHapps(mContext)+"");
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
