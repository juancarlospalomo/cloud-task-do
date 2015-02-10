package com.productivity.cloudtaskdo.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.productivity.cloudtaskdo.R;
import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.loader.TaskLoader;

/**
 * Created by JuanCarlos on 05/02/2015.
 */
public class TaskFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //For logging purpose
    private final static String LOG_TAG = TaskFragment.class.getSimpleName();

    //LoaderId for task loader
    private static final int TASK_LOADER_ID = 1;
    //Type task to be loaded in the fragment
    private TaskContract.TypeTask mTypeTask;
    private RecyclerView mTaskRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskAdapter mAdapter;

    /**
     *  Creates and returns the view hierarchy associated with the fragment
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    /**
     * Tells the fragment that its activity has completed its own Activity.onCreated()
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        //Get arguments
        mTaskRecyclerView = (RecyclerView) view.findViewById(R.id.task_list_view);
        //Change in content will not change the layout size of the recycler view
        //Of this way, we improve the performance
        mTaskRecyclerView.setHasFixedSize(true);
        //It will use a LinearLayout
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTaskRecyclerView.setLayoutManager(mLayoutManager);
        //Init loader.  Data will be set to Adapter in onLoadFinished()
        getLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * Set the task type that fragment has to manage
     * @param typeTask type task
     */
    public void setTypeTask(TaskContract.TypeTask typeTask) {
        mTypeTask = typeTask;
    }

    /**
     * Triggered by Loader Manager after init the loaded when it doesn't exist
     * or when restartLoader is called
     * @param id Loader Id identifier
     * @param args arguments received
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new TaskLoader(getActivity(), mTypeTask);
    }

    /**
     * Called when Cursor Loader wants to deliver the result to a loader
     * @param loader Loader than supply the data
     * @param data data supplied
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == TASK_LOADER_ID) {
            if (data != null) {
                mAdapter = new TaskAdapter(data);
                mTaskRecyclerView.setAdapter(mAdapter);
                Log.v(LOG_TAG, "onLoadFinished cursor: " + String.valueOf(data.getCount()));
            }
        }
    }

    /**
     * Called when the loader is stopped
     * @param loader loader is stopped
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == TASK_LOADER_ID) {
            mAdapter.releaseResources();
            mTaskRecyclerView.setAdapter(null);
        }
    }

    /**
     * Adapter for task.  It use the recyclerview adapter
     */
    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

        //Dataset for the Adapter
        private Cursor mCursor;

        /**
         * View Holder pattern
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView mAvatarView;
            private TextView mTextPrimaryText;
            private TextView mTextSecondaryText;
            private ImageView mIconView;

            public ViewHolder(View itemView) {
                super(itemView);
                mAvatarView = (ImageView) itemView.findViewById(R.id.image_primary_action_avatar);
                mTextPrimaryText = (TextView) itemView.findViewById(R.id.textview_primary_text);
                mTextSecondaryText = (TextView) itemView.findViewById(R.id.textview_secondary_text);
                mIconView = (ImageView) itemView.findViewById(R.id.image_secondary_action_icon);
            }
        }

        /**
         * Constructor
         * @param mCursor
         */
        public TaskAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        /**
         * Create a new ViewHolder when it is required from LayoutManager
         *
         * @param parent   RecyclerView viewgroup
         * @param viewType
         * @return ViewHolder
         */
        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Create a new list item inflating the layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        /**
         * Replace the contents of a list item when it is called from the LayoutManager
         *
         * @param holder:   ViewHolder containing the views
         * @param position: position from the Dataset to be showed
         */
        @Override
        public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
            //TODO: Replace the constant for the value fetched from the cursor
            final boolean taskEnded = false;
            //TODO: Replace the constant for the value fetched from the cursor
            final boolean hasNotifications = false;
            if (hasNotifications) {
                holder.mAvatarView.setImageResource(R.drawable.ic_alarm_on);
            } else {
                holder.mAvatarView.setImageResource(R.drawable.ic_alarm_off);
            }
            if (taskEnded) {
                holder.mIconView.setImageResource(R.drawable.ic_check_on);
            } else {
                holder.mIconView.setImageResource(R.drawable.ic_check_off);
            }
            //Place the cursor position in the position required by the ViewAdapter
            mCursor.moveToPosition(position);
            holder.mTextPrimaryText.setText(mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME)));
            holder.mTextSecondaryText.setText(mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TARGET_DATE_TIME)));
        }

        /**
         * Called to know the total number of items in the data set hold by the adapter
         * @return the total number of items
         */
        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        /**
         * Release the resources held by the adapter
          */
        public void releaseResources() {
            if (mCursor != null)
                mCursor.close();
        }
    }

}
