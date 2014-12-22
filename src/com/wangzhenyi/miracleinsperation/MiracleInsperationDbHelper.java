package com.wangzhenyi.miracleinsperation;

import java.util.Date;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wangzhenyi.miracleinsperation.MiracleInsperationContract.InsperatioinEntry;

/* Database helperfor the Miracle Insperation app */
public class MiracleInsperationDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "MiracleInsperation.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String LONG_TYPE = " LONG";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP= ",";
	private static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + InsperatioinEntry.TABLE_NAME + " (" +
					InsperatioinEntry._ID +	" INTEGER PRIMARY KEY AUTOINCREMENT," +
					InsperatioinEntry.COLUMN_NAME_DATE + LONG_TYPE + COMMA_SEP +
					InsperatioinEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
					InsperatioinEntry.COLUMN_NAME_DELETED + INTEGER_TYPE + " DEFAULT 0" +
					" )";
	private static final String SQL_DELETE_ENTRIES = 
			"DROP TABLE IF EXISTS " + InsperatioinEntry.TABLE_NAME;
	
	public MiracleInsperationDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		clear(db);
	}
	
	/* Simply discard the data and start over */
	public void clear(SQLiteDatabase db) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
	
	/* Selects all the visible items from database */
	public Vector<Insperation> getInsperations() {
		Vector<Insperation> insperations = new Vector<Insperation>();

		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Defines a projection that specifies the queried columns from
		// the database
		String[] projection = { InsperatioinEntry._ID,
				InsperatioinEntry.COLUMN_NAME_DATE,
				InsperatioinEntry.COLUMN_NAME_CONTENT };

		// Sort order
		String sortOrder = InsperatioinEntry.COLUMN_NAME_DATE; // + " DESC";

		String whereColumns = InsperatioinEntry.COLUMN_NAME_DELETED + "=?";
		String[] whereValues = { "0" };

		Cursor c = db.query(InsperatioinEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				whereColumns, // The columns for the WHERE clause
				whereValues, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by tow groups
				sortOrder // The sort order
				);

		if (!c.moveToFirst()) {
			db.close();
			return insperations;
		}

		// Adds items to vectors
		do {
			int id = c.getInt(c.getColumnIndexOrThrow(InsperatioinEntry._ID));
			long dateLong = c.getLong(c
					.getColumnIndexOrThrow(InsperatioinEntry.COLUMN_NAME_DATE));
			Date date = new Date(dateLong);
			String content = c
					.getString(c
							.getColumnIndexOrThrow(InsperatioinEntry.COLUMN_NAME_CONTENT));
			Insperation insperation = new Insperation(id, date, content);
			insperations.add(insperation);
		} while (c.moveToNext());

		// Closes cursor and repository
		c.close();
		db.close();

		return insperations;
	}
	
	/* Updates the state of an item to deleted in database */
	public int deleteInsperation(int id) {
		// Create a new map of value
		ContentValues values = new ContentValues();
		values.put(InsperatioinEntry.COLUMN_NAME_DELETED, "1");

		String whereClause = InsperatioinEntry._ID + "=?";
		String[] whereArgs = {id + ""};
		
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Update the record, returning the number of rows affected
		int result= db.update(InsperatioinEntry.TABLE_NAME, values, whereClause, whereArgs);

		// Closes the database
		db.close();

		return result;
	}
	
	/* Inserts an item, assigning an id to the item object */
	public void addInsperation(Insperation insperation) {
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(InsperatioinEntry.COLUMN_NAME_CONTENT,
				insperation.getContent());
		values.put(InsperatioinEntry.COLUMN_NAME_DATE, insperation.getDate()
				.getTime());

		// Insert the new row, returning the primary key value of the new row
		// long newRowId;
		// newRowId = db.insert(InsperatioinEntry.TABLE_NAME, null, values);
		db.insert(InsperatioinEntry.TABLE_NAME, null, values);

		// Selects the new id and assigns to the item object
		Cursor cursor = db.rawQuery("select LAST_INSERT_ROWID() ",null);
        if(cursor.moveToFirst()){
        	int newId = cursor.getInt(0);
        	insperation.setId(newId);
        }
        
		db.close();
	}
}
