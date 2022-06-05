package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * code written by TA dhrumil shah from the lab session on march 2nd.
 * this section is used for the Job Search functionality of scroll.
 */
public class WrapLinearLayoutManager extends LinearLayoutManager {


    public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("WrapLinearLayoutManager", "Index Out Of Bound Exception in RecyclerView");
        }
    }
}
