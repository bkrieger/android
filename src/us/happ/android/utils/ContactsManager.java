package us.happ.android.utils;

import java.util.HashMap;

import us.happ.android.service.ServiceHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactsManager {
	
	private HashMap<String, String> map;
	private Context mContext;
	
	public ContactsManager(Context context){
		map = new HashMap<String, String>();
		mContext = context;
		
		makeContactsMapping();
	}

	public String[] getAllContacts(){
		String[] arr = new String[map.size()];
		return map.keySet().toArray(arr);
		
	}
	
	public String getName(String number){
		return map.get(number);
	}
	
	private void makeContactsMapping(){
		
		ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                     Cursor pCur = cr.query(
                               Phone.CONTENT_URI,
                               null,
                               Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                     while (pCur.moveToNext()) {
                         String phoneNo = clearnNumber(pCur.getString(pCur.getColumnIndex(Phone.NUMBER)));
                         map.put(phoneNo, name);
                     }
                    pCur.close();
                }
            }
        }
	}
	
	public static String clearnNumber(String number){
		
		number = number.replaceAll("[^\\d.]", "");
		
		if (number.length() > 10)
			number = number.substring(number.length() - 10, number.length());
		
		return number;
	}
	
}
