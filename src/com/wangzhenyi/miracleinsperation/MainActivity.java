package com.wangzhenyi.miracleinsperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.wangzhenyi.miracleinsperation.MiracleInsperationContract.InsperatioinEntry;

public class MainActivity extends Activity implements View.OnClickListener {
	private MiracleInsperationDbHelper mDbHelper;
	private EditText mInsperationEditText;
	// private ScrollView mScrollView;
	private ListView mInsperationList;
	private ArrayList<HashMap<String, String>> mItemArrayList;
	private SimpleAdapter mAdapter;
	private CharSequence LABEL_FOR_CLIP = "Miracle Insperation";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDbHelper = new MiracleInsperationDbHelper(this);
		// mScrollView = (ScrollView) findViewById(R.id.scroll_view);
		mInsperationEditText = (EditText) findViewById(R.id.edit_innovation);
		mInsperationList = (ListView) findViewById(R.id.innovation_list);

		mInsperationEditText.setOnClickListener(this);

		// Read items from database
		Vector<Insperation> insperations = mDbHelper.getInsperations();

		// Display items
		displayInsperations(insperations);

		// Actions on long click ListView
		// mInsperationList.setOnItemLongClickListener(new
		// OnItemLongClickListener() {
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// listIndex = arg2;
		// TextView idView = (TextView)arg1.findViewById(R.id.insperation_id);
		// String dbId = idView.getText().toString();
		// itemIdInDb = Integer.parseInt(dbId);
		// return false;
		// }
		// });
	}

	@Override
	protected void onResume() {
		super.onResume(); // This is important, otherwise app would not start

	}

	/* Displays a list of insperations on screen */
	private void displayInsperations(Vector<Insperation> insperations) {
		// Return when empty
		if (insperations == null || insperations.isEmpty())
			return;

		// Converts items to arraylist
		mItemArrayList = new ArrayList<HashMap<String, String>>();
		for (Insperation insperation : insperations) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(InsperatioinEntry.COLUMN_NAME_DATE,
					insperation.getFormatedDate());
			map.put(InsperatioinEntry.COLUMN_NAME_CONTENT,
					insperation.getContent());
			map.put(InsperatioinEntry._ID, insperation.getId() + "");
			mItemArrayList.add(map);
		}
		// Builds adapter
		mAdapter = new SimpleAdapter(this, // Context
				mItemArrayList,// Data source
				R.layout.insperation_item,// id for ListItem

				// corresponding key values
				new String[] { InsperatioinEntry.COLUMN_NAME_DATE,
						InsperatioinEntry.COLUMN_NAME_CONTENT,
						InsperatioinEntry._ID },

				// IDs for the corresponding TextViews
				new int[] { R.id.insperation_date, R.id.insperation_content,
						R.id.insperation_id });

		// Binds the adapter to the ListView
		mInsperationList.setAdapter(mAdapter);

		// Registers the ListView to respond to long click actions
		registerForContextMenu(mInsperationList);

		// Scrolls to the bottom
		scrollToBottom();
	}

	/* Scrolls the list to bottom */
	private void scrollToBottom() {
		mInsperationList.post(new Runnable() {
			public void run() {
				mInsperationList.setSelection(mInsperationList.getBottom());
			}
		});
	}

	/* Option menu */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Long click menu */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item_longclick_menu, menu);
	}

	/* Defines the action to take for the long click menu items */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.longclick_copy:
			copyItem(info.id);
			return true;
		case R.id.longclick_remove:
			deleteItem(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/* Removes an item both from database and from screen */
	private void deleteItem(long idArr) {
		// Gets the corresponding database id
		int index = (int) idArr;
		String idInDbStr = mItemArrayList.get(index).get(InsperatioinEntry._ID);
		int idInDb = Integer.parseInt(idInDbStr);

		// Removes the item in database
		int result = mDbHelper.deleteInsperation(idInDb);

		// If success removes from screen and show message
		// with an Undo option
		if (result > 0) {
			int idArrInt = (int) idArr;
			mItemArrayList.remove(idArrInt);
			mAdapter.notifyDataSetChanged();

			showShortToast(getResources().getString(
					R.string.succeed_delete_database_item));
			// TODO Undo delete in toast
			// else show error message
		} else {
			showShortToast(getResources().getString(
					R.string.error_delete_database_item));
		}

	}

	/* Copies the content of an item to clip board */
	private void copyItem(long idArr) {
		// Retrieves the content
		int index = (int) idArr;
		HashMap<String, String> itemHashMap = mItemArrayList.get(index);
		String dateString = itemHashMap.get(InsperatioinEntry.COLUMN_NAME_DATE);
		dateString = dateString.replace("\n", "");
		String content = itemHashMap.get(InsperatioinEntry.COLUMN_NAME_CONTENT);

		String strToCopy = dateString + "\n" + content;

		// Copies the content to clip board
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText(LABEL_FOR_CLIP,
				strToCopy));

		showShortToast(getResources().getString(R.string.succeed_copy_item));
	}

	/* Defines the action to take for the option menu items */
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

	/* Empties the list */
	private void clearScreen() {
		mItemArrayList.removeAll(mItemArrayList);
		mAdapter.notifyDataSetChanged();
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
		mDbHelper.addInsperation(insperation);

		// Adds new item to display
		addInsperationToDisplay(insperation);

		// Scrolls to the bottom
		scrollToBottom();

		// Clears input
		mInsperationEditText.setText("");
	}

	/* Displays an item */
	private void addInsperationToDisplay(Insperation insperation) {
		// Creates new hashmap
		HashMap<String, String> newItem = new HashMap<String, String>();
		newItem.put(InsperatioinEntry.COLUMN_NAME_DATE,
				insperation.getFormatedDate());
		newItem.put(InsperatioinEntry.COLUMN_NAME_CONTENT,
				insperation.getContent());
		newItem.put(InsperatioinEntry._ID, insperation.getId() + "");

		// Adds new item to list and notify change
		mItemArrayList.add(newItem);
		mAdapter.notifyDataSetChanged();
	}

	private boolean isEmpty(String str) {
		return str == null || str.equals("");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// do nothing
		Log.v("MainActivity", "onClick");
	}

}
