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
import android.provider.Contacts.People;
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
	
	public void makeContactsMapping(){
		Cursor cur = mContentResolver.query(Phone.CONTENT_URI, null, null, null, null);
		Contact contact;
		
		while (cur.moveToNext()){
			String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNo = clearnNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
			int photoId = cur.getInt(cur.getColumnIndex(Phone.PHOTO_ID));
			contact = new Contact(name, photoId);
            map.put(phoneNo, contact);
		}
		
		cur.close();
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
