package com.example.testapplication.CommMgr;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PacketAPI {
    final static String TAG = "PacketMgr";

    public static String makeInputToJsonStr(String srcID, String dstID, int typeID, String payload) {
        JSONObject json = null;

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
