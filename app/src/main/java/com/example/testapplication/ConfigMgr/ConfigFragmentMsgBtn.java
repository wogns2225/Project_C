package com.example.testapplication.ConfigMgr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.DisplayMgr.MsgBtnAdapterMgr;
import com.example.testapplication.R;

public class ConfigFragmentMsgBtn extends Fragment {
    private final String TAG = "ConfigFragmentMsgBtn Class";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_msgbtn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText editText = view.findViewById(R.id.editText_msg_btn_text);

        view.findViewById(R.id.button_msg_btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = editText.getText().toString();
//                Log.d(TAG, "[onClickListener] msgText : " + msgText);
                toAddMyMsgBtn(msgText);
            }
        });

        createListMsgBtn(view);
        setAdapterHandler(view);

    }

    public void setAdapterHandler(final View view) {
        Log.d(TAG, "[setAdapterHandler]");
        MsgBtnAdapterMgr.getInstance().setOnItemClickListener(new MsgBtnAdapterMgr.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String photoId = MsgBtnAdapterMgr.getInstance().getListMsgBtn().get(pos).getMsgBtnID();
                Toast.makeText(getContext(), "change to this msgBtn? : " + photoId, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void toAddMyMsgBtn(String msgText){
        Log.v(TAG, "[toAddMyMsgBtn]");
        if(MsgBtnAdapterMgr.getInstance().isContainMsgBtn(msgText)){
            Log.v(TAG, "[toAddMyPhoto] the msgBtn is already contained");
            return;
        }
        MsgBtnAdapterMgr.getInstance().addMsgBtnList(msgText);
        MsgBtnAdapterMgr.getInstance().notifyDataSetChanged();
    }

    public void createListMsgBtn(View view){
        Log.v(TAG, "[createListMsgBtn]");

        /* Recycler View */
        RecyclerView recyclerView = view.findViewById(R.id.id_recycler_msgbtn_list);
        if(recyclerView == null) return;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(MsgBtnAdapterMgr.getInstance());

        /* Recycler behavior */
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}
