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
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ConfigPhotoFragment extends Fragment {
    final String TAG = "ConfigPhotoFragment";

    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) view.findViewById(R.id.imageView_photo);
        /* todo. to slow showing the image with Uri */
        ArrayList<Uri> imageArray = ConfigMgr.getInstance().getUriOfPhoto();
        if (imageArray.size() > 0)
            imageView.setImageURI(imageArray.get(0));

        view.findViewById(R.id.button_photo_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
//                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Uri selectedImgUri = data.getData();
                Log.d(TAG,"[onActivityResult] Uri of Photo : " + selectedImgUri.toString());
                imageView.setImageURI(selectedImgUri);
                ConfigMgr.getInstance().setUriOfPhoto(selectedImgUri);

            }else if (resultCode == RESULT_CANCELED){
                Log.d(TAG, "[onActivityResult] result is canceled");
            }
        }
    }
}
