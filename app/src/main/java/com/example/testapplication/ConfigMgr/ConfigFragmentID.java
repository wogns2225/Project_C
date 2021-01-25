package com.example.testapplication.ConfigMgr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.testapplication.CommMgr.NaverLoginAPIMgr;
import com.example.testapplication.R;

public class ConfigFragmentID extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_id, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText editTextName = view.findViewById(R.id.edit_text_name);
        final EditText editTextNumber = view.findViewById(R.id.edit_text_number);
        editTextName.setText(NaverLoginAPIMgr.getInstance().getLoginData().getNickName() + NaverLoginAPIMgr.getInstance().getLoginData().getName());
        if (!ConfigMgr.getInstance().getName().equals("")){
            editTextName.setText(ConfigMgr.getInstance().getName());
        }
        if (!ConfigMgr.getInstance().getNumber().equals("")){
            editTextNumber.setText(ConfigMgr.getInstance().getNumber());
        }
        view.findViewById(R.id.button_set_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setName("Name : "+editTextName.getText().toString());
            }
        });
        view.findViewById(R.id.button_set_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigMgr.getInstance().setNumber("Vehicle Number : "+editTextNumber.getText().toString());
            }
        });
    }
}