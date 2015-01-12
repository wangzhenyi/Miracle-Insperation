/**
 * Created by Zhenyi Wang.
 * Last modified on 12 Jan 2015.
 */

package com.wangzhenyi.miracleinsperation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * This class is used by the MiracleInsperation App to display the settings view
 */
public class SettingsActivity extends ActionBarActivity {
    private MiracleInsperationDbHelper mDbHelper;
    private Bundle mSavedInstanceState;
    private final static String APP_FOLDER_NAME = "MiracleInsperation";
    private int mOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialises fields
        mDbHelper = new MiracleInsperationDbHelper(this);
        mSavedInstanceState = savedInstanceState;
        mOption = 0;

        // Option showId
        CheckBox cbShowId = (CheckBox) findViewById(R.id.settings_cb_show_id);

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT, Context.MODE_PRIVATE);
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
    protected void onResume() {
        super.onResume();
        getWindow().setTitle(getString(R.string.title_activity_settings));
    }

    /* Called when the clear database button is clicked */
    public void clearData(View view) {

        // Gets the data repository and resets it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.clear(db);

        // Shows a short toast
        showShortToast(getResources().getString(R.string.succeed_clear_data));

        // Notifies the MainActivity to refresh
        MainActivity.needRefresh = true;

        // Closes database
        db.close();
    }

    /* Changes the display language to system default */
    public void toDefault(View view) {
        savePreference(MainActivity.PREFERENCE_LANGUAGE, MainActivity.PREFERENCE_LOCALE_DEFAULT);
        switchLanguage(Locale.getDefault());
    }

    /* Displays a short toast, does nothing if string is empty */
    private void showShortToast(String string) {
        if (isEmpty(string)) {
            return;
        }

        // Display a toast
        Toast toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
        toast.show();
    }

    /* Changes display language to English */
    public void toEnglish(View view) {
        savePreference(MainActivity.PREFERENCE_LANGUAGE, MainActivity.PREFERENCE_LOCALE_US);
        switchLanguage(new Locale("en"));
    }


    /* Changes display language to Chinese Simplified */
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        Vector<Activity> activities = MainActivity.activityList;

        for (Activity activity : activities) {
            activity.finish();
        }
    }

    /* Save in SharedPreferences */
    private void savePreference(String key, int value) {
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void savePreference(String key,
                                boolean value) {
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public void exportData(View view) {
        Vector<Insperation> insperations = mDbHelper.exportData();
        String exportString = DataFileHelper.convertDataToString(insperations);
        String path = getDefaultBackupFolderPath();
        String fileName = "data" + Util.formatDate(new Date(), "yyyyMMddHHmmss") + ".midata";

        String fullFileName = path + "/" + fileName;
//        showShortToast(fullFileName);

        try {
            DataFileHelper.writeStringToFile(path, fileName, exportString);
            String msg = getResources().getString(R.string.noti_export_succeed);
            msg = msg.replace("%s", fullFileName);
            showShortToast(msg);
        } catch (FileNotFoundException e) {
            String errmsg = getResources().getString(R.string.noti_export_file_not_found);
            showShortToast(errmsg);
        } catch (IOException e) {
            String errmsg = getResources().getString(R.string.noti_export_ioexception);
            showShortToast(errmsg);
        }


//        showShortToast(importedData.get(0).toString());
//        showShortToast(exportString);
    }

    private String getDefaultBackupFolderPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_FOLDER_NAME;
    }

    public void importData(View view) {
//        Vector<Insperation> insperations = mDbHelper.exportData();
//        long result = mDbHelper.importData(insperations);
//
//        showShortToast(result + " items affected");
//        reloadApp();

        // Retrieves the list of backup files
        final File[] backupFileList = getBackupFileList();

        // If there is no backup file
        if(backupFileList == null || backupFileList.length < 1) {
            showShortToast(getResources().getString(R.string.noti_no_backup_file)); //TODO show sd path in notification

        // If there is at least one file
        } else {

            // Creates a list of file names
            int length = backupFileList.length;
            String[] fileNames = new String[length];
            for(int i = 0; i < length; i++) {
                fileNames[i] = backupFileList[i].getName();
            }

            // Shows a dialogue with a list
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.dialog_choose_backup_file))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(fileNames, 0, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    mOption = which;
                                }
                            }
                    )
                    .setPositiveButton(getResources().getString(R.string.dialog_import),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    // Reads from file
                                    String data;
                                    Vector<Insperation> insperations;

                                    try {
                                        data = DataFileHelper.readFileToString(backupFileList[mOption]);
                                        insperations = DataFileHelper.convertStringToData(data);
                                    } catch (IOException e) {
                                        showShortToast(getString(R.string.noti_backup_empty_file));
                                        return;
                                    }

                                    // Fail if converted data is empty
                                    if (insperations == null || insperations.isEmpty()) {
                                        showShortToast(getString(R.string.noti_backup_empty_file));
                                        return;
                                    }

                                    // Clears database
                                    clearData(null);

                                    // Imports data
                                    long result = mDbHelper.importData(insperations);

                                    // Displays result
                                    String msg = getString(R.string.noti_backup_items_affected).replace("%d", result + "");
                                    showShortToast(msg);

                                    // Informs MainActivity to refresh
                                    MainActivity.needRefresh = true;
                                }
                            }
                    )
                    .setNegativeButton(getResources().getString(R.string.dialog_cancel), null)
                    .show();
        }

    }

    /** Returns a list of backup file names from the default backup folder
     *  null if there is no data
     * */
    private File[] getBackupFileList() {
        String path = getDefaultBackupFolderPath();

        File folder = new File(path);

        if(!folder.exists() || !folder.canRead()) {
            return null;
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".midata");
            }
        });

        // Reverses the array to make the latest first
        int size = files.length;
        File[] returnFiles = new File[files.length];
        for(int i = 0; i < size; i++) {
            returnFiles[i] = files[size - i - 1];
        }

        return returnFiles;
    }

//    public void debug(View view) {
//        String sdpath = Environment.getExternalStorageDirectory() + File.separator + "MItest" + File.separator;
//        File file = new File(sdpath);
//        if (file.exists()) {
//            boolean result = file.delete();
//            result = file.setExecutable(true,false);
//            result = file.setReadable(true,false);
//            result = file.mkdir();
//            result = file.setExecutable(true,false);
//            result = file.setReadable(true,false);
//            result = file.canExecute();
//            result = file.canRead();
//            result = file.canWrite();
//            boolean a = file.isHidden();
//            boolean b = false;
//        }
//    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
