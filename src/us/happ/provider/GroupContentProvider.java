package us.happ.provider;

import java.util.Arrays;
import java.util.HashSet;

import us.happ.database.GroupTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class GroupContentProvider extends ContentProvider{

	private us.happ.database.DBHelper dbHelper;
	
	private static final int GROUPS = 0x01;
	private static final int GROUP = 0x02;
	
	// AUTHORITY
	private static final String AUTHORITY = "us.happ.provider";

	// PATH
	private static final String BASE_PATH = "groups";
	
	// CONTENT URI
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	
	// CONTENT TYPE
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/groups";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/groups";
	
	// URI MATCHER
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, GROUPS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", GROUP);
	}

	@Override
	public boolean onCreate(){
		dbHelper = new us.happ.database.DBHelper(getContext());
		return false;
	}
	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		// Uisng SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Check if the caller has requested a column which does not exists
	    checkColumns(uri, projection);

	    // Set the table
	    queryBuilder.setTables(GroupTable.NAME);
	    
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    Cursor cursor;

	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case GROUPS:
	    	cursor = queryBuilder.query(db, projection, selection,
	    		        selectionArgs, null, null, sortOrder);
	    	break;
	    case GROUP:
	    	// Adding the ID to the original query
	    	queryBuilder.appendWhere(GroupTable.COLUMN_ID + "="
	    			+ uri.getLastPathSegment());
	    	cursor = queryBuilder.query(db, projection, selection,
	    		        selectionArgs, null, null, sortOrder);
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
		 // Make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	
	    return cursor;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case GROUPS:
	    	rowsDeleted = db.delete(GroupTable.NAME, selection, selectionArgs);
	    	break;
	    case GROUP:
	    	String id = uri.getLastPathSegment();
		    if (TextUtils.isEmpty(selection)) {
		    	rowsDeleted = db.delete(GroupTable.NAME,
		    			GroupTable.COLUMN_ID + "=" + id, null);
		    } else {
		        rowsDeleted = db.delete(GroupTable.NAME,
		        		GroupTable.COLUMN_ID + "=" + id 
		        		+ " and " + selection, selectionArgs);
		    }
		    break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    
	    long id = 0;
	    
	    switch (uriType) {
	    case GROUPS:
	      id = db.insert(GroupTable.NAME, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    int rowsUpdated = 0;
	    
	    switch (uriType) {
	    case GROUPS:
	    	rowsUpdated = db.update(
	    			GroupTable.NAME, 
	    			values, 
	    			selection,
	    			selectionArgs);
	    	break;
	    case GROUP:
	    	String id = uri.getLastPathSegment();
	    	if (TextUtils.isEmpty(selection)) {
	    		rowsUpdated = db.update(
	    				GroupTable.NAME, 
	    				values,
	    				GroupTable.COLUMN_ID + "=" + id, 
	    				null);
	    	} else {
	    		rowsUpdated = db.update(
	    				GroupTable.NAME, 
	    				values,
	    				GroupTable.COLUMN_ID + "=" + id 
	    				+ " and " 
	    				+ selection,
	    				selectionArgs);
	    	}
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
	
	// Check if all columns which are requested are available
	private void checkColumns(Uri uri, String[] projection) {
		String[] available;
		
		int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case GROUPS:
	    	available = GroupTable.allColumns;
	    	break;
	    case GROUP:
	    	available = GroupTable.allColumns;
	    	break;
	    default:
	    	throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
		    HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
		    if (!availableColumns.containsAll(requestedColumns)) {
		    	throw new IllegalArgumentException("Unknown columns in projection");
		    }
		}
	}

}
