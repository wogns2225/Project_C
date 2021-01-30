package com.example.testapplication;

import android.app.Activity;
import android.os.Bundle;

import com.example.testapplication.DisplayMgr.onBackPressedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    private Activity mActivity = null;

    /* Manage Fragment*/
    private static List<WeakReference<Fragment>> mFragList = new ArrayList<WeakReference<Fragment>>();
    static long mLastTimeBackPressed;

    /* Popup Window */
    public static PopupWindow mPopupWindowExit = null;

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "[onAttachFragment]");
//        mFragList.add(new WeakReference(fragment));
    }

    public List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : mFragList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "[onBackPressed] All fragment's size : " + mFragList.size());
        for (WeakReference<Fragment> ref : mFragList) {
            Fragment fragment = ref.get();
            Log.d(TAG, "[onBackPressed] Fragment is : " + fragment.toString());
            if (fragment instanceof MapFragment) {
                /* todo. should be distinguished for each fragments */
                showPopupWindowExit(fragment.getView());
                return;
            } else if (fragment instanceof SignupFragment) {
                NavHostFragment.findNavController(fragment).navigate(R.id.action_SignupFragment_to_LoginFragment);
                return;
            }
        }

        //두 번 클릭시 어플 종료
        if (System.currentTimeMillis() - mLastTimeBackPressed < 1500) {
//            LoginFragment.getInstance().getOAuthLoginModule().logout(LoginFragment.getInstance().getContext());
            finish();
            return;
        }
        mLastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this, "One more \"back\" for Quit", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "[onOptionsItemSelected]");
                return true;
            case R.id.app_bar_search:
                Log.d(TAG, "[onOptionsItemSelected-app_bar_search]");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement
    }

    public void showPopupWindowExit(View view) {
        /* popupWindow */
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_exit, null);

        if (mPopupWindowExit != null && mPopupWindowExit.isShowing()) {
            mPopupWindowExit.dismiss();
        } else {
            mPopupWindowExit = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindowExit.setAnimationStyle(android.R.style.Animation_InputMethod);
            mPopupWindowExit.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
            mPopupWindowExit.update(view, 0, 0, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public static List<WeakReference<Fragment>> getFragList() {
        return mFragList;
    }

    public static void setFragList(List<WeakReference<Fragment>> fragList) {
        MainActivity.mFragList = fragList;
    }

    public Activity getActivity() {
        return mActivity;
    }

}