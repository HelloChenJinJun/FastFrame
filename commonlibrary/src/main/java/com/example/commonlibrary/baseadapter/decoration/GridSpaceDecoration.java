package com.example.commonlibrary.baseadapter.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.commonlibrary.baseadapter.adapter.BaseRecyclerAdapter;

/**
 * 项目名称:    NewFastFrame
 * 创建人:      陈锦军
 * 创建时间:    2017/12/29     16:27
 * QQ:         1981367757
 */

public class GridSpaceDecoration extends RecyclerView.ItemDecoration {


    private int spanCount;
    private boolean includeEdge;
    private int horizontalSize;
    private int verticalSize;

    public GridSpaceDecoration(int spanCount, int spacing, boolean includeEdge) {
        this(spanCount, spacing, spacing, includeEdge);
    }


    public GridSpaceDecoration(int spanCount, int horizontalSize, int verticalSize, boolean includeEdge) {
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int count = ((BaseRecyclerAdapter) parent.getAdapter()).getHeaderViewCount();
        if (count > position) {
            return;
        }
        position -= count;
        int column = position % spanCount; // item column
        outRect.top = verticalSize;

        if (column == spanCount - 1) {
            if (includeEdge) {
                outRect.right = horizontalSize;
            }
            outRect.left = horizontalSize / 2;
        } else if (column == 0) {
            if (includeEdge) {
                outRect.left = horizontalSize;
            }
            outRect.right = horizontalSize / 2;
        } else {
            outRect.left = horizontalSize / 2;
            outRect.right = horizontalSize / 2;
        }
    }
}
