package com.ruthwikwarrier.cbmanager.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.errorhandle.ExceptionHandler;
import com.ruthwikwarrier.cbmanager.fragments.PrefFragment;

public class SettingsActivity extends AppCompatActivity {


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_settings);
        context = this.getBaseContext();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefFragment()).commit();


    }
}
