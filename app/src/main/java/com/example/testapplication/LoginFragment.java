package com.example.testapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapplication.CommMgr.NaverLoginAPIMgr;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

public class LoginFragment extends Fragment {
    private static String TAG = "LoginFragment";
    private static OAuthLogin mOAuthLoginModule = null;

    public Context mContext;
    MyOAuthLoginHandler mOAuthLoginHandler;

    public OAuthLogin getOAuthLoginModule() {
        return mOAuthLoginModule;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "[onAttach]");
        mContext = context;
        mOAuthLoginHandler = new MyOAuthLoginHandler(mContext, LoginFragment.this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "[onCreate]");
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* todo. (Test) Remove this */
        moveFragment(view);
        loginNaver(view);
    }

    public void moveFragment(View view) {
        view.findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_LoginFragment_to_SignupFragment);
            }
        });
    }

    public void loginNaver(View view) {
        view.findViewById(R.id.button_login_naver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOAuthLoginModule == null) {
                    mOAuthLoginModule = OAuthLogin.getInstance();
                }
                /* init the Auth Object for NAVER */
                mOAuthLoginModule.init(
                        getContext()
                        , getString(R.string.naver_client_id)
                        , getString(R.string.naver_client_secret)
                        , getString(R.string.naver_client_name)
                );
                mOAuthLoginModule.startOauthLoginActivity(getActivity(), mOAuthLoginHandler);
            }
        });
    }

    private static class MyOAuthLoginHandler extends OAuthLoginHandler {
        Context mContext;
        Fragment mFragment;

        public MyOAuthLoginHandler(Context context, Fragment fragment) {
            mContext = context;
            mFragment = fragment;
        }

        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);
                Log.v(TAG, "[MyOauthLoginHandler] " +
                        ", accessToken : [" + accessToken +
                        ", refreshToken : [" + refreshToken +
                        ", expiresAt : [" + expiresAt +
                        ", tokenType : [" + tokenType + "]");

                NavHostFragment.findNavController(mFragment).navigate(R.id.action_LoginFragment_to_MapFragment);

                NaverLoginAPIMgr.getInstance().callRequestAPIwithToken(accessToken);

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Log.d(TAG, "errorCode:" + errorCode + ", errorDesc:" + errorDesc);
            }
        }

    }
}