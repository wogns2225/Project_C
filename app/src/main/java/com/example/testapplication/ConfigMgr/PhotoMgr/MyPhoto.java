package com.example.testapplication.ConfigMgr.PhotoMgr;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class MyPhoto {
    private String mPhotoId = "none";
    private Bitmap mBitmap = null;
    private Uri mUri = null;

    String TAG = "MyPhoto Class";

    public MyPhoto(String photoId, Bitmap bitmap) {
        mPhotoId = photoId;
        mBitmap = bitmap;
        Log.d(TAG, "create photo ID : [" + mPhotoId + "], bitmap : [" + bitmap.toString() + "]");
    }

    public MyPhoto(String photoId, Uri uri) {
        mPhotoId = photoId;
        mUri = uri;
        Log.d(TAG, "create photo ID : [" + mPhotoId + "], bitmap : [" + uri.toString() + "]");
    }

    public String getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(String photoId) {
        this.mPhotoId = photoId;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }
}
