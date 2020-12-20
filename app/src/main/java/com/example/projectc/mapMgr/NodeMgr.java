package com.example.projectc.mapMgr;


import android.view.View;
import android.widget.TextView;

import com.example.projectc.R;
import com.example.projectc.friendsMgr.Friend;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class NodeMgr implements GoogleMap.InfoWindowAdapter {
    View window;
    Friend mFriend;
    public NodeMgr(View window, Friend friend){
        this.window = window;
        this.mFriend = friend;//정보를 담은 객체
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView textViewOk = window.findViewById(R.id.marker_msg_1);
        TextView textViewCancel = window.findViewById(R.id.marker_msg_2);

//        textViewOk.setText(mFriend.getFriendID());
//        textViewCancel.setText( String.valueOf(mFriend.getFriendLat()) );
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
