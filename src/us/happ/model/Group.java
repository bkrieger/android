package us.happ.model;

import us.happ.database.GroupTable;
import us.happ.provider.GroupContentProvider;
import us.happ.utils.Happ;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Group {
	public long id;
	public String name;
	public long created_at;
	public long updated_at;
	public String[] members;
	
	public Group (int id){
		this.id = id;
	}
	
	// need default values
	public Group(int id, String name, long created_at, long updated_at, String db_members){
		this.id = id;
		this.name = name;
		this.created_at = created_at;
		this.updated_at = updated_at;
		
	}
	
	public void saveToDB(Context context) {
		ContentValues values = new ContentValues();
		values.put(GroupTable.COLUMN_ID, id);
	    values.put(GroupTable.COLUMN_NAME, name);
	    values.put(GroupTable.COLUMN_CREATED_AT, created_at);
	    values.put(GroupTable.COLUMN_UPDATED_AT, updated_at);
	    values.put(GroupTable.COLUMN_MEMBERS, Happ.arrayToString(members));
	    
	    if (id == 0){
	    	Uri newGroupUri = context.getContentResolver().insert(GroupContentProvider.CONTENT_URI, values);
	    }else{
	    	Uri updateUri = Uri.parse(GroupContentProvider.CONTENT_URI + "/" + id);
		    context.getContentResolver().update(updateUri, values, null, null);
	    }
	    
	    // TODO
	    // update group when done
	}
	
	public void delete(Context context, Group group) {
		long id = group.id;
		Uri deleteUri = Uri.parse(GroupContentProvider.CONTENT_URI + "/" + id);
		context.getContentResolver().delete(deleteUri, null, null);
	}
	
	public void populate(Context context){
		Uri uri = ContentUris.withAppendedId(GroupContentProvider.CONTENT_URI, this.id);
		String[] projection = GroupTable.allColumns;
		Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
		
		c.moveToFirst();
		
		this.name = c.getString(1);
		this.created_at = c.getLong(2);
		this.updated_at = c.getLong(3);
		this.members = Happ.stringToArray(c.getString(4));
		
	}
	
}
