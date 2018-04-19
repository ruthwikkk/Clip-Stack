package com.ruthwikwarrier.cbmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.ruthwikwarrier.cbmanager.R;

public class MainDialogActivity extends MainActivity {

    @Override
    public void setContentView(int layoutResID) {

        if(layoutResID == R.layout.activity_main){
            super.setContentView(R.layout.activity_main_dialog);
        }else
            super.setContentView(layoutResID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(MainActivity.EXTRA_IS_FROM_NOTIFICATION, true);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean returnInt = super.onCreateOptionsMenu(menu);
        menu.removeGroup(R.id.menu_group);
        return returnInt;
    }

}
