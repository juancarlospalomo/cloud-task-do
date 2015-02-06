package com.productivity.cloudtaskdo.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    public static final String EXTRA_TYPE_TASK = "type_task";
    //LoaderId for task loader
    private static final int TASK_LOADER_ID = 1;
    //Type task to be loaded in the fragment
    private TaskContract.TypeTask mTypeTask;
    private RecyclerView mTaskRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
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

    private void loadExtras() {
        Bundle bundle = getArguments();
        if (bundle!=null) {
            mTypeTask = TaskContract.TypeTask.values()[bundle.getInt(EXTRA_TYPE_TASK)];
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new TaskLoader(getActivity(),mTypeTask);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId()==TASK_LOADER_ID) {
            if (data!=null) {
                mAdapter = new TaskAdapter(data);
                mTaskRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

        private Cursor mCursor;

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

        public TaskAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        /**
         * Create a new ViewHolder when it is required from LayoutManager
         *
         * @param parent   RecyclerView
         * @param viewType
         * @return
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
                holder.mAvatarView.setImageResource(R.drawable.ic_check_off);
            }
            if (taskEnded) {
                holder.mIconView.setImageResource(R.drawable.ic_check_on);
            } else {
                holder.mIconView.setImageResource(R.drawable.ic_check_off);
            }
            holder.mTextPrimaryText.setText(mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME)));
            holder.mTextSecondaryText.setText(mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TARGET_DATE_TIME)));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

}
