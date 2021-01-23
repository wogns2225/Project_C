/*
package com.example.testapplication.ConfigMgr.PhotoMgr;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

public class MyPhoto {
    private String mPhotoId = "none";
    private BitmapFactory mBitmapFactory = null;

    String TAG = "MyPhoto Class";

    public MyPhoto(String friendId, String nodeInfo) {
        mPhotoId = friendId;

        Log.d(TAG, "create Friend ID : [" + mPhotoId +
                "], nodeInfo : [" + nodeInfo + "]");
    }

    public MyPhoto(String friendId, double friendLat, double friendLong) {
        mPhotoId = friendId;
        mFriendLat = friendLat;
        mFriendLong = friendLong;
        Log.d(TAG, "create Friend ID : [" + mPhotoId +
                "], Latitude : [" + mFriendLat +
                "], Longitude : [" + mFriendLong + "]");
        mLatLng = new LatLng(mFriendLat, mFriendLong);
    }

    public MyPhoto(String mPhotoId, Marker marker) {
        this.mPhotoId = mPhotoId;
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
        return mPhotoId;
    }

    public double getFriendLat() {
        return mFriendLat;
    }

    public double getFriendLong() {
        return mFriendLong;
    }

    public void setFriendId(String photoId) {
        this.mPhotoId = photoId;
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
*/
