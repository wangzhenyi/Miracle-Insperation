/**
 * Created by Zhenyi Wang.
 * Last modified on 12 Jan 2015.
 */

package com.wangzhenyi.miracleinsperation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wangzhenyi.miracleinsperation.MiracleInsperationContract.InsperatioinEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class is used by the MiracleInsperation App to display deleted records
 */
public class DeletedItemsActivity extends ActionBarActivity {
	private MiracleInsperationDbHelper mDbHelper;
	private ListView mInsperationList;
	private ArrayList<HashMap<String, String>> mItemArrayList;
	private SimpleAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deleted_items);
		
		mDbHelper = new MiracleInsperationDbHelper(this);
		// mScrollView = (ScrollView) findViewById(R.id.scroll_view);
		mInsperationList = (ListView) findViewById(R.id.deleted_innovation_list);
//		mInsperationList.setEnabled(false); // no click

		// Read items from database
		Vector<Insperation> insperations = mDbHelper.getDeletedInsperations();

		// Display items
		displayInsperations(insperations);
		
		MainActivity.activityList.add(this);
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
					insperation.getFormatedDate() + " " + insperation.getId());
			map.put(InsperatioinEntry.COLUMN_NAME_CONTENT,
					insperation.getContent());
			map.put(InsperatioinEntry._ID, insperation.getId() + "");
			mItemArrayList.add(map);
		}
		// Builds adapter
		mAdapter = new SimpleAdapter(this, // Context
				mItemArrayList,// Data source
				R.layout.deleted_insperation_item,// id for ListItem

				// corresponding key values
				new String[] { InsperatioinEntry.COLUMN_NAME_DATE,
						InsperatioinEntry.COLUMN_NAME_CONTENT,
						InsperatioinEntry._ID },

				// IDs for the corresponding TextViews
				new int[] { R.id.deleted_insperation_date, R.id.deleted_insperation_content,
						R.id.deleted_insperation_id });

		// Binds the adapter to the ListView
		mInsperationList.setAdapter(mAdapter);

	}
	
	@Override
	protected void onResume() {
		super.onResume(); // This is important, otherwise app would not start
		getWindow().setTitle(getString(R.string.title_activity_deleted_items));
	}

}
