package com.example.testapplication.FriendMgr;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.R;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private String TAG = "FriendAdapter";

    static private ArrayList<Friend> mFriendList = new ArrayList<>();

    public static ArrayList<Friend> getFriendList() {
        return mFriendList;
    }

    /* Define Interface to send position information on a click event*/
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public FriendAdapter(ArrayList<Friend> mFriendList) {
        mFriendList = mFriendList;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        protected TextView nodeIndex;
        protected TextView nodeID;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nodeIndex = (TextView) itemView.findViewById(R.id.node_index);
            this.nodeID = (TextView) itemView.findViewById(R.id.node_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
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

    public static void addFriendList(String srcID, Marker marker) {
        Friend friend = new Friend(srcID, marker);
        mFriendList.add(friend);
    }
}
