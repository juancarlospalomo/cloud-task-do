package com.productivity.cloudtaskdo.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.productivity.cloudtaskdo.R;
import com.productivity.cloudtaskdo.cross.Dates;
import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.receiver.AlarmReceiver;

import java.util.Date;

/**
 * Created by JuanCarlos on 30/01/2015.
 * <p/>
 * Manage the notification operations for a task
 */
public class Manager {

    private static final int TASK_NOTIFICATION = 1;

    private Context mContext;

    public Manager(Context context) {
        mContext = context;
    }

    /**
     * Create a notification
     *
     * @param taskId   = task identifier
     * @param datetime = date & time for the notification
     * @return true if it has been set
     */
    public boolean set(int taskId, String datetime) {
        boolean result = false;
        ContentValues values = new ContentValues();
        values.put(TaskContract.NotificationEntry.COLUMN_TASK_KEY, taskId);
        values.put(TaskContract.NotificationEntry.COLUMN_DATE_TIME, datetime);
        Uri uri = mContext.getContentResolver().insert(TaskContract.NotificationEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);
        if (id > 0) {
            Alarm alarm = new Alarm();
            result = alarm.create((int)id, datetime);
        }
        return result;
    }

    /**
     * Remove a notification from a task
     *
     * @param notificationId
     * @return true if the notification could be removed
     */
    public boolean unSet(int notificationId, String dateTime) {
        boolean result = false;
        int rowsDeleted = mContext.getContentResolver().delete(TaskContract.NotificationEntry.CONTENT_URI,
                TaskContract.NotificationEntry._ID + "=?",
                new String[]{String.valueOf(notificationId)});

        if (rowsDeleted > 0) {
            Alarm alarm = new Alarm();
            alarm.remove(notificationId, dateTime);
            result = true;
        }
        return result;
    }

    //TODO: Build the notification correctly
    // Refer to: http://developer.android.com/guide/topics/ui/notifiers/notifications.html
    //           http://developer.android.com/design/patterns/notifications.html
    public void send() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("CloudTaskDo")
                .setContentText("Task A")
                .setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        // Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        // Moves the expanded layout object into the notification object.
        builder.setStyle(inboxStyle);
        // Issue the notification here.

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(TASK_NOTIFICATION, builder.build());
    }

    /**
     * Class to set and cancel the alarms
     */
    public final class Alarm {

        private final static String NOTIFICATION_ID = "notification_id";

        /**
         * Schedule an alarm to be broadcasted with PendingIntent to AlarmReceiver
         *
         * @param notificationId = to identify the intent
         * @param dateTime       = datetime to set the alarm.  It must be in database format
         * @return true if the alarm could be set
         */
        public boolean create(int notificationId, String dateTime) {
            boolean result = false;
            Date date = Dates.getDate(dateTime);
            if (date != null) {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                intent.putExtra(NOTIFICATION_ID, notificationId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                        notificationId,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
                result = true;
            }
            return result;
        }

        /**
         * Cancel an alarm for a notification
         *
         * @param notificationId = notification identifier
         * @param dateTime       = date & time of the existing notification
         */
        public void remove(int notificationId, String dateTime) {
            Date date = Dates.getDate(dateTime);
            if (date != null) {
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                intent.putExtra(NOTIFICATION_ID, notificationId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                        notificationId,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }
        }

    }
}
