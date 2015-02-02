package com.productivity.cloudtaskdo.tests;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.text.TextUtils;

import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.notification.Manager;
import com.productivity.cloudtaskdo.receiver.AlarmReceiver;

/**
 * Created by JuanCarlos on 02/02/2015.
 */
public class testNotification extends AndroidTestCase {

    private static final String LOG_TAG = testNotification.class.getSimpleName();
    private final static String NOTIFICATION_ID = "notification_id";

    public void setUp() {
        deleteAllNotifications();
        deleteAllTasks();
    }

    private void deleteAllTasks() {
        mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, null, null);
    }

    private boolean deleteAllNotifications() {
        boolean result = true;
        //First, we get all existing notifications in database to cancel the alarms
        Cursor cursor = mContext.getContentResolver().query(TaskContract.NotificationEntry.CONTENT_URI,
                null, null, null, null);
        if ((cursor!=null) && (cursor.moveToFirst())) {
            int notificationId = cursor.getInt(cursor.getColumnIndex(TaskContract.NotificationEntry._ID));
            String date = cursor.getString(cursor.getColumnIndex(TaskContract.NotificationEntry.COLUMN_DATE_TIME));
            if (TextUtils.isEmpty(date)) {
                fail(String.format("date for %d is empty", notificationId));
                result = false;
            } else {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                intent.putExtra(NOTIFICATION_ID, notificationId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                        notificationId,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
            mContext.getContentResolver().delete(TaskContract.NotificationEntry.CONTENT_URI,
                    TaskContract.NotificationEntry._ID + "=?", new String[] {String.valueOf(notificationId)});
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public void testCreateNotification() {
        String[] taskNotificationDates = {"2015-03-22 10:00"};
        ContentValues values = testDb.createTaskCompleteValues();
        Uri uri = mContext.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
        long taskId = ContentUris.parseId(uri);

        Manager notificationManager = new Manager(mContext);
        for(int index=0; index<taskNotificationDates.length; index++) {
            notificationManager.set((int) taskId, taskNotificationDates[index]);
        }
        //Now, we check if the record was saved in database and the alarm was created
        Cursor cursor = mContext.getContentResolver().query(TaskContract.NotificationEntry.CONTENT_URI,
                null, TaskContract.NotificationEntry.COLUMN_TASK_KEY + "=?",
                new String[] {String.valueOf(taskId)}, null);

        //Check if the records count is equal to the taskNotificationDates array length
        if (cursor!=null) {
            assertEquals(taskNotificationDates.length, cursor.getCount());

            //Now check if date are the same
            cursor.moveToFirst();
            int index = 0;
            while(!cursor.isAfterLast()) {
                assertEquals(taskNotificationDates[index], cursor.getString(cursor.getColumnIndex(TaskContract.NotificationEntry.COLUMN_DATE_TIME)));
                //Is alarm set up?
                assertEquals(true,
                        isAlarmSet(cursor.getInt(cursor.getColumnIndex(TaskContract.NotificationEntry._ID)),
                                taskNotificationDates[index]));
                cursor.moveToNext();
                index++;
            }
        }
    }

    public void testSendNotification() {
        Manager notificationManager = new Manager(mContext);
        notificationManager.send();
    }

    private boolean isAlarmSet(int notificationId, String date) {
        boolean alarmUp = (PendingIntent.getBroadcast(mContext, notificationId,
                new Intent(mContext, AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        return alarmUp;
    }
}
