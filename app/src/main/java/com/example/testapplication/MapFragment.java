package com.example.testapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication.CommMgr.InterfaceForServerAPI;
import com.example.testapplication.CommMgr.ProtocolDefine;
import com.example.testapplication.CommMgr.SocketMgr;
import com.example.testapplication.DeviceMgr.DeviceAPI;
import com.example.testapplication.FriendMgr.FriendAdapterMgr;
import com.example.testapplication.MapMgr.MapConfigMgr;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static String TAG = "MapFragment";
    public static boolean mInitialized = false;
    private static View mView;
    private static Context mContext;

    /* NAVER MAP */
    public static NaverMap mNaverMap = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource mLocationSource;
    public static Overlay.OnClickListener mListener;
    public static InfoWindow mInfoWindow = null;

    /* MAP UI*/
    private static boolean mIsClickedFollowCamera = false;
    private static String mIsClickedSharePos = "1";
    public static PopupWindow mMsgPopupWindow = null;
    public static PopupWindow mNodePopupWindow = null;

    /* Server Comm */
    private static String mSrcID = "00";

    /* Socket */
    public static final MySocketHandler mMySocketHandler = new MySocketHandler();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity.getFragList().clear();
        MainActivity.getFragList().add(new WeakReference<Fragment>(this));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated");
        mView = view;
        mContext = getContext();
        if (!mInitialized) {
            SocketMgr.getInstance().createSocket();

            String macAdd = DeviceAPI.getMACAddress("wlan0");
            mSrcID = "C" + macAdd.substring(macAdd.lastIndexOf(":") + 1);
            mLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
            FragmentManager fm = getChildFragmentManager();
            com.naver.maps.map.MapFragment mapFragment = (com.naver.maps.map.MapFragment) fm.findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);

            /* todo. getActivity should be called after onActivityCreated? */
            if (getContext() != null) {
                NaverMapSdk.getInstance(getContext()).setOnAuthFailedListener(new NaverMapSdk.OnAuthFailedListener() {
                    @Override
                    public void onAuthFailed(@NonNull NaverMapSdk.AuthFailedException e) {
                        Log.d(TAG, "[onViewCreated-onAuthFailed] ");
                    }
                });
            } else {
                /* safe code */
                Log.d(TAG, "[onViewCreated-onAuthFailed] getActivity() is not working. please check the call to get the context ");
            }

            setAdapterHandler(view);
            setButtonHandler(view);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "[onPause]");
        MapConfigMgr.getInstance().saveCameraPosition();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "[onStop]");
        super.onStop();
    }

    public void setAdapterHandler(final View view) {
        FriendAdapterMgr.getInstance().setOnItemClickListener(new FriendAdapterMgr.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                LatLng cameraPosition = FriendAdapterMgr.getInstance().getListFriend().get(pos).getLatLng();
                MapConfigMgr.getInstance().moveCameraPosition(cameraPosition);

                toShowMsgPopupWindow(FriendAdapterMgr.getInstance().getListFriend().get(pos).getFriendID());
            }
        });
    }

    public void setButtonHandler(View view) {
        Log.d(TAG, "[setButtonConfig]");
        view.findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "[onViewCreated-onClick]");
