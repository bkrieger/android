package us.happ.adapter;

import us.happ.R;
import us.happ.adapter.ContactsAdapter.ViewHolder;
import us.happ.model.Group;
import us.happ.model.Mood;
import us.happ.view.PickerListView;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class GroupAdapter extends CursorAdapter{
	
	private static final int TYPE_COUNT = 2;
	private static final int TYPE_LABEL = 0x0;
	private static final int TYPE_NORMAL = 0x1;

	private LayoutInflater inflater;
	private Context mContext;
	private AbsListView.LayoutParams layoutParams;
	private int mTagId;
	private Cursor mCursor;

	public GroupAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mCursor = c;
		inflater = LayoutInflater.from(context);
		mContext = context;
	}
	
	@Override
	public int getViewTypeCount(){
		return TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position){
		if (position < 2){
			return TYPE_LABEL;
		}
		return TYPE_NORMAL;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor){
		super.swapCursor(newCursor);
		mCursor = newCursor;
		Log.i("CURSOR", "SWAPPED");
		return mCursor;
	}
	
	public void setTag(int tagId){
		mTagId = tagId;
	}
	
	@Override
	public long getItemId (int position){
		return position;
	}
	
	@Override
	public int getCount(){
		if (mCursor == null) return 2;
		return mCursor.getCount() + 2;
	}
	
	public int getCursorPosition(int pos){
		return pos - 2;
	}
	
	public String getGroupLabel(int position){
		if (position == 0){
			return "+ Select friends " + Mood.moodActionMap.get(mTagId);
		} else if (position == 1){
			return "friends";
		} else {
			if (mCursor != null && mCursor.getCount() > position-2){
				mCursor.moveToPosition(position-2);
				return mCursor.getString(1); // name
			} else {
				return "";
			}
		}
	}
	
	public Group getGroupValue(int position){
		if (position > 1 && mCursor != null && mCursor.getCount() > position - 2){
			mCursor.moveToPosition(position-2);
			return new Group(mCursor.getInt(0)); // id
		}
		return null;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		
		if (viewType == TYPE_NORMAL){
			return super.getView(getCursorPosition(position), convertView, parent);
		} else {
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
			
			((TextView) v).setText(getGroupLabel(position));
				
            return v;
		}
	}

	@Override
	public void bindView(View convertView, Context context, Cursor c) {
		((TextView) convertView).setText("WHAT WHAT");
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		final View v = inflater.inflate(R.layout.list_item_picker_groups, parent, false);
		
//		ViewHolder holder = new ViewHolder();
//		
//		holder.name = (TextView) v.findViewById(R.id.contact_name);
//		holder.number = (TextView) v.findViewById(R.id.contact_number);
//		holder.checkbox = (CheckBox) v.findViewById(R.id.contact_checkbox);
//		holder.divider = v.findViewById(R.id.contact_divider);
		
//		v.setTag(holder);
		
		// TODO fix this. Should just get it from the inflate (parent)
		if (layoutParams == null)
			layoutParams = new AbsListView.LayoutParams(parent.getWidth(), ((PickerListView) parent).getChildHeight());
		
		v.setLayoutParams(layoutParams);
		
		return v;
	}
	
}