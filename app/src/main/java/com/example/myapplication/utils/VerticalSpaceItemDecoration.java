package com.example.myapplication.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // Apply the space to the bottom of the current item's view
        outRect.bottom = verticalSpaceHeight;

        // Optionally, you can skip the last item if needed:
        // if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
        //     outRect.bottom = 0;
        // }
    }
}