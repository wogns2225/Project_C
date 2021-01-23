package com.example.testapplication.ConfigMgr;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.testapplication.FriendMgr.Friend;

import java.util.ArrayList;

public class ConfigMgr {
    private final String TAG = "ConfigMgr";
    private ArrayList<Uri> mUriOfPhoto = new ArrayList<Uri>();
    private ArrayList<Bitmap> mBitmapOfPhoto = new ArrayList<Bitmap>();

    private ConfigMgr() {
    }

    public static ConfigMgr getInstance() {
        return ConfigMgr.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ConfigMgr INSTANCE = new ConfigMgr();
    }

    public ArrayList<Uri> getUriOfPhoto() {
        return mUriOfPhoto;
    }

    public void setUriOfPhoto(Uri uriOfPhoto) {
        if(!mUriOfPhoto.contains(uriOfPhoto)) {
            mUriOfPhoto.add(uriOfPhoto);
        }
    }

    public ArrayList<Bitmap> getBitmapOfPhoto() {
        return mBitmapOfPhoto;
    }

    public void setBitmapOfPhoto(Bitmap bitmapOfPhoto) {
        if(!mBitmapOfPhoto.contains(bitmapOfPhoto)) {
            mBitmapOfPhoto.add(bitmapOfPhoto);
        }
    }
}
