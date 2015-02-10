package com.productivity.cloudtaskdo.tests;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.productivity.cloudtaskdo.R;
import com.productivity.cloudtaskdo.TaskActivity;
import com.productivity.cloudtaskdo.data.TaskContract;

import java.util.List;

/**
 * Created by JuanCarlos on 09/02/2015.
 */
public class testActivityTask extends ActivityInstrumentationTestCase2<TaskActivity> {

    private Activity mActivity;
    private Context mContext;

    public testActivityTask() {
        super(TaskActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        Intent intent = new Intent();
        intent.putExtra(TaskActivity.EXTRA_TYPE_TASK, TaskContract.TypeTask.Today.getValue());
        setActivityIntent(intent);
        mActivity = getActivity();
        mContext = getActivity().getBaseContext();
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

    public void testLoadTasksInRecyclerView() {
        deleteAllRecords();

        List<ContentValues> taskCollection = testDb.createTaskCollection();
        for (int index = 0; index < taskCollection.size(); index++) {
            mActivity.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI,
                    taskCollection.get(index));
        }

        Cursor cursor = mActivity.getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                null, null, null, null);

        testDb.validateFullCursor(cursor, taskCollection);
        cursor.close();

        RecyclerView view = (RecyclerView) mActivity.findViewById(R.id.task_list_view);
        assertTrue(view != null);

        assertTrue(view.getVisibility() == View.VISIBLE);

        Log.d(testActivityTask.class.getSimpleName(), String.valueOf(view.getAdapter().getItemCount()));

//        assertEquals(1, view.getAdapter().getItemCount());
    }


}
