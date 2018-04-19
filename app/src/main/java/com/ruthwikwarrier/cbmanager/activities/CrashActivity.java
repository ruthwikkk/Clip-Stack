package com.ruthwikwarrier.cbmanager.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.errorhandle.ExceptionHandler;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CrashActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_crash);
        ButterKnife.bind(this);

        initToolbar();
        Log.e("CrashActivity", getIntent().getStringExtra(Intent.EXTRA_TEXT));
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(AppUtils.dip2px(this, 4));
        } else {
            View mToolbarShadow = findViewById(R.id.my_toolbar_shadow);
            if (mToolbarShadow != null) {
                mToolbarShadow.setVisibility(View.VISIBLE);
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onBackPressed();
    }

    private void initToolbar(){

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Application Error");
        setSupportActionBar(toolbar);
    }
}
