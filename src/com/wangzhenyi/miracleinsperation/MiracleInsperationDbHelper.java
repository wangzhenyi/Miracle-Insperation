package com.wangzhenyi.miracleinsperation;

import android.content.Context;
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
	
}
