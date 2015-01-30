package com.productivity.cloudtaskdo.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;

import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.notification.Manager;

/**
 * Created by JuanCarlos on 30/01/2015.
 */
public class BootService extends IntentService {

    //Service name for the worker thread.
    private final static String SERVICE_NAME = BootService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BootService() {
        super(SERVICE_NAME);
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * name SERVICE_NAME with the intent that started the service.  When this
     * method returns, IntentService tops the service itself, so it hasn't to
     * call to stopItSelf()
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        restoreAlarms();
    }

    /**
     * Create alarms again if notifications exist as they are lost
     * after reboot the device
     */
    protected void restoreAlarms() {
        Cursor cursor = this.getContentResolver().query(TaskContract.NotificationEntry.CONTENT_URI,
                null, null, null, null);
        //Once get the stored notifications, create the alarms
        if ((cursor!=null) && (cursor.moveToFirst())) {
            while (!cursor.isAfterLast()) {
                String dateTime = cursor.getString(cursor.getColumnIndex(TaskContract.NotificationEntry.COLUMN_DATE_TIME));
                int notificationId = cursor.getInt(cursor.getColumnIndex(TaskContract.NotificationEntry._ID));
                Manager.Alarm alarm = new Manager(this).new Alarm();
                if (!alarm.create(notificationId, dateTime)) {
                    //TODO: trace log
                }
                alarm = null;
                cursor.moveToNext();
            }
        }
        //Always close the cursor
        cursor.close();
    }
}
