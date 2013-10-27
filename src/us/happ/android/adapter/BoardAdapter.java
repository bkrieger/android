package us.happ.android.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import us.happ.android.R;
import us.happ.android.model.Mood;
import us.happ.android.utils.BitmapCache;
import us.happ.android.utils.ContactsManager;
import us.happ.android.utils.Happ;
import us.happ.android.utils.Media;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class BoardAdapter extends ArrayAdapter<Mood> {
	
	private ArrayList<Mood> data;
	private LayoutInflater inflater;
	private ContactsManager mContactsManager;
	private Context mContext;
	private BitmapCache mBitmapCache;
	private boolean showCheckbox;
	
	private LinkedHashMap<String, String> checkedContacts;
	
	public BoardAdapter(Context context, int resource) {
		super(context, resource);
		inflater = LayoutInflater.from(context);
		data = new ArrayList<Mood>();
		mContactsManager = ContactsManager.getInstance(context);
		mContext = context;
		mBitmapCache = new BitmapCache();
		checkedContacts = new LinkedHashMap<String, String>();
	}
	
	@Override
	public Mood getItem(int position){
		return data.get(position);
	}
	
	@Override
	public long getItemId (int position){
		return position;
	}
	
	@Override
	public int getCount(){
		return data.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		ViewHolder holder;
		if (convertView == null){
			v = inflater.inflate(R.layout.list_item_board, parent, false);
			holder = new ViewHolder();
			holder.avatar = (ImageView) v.findViewById(R.id.board_avatar);
			holder.name = (TextView) v.findViewById(R.id.board_name);
			holder.message = (TextView) v.findViewById(R.id.board_message);
			holder.tag = (ImageView) v.findViewById(R.id.board_tag);
			holder.checkbox = (CheckBox) v.findViewById(R.id.board_checkbox);
			holder.timestamp = (TextView) v.findViewById(R.id.board_timestamp);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}
		
		holder.checkbox.setTag(position);
		
		Mood m = data.get(position);
		
		// TODO
		// lazy load
		float decay = ((float) (m.getTimestamp().getTime() + m.getDuration()*1000 - new Date().getTime()))/(m.getDuration()*1000);
		if (decay < 0) decay = 0;
		// Check cache first
		Bitmap bitmap = mBitmapCache.getBitmapFromMemCache(m.getNumber());

		if (bitmap == null){
			// TODO use workers
			Bitmap b = Media.getRoundedCornerBitmap(
					mContext, mContactsManager.getAvatar(m.getNumber()), decay, Long.parseLong(m.getNumber()));
			holder.avatar.setImageBitmap(b);
			mBitmapCache.addBitmapToMemoryCache(m.getNumber(), b);
		} else {
			holder.avatar.setImageBitmap(bitmap);
		}
		
		// checkboxes
		Happ.showViewIf(holder.checkbox, holder.tag, showCheckbox && m.getChecked());
		
		holder.checkbox.setChecked(m.getChecked());
		
		holder.tag.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), m.getResId()));
		
		holder.name.setText(mContactsManager.getName(m.getNumber()));
		holder.message.setText(m.getMessage());
		
		DateFormat outputFormatter = new SimpleDateFormat("h:mma");
		holder.timestamp.setText(outputFormatter.format(m.getTimestamp()));
		
		return v;
		
	}
	
	class ViewHolder {
		ImageView avatar;
		TextView name;
		TextView message;
		ImageView tag;
		TextView timestamp;
		CheckBox checkbox;
	}
	
	public void updateData(ArrayList<Mood> moods){
		data = moods;
		Mood m;
		// See what else was checked
		for (int i = 0; i < data.size(); i++){
			m = data.get(i);
			if (checkedContacts.containsKey(m.getNumber())){
				m.setChecked(true);
			}
		}
		// TODO
		// remove expired checked boxes from checkedContacts
		notifyDataSetChanged();
	}
	
	public boolean isCheckboxShown(){
		return showCheckbox;
	}
	
	public void hideCheckbox(){
		showCheckbox = false;
		for (int i = 0; i < data.size(); i++){
			data.get(i).setChecked(false);
		}
		checkedContacts.clear();
		notifyDataSetChanged();
	}
	
	public void check(int position){
		Mood m = getItem(position);
		addCheckedContacts(m);
		showCheckbox = !checkedContacts.isEmpty();
		notifyDataSetChanged();
	}
	
	private void addCheckedContacts(Mood m){
		boolean wasChecked = m.getChecked();
		m.setChecked(!m.getChecked());
		if (wasChecked){
			checkedContacts.remove(m.getNumber());
		} else {
			checkedContacts.put(m.getNumber(), mContactsManager.getName(m.getNumber()).split(" ")[0]); // only first names
		}
	}
	
	public String[] getCheckedNumbers(){
		String[] numbers = new String[checkedContacts.size()];
		checkedContacts.keySet().toArray(numbers);
		return numbers;
	}
	
	public String[] getCheckedNames(){
		String[] names = new String[checkedContacts.size()];
		checkedContacts.values().toArray(names);
		return names;
	}
	

}
