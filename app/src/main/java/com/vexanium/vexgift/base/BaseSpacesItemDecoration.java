package com.vexanium.vexgift.base;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by mac on 11/17/17.
 */

public class BaseSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public BaseSpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        if (parent.getAdapter().getItemId(position) == -1) return;

        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.top = 0;
        outRect.bottom = 0;

        if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final int index = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();

            if (index % 2 == 0) {
                outRect.right = mSpace / 18 * 4;
            } else {
                outRect.left = mSpace / 18 * 4;
            }
        } else if (parent.getLayoutManager() instanceof GridLayoutManager) {
            if (parent.getAdapter().getItemId(position) % 2 == 0) {
                outRect.right = mSpace / 18 * 4;
            } else {
                outRect.left = mSpace / 18 * 4;
            }
        }
    }
}
