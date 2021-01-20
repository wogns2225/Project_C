package com.example.testapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapplication.ConfigMgr.ConfigNameFragment;
import com.example.testapplication.ConfigMgr.ConfigPhotoFragment;

public class ConfigFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* todo. After changing fragments, 1. sending position is quit, 2. MapConfiguration initialized*/
        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ConfigFragment.this)
                        .navigate(R.id.action_ConfigFragment_to_MapFragment);
            }
        });

//        getChildFragmentManager().beginTransaction().add(R.id.fragment_place, new ConfigPhotoFragment()).commit();

        view.findViewById(R.id.button_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().add(R.id.fragment_place, new ConfigPhotoFragment()).commit();
                Toast.makeText(getContext(), "photo", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.button_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().add(R.id.fragment_place, new ConfigNameFragment()).commit();
                Toast.makeText(getContext(), "Name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}