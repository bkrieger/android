package us.happ.android.adapter;

import java.util.HashSet;

import us.happ.android.R;
import us.happ.android.utils.ContactsManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactsAdapter extends CursorAdapter implements SectionIndexer{

	private static final CharSequence alphabet = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private LayoutInflater mInflater;
	private AlphabetIndexer mIndexer;
	private HashSet<String> blockedNumbers;
	private Cursor mCursor;

	public ContactsAdapter(Context context, Cursor c, HashSet<String> blockedNumbers) {
		super(context, c, 0);
		mInflater = LayoutInflater.from(context);
		
		mIndexer = new AlphabetIndexer(c, c.getColumnIndex(Phone.DISPLAY_NAME), alphabet);
		mCursor = c;
		this.blockedNumbers = blockedNumbers;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		
		String name = c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
		String number = c.getString(c.getColumnIndex(Phone.NUMBER));
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
		holder.name.setText(name);
		holder.number.setText(number);
		
		holder.checkbox.setChecked(!blockedNumbers.contains(ContactsManager.clearnNumber(number)));
		
	}

	@Override
	public View newView(Context view, Cursor c, ViewGroup parent) {
		final View v = mInflater.inflate(R.layout.list_item_contact, parent, false);
		
		ViewHolder holder = new ViewHolder();
		
		holder.name = (TextView) v.findViewById(R.id.contact_name);
		holder.number = (TextView) v.findViewById(R.id.contact_number);
		holder.checkbox = (CheckBox) v.findViewById(R.id.contact_checkbox);
		
		v.setTag(holder);
		
		return v;
	}
	
	class ViewHolder {
		TextView name;
		TextView number;
		CheckBox checkbox;
	}
	
	public void check(int position){
		mCursor.moveToPosition(position);
		String number = ContactsManager.clearnNumber(mCursor.getString(mCursor.getColumnIndex(Phone.NUMBER)));
		if (blockedNumbers.contains(number)){
			blockedNumbers.remove(number);
		} else {
			blockedNumbers.add(number);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getPositionForSection(int section) {
		return mIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return mIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		return mIndexer.getSections();
	}

}
