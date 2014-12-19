package com.wangzhenyi.miracleinsperation;

import java.util.Date;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.wangzhenyi.miracleinsperation.MiracleInsperationContract.InsperatioinEntry;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{
	private MiracleInsperationDbHelper mDbHelper;
	private EditText mInsperationEditText;
	private ScrollView mScrollView;
	private LinearLayout mInsperationList;
;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDbHelper = new MiracleInsperationDbHelper(this);
		mScrollView = (ScrollView) findViewById(R.id.scroll_view);
		mInsperationEditText = (EditText) findViewById(R.id.edit_innovation);
		mInsperationList = (LinearLayout) findViewById(R.id.innovation_list);
		
		mInsperationEditText.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume(); // This is important, otherwise app would not start

		// Read items from database
		Vector<Insperation> insperations = getInsperationsFromDb();

		// Display items
		displayInsperations(insperations);
	}

	private Vector<Insperation> getInsperationsFromDb() {
		Vector<Insperation> insperations = new Vector<Insperation>();

		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Defines a projection that specifies the queried columns from
		// the database
		String[] projection = { InsperatioinEntry._ID,
				InsperatioinEntry.COLUMN_NAME_DATE,
				InsperatioinEntry.COLUMN_NAME_CONTENT};

		// Sort order
		String sortOrder = InsperatioinEntry.COLUMN_NAME_DATE; // + " DESC";

		Cursor c = db.query(InsperatioinEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
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

	/* Displays a list of insperations on screen */
	private void displayInsperations(Vector<Insperation> insperations) {
		// Return when empty
		if (insperations == null || insperations.isEmpty())
			return;
		
		// Clears screen
		clearScreen();

		// Displays the items
		for (Insperation insperation : insperations)
			addInsperationToDisplay(insperation);
		
		// Scrolls to the bottom
		scrollToBottom();
	}

	/* Moves the scrolllayout to bottom */
	private void scrollToBottom() {
		mScrollView.post( new Runnable() {
	        public void run() { 
	        	boolean b = mScrollView.fullScroll(ScrollView.FOCUS_DOWN); 
	        }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_clear:
			clearData();
			return true;
		case R.id.action_settings:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* Called when the database button is clicked /
	public void database(View view) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(InsperatioinEntry.COLUMN_NAME_CONTENT, "hi");
		values.put(InsperatioinEntry.COLUMN_NAME_DATE, new Date().getTime());

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(InsperatioinEntry.TABLE_NAME, null, values);

		output("Inserted a new row, id " + newRowId);

		// Define a projection that specifies the queried columns from
		// the database
		String[] projection = { InsperatioinEntry._ID,
				InsperatioinEntry.COLUMN_NAME_DATE,
				InsperatioinEntry.COLUMN_NAME_CONTENT,
				InsperatioinEntry.COLUMN_NAME_DELETED };

		// Sort order
		String sortOrder = InsperatioinEntry._ID + " DESC";

		Cursor c = db.query(InsperatioinEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by tow groups
				sortOrder // The sort order
				);

		if (!c.moveToFirst()) {
			db.close();
			return;
		}

		output(null);
		do {
			long id = c.getLong(c.getColumnIndexOrThrow(InsperatioinEntry._ID));
			long date = c.getLong(c
					.getColumnIndexOrThrow(InsperatioinEntry.COLUMN_NAME_DATE));
			String content = c
					.getString(c
							.getColumnIndexOrThrow(InsperatioinEntry.COLUMN_NAME_CONTENT));
			int deleted = c
					.getInt(c
							.getColumnIndexOrThrow(InsperatioinEntry.COLUMN_NAME_DELETED));
			output(id + "," + date + "," + content + "," + deleted);
		} while (c.moveToNext());

		c.close();
		db.close();

	}*/

	/* Called when the clear database button is clicked */
	public void clearData() {

		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		mDbHelper.clear(db);
		showShortToast("database cleared!");

		// Closes database
		db.close();
		
		// Removes items from screen
		clearScreen();
	}

	private void clearScreen() {
		mInsperationList.removeAllViews();
	}

	/* Display a short toast */
	private void showShortToast(String string) {
		if (isEmpty(string)) {
			return;
		}
		
		// Display a toast
		Toast toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
		toast.show();

	}

	/* Called when the add button is clicked */
	public void addInsperation(View view) {

		// Gets input from EditText
		String content = mInsperationEditText.getText().toString();

		// If input is empty then do nothing
		if (isEmpty(content))
			return;

		// Creates new item
		Insperation insperation = new Insperation(content);
		
		// Adds new item to database
		addInsperationToDb(insperation);
		
		// Adds new item to display
		addInsperationToDisplay(insperation);

		// Scrolls to the bottom
		scrollToBottom();
				
		// Clears input
		mInsperationEditText.setText("");
	}

	/* Displays an item */
	private void addInsperationToDisplay(Insperation insperation) {
		// Adds a view to the viewgroup
		mInsperationList.addView(insperation.toView(this));
	}

	private void addInsperationToDb(Insperation insperation) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(InsperatioinEntry.COLUMN_NAME_CONTENT, insperation.getContent());
		values.put(InsperatioinEntry.COLUMN_NAME_DATE, insperation.getDate().getTime());

		// Insert the new row, returning the primary key value of the new row
//		long newRowId;
//		newRowId = db.insert(InsperatioinEntry.TABLE_NAME, null, values);
		db.insert(InsperatioinEntry.TABLE_NAME, null, values);

		db.close();
	}

	private boolean isEmpty(String str) {
		return str == null || str.equals("");
	}

	/**
	 *  Called when the EditText is clicked
	 * to adjust the height of scrollview
	 */
	public void onClick(View arg0) {
		scrollToBottom(100);
	}
	
	/* Moves the scrolllayout to bottom, delayed */
	private void scrollToBottom(long delay) {
		mScrollView.postDelayed( new Runnable() {
	        public void run() { 
	        	boolean b = mScrollView.fullScroll(ScrollView.FOCUS_DOWN); 
	        }
		}, delay);
	}
}
