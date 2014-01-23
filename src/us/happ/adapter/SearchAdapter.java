package us.happ.adapter;

import us.happ.R;
import us.happ.utils.ContactsManager;
import us.happ.utils.Happ;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchAdapter extends CursorAdapter{

	private LayoutInflater inflater;

	public SearchAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mCursor = c;
		inflater = LayoutInflater.from(context);
		mContext = context;
	}
	
	@Override
	public int getCount(){
		if (mCursor == null) return 0;
		return mCursor.getCount();
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void bindView(View convertView, Context context, Cursor c) {
		String name = Happ.hasHoneycomb ? c.getString(c.getColumnIndex(Phone.DISPLAY_NAME_PRIMARY))
										: c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
		
		String number = c.getString(c.getColumnIndex(Phone.NUMBER));
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.name.setText(name);
		holder.number.setText(number);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		final View v = inflater.inflate(R.layout.list_item_search, parent, false);
		
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) v.findViewById(R.id.contact_name);
		holder.number = (TextView) v.findViewById(R.id.contact_number);
		
		v.setTag(holder);
		return v;
	}
	
	class ViewHolder {
		TextView name;
		TextView number;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor){
		super.swapCursor(newCursor);
		mCursor = newCursor;
		return mCursor;
	}

}
