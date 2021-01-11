package com.example.testapplication.FriendMgr;

import android.util.Log;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

public class Friend {
    private String mFriendId = "none";
    private double mFriendLat = 0;
    private double mFriendLong = 0;
    private Marker mMarker;
    private LatLng mLatLng;

    String TAG = "Friend Class";

    public Friend(String friendId, String nodeInfo) {
        mFriendId = friendId;

        Log.d(TAG, "create Friend ID : [" + mFriendId +
                "], nodeInfo : [" + nodeInfo + "]");
    }

    public Friend(String friendId, double friendLat, double friendLong) {
        mFriendId = friendId;
        mFriendLat = friendLat;
        mFriendLong = friendLong;
        Log.d(TAG, "create Friend ID : [" + mFriendId +
                "], Latitude : [" + mFriendLat +
                "], Longitude : [" + mFriendLong + "]");
        mLatLng = new LatLng(mFriendLat, mFriendLong);
    }

    public Friend(String mFriendId, Marker marker) {
        this.mFriendId = mFriendId;
        this.mMarker = marker;
        mLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarkerOptions(Marker marker) {
        this.mMarker = marker;
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

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng latLng) {
        this.mLatLng = latLng;
    }
}
