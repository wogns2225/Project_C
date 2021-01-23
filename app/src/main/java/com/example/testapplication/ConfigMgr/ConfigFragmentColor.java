package com.example.testapplication.ConfigMgr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testapplication.R;

public class ConfigFragmentColor extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_color, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setColor("BLACK");
            }
        });
        view.findViewById(R.id.button_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setColor("WHITE");
            }
        });
        view.findViewById(R.id.button_grey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setColor("GREY");
            }
        });
        view.findViewById(R.id.button_blue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setColor("BLUE");
            }
        });
        view.findViewById(R.id.button_red).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setColor("RED");
            }
        });
    }
}