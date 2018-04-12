package com.ruthwikwarrier.cbmanager.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.errorhandle.ExceptionHandler;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;
import com.ruthwikwarrier.cbmanager.database.DBHelper;
import com.ruthwikwarrier.cbmanager.model.ClipObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_addedit) Toolbar toolbar;
    @BindView(R.id.fab_addedit_save) FloatingActionButton btnSave;
    @BindView(R.id.edt_addedit_clip) EditText edtText;

    ClipObject clipObject;
    DBHelper dbHelper;

    MenuItem itemStar;

    boolean isStarred = false;
    boolean isFromAdd = false;
    String clip_id = "";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        context = this.getBaseContext();
        setContentView(R.layout.activity_add_edit);
        dbHelper = new DBHelper(context);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        isFromAdd = intent.getBooleanExtra(MainActivity.EXTRA_IS_FROM_ADD,false);
        Log.e("AddEditActivity","isFromAdd: "+isFromAdd);
        if(!isFromAdd){
            clip_id = intent.getStringExtra("CLIP_ID");
            clipObject = dbHelper.readSingleClip(clip_id);
            isStarred = clipObject.isStarred();
            edtText.setText(clipObject.getText());
            Log.e("AddEditActivity","Clip Id: "+clip_id);
            Log.e("AddEditActivity","Clip Text: "+clipObject.getText());
            Log.e("AddEditActivity","Clip Starred: "+clipObject.isStarred());
        }


        initViews();
        initToolbar();


    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(AppUtils.dip2px(context, 4));
        } else {
            View mToolbarShadow = findViewById(R.id.my_toolbar_shadow);
            if (mToolbarShadow != null) {
                mToolbarShadow.setVisibility(View.VISIBLE);
            }
        }

        super.onResume();
    }

    private void initToolbar(){

        // toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
        toolbar.setTitleTextColor(Color.WHITE);
        if(isFromAdd)
            toolbar.setTitle("Add New");
        else
            toolbar.setTitle("Edit Clip");
        setSupportActionBar(toolbar);
    }

    private void initViews(){

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = edtText.getText().toString();
                text =text.trim();

                if( text.length() > 0 ){

                    if(isFromAdd){
                        clipObject = new ClipObject (text,new Date(), isStarred);
                        dbHelper.insertClipToHistory(clipObject);
                    }
                    else{
                        clipObject = new ClipObject(Integer.parseInt(clip_id), text, new Date(), isStarred);
                        dbHelper.updateClip(clipObject);
                    }

                    Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
                    finish();
                }else
                    Toast.makeText(context,"Empty !",Toast.LENGTH_SHORT).show();

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        itemStar = menu.findItem(R.id.action_star);
        setStarredIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){

            case R.id.action_star:
                onStarredMenuClicked();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void onStarredMenuClicked() {
        isStarred = !isStarred;
        setStarredIcon();
    }

    private void setStarredIcon() {

        //Function to set starred icon
        if (itemStar == null) return;
        if (isStarred) {
            itemStar.setIcon(R.drawable.ic_action_star_white);
        } else {
            itemStar.setIcon(R.drawable.ic_action_star_outline_white);
        }
    }
}
