package com.example.testapplication.MapMgr;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCoderMgr {
    private static String TAG = "GeocoderMgr";

    public GeoCoderMgr() {
    }

    public static Address getCurrentAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "[getCurrentAddress] Impossible to use geocoder" + e);
            return null;
        }
        if (addresses == null || addresses.size() == 0) {
            Log.d(TAG, "[getCurrentAddress] 주소 미발견");
            return null;
        } else {
            Address address = addresses.get(0);

            return address;
        }
    }
}
