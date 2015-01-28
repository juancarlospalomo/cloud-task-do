package com.productivity.cloudtaskdo.tests;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.productivity.cloudtaskdo.data.TaskContract;

/**
 * Created by JuanCarlos on 27/01/2015.
 */
public class testProvider extends AndroidTestCase {

    public static final String LOG_TAG = testProvider.class.getSimpleName();

    //On start this test class, we do the configuration
    public void setUp() {
        deleteAllRecords();
    }


    //Bring our database to an empty state
    public void deleteAllRecords() {

        //Delete all records from notification
        mContext.getContentResolver().delete(TaskContract.NotificationEntry.CONTENT_URI, null, null);
        //Delete all records from Task
        mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, null, null);
        //Delete all records from History
        mContext.getContentResolver().delete(TaskContract.HistoryEntry.CONTENT_URI, null, null);

        //Have been all notifications deleted?
        Cursor cursor = mContext.getContentResolver().query(TaskContract.NotificationEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        //Have been all tasks deleted?
        cursor = mContext.getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();

        //Have been all history tasks deleted?
        cursor = mContext.getContentResolver().query(TaskContract.HistoryEntry.CONTENT_URI,
                null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testValues = testDb.createTaskBasicValues();

        Uri taskUri = mContext.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI,
                testValues);
        long taskId = ContentUris.parseId(taskUri);
        //verify if we got a row back
        assertTrue(taskId != -1);

        //Data's inserted. We verify the data are right.
        Cursor cursor = mContext.getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                null, //returns all columns
                null, //no where clause
                null, //no values for where clause
                null // sort order
        );

        testDb.validateCursor(cursor, testValues);
        cursor.close();

        //We're going to create a notification for the previously created task

        //First, create the values for the task id
        testValues = testDb.createNotificationBasicValues(taskId);
        Uri notificationUri = mContext.getContentResolver().insert(TaskContract.NotificationEntry.CONTENT_URI,
                testValues);
        long notificationId = ContentUris.parseId(notificationUri);
        assertTrue(notificationId != -1); //has it been inserted?

        //Check if the values have been inserted correctly
        cursor = mContext.getContentResolver().query(TaskContract.NotificationEntry.buildNotificationWithTaskIdUri(taskId),
                null, null, null, null);
        //Has the row the same values?
        testDb.validateCursor(cursor, testValues);
        cursor.close();

    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(TaskContract.TaskEntry.CONTENT_URI);
        assertEquals(TaskContract.TaskEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TaskContract.TaskEntry.buildTaskUri(1));
        assertEquals(TaskContract.TaskEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TaskContract.NotificationEntry.CONTENT_URI);
        assertEquals(TaskContract.NotificationEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TaskContract.NotificationEntry.buildNotificationWithTaskIdUri(1));
        assertEquals(TaskContract.NotificationEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TaskContract.HistoryEntry.CONTENT_URI);
        assertEquals(TaskContract.HistoryEntry.CONTENT_TYPE, type);

    }


}
