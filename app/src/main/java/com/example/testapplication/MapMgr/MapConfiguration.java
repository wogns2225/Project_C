package com.example.testapplication.MapMgr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testapplication.CommMgr.InterfaceForServer;
import com.example.testapplication.CommMgr.SocketMgr;
import com.example.testapplication.FriendMgr.FriendAdapter;
import com.example.testapplication.MapFragment;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.Symbol;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapConfiguration {
    private static String TAG = "MapConfiguration";
    private static String mCountryCode;

    private static LatLng mDefaultLocation = new LatLng(37.566649, 126.978448);
    private static LatLng mCurrentLocation;

    private static NaverMap mNaverMap;
    private static Context mContext;

    public MapConfiguration() {
        Log.d(TAG, "[MapConfiguration]");
    }

    public MapConfiguration(final NaverMap naverMap) {
        Log.d(TAG, "[MapConfiguration]");
        mNaverMap = naverMap;
    }

    public static String getCountryCode() {
        return mCountryCode;
    }

    public static void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public static NaverMap getNaverMap() {
        return mNaverMap;
    }

    public static void setNaverMap(NaverMap naverMap) {
        mNaverMap = naverMap;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static LatLng getCurrentLocation() {
        if(mCountryCode.equals("KR")) {
            return mCurrentLocation;
        }else{
            return mDefaultLocation;
        }
    }

    public static void setCurrentLocation(LatLng currentLocation) {
        MapConfiguration.mCurrentLocation = currentLocation;
    }

    public static void setHandleEventListener() {
        Log.d(TAG, "[setHandleEventListener]");
        mNaverMap.addOnOptionChangeListener(new NaverMap.OnOptionChangeListener() {
            @Override
            public void onOptionChange() {
                if (mNaverMap.isNightModeEnabled()) {
                    Log.d(TAG, "[setHandleEventListener-onOptionChange] NightMode On");
                } else {
                    Log.d(TAG, "[setHandleEventListener-onOptionChange] NightMode Off");
                }
            }
        });
        mNaverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                /* to many print out this log */
//                Log.d(TAG, "[setHandleEventListener-onCameraChange] Camera change reason " + i + " animated : " + b);
            }
        });
        mNaverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                initializePosition(location);
                if (mCountryCode.equals("KR")) {
                    MapFragment.onLocationChange(mCurrentLocation);
                } else {
                    /* todo. should be removed */
                    MapFragment.onLocationChange(mDefaultLocation);
                }
            }
        });

        mNaverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                Log.d(TAG, "[setHandleEventListener-onMapClick] Latitude" + latLng.latitude + " longitude" + latLng.longitude);
                Toast.makeText(mContext, "[onMapClick] Latitude [" + latLng.latitude + "] longitude [" + latLng.longitude + "]", Toast.LENGTH_SHORT).show();
            }
        });
        mNaverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                Log.d(TAG, "[setHandleEventListener-onMapLongClick] Latitude" + latLng.latitude + " longitude" + latLng.longitude);
                Toast.makeText(mContext, "[onMapLongClick] Latitude [" + latLng.latitude + "] longitude [" + latLng.longitude + "]", Toast.LENGTH_SHORT).show();

            }
        });
        mNaverMap.setOnSymbolClickListener(new NaverMap.OnSymbolClickListener() {
            @Override
            public boolean onSymbolClick(@NonNull Symbol symbol) {
                Log.d(TAG, "[setHandleEventListener-onSymbolClick] ");
                return false;
            }
        });
    }

    public static void initializePosition(Location location) {
        Log.d(TAG, "[setHandleEventListener-onLocationChange]"
                + " lat : " + location.getLatitude()
                + " long : " + location.getLongitude()
                + " speed : " + location.getSpeed()
                + " time : " + location.getTime()
        );

        Address addresss = GeoCoderMgr.getCurrentAddress(mContext,
                location.getLatitude(),
                location.getLongitude());
        if (addresss != null) {
            mCountryCode = addresss.getCountryCode();
            Log.d(TAG, "[setHandleEventListener] current address : " + mCountryCode);
            /* Default Location of Seoul : 37.566649, 126.978448 */
            if (!mCountryCode.equals("KR")) {
                mCurrentLocation = mDefaultLocation;
            } else {
                mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } else {
            mCurrentLocation = mDefaultLocation;
        }
    }

    /* todo. change NightMode based on time */
    /* todo. change MapType and LayerGroup as dynamic*/
    public static void setMapConfiguration(final NaverMap.MapType mapMode) {
        mNaverMap.setMapType(mapMode);
        mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, false);
        mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);

        if (mapMode == NaverMap.MapType.Navi) {
            mNaverMap.setNightModeEnabled(true);
        } else if (mapMode == NaverMap.MapType.Basic) {
            mNaverMap.setIndoorEnabled(true);
            mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, true);
            mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE, true);
            mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, true);
            mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, true);
        }
        mNaverMap.setSymbolScale(1);

        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        /* todo. make a configuration to change the Tracking Mode (Follow, Face)*/

    }

    public static void setCameraPosition() {
        mNaverMap.setContentPadding(0, 0, 0, 0);
    }

    public static void moveCameraPosition(final LatLng latLng) {
//        LatLng southWest = new LatLng(37.5437889, 126.6452978);
//        LatLng northEast = new LatLng(37.5937889, 126.6852978);
//        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
//        CameraUpdate cameraUpdate = CameraUpdate.fitBounds(bounds);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng).
                animate(CameraAnimation.Fly, 700).
                finishCallback(new CameraUpdate.FinishCallback() {
                    @Override
                    public void onCameraUpdateFinish() {
//                Toast.makeText(mContext, "Complete", Toast.LENGTH_SHORT).show();
                    }
                });

        mNaverMap.moveCamera(cameraUpdate);
    }

    public static void setPolyline() {
        List<LatLng> coords = new ArrayList<>();
        Collections.addAll(coords,
                new LatLng(37.56445, 126.97707),
                new LatLng(37.55855, 126.97822));
        PolylineOverlay polyline = new PolylineOverlay();
        polyline.setCoords(coords);
        polyline.setWidth(10);
        polyline.setColor(Color.GREEN);
        polyline.setMap(mNaverMap);
    }
}
