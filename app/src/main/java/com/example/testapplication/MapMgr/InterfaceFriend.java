package com.example.testapplication.MapMgr;

import android.util.Log;

public class InterfaceFriend {
    private static String TAG = "InterfaceFriend";

    public InterfaceFriend() {
    }

//    /**
//     * to add markers after receiving friends list from server
//     *
//     * @param srcID
//     * @param latitude
//     * @param longitude
//     */
//    public static void toAddFriendPosition(String srcID, Double latitude, Double longitude) {
//        Log.d(TAG, "[toAddFriendPosition] srcID(" + srcID + "), latitude(" + latitude + "), longitude(" + longitude + ")");
//        /* add node in map as marker */
//        MarkerOptions mOptions = new MarkerOptions();
//
//        mOptions.title(srcID);             // 마커 타이틀
//        mOptions.snippet(latitude.toString() + ", " + longitude.toString()); // 마커의 스니펫(간단한 텍스트) 설정
//        mOptions.position(new LatLng(latitude, longitude));
//        mOptions.alpha(0.8f);
//        mMap.addMarker(mOptions);
//
//        /* add node into list */
//        mCountOfFriend++;
//        Friend friend = new Friend(srcID, mOptions);
//        mFriendList.add(friend);
//        mAdapter.notifyDataSetChanged();
//    }
}
