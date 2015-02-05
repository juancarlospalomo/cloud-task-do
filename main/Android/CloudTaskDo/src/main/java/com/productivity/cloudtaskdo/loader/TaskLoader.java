package com.productivity.cloudtaskdo.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import com.productivity.cloudtaskdo.cross.Dates;
import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.data.TaskContract.TaskEntry;

/**
 * Implements a custom cursor loader for Task URI.
 * Created by JuanCarlos on 05/02/2015.
 */
public class TaskLoader extends CursorLoader{

    /**
     * Projection columns for Content Provider
     */
    private static final String[] TASK_COLUMNS = {
            TaskEntry.TABLE_NAME + "." + TaskEntry._ID,
            TaskEntry.COLUMN_TASK_NAME,
            TaskEntry.COLUMN_TARGET_DATE_TIME
    };

    private Context mContext;  //Current Context
    private Cursor mCursor; //Cursor of TaskLoader
    private ForceLoadContentObserver mObserver; //Observer to be notified when a change occurs
    private TaskContract.TypeTask mTypeTask; //Refer to TaskContract.TypeTask


    /**
     * Constructor for TaskLoader
     * @param context execution context in which is running
     * @param typeTask TypeTask to load.  Refer to enum TaskContract.TypeTask
     */
    public TaskLoader(Context context, TaskContract.TypeTask typeTask) {
        super(context);
        mContext = context;
        mTypeTask = typeTask;
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Compose the selection for Content Provider
     * @return selection string
     */
    private String getSelectionTask() {
        String selection = "";

        if (mTypeTask.equals(TaskContract.TypeTask.Expired)) {
            selection = TaskEntry.COLUMN_TARGET_DATE_TIME + "<?";
        } else if(mTypeTask.equals(TaskContract.TypeTask.Today)) {
            selection = TaskEntry.COLUMN_TARGET_DATE_TIME + "=?";
        } else if(mTypeTask.equals(TaskContract.TypeTask.Future)) {
            selection = TaskEntry.COLUMN_TARGET_DATE_TIME + "=?";
        }

        return selection;
    }

    /**
     * Async cursor loader
     * @return Cursor loaded from ContentProvider
     */
    @Override
    public Cursor loadInBackground() {
        String selectionTasks = getSelectionTask();
        String[] selectionTasksArgs = null;
        String orderBy = TaskEntry.COLUMN_TARGET_DATE_TIME + "," +
                TaskEntry.COLUMN_TASK_NAME;

        if (!mTypeTask.equals(TaskContract.TypeTask.AnyTime)) {
            selectionTasksArgs = new String[] {Dates.getCurrentDateTime()};
        }

        mCursor = mContext.getContentResolver().query(TaskEntry.CONTENT_URI,
                TASK_COLUMNS, selectionTasks, selectionTasksArgs, orderBy);

        if (mCursor!=null) {
            //ensure the cursor is filled
            mCursor.getCount();
            //Register a observer to force a reload when the content changes
            mCursor.registerContentObserver(mObserver);
            //To be notified the loader when TaskEntry.CONTENT_URI changes.
            //It will be notified through the registered observer
            mCursor.setNotificationUri(mContext.getContentResolver(), TaskEntry.CONTENT_URI);
        }

        return mCursor;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it
     * @param cursor cursor to be delivered
     */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            if (cursor!=null) {
                ReleaseResources(cursor);
            }
            return;
        }
        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        Cursor oldCursor = mCursor;
        mCursor = cursor;
        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(cursor);
        }
        // Invalidate the old data as we don't need it any more
        if (oldCursor!=null && oldCursor!=cursor) {
            ReleaseResources(oldCursor);
        }
    }

    /**
     * Handle the request to start the loader
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    /**
     * Manage the request to stop the loader
     */
    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();
    }

    /**
     * Manages the request to reset the loader
     */
    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();
        // At this point we can release the resources.
        if (mCursor != null) {
            ReleaseResources(mCursor);
            mCursor = null;
        }
    }

    /**
     * Manage the request to cancel the loader
     * @param cursor
     */
    @Override
    public void onCanceled(Cursor cursor) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(mCursor);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        ReleaseResources(cursor);
    }

    /**
     * Releases the resources
     * @param cursor cursor to be released
     */
    private void ReleaseResources(Cursor cursor) {
        if (cursor!=null) {
            if (mObserver!=null) {
                mCursor.unregisterContentObserver(mObserver);
            }
            cursor.close();
        }
    }


}
