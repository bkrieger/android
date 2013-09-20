package us.happ.android.adapter;

import java.util.Date;

import us.happ.android.R;
import us.happ.android.adapter.HBAdapter.ViewHolder;
import us.happ.android.model.Mood;
import us.happ.android.model.Tag;
import us.happ.android.utils.Media;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TagsAdapter extends ArrayAdapter<Tag>{
	private Tag[] tags;
	private LayoutInflater inflater;
	private Context mContext;

	public TagsAdapter(Context context, int resource, Tag[] tags) {
		super(context, resource);
		this.tags = tags;
		inflater = LayoutInflater.from(context);
		mContext = context;
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
		ViewHolder holder;
		if (convertView == null){
			v = inflater.inflate(R.layout.list_item_dialog_tags, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) v.findViewById(R.id.dialog_tags_title);
			holder.icon = (ImageView) v.findViewById(R.id.dialog_tags_icon);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}
		
		Tag t = tags[position];
	
		holder.title.setText(t.label);
		holder.icon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), Mood.resIdFromTag(t.valueForPost)));
				
		return v;
		
	}
	
	class ViewHolder {
		TextView title;
		ImageView icon;
	}
}
