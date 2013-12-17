package us.happ.adapter;

import java.util.HashSet;
import java.util.LinkedHashMap;

import us.happ.R;
import us.happ.utils.ContactsManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactsAdapter extends CursorAdapter implements SectionIndexer{

	public static final CharSequence alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private LayoutInflater mInflater;
	private AlphabetIndexer mIndexer;
	private HashSet<String> blockedNumbers;
	private Cursor mCursor;
	
	private static final int TYPE_COUNT = 2;
	private static final int TYPE_HEADER = 0;
	private static final int TYPE_NORMAL = 1;
	
	private LinkedHashMap<Integer, String> sectionsIndexer;
	private LinkedHashMap<Integer, Integer> rSectionsIndexer;

	public ContactsAdapter(Context context, Cursor c, HashSet<String> blockedNumbers) {
		super(context, c, 0);
		mInflater = LayoutInflater.from(context);
		
		mIndexer = new AlphabetIndexer(c, c.getColumnIndex(Phone.DISPLAY_NAME), alphabet);
		mCursor = c;
		this.blockedNumbers = blockedNumbers;
		
		sectionsIndexer = new LinkedHashMap<Integer, String>();
		rSectionsIndexer = new LinkedHashMap<Integer, Integer>();
		calculateSectionHeaders();
	}
	
	private void calculateSectionHeaders(){
		sectionsIndexer.clear();
		rSectionsIndexer.clear();
		String prev = "";
		int count = 0;
		int i = 0;
		int a = -1;
		
		mCursor.moveToPosition(-1);
		while(mCursor.moveToNext()){
			String l = mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME)).substring(0,1);
			String letter;
			if (!Character.isLetter(l.charAt(0))){
				letter = "#";
			} else {
				letter = mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME)).substring(0,1).toUpperCase();
			}
			
			if (!prev.equals(letter)){
				sectionsIndexer.put(i + count, letter);
				do {
					a += 1;
					rSectionsIndexer.put(a, i + count);
				} while (a < alphabet.length() && !alphabet.subSequence(a, a + 1).equals(letter));

				prev = letter;
				count ++;
			}
			i ++;
		}
	}
	
	@Override
	public boolean areAllItemsEnabled(){
		return false;
	}
	
	@Override
	public boolean isEnabled(int position){
		return getItemViewType(position) == TYPE_NORMAL;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		
		if (viewType == TYPE_NORMAL){
			return super.getView(getCursorPosition(position), convertView, parent);
		} else {
			TextView headerText;

            if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_item_contact_section, parent, false);
                    headerText = (TextView) convertView.findViewById(R.id.contact_section);
                    convertView.setTag(headerText);
            } else {
            	headerText = (TextView) convertView.getTag();
            }
            
            headerText.setText(sectionsIndexer.get(position));
            return convertView;
		}
	}
	
	@Override
	public int getViewTypeCount(){
		return TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position){
		if (isPositionASection(position)){
			return TYPE_HEADER;
		}
		return TYPE_NORMAL;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		
		String name = c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
		String number = c.getString(c.getColumnIndex(Phone.NUMBER));
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
		holder.name.setText(name);
		holder.number.setText(number);
		
		holder.checkbox.setChecked(!blockedNumbers.contains(ContactsManager.cleanNumber(number)));
		
		if (c.getPosition() == 0 || mIndexer.getSectionForPosition(c.getPosition()) != mIndexer.getSectionForPosition(c.getPosition() - 1)){
			holder.divider.setVisibility(View.GONE);
		} else {
			holder.divider.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public View newView(Context view, Cursor c, ViewGroup parent) {
		final View v = mInflater.inflate(R.layout.list_item_contact, parent, false);
		
		ViewHolder holder = new ViewHolder();
		
		holder.name = (TextView) v.findViewById(R.id.contact_name);
		holder.number = (TextView) v.findViewById(R.id.contact_number);
		holder.checkbox = (CheckBox) v.findViewById(R.id.contact_checkbox);
		holder.divider = v.findViewById(R.id.contact_divider);
		
		v.setTag(holder);
		
		return v;
	}
	
	class ViewHolder {
		View divider;
		TextView name;
		TextView number;
		CheckBox checkbox;
	}
	
	public void check(int position){
		mCursor.moveToPosition(position);
		String number = ContactsManager.cleanNumber(mCursor.getString(mCursor.getColumnIndex(Phone.NUMBER)));
		if (blockedNumbers.contains(number)){
			blockedNumbers.remove(number);
		} else {
			blockedNumbers.add(number);
		}
		notifyDataSetChanged();
	}
	
	public void resetChecks(){
		blockedNumbers.clear();
		notifyDataSetChanged();
	}
	
	public int getTotalContacts(){
		return mCursor.getCount();
	}
	
	public int getTotalChecked(){
		return mCursor.getCount() - blockedNumbers.size();
	}

	@Override
	public int getCount(){
		return mCursor.getCount() + sectionsIndexer.size();
	}
	
	public boolean isPositionASection(int position){
		return sectionsIndexer.containsKey(position);
	}
	
	private int getCursorPosition(int position){
		int offset = 0;
        for (Integer key : sectionsIndexer.keySet()) {
            if (position > key) {
            	offset++;
            } else {
            	break;
            }
        }

        return position - offset;
	}
	
	public int getTruePosition(int position){
		return getCursorPosition(position - 1); // - 1 for header
	}
	
	public String getSectionTextForPosition(int position){
		return getSections()[getSectionForPosition(position)];
	}
	
	@Override
	public int getPositionForSection(int section) {
		return rSectionsIndexer.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		
		for (int i = 0; i < alphabet.length(); i ++) {
        	if (position < getPositionForSection(i)){
        		return i - 1;
            }
        }
        
        return alphabet.length() - 1;
	}

	@Override
	public String[] getSections() {
		return (String[]) mIndexer.getSections();
	}

}
