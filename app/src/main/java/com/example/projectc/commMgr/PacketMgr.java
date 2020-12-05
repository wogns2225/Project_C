package com.example.projectc.commMgr;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PacketMgr {
    JSONObject json = null;
    String TAG = "PacketMgr";

    public String makePktPosition(int srcID, int dstID, int typeID, String payload) {

        /* ex. {"srcID":"0", "dstID":"1", "typeID":"1", "payload":"..."} */
        try {
            String message = "{\"srcID\":\"" + srcID + "\", " +
                    "\"destID\":\"" + dstID + "\", " +
                    "\"typeID\": \"" + typeID + "\", " +
                    "\"payload\":\"" + payload + "\"}"; // String
            json = new JSONObject(message);
//            Log.d(TAG, "JSON string : " + message);
            Log.d(TAG, "JSON obj : " + json.toString());
            return json.toString();
        }catch(JSONException e){
            Log.e(TAG, "JSON Error : " + e);
            return "";
        }
    }
}
