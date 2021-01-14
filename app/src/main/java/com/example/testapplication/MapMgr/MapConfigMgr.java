package com.example.testapplication.MapMgr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.testapplication.MapFragment;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.Symbol;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.PolylineOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapConfigMgr {
    private MapConfigMgr() {
        Log.d(TAG, "[MapConfiguration]");
    }

    public static MapConfigMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final MapConfigMgr INSTANCE = new MapConfigMgr();
    }

    private String TAG = "MapConfiguration";
    private String mCountryCode;

    private LatLng mDefaultLocation = new LatLng(37.566649, 126.978448);
    private LatLng mCurrentLocation;

    private NaverMap mNaverMap;
    private Context mContext;

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public NaverMap getNaverMap() {
        return mNaverMap;
    }

    public void setNaverMap(NaverMap naverMap) {
        mNaverMap = naverMap;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public LatLng getCurrentLocation() {
        if (mCountryCode.equals("KR")) {
            return mCurrentLocation;
        } else {
            return mDefaultLocation;
        }
    }

    public void setCurrentLocation(LatLng currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public void setHandleEventListener() {
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
                MapFragment.mInfoWindow.close();
                MapFragment.mMsgPopupWindow.dismiss();
                MapFragment.mNodePopupWindow.dismiss();
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

    public void initializePosition(Location location) {
        Log.d(TAG, "[setHandleEventListener-onLocationChange]"
                + " lat : " + location.getLatitude()
                + " long : " + location.getLongitude()
                + " speed : " + location.getSpeed()
                + " time : " + location.getTime()
        );

        Address addresss = GeoCoderAPI.getCurrentAddress(mContext,
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
    public void setMapConfiguration(final NaverMap.MapType mapMode) {
        mNaverMap.setMapType(mapMode);
        mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, false);
        mNaverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true);

        if (mapMode == NaverMap.MapType.Navi) {
            mNaverMap.setNightModeEnabled(false);
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

    public void setCameraPosition() {
        mNaverMap.setContentPadding(0, 0, 0, 0);
    }

    public void moveCameraPosition(final LatLng latLng) {
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

    public void setPolyline() {
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
