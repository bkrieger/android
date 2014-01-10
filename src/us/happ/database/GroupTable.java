package us.happ.database;

import android.database.sqlite.SQLiteDatabase;

public class GroupTable {
	public final static String NAME = "Groups";
	public final static String COLUMN_ID = "_id";
	public final static String COLUMN_NAME = "name";
	public final static String COLUMN_CREATED_AT = "created_at";
	public final static String COLUMN_UPDATED_AT = "updated_at";
	public final static String COLUMN_MEMBERS = "members";
	
	public final static String[] allColumns = {
		COLUMN_ID,				// 0
		COLUMN_NAME,			// 1
		COLUMN_CREATED_AT,		// 2
		COLUMN_UPDATED_AT,		// 3
		COLUMN_MEMBERS			// 4
	};
	
	// Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
		  + NAME + "(" + 
		  COLUMN_ID + " integer primary key, " 
		  + COLUMN_NAME + " text not null, "
		  + COLUMN_CREATED_AT + " integer not null, "
		  + COLUMN_UPDATED_AT + " integer not null, "
		  + COLUMN_MEMBERS + " text not null)";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
  	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + NAME);
    	onCreate(database);
	}
}
