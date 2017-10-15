package com.weetbix.rabobank.branches.generate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


public class AllBranches {

    public static List<Branch> Branches = new ArrayList<Branch>();

    public static void LoadBranches(RecyclerView recycler, BranchListAdapter adapter) {
        // Set up recycler adapter
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(adapter);
        alphaAdapter.setDuration(300);


        // Convert to density pixels
        Context context = recycler.getContext();
        float density = context.getResources().getDisplayMetrics().density;

        recycler.setAdapter(alphaAdapter);
        // Convert dp to px
        recycler.addItemDecoration(new BranchMarginDecoration((int) (75 * density)));
    }

}
