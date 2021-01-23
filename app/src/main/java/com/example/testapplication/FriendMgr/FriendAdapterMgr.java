package com.example.testapplication.FriendMgr;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.MapFragment;
import com.example.testapplication.R;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendAdapterMgr extends RecyclerView.Adapter<FriendAdapterMgr.FriendViewHolder> {
    private final String TAG = "FriendAdapterMgr Class";
    private ArrayList<Friend> mListFriend;
    static private HashMap<String, Friend> mMapFriend;

    private FriendAdapterMgr() {
        mListFriend = new ArrayList<>();
        mMapFriend = new HashMap<>();
    }

    public static FriendAdapterMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final FriendAdapterMgr INSTANCE = new FriendAdapterMgr();
    }

    public ArrayList<Friend> getListFriend() {
        return mListFriend;
    }

    public HashMap<String, Friend> getMapFriend() {
        return mMapFriend;
    }

    /* Define Interface to send position information on a click event*/
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
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
        Log.v(TAG, "[onCreateViewHolder]");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node_list_resource, parent, false);
        FriendViewHolder viewHolder = new FriendViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Log.v(TAG, "[onBindViewHolder]");

        holder.nodeIndex.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        holder.nodeID.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        holder.nodeIndex.setGravity(Gravity.LEFT);
        holder.nodeID.setGravity(Gravity.LEFT);

        String indexString = "[" + position + "] ";
        holder.nodeIndex.setText(indexString);
        holder.nodeID.setText(mListFriend.get(position).getFriendID());

    }

    @Override
    public int getItemCount() {
        return (null != mListFriend ? mListFriend.size() : 0);
    }

    public void addFriendList(String srcID, Marker marker) {
        if (isContainFriend(srcID)) {
            Log.v(TAG, "[addFriendList] the Map is already have the item");
            return;
        }
        Log.v(TAG, "[addFriendList] add Friend in Friend list : [" + srcID + "]");
        Friend friend = new Friend(srcID, marker);
        mListFriend.add(friend);
        mMapFriend.put(srcID, friend);
    }

    public boolean loadFriendList() {
        Log.d(TAG, "load Friend list into map, size : " + mListFriend.size());
        if (!mMapFriend.isEmpty()) {
            for (Friend friend : mListFriend) {
                MapFragment.setMarker(friend.getLatLng(), "Friend", friend.getFriendID());
            }
            return true;
        } else {
            return false;
        }
    }

    public void clearFriendList(String srcID, Marker marker) {
        /* clear FriendList*/
    }

    public boolean isContainFriend(String srcID) {
        return mMapFriend.get(srcID) != null;
    }

}
