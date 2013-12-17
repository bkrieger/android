package us.happ.adapter;

import us.happ.R;
import us.happ.model.Duration;
import us.happ.view.PickerListView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DurationAdapter extends ArrayAdapter<Duration>{
	private Duration[] duration;
	private LayoutInflater inflater;
	private Context mContext;
	private AbsListView.LayoutParams layoutParams;

	public DurationAdapter(Context context, int resource, Duration[] duration) {
		super(context, resource);
		this.duration = duration;
		inflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public Duration getItem(int position){
		return duration[position];
	}
	
	@Override
	public long getItemId (int position){
		return position;
	}
	
	@Override
	public int getCount(){
		return duration.length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null){
			v = inflater.inflate(R.layout.list_item_picker_duration, parent, false);
			
			// TODO fix this. Should just get it from the inflate (parent)
			if (layoutParams == null)
				layoutParams = new AbsListView.LayoutParams(parent.getWidth(), ((PickerListView) parent).getChildHeight());
			
			v.setLayoutParams(layoutParams);

		} else {
			v = convertView;
		}
		
		Duration d = duration[position];
	
		((TextView) v).setText(d.label);
			
		return v;
		
	}
	
}
