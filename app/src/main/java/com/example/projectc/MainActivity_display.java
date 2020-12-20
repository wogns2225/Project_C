package com.example.projectc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.projectc.commMgr.PacketMgr;
import com.example.projectc.commMgr.ProtocolDefine;
import com.example.projectc.commMgr.SocketMgr;
import com.example.projectc.friendsMgr.Friend;
import com.example.projectc.mapMgr.NodeMgr;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity_display extends AppCompatActivity implements OnMapReadyCallback {
    private static GoogleMap mMap;
    private Marker currentMarker = null;

    static String TAG = "MainActivity_Display";

    private static Context mContext;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // onRequestPermissionsResult 에서 수신된 결과에서 ActivityCompat.request Permissions 를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    /* selected a position to check some information */
    boolean mSelectedOthers = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    // location variable
    private Location mLastKnownLocation; // 현재 위치의 주위 Location 정보를 담는 location 변수
    private CameraPosition mCameraPosition;

    Location mCurrentLocation;
    LatLng mCurrentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    /* create instance for the other class */
    SocketMgr sock = new SocketMgr();
    public static final MySocketHandler mMySocketHandler = new MySocketHandler();

    /* summary : for a protocol with server*/
    String mSrcID = "0";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable("location");
            mCameraPosition = savedInstanceState.getParcelable("camera_position");
        }

        setContentView(R.layout.activity_main_display);
        mContext = getApplicationContext();

        String macAdd = MainActivity.getMACAddress("wlan0");
        mSrcID = macAdd.substring(macAdd.lastIndexOf(":") + 1);
        Log.d(TAG, "[onCreate] mSrcID : (" + mSrcID + ") MAC Add : (" + macAdd + ")");

        // location settings
        mLayout = findViewById(R.id.layout_main_display);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // map test
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "next from the map");

        /* socket test */
        Button btn_back = findViewById(R.id.button_back);
        Button btn_send_position = findViewById(R.id.button_send_position);
        Button btn_get_position = findViewById(R.id.button_get_position);

        sock.createSocket();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[onCreate-btn_back] onClick");
                Intent intent_change_disp = new Intent(MainActivity_display.this, MainActivity.class);
                startActivity(intent_change_disp);
            }
        });

        btn_send_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[onCreate-btn_send_position] onClick");
                if (mCurrentPosition != null) {
                    String payload = String.valueOf(mCurrentPosition.latitude) + ',' + mCurrentPosition.longitude;
                    toSendMessageWithSocket(mSrcID, 1, 1, payload);
                } else {
                    Log.e(TAG, "[onCreate-btn_send_position] mCurrentPosition is NULL");
                }
            }
        });

        btn_get_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[onCreate-btn_get_position] onClick");
                toSendMessageWithSocket(mSrcID, 1, 2, "");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable("camera_position", mMap.getCameraPosition());
            outState.putParcelable("location", mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    //      map test
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady start");
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);
        setDefaultLocation(SEOUL);

        /* error is about one block */
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED && hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "[onMapReady] Permission is granted");
            startLocationUpdates();
        } else {
            Log.d(TAG, "[onMapReady] Permission should be get to play");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "[onMapReady] 앱을 실행하기 위해 위치 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).
                        setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity_display.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "[onMapReady] MyLocation : (" + latLng.latitude + ", " + latLng.latitude + ")");
            }
        });

        /* show the info when the node is clicked */
        /*
        Friend friend = new Friend("friend1", 0.22, 0.33);
        View infoWindow = getLayoutInflater().inflate(R.layout.node_info, null);
        NodeMgr nodeMgr = new NodeMgr(infoWindow, friend);
        mMap.setInfoWindowAdapter(nodeMgr);*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("TAG", "[onMapReady-setOnMarkerClickListener] onMarkerClick");
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                Log.d("TAG", "[onMapReady-setInfoWindowAdapter] getInfoWindow");
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Log.d("TAG", "[onMapReady-setInfoWindowAdapter] getInfoContents");
                String makerInfo = "[ Node ID : " + marker.getTitle() +"], [" + marker.getSnippet() + "]";
                Friend friend = new Friend(marker.getTitle(), marker.getSnippet());
                View infoWindow = getLayoutInflater().inflate(R.layout.node_info, null);

                TextView markerTitle = (TextView) infoWindow.findViewById(R.id.marker_name);        // set Text View for a selected marker
                final Button helloButton = (Button) infoWindow.findViewById(R.id.marker_msg_1);           // set Button for "hello" message
                final Button accidentButton = (Button) infoWindow.findViewById(R.id.marker_msg_2);        // set Button for "accident" message

                markerTitle.setText(makerInfo);
                final int dstID = Integer.parseInt(marker.getTitle());
                helloButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toSendMessageWithSocket(mSrcID, dstID, 40, (String) helloButton.getText());
                    }
                });
                accidentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toSendMessageWithSocket(mSrcID, dstID, 40, (String) accidentButton.getText());
                    }
                });

                NodeMgr nodeMgr = new NodeMgr(infoWindow, friend);
                return infoWindow;
            }
        });

    }

    private void startLocationUpdates() {
        if (!checkLocationServiceStatus()) {
            Log.d(TAG, "[startLocationUpdates] location service is not enabled");
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "[startLocationUpdates] 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "[startLocationUpdates] call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }

    public boolean checkLocationServiceStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasFineLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * if the permission behavior was needed, this function is callbacked after user select options
     *
     * @param permsRequestCode
     * @param permissions
     * @param grandResults
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                startLocationUpdates();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    /* 사용자가 거부만 선택 */
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }


    /**
     * show popup(Dialog) for location service setting(GPS and Network)
     *
     * @param none
     * @author wogns2225@gmail.com
     * @version 0
     */
    private void showDialogForLocationServiceSetting() {
        Log.d(TAG, "[showDialogForLocationServiceSetting] call builder");

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity_display.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해 위치 서비스가 필요합니다.\n 위치 설정을 수정할까요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "[showDialogForLocationServiceSetting] call setPositiveButton");
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                /* todo. how about the network provider? */
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "[showDialogForLocationServiceSetting] call setNegativeButton");
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    /**
     * this function is callbacked after startActivityForResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /* todo. how about the network enable case? */
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServiceStatus()) {
                    Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                    needRequest = true;
                    return;
                }
                break;
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                /* Todo. should be removed */
                location.setLatitude(37.5737889);
                location.setLongitude(126.6852978);
                /* Todo End */
                mCurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());

