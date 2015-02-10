package com.productivity.cloudtaskdo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.fragments.TaskFragment;

public class TaskActivity extends ActionBarActivity {

    private final String LOG_TAG = TaskActivity.class.getSimpleName();

    public static final String EXTRA_TYPE_TASK = "type_task";

    //To track if the activity has two fragments or not
    private boolean mTwoPane;
    //Contains the task type to manage
    private TaskContract.TypeTask mTypeTask;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        loadExtras();
        //TODO: check if the layout is large screen (res/layout-sw600dp)
        //if this view is present, then the activity should be in two-pane mode
        TaskFragment taskFragment = (TaskFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_taskactivity);
        taskFragment.setTypeTask(mTypeTask);
    }


    /**
     * Load the extras, in private variables, supported by this activity
     */
    private void loadExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTypeTask = TaskContract.TypeTask.values()[bundle.getInt(EXTRA_TYPE_TASK)];
        }
    }

}
