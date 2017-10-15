package com.weetbix.rabobank.branches.generate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weetbix.rabobank.R;

import java.util.List;


public class BranchListAdapter extends RecyclerView.Adapter<BranchListAdapter.branch_holder> {

    public BranchListAdapter(List<Branch> branches) {
        this.branches = branches;
    }

    private List<Branch> branches;

    class branch_holder extends RecyclerView.ViewHolder {

        public TextView initialHolder;
        public TextView branchName;
        public ImageView continueButton;
        public TextView countryName;

        public branch_holder(View itemView) {
            super(itemView);
            initialHolder = (TextView) itemView.findViewById(R.id.initialHolder);
            branchName = (TextView) itemView.findViewById(R.id.branchName);
            continueButton = (ImageView) itemView.findViewById(R.id.continueButton);
            countryName = (TextView) itemView.findViewById(R.id.country);
        }
    }


    public void clearData() {
        int size = branches.size();
        branches.clear();
        notifyItemRangeRemoved(0, size);
    }


    public void setData(List<Branch> branches) {
        this.branches = branches;
    }


    public List<Branch> getDataset() {
        return branches;
    }

    @Override
    public branch_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.branch_list_element, parent, false);

        return new branch_holder(v);
    }

    @Override
    public void onBindViewHolder(branch_holder holder, int position) {
        Branch branch = branches.get(position);
        holder.initialHolder.setText(new String(new char[]{branch.name.charAt(0)}));
        holder.branchName.setText(branch.name);
        holder.countryName.setText(branch.country);
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

}
