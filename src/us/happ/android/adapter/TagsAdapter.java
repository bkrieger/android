package us.happ.android.adapter;

import us.happ.android.R;
import us.happ.android.model.Mood;
import us.happ.android.model.Tag;
import us.happ.android.utils.Media;
import us.happ.android.view.PickerListView;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TagsAdapter extends ArrayAdapter<Tag>{
	private Tag[] tags;
	private LayoutInflater inflater;
	private Context mContext;
	private android.widget.AbsListView.LayoutParams layoutParams;
	private int padding;
	private Rect mRect;

	public TagsAdapter(Context context, int resource, Tag[] tags) {
		super(context, resource);
		this.tags = tags;
		inflater = LayoutInflater.from(context);
		mContext = context;
		
		padding = (int) Media.pxFromDp(context, 10);
	}

	@Override
	public Tag getItem(int position){
		return tags[position];
	}
	
	@Override
	public long getItemId (int position){
		return position;
	}
	
	@Override
	public int getCount(){
		return tags.length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		TextView tagView;
		if (convertView == null){
			v = inflater.inflate(R.layout.list_item_picker_tags, parent, false);
			
			// TODO fix this. Should just get it from the inflate (parent)
			if (layoutParams == null){
				int height = ((PickerListView) parent).getChildHeight();
				layoutParams = new AbsListView.LayoutParams(parent.getWidth(), height);
				mRect = new Rect(0, 0, height - padding, height - padding);

			}
			v.setLayoutParams(layoutParams);
			
			tagView = (TextView) v.findViewById(R.id.picker_tag);
			v.setTag(tagView);
		} else {
			v = convertView;
			tagView = (TextView) v.getTag();
		}
		
		Tag t = tags[position];
		
		tagView.setText(t.label);
		Drawable d = mContext.getResources().getDrawable(Mood.resIdFromTag(t.valueForPost));
		d.setBounds(mRect);
		tagView.setCompoundDrawables(d, null, null, null);
		
		return v;
		
	}
	
	class ViewHolder {
		TextView title;
		ImageView icon;
	}
}
