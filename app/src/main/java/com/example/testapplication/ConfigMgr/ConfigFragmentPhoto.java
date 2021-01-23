package com.example.testapplication.ConfigMgr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.ConfigMgr.PhotoMgr.MyPhotoAdapterMgr;
import com.example.testapplication.R;

import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ConfigFragmentPhoto extends Fragment {
    private final String TAG = "ConfigPhotoFragment Class";
    public static final char URI_METHOD = 0;
    public static final char BIT_METHOD = 1;
    private static char mModeImg = BIT_METHOD;

    public static char getModeImg() {
        return mModeImg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_photo_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        createListPhoto(view);
        setAdapterHandler(view);

    }

    public void setAdapterHandler(final View view) {
        Log.d(TAG, "[setAdapterHandler]");
        MyPhotoAdapterMgr.getInstance().setOnItemClickListener(new MyPhotoAdapterMgr.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String photoId = MyPhotoAdapterMgr.getInstance().getListPhoto().get(pos).getPhotoId();
                Toast.makeText(getContext(), "change to this photo? : " + photoId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "[onActivityResult] requestCode : " + requestCode + ", mModeImg : " +mModeImg);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data == null || data.getData() == null)
                    return;

                if (mModeImg == URI_METHOD) {
                    Uri selectedImgUri = data.getData();
//                    MyPhotoAdapterMgr.getInstance().addPhotoList(data.getDataString(), selectedImgUri);
                    Log.v(TAG, "[onActivityResult] Uri of Photo : " + selectedImgUri.toString());
                } else if (mModeImg == BIT_METHOD) {
                    try {
                        InputStream in = getContext().getContentResolver().openInputStream(data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        in.close();
                        toAddMyPhoto(data.getDataString(), bitmap);
                        Log.v(TAG, "[onActivityResult] Bitmap of Photo : " + data.getData().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "[onActivityResult] result is canceled");
            }
        }
    }

    public void toAddMyPhoto(String photoId, Bitmap bitmap){
        Log.v(TAG, "[toAddMyPhoto]");
        if(MyPhotoAdapterMgr.getInstance().isContainPhoto(photoId)){
            Log.v(TAG, "[toAddMyPhoto] the Id is already contained");
            return;
        }
        MyPhotoAdapterMgr.getInstance().addPhotoList(photoId, bitmap);
        MyPhotoAdapterMgr.getInstance().notifyDataSetChanged();
    }

    public void createListPhoto(View view){
        Log.v(TAG, "[createListPhoto]");

        /* Recycler View */
        RecyclerView recyclerView = view.findViewById(R.id.id_recycler_photo_list);
        if(recyclerView == null) return;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(MyPhotoAdapterMgr.getInstance());

        /* Recycler behavior */
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
