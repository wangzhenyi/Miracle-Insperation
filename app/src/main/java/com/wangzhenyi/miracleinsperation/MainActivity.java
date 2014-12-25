package com.wangzhenyi.miracleinsperation;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wangzhenyi.miracleinsperation.MiracleInsperationContract.InsperatioinEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends Activity implements View.OnClickListener {
    private MiracleInsperationDbHelper mDbHelper;
    private EditText mInsperationEditText;
    private ListView mInsperationList;
    private ArrayList<HashMap<String, String>> mItemArrayList;
    private SimpleAdapter mAdapter;
    private final CharSequence LABEL_FOR_CLIP = "Miracle Insperation";
    public static final String PREFERENCE_LANGUAGE = "Preference language";
    public static final String PREFERENCE_SHOW_ID = "Preference show id";
    public static final String SHARED_PEREFENCE_DAFAULT = "default";
    public static final int PREFERENCE_LOCALE_DEFAULT = -1;
    public static final int PREFERENCE_LOCALE_US = 0;
    public static final int PREFERENCE_LOCALE_CHINESE = 1;
    public static Vector<Activity> activityList;
    public static boolean needRefresh = false;
    private static boolean mBoolShowId = false;
    private boolean mInCopyMode = false;
    private boolean mInDeleteMode = false;
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        mDbHelper = new MiracleInsperationDbHelper(this);
        // mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mInsperationEditText = (EditText) findViewById(R.id.edit_innovation);
        mInsperationList = (ListView) findViewById(R.id.innovation_list);
//mInsperationList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mInsperationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mInCopyMode) {
                    appendItemViewToClipboard(view);
                }

                if(mInDeleteMode) {
                    deleteItem((long)i,false);
                }
            }
        });

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT, Context.MODE_PRIVATE);
        mBoolShowId = sharedPref.getBoolean(PREFERENCE_SHOW_ID, false);


        mItemArrayList = new ArrayList<HashMap<String, String>>();

        mInsperationEditText.setOnClickListener(this);

        // Read items from database
        Vector<Insperation> insperations = mDbHelper.getInsperations();

        // Display items
        displayInsperations(insperations);

        activityList = new Vector<Activity>();
        activityList.add(this);

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

    private void appendItemViewToClipboard(View view) {
        Insperation insperation = getInsperationByItemView(view);
        appendClipboard(insperation.getFormatedDate("HH:mm") + "\n" + insperation.getContent());
    }

    private void appendClipboard(String s) {
        StringBuilder sb = new StringBuilder(getClipboardString());
        sb.append("\n");
        sb.append("\n");
        sb.append(s);
        setClipboard(sb.toString());
    }

    private Insperation getInsperationByItemView(View view) {
        String idStr = ((TextView) view.findViewById(R.id.insperation_id)).getText().toString();
        String dateLongStr = ((TextView) view.findViewById(R.id.insperation_date_long)).getText().toString();
        String content = ((TextView) view.findViewById(R.id.insperation_content)).getText().toString();

        Date date = new Date(Long.parseLong(dateLongStr));
        int id = Integer.parseInt(idStr);

        Insperation insperation = new Insperation(id, date, content);
        return insperation;
    }

    private void setLanguage() {
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.SHARED_PEREFENCE_DAFAULT, Context.MODE_PRIVATE);
        int language = sharedPref.getInt(PREFERENCE_LANGUAGE, PREFERENCE_LOCALE_DEFAULT);

        switch (language) {
            case PREFERENCE_LOCALE_US:
                switchLanguage(new Locale("en"));
                break;
            case PREFERENCE_LOCALE_CHINESE:
                switchLanguage(new Locale("zh", "CN"));
                break;
            default:
                break;
        }
    }

    public void switchLanguage(Locale locale) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }

    @Override
    protected void onResume() {
        super.onResume(); // This is important, otherwise app would not start
        getWindow().setTitle(getString(R.string.app_name));

        if(needRefresh) {
            refreshInsperations();
            needRefresh = false;
        }
    }

    private void refreshInsperations() {
        displayInsperations(mDbHelper.getInsperations());
    }

    /* Displays a list of insperations on screen */
    private void displayInsperations(Vector<Insperation> insperations) {
        mItemArrayList.clear();

        // Avoid null pointer
        if (insperations == null)
            insperations = new Vector<Insperation>();

        // Converts items to arraylist
        for (Insperation insperation : insperations) {
            HashMap<String, String> map = new HashMap<String, String>();
            if (mBoolShowId) {
                map.put(InsperatioinEntry.COLUMN_NAME_DATE,
                        insperation.getFormatedDate() + " " + insperation.getId());
            } else {
                map.put(InsperatioinEntry.COLUMN_NAME_DATE,
                        insperation.getFormatedDate());
            }
            map.put(InsperatioinEntry.COLUMN_NAME_CONTENT,
                    insperation.getContent());
            map.put(InsperatioinEntry._ID, insperation.getId() + "");
            map.put(InsperatioinEntry.DATE_LONG, insperation.getDate().getTime() + "");
            mItemArrayList.add(map);
        }
        // Builds adapter
        mAdapter = new SimpleAdapter(this, // Context
                mItemArrayList,// Data source
                R.layout.insperation_item,// id for ListItem

                // corresponding key values
                new String[]{InsperatioinEntry.COLUMN_NAME_DATE,
                        InsperatioinEntry.COLUMN_NAME_CONTENT,
                        InsperatioinEntry._ID,
                        InsperatioinEntry.DATE_LONG
                },

                // IDs for the corresponding TextViews
                new int[]{R.id.insperation_date, R.id.insperation_content,
                        R.id.insperation_id, R.id.insperation_date_long}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                return view;
            }
        };

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

    /* Defines the action to take for the option menu items */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_deleted_items:
                Intent intent = new Intent(this, DeletedItemsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                return true;
            case R.id.action_multi_copy:
                enterMultiCopyMode();
                return true;
            case R.id.action_multi_delete:
                enterMultiDeleteMode();
                return true;
            case R.id.action_generate_samples:
                generateSample();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void generateSample() {
        mDbHelper.generateSamples();
        refreshInsperations();
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
                copyItemByArrayId(info.id);
                return true;
            case R.id.longclick_remove:
                deleteItem(info.id,true);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void enterMultiCopyMode() {
        setClipboard("");
        Button bt = (Button) findViewById(R.id.button_end_multi_copy);
        bt.setVisibility(View.VISIBLE);
        mInCopyMode = true;
        getWindow().setTitle(getString(R.string.longclick_multi_copy));
    }

    public void endMultiCopy(View view) {
        Button bt = (Button) findViewById(R.id.button_end_multi_copy);
        bt.setVisibility(View.GONE);
        mInCopyMode = false;
        getWindow().setTitle(getString(R.string.app_name));
    }

    private void enterMultiDeleteMode() {
        Button bt = (Button) findViewById(R.id.button_end_multi_delete);
        bt.setVisibility(View.VISIBLE);
        mInDeleteMode = true;
        getWindow().setTitle(getString(R.string.action_multi_delete));
    }

    public void endMultiDelete(View view) {
        Button bt = (Button) findViewById(R.id.button_end_multi_delete);
        bt.setVisibility(View.GONE);
        mInDeleteMode = false;
        getWindow().setTitle(getString(R.string.app_name));
    }

    /* Removes an item both from database and from screen */
    private void deleteItem(long idInArrArr, boolean showToast) {
        // Gets the corresponding database id
        int index = (int) idInArrArr;
        String idString = mItemArrayList.get(index).get(InsperatioinEntry._ID);
        int idInDb = Integer.parseInt(idString);

        // Removes the item in database
        int result = mDbHelper.deleteInsperation(idInDb);

        // If success removes from screen and show message
        // with an Undo option
        if (result > 0) {
            int idInArrInt = (int) idInArrArr;
            mItemArrayList.remove(idInArrInt);
            mAdapter.notifyDataSetChanged();

            if(showToast)
                showShortToast(getResources().getString(
                    R.string.succeed_delete_database_item));
            // TODO Undo delete in toast
            // else show error message
        } else {
            if(showToast)
                showShortToast(getResources().getString(
                    R.string.error_delete_database_item));
        }

    }

    /* Copies the content of an item to clip board */
    private void copyItemByArrayId(long idArr) {
        // Retrieves the content
        int index = (int) idArr;
        Insperation insperation = getInsperationByIdInArray(index);

        String strToCopy = insperation.toStringToCopy();

        // Copies the content to clip board
        setClipboard(strToCopy);

        showShortToast(getResources().getString(R.string.succeed_copy_item));
    }

    private void setClipboard(String strToCopy) {
        clipboard.setPrimaryClip(ClipData.newPlainText(LABEL_FOR_CLIP,
                strToCopy));
    }

    private String getClipboardString() {
        // Nothing
        if (!clipboard.hasPrimaryClip()) {
            return "";
        }

        // TEXT
        if ((clipboard.getPrimaryClipDescription().hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN))) {
            ClipData cdText = clipboard.getPrimaryClip();
            ClipData.Item item = cdText.getItemAt(0);

            if (item.getText() == null) {
                return "";
            } else {
                return item.getText() + "";
            }
        }
        return "";
    }


//	/* Empties the list */
//	private void clearScreen() {
//		mItemArrayList.removeAll(mItemArrayList);
//		mAdapter.notifyDataSetChanged();
//	}

    private Insperation getInsperationByIdInArray(int index) {
        HashMap<String, String> itemHashMap = mItemArrayList.get(index);

        String idStr = mItemArrayList.get(index).get(InsperatioinEntry._ID);
        String dateLongStr = mItemArrayList.get(index).get(InsperatioinEntry.DATE_LONG);
        String content = itemHashMap.get(InsperatioinEntry.COLUMN_NAME_CONTENT);

        Date date = new Date(Long.parseLong(dateLongStr));
        int id = Integer.parseInt(idStr);

        Insperation insperation = new Insperation(id, date, content);
        return insperation;
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

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int language = sharedPref.getInt(PREFERENCE_LANGUAGE, PREFERENCE_LOCALE_DEFAULT);

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
