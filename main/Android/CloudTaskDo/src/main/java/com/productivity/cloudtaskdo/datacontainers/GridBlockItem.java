package com.productivity.cloudtaskdo.datacontainers;

import com.productivity.cloudtaskdo.R;

/**
 * Created by JuanCarlos on 13/01/2015.
 */
public class GridBlockItem {

    public static GridBlockItem[] ITEMS = new GridBlockItem[]{
            new GridBlockItem(R.string.textTileTitleExpired, R.string.textTileContentExpired),
            new GridBlockItem(R.string.textTileTitleToday, R.string.textTileContentToday),
            new GridBlockItem(R.string.textTileTitleFuture, R.string.textTileContentFuture),
            new GridBlockItem(R.string.textTileTitleAny, R.string.textTileContentAny)
    };

    public static GridBlockItem getItem(int id) {
        for (GridBlockItem item : ITEMS) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    private final int mTileTitle;
    private final int mTileContent;

    GridBlockItem(int title, int content) {
        mTileTitle = title;
        mTileContent = content;
    }

    public int getId() {
        return mTileTitle;
    }

    public int getTileTitle() {
        return mTileTitle;
    }

    public int getTileContent() {
        return mTileContent;
    }
}
