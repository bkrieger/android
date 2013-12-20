package us.happ.adapter;

import us.happ.R;
import us.happ.model.Group;
import us.happ.view.PickerListView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GroupAdapter extends ArrayAdapter<Group>{
	private Group[] groups;
	private LayoutInflater inflater;
	private Context mContext;
	private AbsListView.LayoutParams layoutParams;

	public GroupAdapter(Context context, int resource, Group[] groups) {
		super(context, resource);
		this.groups = groups;
		inflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public Group getItem(int position){
		return groups[position];
	}
	
	@Override
	public long getItemId (int position){
		return position;
	}
	
	@Override
	public int getCount(){
		return groups.length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null){
			v = inflater.inflate(R.layout.list_item_picker_groups, parent, false);
			
			// TODO fix this. Should just get it from the inflate (parent)
			if (layoutParams == null)
				layoutParams = new AbsListView.LayoutParams(parent.getWidth(), ((PickerListView) parent).getChildHeight());
			
			v.setLayoutParams(layoutParams);

		} else {
			v = convertView;
		}
		
		Group d = groups[position];
	
		((TextView) v).setText(d.label);
			
		return v;
		
	}
	
}