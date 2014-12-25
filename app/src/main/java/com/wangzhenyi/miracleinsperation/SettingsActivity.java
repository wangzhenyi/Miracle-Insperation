package com.wangzhenyi.miracleinsperation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import java.util.Locale;
import java.util.Vector;

public class SettingsActivity extends ActionBarActivity {
	private MiracleInsperationDbHelper mDbHelper;
	private Bundle mSavedInstanceState;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mDbHelper = new MiracleInsperationDbHelper(this);
		mSavedInstanceState = savedInstanceState;
		
		
		CheckBox cbShowId = (CheckBox) findViewById(R.id.settings_cb_show_id);
		
		SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT,Context.MODE_PRIVATE);
		boolean showId = sharedPref.getBoolean(MainActivity.PREFERENCE_SHOW_ID, false);
		cbShowId.setChecked(showId);
		
		cbShowId.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				savePreference(MainActivity.PREFERENCE_SHOW_ID, isChecked);
				reloadApp();
			}

		});
		
		MainActivity.activityList.add(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getWindow().setTitle(getString(R.string.title_activity_settings));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	/* Called when the clear database button is clicked */
	public void clearData(View view) {
		

		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		mDbHelper.clear(db);
		showShortToast(getResources().getString(R.string.succeed_clear_data));
        MainActivity.needRefresh = true;

		// Closes database
		db.close();

		// Removes items from screen
//		clearScreen();
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
	
	public void toDefault(View view) {
		savePreference(MainActivity.PREFERENCE_LANGUAGE, MainActivity.PREFERENCE_LOCALE_DEFAULT);
		switchLanguage(Locale.getDefault());
	}
	
	public void toEnglish(View view) {
        savePreference(MainActivity.PREFERENCE_LANGUAGE, MainActivity.PREFERENCE_LOCALE_US);
		switchLanguage(new Locale("en"));
	}

	
	public void toChineseS(View view) {
		savePreference(MainActivity.PREFERENCE_LANGUAGE, MainActivity.PREFERENCE_LOCALE_CHINESE);
		switchLanguage(new Locale("zh", "CN"));
	}
	
	public void switchLanguage(Locale locale) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
        
        reloadApp();
    }

	private void reloadApp() {
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		
		Vector<Activity> activities = MainActivity.activityList;
		
		for(Activity activity : activities) {
			activity.finish();
		}
	}

	/* Save in SharedPreferences */
	private void savePreference(String key, int value) {
		SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
	}
	
	private void savePreference(String key,
			boolean value) {
		SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
	}
	
	private boolean isEmpty(String str) {
		return str == null || str.equals("");
	}
}
