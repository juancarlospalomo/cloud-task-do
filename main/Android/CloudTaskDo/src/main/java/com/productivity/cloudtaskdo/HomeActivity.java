package com.productivity.cloudtaskdo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.productivity.cloudtaskdo.data.TaskContract;
import com.productivity.cloudtaskdo.datacontainers.GridBlockItem;
import com.productivity.cloudtaskdo.views.TileTitle;

public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.taskToolBar);
        setSupportActionBar(toolbar);

        GridView gridViewBlocks = (GridView) findViewById(R.id.gridTaskPeriods);
        GridTaskBlocksAdapter adapter = new GridTaskBlocksAdapter();
        gridViewBlocks.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ViewHolder {
        TileTitle tileTitleView;
        TextView tileContentView;
    }

    private class GridTaskBlocksAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return GridBlockItem.ITEMS.length;
        }

        @Override
        public GridBlockItem getItem(int position) {
            return GridBlockItem.ITEMS[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tileTitleView = (TileTitle) convertView.findViewById(R.id.tileTileId);
                viewHolder.tileContentView = (TextView) convertView.findViewById(R.id.textViewTileContentId);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final GridBlockItem item = getItem(position);

            viewHolder.tileTitleView.setText(getResources().getString(item.getTileTitle()));
            viewHolder.tileTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, TaskActivity.class);
                    intent.putExtra(TaskActivity.EXTRA_TYPE_TASK, TaskContract.TypeTask.Future.getValue());
                    startActivity(intent);
                }
            });
            viewHolder.tileContentView.setText(getResources().getString(item.getTileContent()));

            return convertView;
        }
    }
}
