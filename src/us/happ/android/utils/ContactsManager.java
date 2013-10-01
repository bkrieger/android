package us.happ.android.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class ContactsManager {
	
	private static ContactsManager instance;
	private HashMap<String, Contact> map;
	private Context mContext;
	private ContentResolver mContentResolver;
	private HashSet<String> blockedNumbers;
	private String[] cachedFriends;
	private boolean friendsIsDirty = true;
	private boolean hasFetchedContacts = false;
	
	private ArrayList<FetchContactsListener> listeners;
	
	private static final String[] PHOTO_BITMAP_PROJECTION = new String[] {
	    ContactsContract.CommonDataKinds.Photo.PHOTO
	};
	
	public ContactsManager(Context context){
		map = new HashMap<String, Contact>();
		mContext = context;
		mContentResolver = mContext.getContentResolver();
		listeners = new ArrayList<FetchContactsListener>();
	}
	
	public static synchronized ContactsManager getInstance(Context context){
		if (instance == null){
			instance = new ContactsManager(context);
		}
		return instance;
	}

	public String[] getAllContacts(){
		String[] arr = new String[map.size()];
		return map.keySet().toArray(arr);
	}
	 
	public String[] getAllFriends(){
		Set<String> set = map.keySet();
		if (friendsIsDirty || cachedFriends == null){
			blockedNumbers = Storage.getBlockedNumbers(mContext);
			cachedFriends = new String[set.size() - blockedNumbers.size()];
			int i = 0;
			for (String s: set){
				if (!blockedNumbers.contains(s)){
					cachedFriends[i] = s;
					i += 1;
				}
			}
			friendsIsDirty = false;
		}
		return cachedFriends;
	}
	
	public void setFriendsDirty(boolean isDirty){
		friendsIsDirty = isDirty;
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
			String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
			String phoneNo = clearnNumber(cur.getString(cur.getColumnIndex(Phone.NUMBER)));
			int photoId = cur.getInt(cur.getColumnIndex(Phone.PHOTO_ID));
			int contactId = cur.getInt(cur.getColumnIndex(Phone.CONTACT_ID));
			contact = new Contact(name, contactId, photoId);
			map.put(phoneNo, contact);
		}
		
		cur.close();
		
		// callbacks
		hasFetchedContacts  = true;
		for (int i = 0; i < listeners.size(); i ++){
			listeners.get(i).onContactsFetched();
		}
	}
	
	public static String clearnNumber(String number){
		
		number = number.replaceAll("[^\\d.]", "");
		
		if (number.length() > 10)
			number = number.substring(number.length() - 10, number.length());
		
		return number;
	}
	
	// TODO
	// scale this image?
	public InputStream openDisplayPhoto(long contactId) {
	     Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	     Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.DISPLAY_PHOTO);
	     try {
	         AssetFileDescriptor fd =
	             mContentResolver.openAssetFileDescriptor(displayPhotoUri, "r");
	         return fd.createInputStream();
	     } catch (IOException e) {
	         return null;
	     }
	 }
	
	final private Bitmap fetchThumbnail(final int thumbnailId) {

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
	
	public boolean hasFetchedContacts(){
		return hasFetchedContacts;
	}
	
	public void addFetchContactsListener(FetchContactsListener listener){
		listeners.add(listener);
	}
	
	public interface FetchContactsListener {
		public void onContactsFetched();
	}
	
	class Contact {
		String name;
		int id;
		int photoId;
		private Bitmap avatar;
		
		public Contact(String name, int contactId, int photoId){
			this.name = name;
			this.id = contactId;
			this.photoId = photoId;
		}
		
		public Bitmap getAvatar(){
			if (avatar == null){
				if (Build.VERSION.SDK_INT >= 14)
					avatar = BitmapFactory.decodeStream(openDisplayPhoto(id));
				else
					avatar = fetchThumbnail(photoId);
			}
			return avatar;
		}
		
	}
	
}
