package com.example.projectc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class MainActivity_display extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    String TAG = "MainActivity_Display";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display);

//        map test
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "next from the map");

//        socket test
        Button btn_back;
        Button btn_send_msg;

        btn_back = (Button) findViewById(R.id.button_back);
        btn_send_msg = (Button) findViewById(R.id.button_msg);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_change_disp = new Intent(MainActivity_display.this, MainActivity.class);
                startActivity(intent_change_disp);

            }
        });

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = "test socket function";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("MainActivity_display", "[send btn clicked]");
                        send(data);
                    }
                }).start();
            }
        });
    }
//      map test
    @Override
    public void onMapReady(final GoogleMap googleMap){
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);


        // 기존에 사용하던 다음 2줄은 문제가 있습니다.

        // CameraUpdateFactory.zoomTo가 오동작하네요.
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));
    }

//    socket test
    public void send(String data){
        String host_ip = "192.168.0.5";
        int port = 20000;

        try {
            Socket sock = new Socket(host_ip, port);
            Log.i("MainActivity_display","create socket ip : [" + host_ip + "] port : [" + port + "]");

            DataOutputStream out_stream = new DataOutputStream(sock.getOutputStream());
            out_stream.writeUTF(data);
            out_stream.flush();
            // out_stream.flush();
            Log.i("MainActivity_display","send message : [" + data + "]");
            byte[] bufRcv = new byte[1024];

            DataInputStream inputStream = new DataInputStream(sock.getInputStream());
            int rcv_size = inputStream.read(bufRcv);
            String recv_str = new String(bufRcv, 0, rcv_size);

            Log.i("MainActivity_display","read data for received size : ["+ rcv_size + "], recv_str :[" + recv_str + "]");
            System.out.println("println str : " + recv_str);

            sock.close();
        }catch (Exception e){
            e.getMessage();
        }
    }
}
