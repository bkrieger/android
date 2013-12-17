package us.happ.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;


import us.happ.R;
import us.happ.bitmap.ImageLoader;
import us.happ.bitmap.ImageResizer;
import us.happ.bitmap.ImageCache.ImageCacheParams;
import us.happ.model.Mood;
import us.happ.utils.BitmapCache;
import us.happ.utils.ContactsManager;
import us.happ.utils.Happ;
import us.happ.utils.Media;
import us.happ.view.AvatarView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class BoardAdapter extends ArrayAdapter<Mood> {
	
	private static final int AVATAR_HEIGHT = 70; //dp
	private static final String IMAGE_CACHE_DIR = "images";
	
	private ArrayList<Mood> data;
	private LayoutInflater inflater;
	private ContactsManager mContactsManager;
	private Context mContext;
	private ImageLoader mImageLoader;
	private boolean showCheckbox;
	private HashMap<String, Integer> mLastSeenDecay;
	
	private LinkedHashMap<String, String> checkedContacts;
	private SimpleDateFormat outputFormatter;
	
	public BoardAdapter(Context context, int resource) {
		super(context, resource);
		inflater = LayoutInflater.from(context);
		data = new ArrayList<Mood>();
		mContactsManager = ContactsManager.getInstance(context);
		mContext = context;
		
		int avatarHeight = (int) Media.pxFromDp(context, AVATAR_HEIGHT);
		
		ImageCacheParams cacheParams = new ImageCacheParams(context, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		mImageLoader = new ImageLoader(context, avatarHeight); // Size of ImageView to be calculated
//		mImageLoader.setLoadingImage(R.drawable.ic_tag_chill);
		mImageLoader.addImageCache(((FragmentActivity) context).getSupportFragmentManager(), cacheParams);

		checkedContacts = new LinkedHashMap<String, String>();
		mLastSeenDecay = new HashMap<String, Integer>();
		
		outputFormatter = new SimpleDateFormat("h:mma");
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
			holder.avatar = (AvatarView) v.findViewById(R.id.board_avatar);
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
		
		// Decay, out of 360
		int decay = (int) (Mood.getDecay(m.getDuration(), m.getTimestamp().getTime())*360);
		if (decay < 0) decay = 0;
		
		int id = mContactsManager.getId(m.getNumber());
		int avatarId = mContactsManager.getAvatarResId(m.getNumber());
		mImageLoader.loadImage(
				new AvatarData(id, avatarId,
						m.getNumber(),
						decay), holder.avatar);
		holder.avatar.setDecay(decay);
		holder.avatar.setNumber(Long.parseLong(m.getNumber()));
		
		if (!mLastSeenDecay.containsKey(m.getNumber())){
			holder.avatar.animateDecay(360);
		} else {
			int lastDecay = mLastSeenDecay.get(m.getNumber());
			if (lastDecay > decay + 5){
				holder.avatar.animateDecay(lastDecay);
			} else if (lastDecay < decay){
				holder.avatar.animateDecay(360);
			} else {
				holder.avatar.invalidate();
			}
		}
		mLastSeenDecay.put(m.getNumber(), decay);
		
		// checkboxes
		Happ.showViewIf(holder.checkbox, holder.tag, showCheckbox && m.getChecked());
		
		holder.checkbox.setChecked(m.getChecked());
		
		holder.tag.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), m.getResId()));
		
		holder.name.setText(mContactsManager.getName(m.getNumber()));
		holder.message.setText(m.getMessage());
		
		holder.timestamp.setText(outputFormatter.format(m.getTimestamp()));
		
		return v;
		
	}
	
	class ViewHolder {
		AvatarView avatar;
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
		// TODO
		// update individual row instead of notifyDataSetChanged
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
	
	public class AvatarData {
		public Bitmap bitmap;
		public int id;
		public int avatarId;
		public String number;
		public int decay;
		
		public AvatarData(int id, int avatarId, String number, int decay){
			this.id = id;
			this.avatarId = avatarId;
			this.number = number;
			this.decay = decay;
		}
		
		@Override
		public String toString(){
			return number; // + "-" + decay/18; // 20 projections
		}
	}
	

}
