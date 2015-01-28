package com.productivity.cloudtaskdo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.UnknownFormatConversionException;

/**
 * Created by JuanCarlos on 22/01/2015.
 */
public class TaskProvider extends ContentProvider {

    private TaskDbHelper mTaskDbHelper;
    private final static UriMatcher mUriMatcher = buildUriMatcher();

    private static final int TASK = 100;
    private static final int TASK_WITH_NOTIFICATIONS = 101;

    private static final int NOTIFICATION = 200;
    private static final int NOTIFICATION_BY_TASK = 201;

    private static final int HISTORY = 300;

    private static SQLiteQueryBuilder getNotificationByTaskQueryBuilder() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TaskContract.NotificationEntry.TABLE_NAME + " INNER JOIN " +
                TaskContract.TaskEntry.TABLE_NAME +
                " ON " + TaskContract.NotificationEntry.TABLE_NAME + "." + TaskContract.NotificationEntry.COLUMN_TASK_KEY + "=" +
                TaskContract.TaskEntry.TABLE_NAME + "." + TaskContract.TaskEntry._ID);

        return queryBuilder;
    }

    private static final String sNotificationByTaskSelection =
            TaskContract.NotificationEntry.TABLE_NAME +
                    "." + TaskContract.NotificationEntry.COLUMN_TASK_KEY + " = ? ";

    private Cursor getNotificationsByTask(Uri uri, String[] projection, String sortOrder) {
        int taskId = TaskContract.NotificationEntry.getTaskFromUri(uri);
        if (taskId == 0) {
            throw new UnknownFormatConversionException("Unknown uri: " + uri);
        } else {
            String[] selectionArgs;
            String selection = sNotificationByTaskSelection;
            selectionArgs = new String[]{String.valueOf(taskId)};

            return getNotificationByTaskQueryBuilder().query(mTaskDbHelper.getReadableDatabase(),
                    projection, selection, selectionArgs, null, null, sortOrder);

        }
    }

    @Override
    public boolean onCreate() {
        mTaskDbHelper = new TaskDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (mUriMatcher.match(uri)) {

            case TASK:
                retCursor = mTaskDbHelper.getReadableDatabase().query(
                        TaskContract.TaskEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case NOTIFICATION:
                retCursor = mTaskDbHelper.getReadableDatabase().query(TaskContract.NotificationEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case NOTIFICATION_BY_TASK:
                retCursor = getNotificationsByTask(uri, projection, sortOrder);
                break;

            case HISTORY:
                retCursor = mTaskDbHelper.getReadableDatabase().query(TaskContract.HistoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        //What kind of URI is it?
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case TASK:
                return TaskContract.TaskEntry.CONTENT_TYPE;

            case TASK_WITH_NOTIFICATIONS:
                return TaskContract.TaskEntry.CONTENT_TYPE;

            case NOTIFICATION:
                return TaskContract.NotificationEntry.CONTENT_TYPE;

            case NOTIFICATION_BY_TASK:
                return TaskContract.NotificationEntry.CONTENT_TYPE;

            case HISTORY:
                return TaskContract.HistoryEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;
        long id;

        switch (match) {
            case TASK:
                id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TaskContract.TaskEntry.buildTaskUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;

            case NOTIFICATION:
                id = db.insert(TaskContract.NotificationEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TaskContract.NotificationEntry.buildNotificationUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted; //rows affected
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case TASK:
                rowsDeleted = db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case HISTORY:
                rowsDeleted = db.delete(TaskContract.HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case NOTIFICATION:
                rowsDeleted = db.delete(TaskContract.NotificationEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case TASK:
                rowsUpdated = db.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {
        //All paths added to the UriMatcher must have a corresponding code to return
        //By default, it will return NO_MATCH
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASK, TASK);
        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASK + "/#", TASK_WITH_NOTIFICATIONS);

        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_NOTIFICATION, NOTIFICATION);
        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_NOTIFICATION + "/#", NOTIFICATION_BY_TASK);

        matcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_HISTORY, HISTORY);

        return matcher;
    }

}
