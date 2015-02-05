package com.productivity.cloudtaskdo.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JuanCarlos on 20/01/2015.
 */
public class TaskContract {

    //Content authority is a name for the entire content provider. A good choice
    //to use as this string is the package name of the app, which is unique on
    //the device
    public static final String CONTENT_AUTHORITY = "com.productivity.cloudtaskdo";

    //Base for all URI´s in the app
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Paths to be added to the base content URI for setting the URI´s
    //For instance, content://com.productivity.cloudtaskdo/task will be a valid path
    public static final String PATH_TASK = "task";
    public static final String PATH_NOTIFICATION = "notification";
    public static final String PATH_HISTORY = "history";

    public enum TypeTask {
        Expired(1),
        Today(2),
        Future(3),
        AnyTime(4);

        private int mValue = 0;
        private TypeTask(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }


    public interface BaseEntry extends BaseColumns {

        //Server id
        public static final String COLUMN_SERVER_ID = "server_id";

        //Last timestamp of the row in server
        public static final String COLUMN_SERVER_TIME_STAMP = "server_timestamp";

        // Operation done to the row in the local data since row was synchronized with server.
        // It can be created, updated, deleted or none
        public static final String COLUMN_OPERATION = "operation";
    }

    /* inner class that defines the table contents of the Task table */
    public static final class TaskEntry implements BaseEntry {
        //content URI for Task Entities
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASK).build();

        //MIME format for Android cursor
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                "vnd." + CONTENT_AUTHORITY + "." + PATH_TASK;

        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" +
                "vnd." + CONTENT_AUTHORITY + "." + PATH_TASK;

        //Table name
        public static final String TABLE_NAME = "task";

        //Task name
        public static final String COLUMN_TASK_NAME = "task_name";

        //Date & time for the task
        public static final String COLUMN_TARGET_DATE_TIME = "target_datetime";

        public static Uri buildTaskUri(long taskId) {
            return ContentUris.withAppendedId(CONTENT_URI, taskId);
        }

    }

    /* inner class that defines the table contents of the Notification table */
    public static final class NotificationEntry implements BaseEntry {
        //Content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTIFICATION).build();
        //Mime Types
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                "vnd." + CONTENT_AUTHORITY + "." + PATH_NOTIFICATION;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" +
                "vnd." + CONTENT_AUTHORITY + "." + PATH_NOTIFICATION;

        //Table name
        public static final String TABLE_NAME = "notification";

        //Date and time when the notification will be triggered
        public static final String COLUMN_DATE_TIME = "notification_datetime";

        //Foreign key to task table
        public static final String COLUMN_TASK_KEY = "task_id";

        public static int getTaskFromUri(Uri uri) {
            int taskId = 0;
            try {
                taskId = Integer.parseInt(uri.getPathSegments().get(1));
            } catch (NumberFormatException e) {
                e.printStackTrace(); //TODO
            }
            return taskId;
        }

        public static Uri buildNotificationUri(long notificationId) {
            return ContentUris.withAppendedId(CONTENT_URI, notificationId);
        }

        public static Uri buildNotificationWithTaskIdUri(long taskId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(taskId)).build();
        }

    }

    /* inner class that defines the table contents of the completed tasks history */
    public static final class HistoryEntry implements BaseEntry {
        //Content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();
        //MIME types
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                "vnd." + CONTENT_AUTHORITY + "." + PATH_HISTORY;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" +
                "vnd." + CONTENT_AUTHORITY + "." + PATH_HISTORY;

        //Table name
        public static final String TABLE_NAME = "history";

        //Task name
        public static final String COLUMN_TASK_NAME = "task_name";

        //When the task was first forecasted to be carried out
        public static final String COLUMN_FORECASTED_DATE_TIME = "forecast_datetime";

        //When the task actually ended
        public static final String COLUMN_ENDED_DATE_TIME = "end_date_time";

    }

}
