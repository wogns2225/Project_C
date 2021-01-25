package com.example.testapplication.CommMgr;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NaverLoginAPIMgr {
    private static String TAG = "NaverLoginAPIMgr";
    private static LoginData mLoginData = new LoginData();

    /* todo. the LoginData class should be divided as another class to collect Login Data for Naver and Kakao */
    public static class LoginData {
        private String mID = "";
        private String mNickName = "";
        private String mEmail = "";
        private String mName = "";

        public String getID() {
            return mID;
        }

        public String getNickName() {
            return mNickName;
        }

        public String getEmail() {
            return mEmail;
        }

        public String getName() {
            return mName;
        }
    }

    public LoginData getLoginData() {
        return mLoginData;
    }

    private NaverLoginAPIMgr() {
    }

    public static NaverLoginAPIMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final NaverLoginAPIMgr INSTANCE = new NaverLoginAPIMgr();
    }

    public void callRequestAPIwithToken(String token) {
        String header = "Bearer " + token; // Bearer 다음에 공백 추가
        String apiURL = "https://openapi.naver.com/v1/nid/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);
        get(apiURL, requestHeaders);
    }

    private static void get(String apiUrl, final Map<String, String> requestHeaders) {
        final HttpURLConnection con = connect(apiUrl);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    con.setRequestMethod("GET");
                    for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                        con.setRequestProperty(header.getKey(), header.getValue());
                    }

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                        String receivedData = readBody(con.getInputStream());
                        Log.v(TAG, "[get] response body : " + receivedData);
                        parserJsonString(receivedData);
                    } else { // 에러 발생
                        Log.v(TAG, "[get] response body : " + readBody(con.getErrorStream()));
                    }
                } catch (IOException | JSONException e) {
                    throw new RuntimeException("API 요청과 응답 실패", e);
                } finally {
                    con.disconnect();
                }
            }
        };
        thread.start();

    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static void parserJsonString(String inputJsonString) throws JSONException {
        JSONObject json = new JSONObject(inputJsonString);
        Log.d(TAG, "[parserJsonString] received message : " + json);

        mLoginData.mID = json.getJSONObject("response").getString("id");
        mLoginData.mNickName = json.getJSONObject("response").getString("nickname");
        mLoginData.mEmail = json.getJSONObject("response").getString("email");
        mLoginData.mName = json.getJSONObject("response").getString("name");
    }
}
