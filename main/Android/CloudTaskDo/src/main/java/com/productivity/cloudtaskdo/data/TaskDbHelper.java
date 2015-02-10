package com.productivity.cloudtaskdo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.productivity.cloudtaskdo.data.TaskContract.HistoryEntry;
import com.productivity.cloudtaskdo.data.TaskContract.NotificationEntry;
import com.productivity.cloudtaskdo.data.TaskContract.TaskEntry;

/**
 * Created by JuanCarlos on 20/01/2015.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    //Scheme version of database. Each time the scheme is changed
    //the number has to be increased
    private static final int DATABASE_VERSION = 1;

    //Database file name
    public static final String DATABASE_NAME = "cloudtaskdo.db";


    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String getSQLTaskTable() {

        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL COLLATE NOCASE, " +
                TaskEntry.COLUMN_TARGET_DATE_TIME + " DATE, " +
                TaskEntry.COLUMN_SERVER_ID + " INTEGER, " +
                TaskEntry.COLUMN_SERVER_TIME_STAMP + " DATE, " +
                TaskEntry.COLUMN_OPERATION + " INTEGER); ";

        return SQL_CREATE_TASK_TABLE;
    }

    private static final String getSQLNotificationTable() {

        final String SQL_CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + NotificationEntry.TABLE_NAME + " (" +
                NotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotificationEntry.COLUMN_TASK_KEY + " INTEGER, " +
                NotificationEntry.COLUMN_DATE_TIME + " DATE, " +
                NotificationEntry.COLUMN_SERVER_ID + " INTEGER, " +
                NotificationEntry.COLUMN_SERVER_TIME_STAMP + " DATE, " +
                NotificationEntry.COLUMN_OPERATION + " INTEGER, " +
                " FOREIGN KEY (" + NotificationEntry.COLUMN_TASK_KEY + ") REFERENCES " +
                TaskEntry.TABLE_NAME + " (" + TaskEntry._ID + "));";

        return SQL_CREATE_NOTIFICATION_TABLE;
    }

    private static final String getSQLHistoryTable() {
        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HistoryEntry.COLUMN_TASK_NAME + " TEXT COLLATE NOCASE, " +
                HistoryEntry.COLUMN_FORECASTED_DATE_TIME + " DATE, " +
                HistoryEntry.COLUMN_ENDED_DATE_TIME + " DATE, " +
                HistoryEntry.COLUMN_SERVER_ID + " INTEGER, " +
                HistoryEntry.COLUMN_SERVER_TIME_STAMP + " DATE, " +
                HistoryEntry.COLUMN_OPERATION + " INTEGER); ";

        return SQL_CREATE_HISTORY_TABLE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getSQLTaskTable());
        db.execSQL(getSQLNotificationTable());
        db.execSQL(getSQLHistoryTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
