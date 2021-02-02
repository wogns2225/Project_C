package com.example.testapplication.DisplayMgr;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.FriendMgr.Friend;
import com.example.testapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MsgBtnAdapterMgr extends RecyclerView.Adapter<MsgBtnAdapterMgr.MsgBtnViewHolder> {
    private final String TAG = "MsgBtnAdapterMgr Class";
    private ArrayList<MsgBtn> mListMsgBtn;
    static private HashMap<String, MsgBtn> mMapMsgBtn;

    private MsgBtnAdapterMgr() {
        mListMsgBtn = new ArrayList<>();
        mMapMsgBtn = new HashMap<>();
    }

    public static MsgBtnAdapterMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final MsgBtnAdapterMgr INSTANCE = new MsgBtnAdapterMgr();
    }

    public ArrayList<MsgBtn> getListMsgBtn() {
        return mListMsgBtn;
    }

    public HashMap<String, MsgBtn> getMapMsgBtn() {
        return mMapMsgBtn;
    }

    /* Define Interface to send position information on a click event*/
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private MsgBtnAdapterMgr.OnItemClickListener mListener = null;

    public void setOnItemClickListener(MsgBtnAdapterMgr.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class MsgBtnViewHolder extends RecyclerView.ViewHolder {
        protected Button msgBtn;

        public MsgBtnViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.v(TAG, "[MsgBtnViewHolder]");
            this.msgBtn = (Button) itemView.findViewById(R.id.button_node_msg);

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
    public MsgBtnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v(TAG, "[onCreateViewHolder]");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_msg_list, parent, false);
        MsgBtnViewHolder viewHolder = new MsgBtnViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MsgBtnViewHolder holder, int position) {
        Log.v(TAG, "[onBindViewHolder] position " + position);
        String msgText = mListMsgBtn.get(position).getMsgBtnID();
        holder.msgBtn.setText(msgText);
    }

    @Override
    public int getItemCount() {
        return (null != mListMsgBtn ? mListMsgBtn.size() : 0);
    }

    public void addMsgBtnList(String msgText) {
        if (isContainMsgBtn(msgText)) {
            Log.v(TAG, "[addMsgBtnList] the Map is already have the item");
            return;
        }
        Log.v(TAG, "[addMsgBtnList] add msg btn in msgbtn list : [" + msgText + "]");
        MsgBtn msgBtn = new MsgBtn(msgText);
        mListMsgBtn.add(msgBtn);
        mMapMsgBtn.put(msgText, msgBtn);
    }

    public boolean loadMsgBtnList() {
        Log.d(TAG, "load msgBtn list into map, size : " + mListMsgBtn.size());
        if (!mMapMsgBtn.isEmpty()) {
            for (MsgBtn msgBtn : mListMsgBtn) {
//                MapFragment.setMarker(friend.getLatLng(), "Friend", friend.getFriendID());
            }
            return true;
        } else {
            return false;
        }
    }

    public void clearAllMsgBtnList() {
        if (mListMsgBtn.size() < 1) {
            return;
        }
        Log.d(TAG, "[clearAllMsgBtnList] clear MsgBtn List : " + mListMsgBtn.size());
        /* clear MsgBtnList*/
        for (MsgBtn msgBtn : mListMsgBtn) {
//            msgBtn.getMarker().setMap(null);
        }
        mListMsgBtn.clear();
        mMapMsgBtn.clear();
        Log.d(TAG, "[clearAllMsgBtnList] clear MsgBtn List : " + mListMsgBtn.size());
    }

    public boolean isContainMsgBtn(String srcID) {
        return mMapMsgBtn.get(srcID) != null;
    }
}
