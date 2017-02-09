package com.ronda.bledatatransdemo.base;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Activity的基类，方便进行后来的开发
 * Created by HandsomeDragon_Wu on 2016-01-15.
 */
public class BaseActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不自动弹出输入法
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppManager.getInstance().addActivity(this);
        AppManager.logActivityStack();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hiddenNavigationBar();
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(this);
        //setContentView(R.layout.activity_empty);
        AppManager.logActivityStack();
        System.gc();
    }

    protected void initActionBar(String title) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // ActionBar 中左边箭头的事件
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item); // 必须要这样写
    }


    private void hiddenNavigationBar() {
        int flags;
        int curApiVersion = Build.VERSION.SDK_INT;
        if (curApiVersion >= Build.VERSION_CODES.KITKAT) {
            // This work only for android 4.4+
            // hide navigation bar permanently in android activity
            // touch the screen, the navigation bar will not show
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
        } else {
            // touch the screen, the navigation bar will show
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // must be executed in main thread :)
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private void hiddenNavigationBar1() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                new Handler().post(new Runnable() {
                                       @Override
                                       public void run() {
                                           hiddenNavigationBar();
                                       }

                                   }
                );
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(this, "onTouchEvent", Toast.LENGTH_SHORT).show();
//        return super.onTouchEvent(event);
        return  true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //AppManager.getInstance().removeActivity(AppManager.getInstance().currentActivity());
            finish();


        }
        return super.onKeyDown(keyCode, event);
    }
}

