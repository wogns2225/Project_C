package com.example.projectc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * client 정보를 넣어준다.
     */
    private static String OAUTH_CLIENT_ID = "cknsa1k3G_xZP5DOWTBQ";
    private static String OAUTH_CLIENT_SECRET = "cknsa1k3G_xZP5DOWTBQ";
    private static String OAUTH_CLIENT_NAME = "무테스트";

    private String TAG = "MainActivity";

    LinearLayout ll_naver_login;
    Button btn_logout;

    OAuthLogin mOAuthLoginModule;
    private static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        ll_naver_login = findViewById(R.id.ll_naver_login);
        btn_logout = findViewById(R.id.btn_logout);

//        mMacAddress = getMACAddress("wlan0");
//        Log.d(TAG, "[onCreate] getMACAddress : " + macAddress);

        ll_naver_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule = OAuthLogin.getInstance();
                mOAuthLoginModule.init(
                        mContext
                        ,OAUTH_CLIENT_ID
                        ,OAUTH_CLIENT_SECRET
                        ,OAUTH_CLIENT_NAME
                        //,OAUTH_CALLBACK_INTENT
                        // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
                );

                @SuppressLint("HandlerLeak")
                OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
                    @Override
                    public void run(boolean success) {
                        Log.d(TAG, "[OAuthLoginHandler] result :" + success);

                        if (success) {
                            String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                            String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                            long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                            String tokenType = mOAuthLoginModule.getTokenType(mContext);

                            Log.d(TAG,"accessToken : "+ accessToken);
                            Log.d(TAG,"refreshToken : "+ refreshToken);
                            Log.d(TAG,"expiresAt : "+ expiresAt);
                            Log.d(TAG,"tokenType : "+ tokenType);

//                            Intent intent_change_disp = new Intent(MainActivity.this, MainActivity_display.class);
//                            startActivity(intent_change_disp);
                        } else {
                            String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                            Log.d(TAG, "[setOnClickListener] errorCode:" + errorCode + ", errorDesc:" + errorDesc);
                            Toast.makeText(mContext, "[setOnClickListener] Login Failed", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent_change_disp = new Intent(MainActivity.this, MainActivity_display.class);
                            startActivity(intent_change_disp);
                    }
                };
                Log.d(TAG, "[setOnClickListener] after Login Handler");
                mOAuthLoginModule.startOauthLoginActivity(MainActivity.this, mOAuthLoginHandler);

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule.logout(mContext);
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
}