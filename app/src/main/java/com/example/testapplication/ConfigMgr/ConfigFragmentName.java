package com.example.testapplication.ConfigMgr;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.testapplication.R;

public class ConfigFragmentName extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText editText = view.findViewById(R.id.edit_text_name);
        if (!ConfigMgr.getInstance().getName().equals("")){
            editText.setText(ConfigMgr.getInstance().getName());
        }
        view.findViewById(R.id.button_set_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setName(editText.getText().toString());
            }
        });
    }
}