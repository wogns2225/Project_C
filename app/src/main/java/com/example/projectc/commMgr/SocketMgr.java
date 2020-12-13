package com.example.projectc.commMgr;

import android.os.Message;
import android.util.Log;

import com.example.projectc.MainActivity_display;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketMgr {
    //    socket test
    String TAG = "SocketMgr";
    final String host_ip = "192.168.0.5";
    final int port = 20000;

    Socket mSocket;

    DataOutputStream output_stream;
    DataInputStream input_Stream;

    boolean mStocketStatue = false;

    public void createSocket() {
        Log.d(TAG, "[createSocket] Start");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "[createSocket] ip : [" + host_ip + "] port : [" + port + "]");
                try {
                    mSocket = new Socket(host_ip, port);
                    output_stream = new DataOutputStream(mSocket.getOutputStream());
                    input_Stream = new DataInputStream(mSocket.getInputStream());
                    mStocketStatue = true;
                } catch (IOException e) {
                    Log.e(TAG, "[createSocket] is failed");
                    e.printStackTrace();
                }

                while (mStocketStatue) {
                    byte[] bufRcv = new byte[1024];
                    int rcv_size = 0;
                    try {
                        Log.d(TAG, "[createSocket] socket state" + mSocket.isConnected());
                        rcv_size = input_Stream.read(bufRcv);
//                        MainActivity_display.mSocketRcvHandler.post(MainActivity_display.ru)
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "[createSocket] read process is failed");
                        try {
                            closeSocket();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    String rcv_str = new String(bufRcv, 0, rcv_size);
                    if (rcv_size != 0) {
                        Log.d(TAG, "[createSocket] read data for received size : [" + rcv_size + "], rcv_str :[" + rcv_str + "]");
                        Message msg = MainActivity_display.mMySocketHandler.obtainMessage();
                        msg.obj = rcv_str;
                        MainActivity_display.mMySocketHandler.sendMessage(msg);
                    }
                }
//                Log.d(TAG, "[createSocket] rcv thread is finished");
            }
        }).start();
    }

    public void send(final String data) {
        Log.d(TAG, "[send] check socket : " + mStocketStatue);
        if (!mStocketStatue) {
            Log.d(TAG, "[send] socket was closed. try to create socket again");
            createSocket();
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        output_stream.writeUTF(data);
                        output_stream.flush();
                        Log.d(TAG, "[send] send message : [" + data + "]");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void closeSocket() throws IOException {
        if (mSocket.isConnected()) {
            mSocket.close();
            mStocketStatue = false;
            Log.d(TAG, "[closeSocket] is success");
        } else {
            Log.e(TAG, "[closeSocket] is failed");
        }
    }
}
