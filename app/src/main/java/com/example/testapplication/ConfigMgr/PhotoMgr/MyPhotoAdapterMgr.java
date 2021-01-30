package com.example.testapplication.ConfigMgr.PhotoMgr;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.ConfigMgr.ConfigFragmentPhoto;
import com.example.testapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

/* todo 1. remove selected item in the list */
public class MyPhotoAdapterMgr extends RecyclerView.Adapter<MyPhotoAdapterMgr.MyPhotoViewHolder> {
    private final String TAG = "MyPhotoAdapterMgr Class";
    private ArrayList<MyPhoto> mListPhoto;
    private HashMap<String, MyPhoto> mMapPhoto;

    public MyPhotoAdapterMgr() {
        mListPhoto = new ArrayList<>();
        mMapPhoto = new HashMap<>();
    }

    public static MyPhotoAdapterMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final MyPhotoAdapterMgr INSTANCE = new MyPhotoAdapterMgr();
    }

    public ArrayList<MyPhoto> getListPhoto() {
        return mListPhoto;
    }

    public HashMap<String, MyPhoto> getMapPhoto() {
        return mMapPhoto;
    }

    /* Define Interface to send position information on a click event*/
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class MyPhotoViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;

        public MyPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_photo);

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
    public MyPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v(TAG, "[onCreateViewHolder]");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_photo_list, parent, false);
        MyPhotoViewHolder viewHolder = new MyPhotoViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPhotoAdapterMgr.MyPhotoViewHolder holder, int position) {
        Log.v(TAG, "[onBindViewHolder]");

        if (ConfigFragmentPhoto.getModeImg() == ConfigFragmentPhoto.BIT_METHOD) {
            holder.imageView.setImageBitmap(mListPhoto.get(position).getBitmap());
        } else if (ConfigFragmentPhoto.getModeImg() == ConfigFragmentPhoto.URI_METHOD) {
            holder.imageView.setImageURI(mListPhoto.get(position).getUri());
        }
    }

    @Override
    public int getItemCount() {
        return (null != mListPhoto ? mListPhoto.size() : 0);
    }

    public void addPhotoList(String photoId, Bitmap bitmap) {
        if (isContainPhoto(photoId)) {
            Log.v(TAG, "[addPhotoList] the Map is already have the item");
            return;
        }
        Log.v(TAG, "[addPhotoList] add Friend in Friend list : [" + photoId + "]");
        MyPhoto myPhoto = new MyPhoto(photoId, bitmap);
        mListPhoto.add(myPhoto);
        mMapPhoto.put(photoId, myPhoto);
    }

//    public boolean loadPhotoList() {
//        Log.d(TAG, "load photo list into map, size : " + mListPhoto.size());
//        if (!mMapPhoto.isEmpty()) {
//            for (MyPhoto myPhoto : mListPhoto) {
//                MapFragment.setMarker(friend.getLatLng(), "Friend", friend.getFriendID());
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }

    public void clearPhotoList(String photoId, Bitmap bitmap) {
        /* clear FriendList*/
    }

    public boolean isContainPhoto(String photoId) {
        return mMapPhoto.get(photoId) != null;
    }
}
