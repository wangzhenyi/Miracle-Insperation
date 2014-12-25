package com.wangzhenyi.miracleinsperation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wangzhenyi.miracleinsperation.MiracleInsperationContract.InsperatioinEntry;

import java.util.Date;
import java.util.Random;
import java.util.Vector;

/* Database helperfor the Miracle Insperation app */
public class MiracleInsperationDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MiracleInsperation.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InsperatioinEntry.TABLE_NAME + " (" +
                    InsperatioinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InsperatioinEntry.COLUMN_NAME_DATE + LONG_TYPE + COMMA_SEP +
                    InsperatioinEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    InsperatioinEntry.COLUMN_NAME_DELETED + INTEGER_TYPE + " DEFAULT 0" +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InsperatioinEntry.TABLE_NAME;

    private static final String[] samples = {
            "耶和华阿，尊大，能力，荣耀，强胜，威严都是你的。凡天上地下的都是你的。国度也是你的，并且你为至高，为万有之首。\n\n\t历代志上 29:11",
            "因为经上说，看哪，我把所拣选所宝贵的房角石，安放在锡安。信靠他的人，必不至于羞愧。\n\n\t彼得前书 2:6",
            "他叫我们能承当这新约的执事。不是凭着字句，乃是凭着精意。因为那字句是叫人死，精意是叫人活。（精意或作圣灵）。\n\n\t哥林多后书 3:6",
            "众先知也为他作见证，说，凡信他的人，必因他的名，得蒙赦罪。\n\n\t使徒行传 10:43",
            "他们说，当信主耶稣，你和你一家都必得救。\n\n\t使徒行传 16:31",
            "神说，要有光，就有了光。\n\n\t创世纪 1:3",
            "神说，我们要照着我们的形像，按着我们的样式造人，使他们管理海里的鱼，空中的鸟，地上的牲畜，和全地，并地上所爬的一切昆虫。\n\n\t创世纪 1:26",
            "亚伯兰信耶和华，耶和华就以此为他的义。\n\n\t创世纪 15:6",
            "你不要害怕，因为我与你同在。不要惊惶，因为我是你的神。我必坚固你，我必帮助你，我必用我公义的右手扶持你。\n\n\t以赛亚书 41:10",
            "凡接待他的，就是信他名的人，他就赐他们权柄，作神的儿女。\n\n\t约翰福音 1:12",
            "神爱世人，甚至将他的独生子赐给他们，叫一切信他的，不至灭亡，反得永生。\n\n\t约翰福音 3:16",
            "信他的人，不被定罪。不信的人，罪已经定了，因为他不信神独生子的名。\n\n\t约翰福音 3:18",
            "信子的人有永生。不信子的人得不着永生，（原文作不得见永生）神的震怒常在他身上。\n\n\t约翰福音 3:36",
            "耶稣说，我就是生命的粮。到我这里来的，必定不饿。信我的，永远不渴。\n\n\t约翰福音 6:35",
            "我实实在在地告诉你们，信的人有永生。\n\n\t约翰福音 6:47",
            "我到世上来，乃是光，叫凡信我的，不住在黑暗里。\n\n\t约翰福音 12:46",
            "耶稣对他说，你因看见了我才信。那没有看见就信的，有福了。\n\n\t约翰福音 20:29",
            "在至高之处荣耀归与神，在地上平安归与他所喜悦的人。（有古卷作喜悦归与人）。\n\n\t路加福音 2:14",
            "所以你们当立定心意，不要预先思想怎样分诉。\n\n\t路加福音 21:14",
            "耶稣对他说，你若能信，在信的人，凡事都能。\n\n\t马可福音 9:23",
            "求你使我清晨得听你慈爱之言，因我倚靠你。求你使我知道当行的路，因我的心仰望你。\n\n\t诗篇 143:8",
            "就如经上所记，我在锡安放一块绊脚的石头，跌人的磐石。信靠他的人必不至于羞愧。\n\n\t罗马书 9:33",
            "因为凡求告主名的，就必得救。\n\n\t罗马书 10:13"
    };

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
        String[] projection = {InsperatioinEntry._ID,
                InsperatioinEntry.COLUMN_NAME_DATE,
                InsperatioinEntry.COLUMN_NAME_CONTENT};

        // Sort order
        String sortOrder = InsperatioinEntry.COLUMN_NAME_DATE; // + " DESC";

        String whereColumns = InsperatioinEntry.COLUMN_NAME_DELETED + "=?";
        String[] whereValues = {"0"};

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
        int result = db.update(InsperatioinEntry.TABLE_NAME, values, whereClause, whereArgs);

        // Closes the database
        db.close();

        return result;
    }

    public void generateSamples() {
        Vector<Insperation> insperations = new Vector<Insperation>();

        for (int i = 0; i < 15; i++) {
            insperations.add(new Insperation(new Date(), getRandomString()));
        }

        for (Insperation insperation : insperations) {
            addInsperation(insperation);
        }
    }

    private String getRandomString() {
        int num = samples.length;
        int ran = new Random().nextInt(num);
        return samples[ran];
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
        Cursor cursor = db.rawQuery("select LAST_INSERT_ROWID() ", null);
        if (cursor.moveToFirst()) {
            int newId = cursor.getInt(0);
            insperation.setId(newId);
        }

        db.close();
    }

    /* Selects all the visible items from database */
    public Vector<Insperation> getDeletedInsperations() {
        Vector<Insperation> insperations = new Vector<Insperation>();

        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Defines a projection that specifies the queried columns from
        // the database
        String[] projection = {InsperatioinEntry._ID,
                InsperatioinEntry.COLUMN_NAME_DATE,
                InsperatioinEntry.COLUMN_NAME_CONTENT};

        // Sort order
        String sortOrder = InsperatioinEntry.COLUMN_NAME_DATE + " DESC";

        String whereColumns = InsperatioinEntry.COLUMN_NAME_DELETED + "=?";
        String[] whereValues = {"1"};

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


}