//                String markerTitle = getCurrentAddress(mCurrentPosition);
                String markerTitle = mSrcID;
                String markerSnippet = "Lat : " + location.getLatitude() + "Long : " + location.getLongitude();
                if(!mSelectedOthers)                                                                    // 다른 정보를 보기위해 current location update를 멈춤.
                    setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocation = location;
            }
        }
    };

    /**
     * return an address by position with geocoder
     *
     * @param position
     * @return a String for an address of selected position and using it as marker title
     * @deprecated there is no need to show the address information in this application till now
     */
    public String getCurrentAddress(LatLng position) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
        } catch (IOException ioException) {
            toShowMessageWithToast("지오코더 서비스 사용불가");
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            toShowMessageWithToast("잘못된 Gps 좌표");
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            toShowMessageWithToast("해당 위치의 주소를 찾지 못함");
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }

    /**
     * 지도의 기본 위치 설청
     * todo1. current position 동작에 문제가 있을 경우에만 호출되도록
     *
     * @param default_Location
     */
    public void setDefaultLocation(LatLng default_Location) {
        String marker_Title = "위치정보를 가져올 수 없음.";
        String marker_Snippet = " 위치 퍼미션과 GPS 활성 여부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(default_Location);
        markerOptions.title(marker_Title);
        markerOptions.snippet(marker_Snippet);
        markerOptions.draggable(true);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_Location, 10));
    }

    /**
     * this handler is for socket communication.
     * the message is transmitted after receiving some message from server
     */
    public static class MySocketHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            JSONObject json = null;
            try {
                json = new JSONObject(msg.obj.toString());
                Log.d(TAG, "[MySocketHandler-handleMessage] this is handler" + msg.obj);

                String typeID = json.getString("typeID");
                /* todo. parser for translate the type ID to description */
                toShowMessageWithToast("전달받은 서비스 종류 : (" + typeID + ")");

                /* todo. make a wrapper function to handle each service easily */
                if (typeID.equals(ProtocolDefine.SID_PutPosition)) {
                    String recv_payload = json.getString("payload"); // {length;srcID,position;srcID,position;}
                    String[] separated = recv_payload.split(";");
                    Log.d(TAG, "[MySocketHandler-handleMessage] Received num of friend pos. " + (separated.length - 1));
                    if (!separated[0].equals("0")) {
                        Log.d(TAG, "[MySocketHandler-handleMessage] Friend position : [" + separated[1] + "]");
                        String[] position = separated[1].split(",");
                        toAddFriendPosition(separated[0], Double.parseDouble(position[1]), Double.parseDouble(position[2]));
                    }
                }else if(typeID.equals(ProtocolDefine.SID_PutMessage)) {
                    String recv_payload = json.getString("payload"); // {length;srcID,position;srcID,position;}
                    String[] separated = recv_payload.split(";");
                    Log.d(TAG, "[MySocketHandler-handleMessage] Received num of friend msg :. " + (separated.length - 1));
                    if (!separated[0].equals("0")) {
                        Log.d(TAG, "[MySocketHandler-handleMessage] Friend msg : [" + separated[1] + "]");
                        String message = "friend ID : (" + separated[0] +"), msg : ("+ separated[1] + ")";
                        toShowMessageWithToast(message);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * to add markers after receiving friends list from server
     *
     * @param srcID
     * @param latitude
     * @param longitude
     */
    public static void toAddFriendPosition(String srcID, Double latitude, Double longitude) {
        MarkerOptions mOptions = new MarkerOptions();

        mOptions.title(srcID);             // 마커 타이틀
        mOptions.snippet(latitude.toString() + ", " + longitude.toString()); // 마커의 스니펫(간단한 텍스트) 설정
        mOptions.position(new LatLng(latitude, longitude));
        mMap.addMarker(mOptions);
    }

    /**
     * a wrapper function to show the message with toast
     * @param str
     */
    public static void toShowMessageWithToast(String str) {
        Toast.makeText(mContext, TAG + "[toShowMessage] : " + str, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param srcID : client ID
     * @param dstID : server ID
     * @param dataType : data type of server protocol
     * @param payload : payload to send message to server
     */
    protected void toSendMessageWithSocket(String srcID, int dstID, int dataType, String payload){
        String jsonForPositionInfo;
        PacketMgr pkt = new PacketMgr();
        jsonForPositionInfo = pkt.makeInputToJsonStr(Integer.parseInt(srcID, 16), dstID, dataType, payload);
        sock.send(jsonForPositionInfo);
    }
}
