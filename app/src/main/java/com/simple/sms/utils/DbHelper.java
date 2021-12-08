package com.simple.sms.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simple.sms.model.LogTable;
import com.simple.sms.model.RuleTable;
import com.simple.sms.model.SenderTable;

import java.util.Arrays;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final String TAG = "DbHelper";
    public static final int DATABASE_VERSION = 2;
    //debug db
//    public static final String DATABASE_NAME = "/sdcard/transpondsms.db";
    public static final String DATABASE_NAME = "transpondsms.db";

    private static final List<String> SQL_CREATE_ENTRIES =
            Arrays.asList(
                    "CREATE TABLE " + LogTable.LogEntry.TABLE_NAME + " (" +
                            LogTable.LogEntry._ID + " INTEGER PRIMARY KEY," +
                            LogTable.LogEntry.COLUMN_NAME_FROM + " TEXT," +
                            LogTable.LogEntry.COLUMN_NAME_CONTENT + " TEXT," +
                            LogTable.LogEntry.COLUMN_NAME_RULE_ID + " INTEGER," +
                            LogTable.LogEntry.COLUMN_NAME_JSON_EXTRA + " TEXT," +
                            LogTable.LogEntry.COLUMN_NAME_TIME + "  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP)"
                    , "CREATE TABLE " + RuleTable.RuleEntry.TABLE_NAME + " (" +
                            RuleTable.RuleEntry._ID + " INTEGER PRIMARY KEY," +
                            RuleTable.RuleEntry.COLUMN_NAME_FILED + " TEXT," +
                            RuleTable.RuleEntry.COLUMN_NAME_CHECK + " TEXT," +
                            RuleTable.RuleEntry.COLUMN_NAME_VALUE + " TEXT," +
                            RuleTable.RuleEntry.COLUMN_NAME_SENDER_ID + " INTEGER," +
                            RuleTable.RuleEntry.COLUMN_NAME_IS_CHOOSE + " INTEGER," +
                            RuleTable.RuleEntry.COLUMN_NAME_TIME + "  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP)"
                    , "CREATE TABLE " + SenderTable.SenderEntry.TABLE_NAME + " (" +
                            SenderTable.SenderEntry._ID + " INTEGER PRIMARY KEY," +
                            SenderTable.SenderEntry.COLUMN_NAME_NAME + " TEXT," +
                            SenderTable.SenderEntry.COLUMN_NAME_STATUS + " INTEGER," +
                            SenderTable.SenderEntry.COLUMN_NAME_TYPE + " INTEGER," +
                            SenderTable.SenderEntry.COLUMN_NAME_JSON_SETTING + " TEXT," +
                            SenderTable.SenderEntry.COLUMN_NAME_TIME + "  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP)"

            );

    private static final List<String> SQL_DELETE_ENTRIES =
            Arrays.asList(
                    "DROP TABLE IF EXISTS " + LogTable.LogEntry.TABLE_NAME + " ; "
                    , "DROP TABLE IF EXISTS " + RuleTable.RuleEntry.TABLE_NAME + " ; "
                    , "DROP TABLE IF EXISTS " + SenderTable.SenderEntry.TABLE_NAME + " ; "

            );


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        for (String createEntries : SQL_CREATE_ENTRIES
        ) {
            Log.d(TAG, "onCreate:createEntries "+createEntries);
            db.execSQL(createEntries);
        }
    }

    public void delCreateTable(SQLiteDatabase db) {
        for (String delCreateEntries : SQL_DELETE_ENTRIES
        ) {
            Log.d(TAG, "delCreateTable:delCreateEntries "+delCreateEntries);
            db.execSQL(delCreateEntries);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        delCreateTable(db);
//        onCreate(db);
        //dbv2之前的全部加字段
        if (oldVersion < 2) {
            String sql = "Alter table " + LogTable.LogEntry.TABLE_NAME + " add column " + LogTable.LogEntry.COLUMN_NAME_JSON_EXTRA + " TEXT ";
            db.execSQL(sql);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
