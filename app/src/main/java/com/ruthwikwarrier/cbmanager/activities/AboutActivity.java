package com.ruthwikwarrier.cbmanager.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.services.ClipActionBridge;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.ll_card_about_2_shop) LinearLayout ll_card_about_2_shop;
    @BindView(R.id.ll_card_about_2_email) LinearLayout ll_card_about_2_email;
    @BindView(R.id.ll_card_about_2_git_hub) LinearLayout ll_card_about_2_git_hub;
    @BindView(R.id.text_about_version) TextView tv_about_version;
    @BindView(R.id.toolbar_about) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initToolbar();

        //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        initView();
    }

    public void initView() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_about_card_show);
        ScrollView scroll_about = findViewById(R.id.scroll_about);
        scroll_about.startAnimation(animation);

        ll_card_about_2_shop.setOnClickListener(this);
        ll_card_about_2_email.setOnClickListener(this);
        ll_card_about_2_git_hub.setOnClickListener(this);


        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setStartOffset(600);


        tv_about_version.setText(AppUtils.getVersionName(this));
        tv_about_version.startAnimation(alphaAnimation);
    }

    private void initToolbar(){

        toolbar.setNavigationIcon(R.mipmap.ic_launcher_round);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_card_about_2_shop:
                openIntent(ClipActionBridge.ACTION_LIKE_APP);
                break;
            case R.id.ll_card_about_2_email:
                openIntent(ClipActionBridge.ACTION_FEED_BACK);
                break;
            case R.id.ll_card_about_2_git_hub:
                break;
        }
    }

    private void openIntent(int action){
        Intent intent = new Intent(this, ClipActionBridge.class)
                .putExtra(Intent.EXTRA_TEXT, "")
                .putExtra(ClipActionBridge.ACTION_CODE, action);
        startService(intent);

    }
}