//                moveCameraPosition();
                NavHostFragment.findNavController(MapFragment.this).navigate(R.id.action_MapFragment_to_ConfigFragment);
            }
        });

        Switch switch_pos = view.findViewById(R.id.switch_current_position);
        switch_pos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsClickedFollowCamera = isChecked;
            }
        });

        Switch switch_share = view.findViewById(R.id.switch_share_position);
        switch_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsClickedSharePos = isChecked?"1":"0";
                Log.d(TAG, "[onCheckedChanged] isChecked" + isChecked + ", flag :" +mIsClickedSharePos);
            }
        });
        view.findViewById(R.id.button_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterfaceForServerAPI.toSendMessageWithSocket(SocketMgr.getInstance(), mSrcID, "S0", ProtocolDefine.SID_GetPosition, "");
            }
        });

        view.findViewById(R.id.button_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.button_around).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!mLocationSource.isActivated()) {
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d(TAG, "[onMapReady] ");

        mNaverMap = naverMap;
        mNaverMap.setLocationSource(mLocationSource);
        MapConfigMgr.getInstance().setNaverMap(mNaverMap);
        MapConfigMgr.getInstance().setContext(getContext());
        MapConfigMgr.getInstance().setHandleEventListener();
        MapConfigMgr.getInstance().setMapConfiguration(NaverMap.MapType.Navi);
        MapConfigMgr.getInstance().setCameraPosition();
        MapConfigMgr.getInstance().setPolyline();

        /* Marker Configuration */
        mInfoWindow = new InfoWindow();
        mInfoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                CharSequence strTag = (CharSequence) infoWindow.getMarker().getTag();
                if (strTag != null) {
                    return "User Information : " + (CharSequence) infoWindow.getMarker().getTag();
                } else {
                    return "User Information : n/a";
                }
            }
        });

        mListener = new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                Marker markerTemp = (Marker) overlay;

                if (markerTemp.getInfoWindow() == null) {
                    mInfoWindow.open(markerTemp);
                } else {
                    mInfoWindow.close();
                }
                toShowMsgPopupWindow(markerTemp.getTag().toString());
                toShowListPopupWindow();
                return true;
            }
        };

        if (FriendAdapterMgr.getInstance().loadFriendList()) {
            Log.d(TAG, "[onMapReady] There was loading friend list");
        }

        if (MapConfigMgr.getInstance().getCameraPosition() != null) {
            Log.d(TAG, "[onMapReady] There was MAP before this fragment");
            MapConfigMgr.getInstance().moveCameraPosition(
                    MapConfigMgr.getInstance().getCameraPosition()
            );
        }
    }

    public static void onLocationChange(LatLng latLng) {
        Log.d(TAG, "[onLocationChange] switch state follow:" + mIsClickedFollowCamera +", share:"+ mIsClickedSharePos);
        if (mIsClickedFollowCamera) {
            MapConfigMgr.getInstance().moveCameraPosition(MapConfigMgr.getInstance().getCurrentLocation());
        }
        String isShare = mIsClickedSharePos + ",";
        String currentPosition = String.valueOf(latLng.latitude) + ',' + String.valueOf(latLng.longitude);
        InterfaceForServerAPI.toSendMessageWithSocket(
                SocketMgr.getInstance(), mSrcID, "S0", ProtocolDefine.SID_RegisterPosition, isShare + currentPosition);
    }

    /**
     * to show the popup window for messaging to friend
     *
     * @param markerID
     */
    public void toShowMsgPopupWindow(String markerID) {
        Log.d(TAG, "[toShowMsgPopupWindow] create Node Popup Window");

        /* popupWindow */
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_node_info, null);

        if (mMsgPopupWindow != null) mMsgPopupWindow.dismiss();
        mMsgPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mMsgPopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        mMsgPopupWindow.showAtLocation(mView, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        mMsgPopupWindow.update(mView, 0, 70, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView markerTitle = popupView.findViewById(R.id.marker_name);        // set Text View for a selected marker
        final Button helloButton = popupView.findViewById(R.id.marker_msg_1);           // set Button for "hello" message
        final Button accidentButton = popupView.findViewById(R.id.marker_msg_2);        // set Button for "accident" message

        /* marker Info */
        String makerInfo = "[ To : " + markerID + " ]";
        final String dstID = markerID;
        markerTitle.setText(makerInfo);

        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "[onMapReady-setInfoWindowAdapter] helloButton onClick (SendString) ." + helloButton.getText());
                InterfaceForServerAPI.toSendMessageWithSocket(
                        SocketMgr.getInstance(), mSrcID, dstID, ProtocolDefine.SID_ToSendMessage, (String) helloButton.getText());
            }
        });
        accidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "[onMapReady-setInfoWindowAdapter] accidentButton onClick (SendString)" + accidentButton.getText());
                InterfaceForServerAPI.toSendMessageWithSocket(
                        SocketMgr.getInstance(), mSrcID, dstID, ProtocolDefine.SID_ToSendMessage, (String) accidentButton.getText());
            }
        });
    }

    public void toShowListPopupWindow() {
        /* popupWindow */
        if (FriendAdapterMgr.getInstance().getListFriend().size() <= 0) {
            Log.d(TAG, "[toShowListPopupWindow] mCountOfFriend is lesser then 1");
        } else if ((mNodePopupWindow != null) && mNodePopupWindow.isShowing()) {
            Log.d(TAG, "[toShowListPopupWindow] Node list popup is already created");
        } else {
            Log.d(TAG, "[toShowListPopupWindow] create Node Popup Window");

            /* Popup Window */
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View listPopupView = layoutInflater.inflate(R.layout.popup_node_list, null);

            mNodePopupWindow = new PopupWindow(listPopupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mNodePopupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
            mNodePopupWindow.showAtLocation(mView, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
            mNodePopupWindow.update(mView, 20, 70, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            /* Recycler View */
            RecyclerView mRecyclerView = listPopupView.findViewById(R.id.id_recycler_main_list); // findViewById(R.id.recyclerview_main_list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(FriendAdapterMgr.getInstance());

            /* Recycler behavior */
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), linearLayoutManager.getOrientation());
            mRecyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    public static Marker setMarker(LatLng latLng, String nodeType, String ID) {
        if (nodeType.equals("Friend")) {
            Marker marker = new Marker(latLng);
            marker.setMap(mNaverMap);

            marker.setTag(ID);
            marker.setCaptionText("Test Node");

            /* priority */
            marker.setZIndex(100);

            /* marker Color */
            marker.setIcon(MarkerIcons.BLUE);
            marker.setAlpha(1.0f);
            marker.setCaptionColor(Color.BLUE);
            marker.setCaptionHaloColor(Color.rgb(200, 255, 200));

            marker.setCaptionAligns(Align.Bottom);
            marker.setHideCollidedSymbols(true);
            marker.setOnClickListener(mListener);

            return marker;

            /* marker size */
//        marker.setWidth(Marker.SIZE_AUTO);
//        marker.setHeight(50);
//        marker.setCaptionRequestedWidth(200);
//        marker.setCaptionTextSize(16);

            /* marker position */
//        marker.setAnchor(new PointF(1,1));

            /* 원근감 */
//        marker.setIconPerspectiveEnabled();

        }
        return null;
    }

    /**
     * to add markers after receiving friends list from server
     *
     * @param srcID
     * @param latitude
     * @param longitude
     */
    public static void addFriendPosition(String srcID, Double latitude, Double longitude) {
        Log.d(TAG, "[toAddFriendPosition] srcID(" + srcID + "), latitude(" + latitude + "), longitude(" + longitude + ")");
        /* add node in map as marker */

        if (FriendAdapterMgr.getInstance().isContainFriend(srcID)) {
            Log.d(TAG, "[toAddFriendPosition] the Map is already have the item");
            return;
        }
        Marker marker = setMarker(new LatLng(latitude, longitude), "Friend", srcID);
        FriendAdapterMgr.getInstance().addFriendList(srcID, marker);
        FriendAdapterMgr.getInstance().notifyDataSetChanged();
    }

    public static void clearFriendPosition(){
        FriendAdapterMgr.getInstance().clearAllFriendList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(getContext(), "OptionItemSelected", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    /**
     * this handler is for socket communication.
     * the message is transmitted after receiving some message from server
     */
    public static class MySocketHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            int numOfComponent = 0;
            JSONObject json = null;
            try {
                json = new JSONObject(msg.obj.toString());
                Log.d(TAG, "[MySocketHandler-handleMessage] received message : " + msg.obj);

                String typeID = json.getString("typeID");
                /* todo. parser for translate the type ID to description */
                Log.d(TAG, "[MySocketHandler-handleMessage] 전달받은 서비스 종류 : (" + typeID + ")");

                /* todo. make a wrapper function to handle each service easily */
                if (typeID.equals(ProtocolDefine.SID_PutPosition)) {
                    String recv_payload = json.getString("payload"); // {length;srcID,position;srcID,position;}
                    String[] separated = recv_payload.split(";");
                    if (separated[0].equals("0")) {
                        Log.d(TAG, "[MySocketHandler-handleMessage] Number of friend is 0");
                        clearFriendPosition();
                    } else {
                        /* todo. to show only the shared position */
                        for (numOfComponent = 1; numOfComponent < separated.length; numOfComponent++) {
                            Log.d(TAG, "[MySocketHandler-handleMessage] Friend position : [" + separated[numOfComponent] + "]");
                            String[] position = separated[numOfComponent].split(",");
                            addFriendPosition(position[0], Double.parseDouble(position[1]), Double.parseDouble(position[2]));
                        }

                    }

                } else if (typeID.equals(ProtocolDefine.SID_PutMessage)) {
                    String recv_payload = json.getString("payload"); // {length;srcID,position;srcID,position;}
                    Log.d(TAG, "[MySocketHandler-handleMessage] Received num of friend msg :. " + recv_payload);
                    Toast.makeText(mContext, recv_payload + " from [whom]", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}