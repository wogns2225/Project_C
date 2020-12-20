package com.example.projectc.friendsMgr;

import android.util.Log;

public class Friend {
    String mFriendId = "none";
    double mFriendLat = 0;
    double mFriendLong = 0;

    String TAG = "Friend Class";

    public Friend(String friendId, String snippet) {
        mFriendId = friendId;

        Log.d(TAG, "create Friend ID : [" + mFriendId +
                "], Snippet : [" + snippet + "]");
    }

    public Friend(String friendId, double friendLat, double friendLong) {
        mFriendId = friendId;
        mFriendLat = friendLat;
        mFriendLong = friendLong;
        Log.d(TAG, "create Friend ID : [" + mFriendId +
                "], Latitude : [" + mFriendLat +
                "], Longitude : [" + mFriendLong + "]");
    }

    public String getFriendID(){
        return mFriendId;
    }
    public double getFriendLat(){
        return mFriendLat;
    }
    public double getFriendLong(){
        return mFriendLong;
    }
}
