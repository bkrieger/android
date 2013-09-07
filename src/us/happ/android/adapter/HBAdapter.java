package us.happ.android.adapter;

import java.util.ArrayList;

import us.happ.android.R;
import us.happ.android.model.Mood;
import us.happ.android.utils.ContactsManager;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HBAdapter extends ArrayAdapter<Mood> {
	
	private ArrayList<Mood> data;
	private LayoutInflater inflater;
	private ContactsManager mContactsManager;
	
	public HBAdapter(Context context, int resource, ContactsManager contactsManager) {
		super(context, resource);
		inflater = LayoutInflater.from(context);
		data = new ArrayList<Mood>();
		mContactsManager = contactsManager;
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
			holder.name = (TextView) v.findViewById(R.id.board_name);
			holder.message = (TextView) v.findViewById(R.id.board_message);
			v.setTag(holder);
		} else {
			v = convertView;
			holder = (ViewHolder) v.getTag();
		}
		
		Mood m = data.get(position);
		
		holder.name.setText(mContactsManager.getName(m.getNumber()));
		holder.message.setText(m.getMessage());
		
		return v;
		
	}
	
	class ViewHolder {
		TextView name;
		TextView message;
	}
	
	public void updateData(ArrayList<Mood> moods){
		data = moods;
		notifyDataSetChanged();
	}

}
