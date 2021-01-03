package com.example.projectc.friendsMgr;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectc.R;

import java.util.ArrayList;

public class FriendAdaptor extends RecyclerView.Adapter<FriendAdaptor.FriendViewHolder> {
    private ArrayList<Friend> mFriendList;

    public FriendAdaptor(ArrayList<Friend> mFriendList) {
        this.mFriendList = mFriendList;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{
        protected TextView nodeIndex;
        protected TextView nodeID;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nodeIndex = (TextView) itemView.findViewById(R.id.node_index);
            this.nodeID = (TextView) itemView.findViewById(R.id.node_id);
        }
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node_list_resource, parent, false);

        FriendViewHolder viewHolder = new FriendViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.nodeIndex.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        holder.nodeID.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        holder.nodeIndex.setGravity(Gravity.LEFT);
        holder.nodeID.setGravity(Gravity.LEFT);

        String indexString = "[" + position + "] ";
        holder.nodeIndex.setText(indexString);
        holder.nodeID.setText(mFriendList.get(position).getFriendID());

    }

    @Override
    public int getItemCount() {
        return (null != mFriendList ? mFriendList.size() : 0);
    }



}
