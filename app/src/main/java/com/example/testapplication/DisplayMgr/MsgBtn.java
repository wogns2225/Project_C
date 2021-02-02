package com.example.testapplication.DisplayMgr;

import android.util.Log;

public class MsgBtn {
    private String mMsgBtnId = "none";
    private String mMsgText = "";
    private String TAG = "MsgBtn Class";

    public MsgBtn(String msgBtnId) {
        mMsgBtnId = msgBtnId;
        Log.d(TAG, "create MsgBtn ID : [" + mMsgBtnId + "]");
    }

    public String getMsgBtnID() {
        return mMsgBtnId;
    }

    public void setMsgBtnId(String msgBtnId) {
        this.mMsgBtnId = msgBtnId;
    }

}
