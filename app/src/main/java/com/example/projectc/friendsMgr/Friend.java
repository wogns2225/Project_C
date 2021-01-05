package com.example.projectc.friendsMgr;

import android.util.Log;

import com.google.android.gms.maps.model.MarkerOptions;

public class Friend {
    private String mFriendId = "none";
    private double mFriendLat = 0;
    private double mFriendLong = 0;
    private MarkerOptions markerOptions;

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

    public Friend(String mFriendId, MarkerOptions markerOptions) {
        this.mFriendId = mFriendId;
        this.markerOptions = markerOptions;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public String getFriendID() {
        return mFriendId;
    }

    public double getFriendLat() {
        return mFriendLat;
    }

    public double getFriendLong() {
        return mFriendLong;
    }

    public void setFriendId(String mFriendId) {
        this.mFriendId = mFriendId;
    }

    public void setFriendLat(double mFriendLat) {
        this.mFriendLat = mFriendLat;
    }

    public void setFriendLong(double mFriendLong) {
        this.mFriendLong = mFriendLong;
    }
}
