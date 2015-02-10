package com.productivity.cloudtaskdo.tests;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.productivity.cloudtaskdo.cross.Dates;
import com.productivity.cloudtaskdo.data.TaskContract.NotificationEntry;
import com.productivity.cloudtaskdo.data.TaskContract.TaskEntry;
import com.productivity.cloudtaskdo.data.TaskDbHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JuanCarlos on 20/01/2015.
 */
public class testDb extends AndroidTestCase {

    public void testShouldCreateDb() throws Throwable {
        mContext.deleteDatabase(TaskDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testShouldInsertBasicTask() throws Throwable {
        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
        ContentValues values = createTaskBasicValues();
        long rowId = db.insert(TaskEntry.TABLE_NAME, null, values);
        assertTrue(rowId != -1);
        db.close();
    }

    public void testShouldInsertCompleteTask() throws Throwable {
        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
        ContentValues values = createTaskCompleteValues();
        long rowId = db.insert(TaskEntry.TABLE_NAME, null, values);
        assertTrue(rowId != -1);
        db.close();
    }

    public void testShouldInsertNotifyBasic() throws Throwable {
        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
        ContentValues values = createNotificationBasicValues(1);
        long rowId = db.insert(NotificationEntry.TABLE_NAME, null, values);
        assertTrue(rowId != -1);
        db.close();
    }

    public void testShouldInsertNotifyComplete() throws Throwable {
        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
        ContentValues values = createNotificationCompleteValues(2);
        long rowId = db.insert(NotificationEntry.TABLE_NAME, null, values);
        assertTrue(rowId != -1);
        db.close();
    }

    //It fails because database has not enabled the foreignkey constraints
    //or constraints was not created
    @TargetApi(16)
    public void testShouldNotInsertNotify() throws Throwable {
        SQLiteDatabase db = new TaskDbHelper(mContext).getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        ContentValues values = createNotificationBasicValues(-1);
        long rowId = db.insert(NotificationEntry.TABLE_NAME, null, values);
        assertTrue(rowId == -1);
        db.close();
    }

    /**
     * Create a Task Collection
     *
     * @return created task list
     */
    static List<ContentValues> createTaskCollection() {
        List<ContentValues> valuesCollection = new ArrayList<ContentValues>();
        Date date = Dates.getDate(Dates.getCurrentDateTime());
        String dateValue = Dates.castToDatabaseFormat(date);
        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put(TaskEntry.COLUMN_TASK_NAME, "Task" + String.valueOf(i));
            values.put(TaskEntry.COLUMN_TARGET_DATE_TIME, dateValue);
            valuesCollection.add(values);
            date = Dates.addDays(date, i + 1);
            dateValue = Dates.castToDatabaseFormat(date);
        }
        return valuesCollection;
    }

    static ContentValues createTaskBasicValues() {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, "task 1");
        values.put(TaskEntry.COLUMN_TARGET_DATE_TIME, "2015-02-03 11:15");
        return values;
    }

    static ContentValues createTaskCompleteValues() {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, "task 2");
        values.put(TaskEntry.COLUMN_TARGET_DATE_TIME, "2015-02-04 12:15");
        values.put(TaskEntry.COLUMN_OPERATION, 1);
        values.put(TaskEntry.COLUMN_SERVER_ID, 3);
        values.put(TaskEntry.COLUMN_SERVER_TIME_STAMP, "2015-01-22 10:20");
        return values;
    }

    static ContentValues createNotificationBasicValues(long taskKey) {
        ContentValues values = new ContentValues();
        values.put(NotificationEntry.COLUMN_TASK_KEY, taskKey);
        values.put(NotificationEntry.COLUMN_DATE_TIME, "2015-03-10 09:05");
        return values;
    }

    static ContentValues createNotificationCompleteValues(long taskKey) {
        ContentValues values = new ContentValues();
        values.put(NotificationEntry.COLUMN_TASK_KEY, taskKey);
        values.put(NotificationEntry.COLUMN_DATE_TIME, "2015-03-10 09:05");
        values.put(NotificationEntry.COLUMN_OPERATION, 2);
        values.put(NotificationEntry.COLUMN_SERVER_ID, 10);
        values.put(NotificationEntry.COLUMN_SERVER_TIME_STAMP, "2015-01-22 10:20");
        return values;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    /**
     * check all rows from a cursor against an expected list of rows
     * @param valueCursor cursor with all rows
     * @param expectedValues expected rows to check
     */
    static void validateFullCursor(Cursor valueCursor, List<ContentValues> expectedValues) {
        assertTrue(valueCursor.moveToFirst());

        int index = 0;
        ContentValues contentValues = expectedValues.get(index);
        while (!valueCursor.isAfterLast()) {
            Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
            for(Map.Entry<String, Object> entry : valueSet) {
                String columnName = entry.getKey();
                int idx = valueCursor.getColumnIndex(columnName);
                assertFalse(idx == -1);
                String expectedValue = entry.getValue().toString();
                assertEquals(expectedValue, entry.getValue().toString());
            }
            valueCursor.moveToNext();
            index++;
            if (index<expectedValues.size()) {
                contentValues = expectedValues.get(index);
            }
        }
    }

}
