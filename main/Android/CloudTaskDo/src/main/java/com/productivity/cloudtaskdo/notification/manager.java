package com.productivity.cloudtaskdo.notification;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.productivity.cloudtaskdo.data.TaskContract;

/**
 * Created by JuanCarlos on 30/01/2015.
 */
public class manager {

    private Context mContext;

    public manager(Context context) {
        mContext = context;
    }

    public boolean set(int taskId, String datetime) {
        boolean result = false;
        ContentValues values = new ContentValues();
        values.put(TaskContract.NotificationEntry.COLUMN_TASK_KEY, taskId);
        values.put(TaskContract.NotificationEntry.COLUMN_DATE_TIME, datetime);
        Uri uri = mContext.getContentResolver().insert(TaskContract.NotificationEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);
        if (id > 0) {
            result = true;
        }
        return result;
    }

    public boolean unSet(int notificationId) {
        boolean result = false;
        int rowsDeleted = mContext.getContentResolver().delete(TaskContract.NotificationEntry.CONTENT_URI,
                TaskContract.NotificationEntry._ID + "=?",
                new String[]{String.valueOf(notificationId)});

        if (rowsDeleted > 0) result = true;
        return result;
    }

}
