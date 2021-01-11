package com.example.testapplication.CommMgr;


import android.os.Message;
import android.util.Log;

import com.example.testapplication.MapFragment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketMgr {
    private SocketMgr() {}

    public static SocketMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final SocketMgr INSTANCE = new SocketMgr();
    }

    final String TAG = "SocketMgr";
    final String host_ip = "192.168.0.5";
    final int port = 20000;

    public Socket getSocket() {
        return mSocket;
    }

    public void setSocket(Socket socket) {
        this.mSocket = socket;
    }

    private Socket mSocket = null;

    DataOutputStream output_stream;
    DataInputStream input_Stream;

    boolean mSocketStatus = false;

    public void createSocket() {
        Log.d(TAG, "[createSocket] Start");

        if (mSocketStatus){
            Log.d(TAG, "[createSocket] socket is already created, so skip");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "[createSocket] ip : [" + host_ip + "] port : [" + port + "]");
                try {
                    mSocket = new Socket(host_ip, port);
                    if(mSocket == null){
                        mSocketStatus = false;
                        return;
                    }
                    output_stream = new DataOutputStream(mSocket.getOutputStream());
                    input_Stream = new DataInputStream(mSocket.getInputStream());
                    mSocketStatus = true;
                } catch (IOException e) {
                    Log.e(TAG, "[createSocket] is failed \n[ exception message " + e.getMessage() + "]");
                    e.printStackTrace();
                }

                while (mSocketStatus) {
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
                        Message msg = MapFragment.mMySocketHandler.obtainMessage();
                        msg.obj = rcv_str;
                        MapFragment.mMySocketHandler.sendMessage(msg);
                    }
                }
//                Log.d(TAG, "[createSocket] rcv thread is finished");
            }
        }).start();
    }

    public void send(final String data) {
        Log.d(TAG, "[send] check socket : " + mSocketStatus);
        if (!mSocketStatus) {
            Log.d(TAG, "[send] socket was closed. try to create socket again");
            createSocket();
        }
        try {
            if(!mSocketStatus) {
                Log.d(TAG, "[send] FAIL to create socket");
                return;
            }
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
            mSocketStatus = false;
            Log.d(TAG, "[closeSocket] is success");
        } else {
            Log.e(TAG, "[closeSocket] is failed");
        }
    }
}
