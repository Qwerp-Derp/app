package com.weetbix.rabobank.branches.generate;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BranchMarginDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public BranchMarginDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = this.mVerticalSpaceHeight;
        }
    }

}
