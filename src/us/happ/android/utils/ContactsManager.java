package us.happ.android.utils;

import java.util.HashMap;

import us.happ.android.service.ServiceHelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class ContactsManager {
	
	private HashMap<String, Contact> map;
	private Context mContext;
	private ContentResolver mContentResolver;
	
	private static final String[] PHOTO_BITMAP_PROJECTION = new String[] {
	    ContactsContract.CommonDataKinds.Photo.PHOTO
	};
	
	public ContactsManager(Context context){
		map = new HashMap<String, Contact>();
		mContext = context;
		mContentResolver = mContext.getContentResolver();
		
		
		makeContactsMapping();
	}

	public String[] getAllContacts(){
		String[] arr = new String[map.size()];
		return map.keySet().toArray(arr);
		
	}
	
	public Bitmap getAvatar(String number){
		return map.get(number).getAvatar();
	}
	
	public String getName(String number){
		return map.get(number).name;
	}
	
	private void makeContactsMapping(){

		Contact contact;
		
        Cursor cur = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                  String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                  String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                  if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                     Cursor pCur = mContentResolver.query(
                               Phone.CONTENT_URI,
                               null,
                               Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
                     while (pCur.moveToNext()) {
                         String phoneNo = clearnNumber(pCur.getString(pCur.getColumnIndex(Phone.NUMBER)));
                         int photoId = pCur.getInt(pCur.getColumnIndex(Phone.PHOTO_ID));
                         contact = new Contact(name, photoId);
                         map.put(phoneNo, contact);
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
	
	final Bitmap fetchThumbnail(final int thumbnailId) {

	    final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
	    final Cursor cursor = mContentResolver.query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

	    try {
	        Bitmap thumbnail = null;
	        if (cursor.moveToFirst()) {
	            final byte[] thumbnailBytes = cursor.getBlob(0);
	            if (thumbnailBytes != null) {
	                thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
	            }
	        }
	        return thumbnail;
	    }
	    finally {
	        cursor.close();
	    }

	}
	
	class Contact {
		String name;
		int photoId;
		private Bitmap avatar;
		
		public Contact(String name, int photoId){
			this.name = name;
			this.photoId = photoId;
		}
		
		public Bitmap getAvatar(){
			if (avatar == null){
				avatar = fetchThumbnail(photoId);
			}
			return avatar;
		}
	}
	
}
