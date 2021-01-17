package com.example.testapplication;

import android.os.Bundle;

import com.example.testapplication.DisplayMgr.onBackPressedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static long mLastTimeBackPressed;
    private List<WeakReference<Fragment>> mFragList = new ArrayList<WeakReference<Fragment>>();

    private static String TAG = "MainActivity";

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        mFragList.add(new WeakReference(fragment));
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
        for (WeakReference<Fragment> ref : mFragList) {
            Fragment fragment = ref.get();
//            Toast.makeText(this, "size : " + mFragList.size(), Toast.LENGTH_SHORT).show();
            if (fragment instanceof MapFragment) {
                /* todo. should be distinguished for each fragments */
                Toast.makeText(this, "MapFragment", Toast.LENGTH_SHORT).show();
                ((onBackPressedListener) fragment).onBackPressed();
                return;
            }
        }

        //두 번 클릭시 어플 종료
        if (System.currentTimeMillis() - mLastTimeBackPressed < 1500) {
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
}