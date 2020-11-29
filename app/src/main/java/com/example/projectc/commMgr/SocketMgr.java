package com.example.projectc.commMgr;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SocketMgr {
    //    socket test
    String TAG = "SocketMgr";

    public void send(String data){
        String host_ip = "192.168.0.5";
        int port = 20000;

        try {
            Socket sock = new Socket(host_ip, port);
            Log.i(TAG,"create socket ip : [" + host_ip + "] port : [" + port + "]");

            DataOutputStream out_stream = new DataOutputStream(sock.getOutputStream());
            out_stream.writeUTF(data);
            out_stream.flush();
            // out_stream.flush();
            Log.i(TAG,"send message : [" + data + "]");
            byte[] bufRcv = new byte[1024];

            DataInputStream inputStream = new DataInputStream(sock.getInputStream());
            int rcv_size = inputStream.read(bufRcv);
            String recv_str = new String(bufRcv, 0, rcv_size);

            Log.i(TAG,"read data for received size : ["+ rcv_size + "], recv_str :[" + recv_str + "]");
            System.out.println("println str : " + recv_str);

            sock.close();
        }catch (Exception e){
            e.getMessage();
        }
    }
}
