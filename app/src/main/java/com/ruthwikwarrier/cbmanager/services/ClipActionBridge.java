package com.ruthwikwarrier.cbmanager.services;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.activities.AddEditActivity;
import com.ruthwikwarrier.cbmanager.activities.MainActivity;
import com.ruthwikwarrier.cbmanager.activities.MainDialogActivity;

public class ClipActionBridge extends IntentService {

    public final static String TAG = "ClipActionBridge";

    public final static String ACTION_CODE = "com.ruthwikwarrier.cbmanager.actionCode";
    public final static int ACTION_COPY = 1;
    public final static int ACTION_ADD = 2;
    public final static int ACTION_EDIT = 3;
    public final static int ACTION_OPEN_MAIN_DIALOG = 5;
    public final static int ACTION_LIKE_APP = 6;
    public final static int ACTION_FEED_BACK = 7;
    public final static int ACTION_ERROR = 9;


    public Handler handler;
    public ClipActionBridge() {
        super("ClipActionBridge");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate()");
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);

        if (intent == null)
            return;

        String clipText = intent.getStringExtra(Intent.EXTRA_TEXT);
        int actionCode = intent.getIntExtra(ACTION_CODE, 0);

        Log.e(TAG, "ACTION CODE: " + actionCode);
        Log.e(TAG, "TEXT: " + clipText);

        switch (actionCode){

            case 0:
                break;
            case ACTION_COPY:
                copyTextToCB(clipText);
                return;
            case ACTION_ADD:
                openAddActivity();
                return;
            case ACTION_EDIT:
                openEditActivity(clipText);
                return;
            case ACTION_OPEN_MAIN_DIALOG:
                openMainDialogActivity();
                return;
            case ACTION_LIKE_APP:
                openPlayStore();
                return;
            case ACTION_FEED_BACK:
                openFeedback();
                return;
            case ACTION_ERROR:
                showErrorLog(clipText);
                return;

        }


    }

    private void openMainDialogActivity() {
        //open by this will be auto closed when copy.
        Log.e(TAG,"Open Dialog Activity calling");
        Intent i = new Intent(this, MainDialogActivity.class)
                .putExtra(MainActivity.EXTRA_IS_FROM_NOTIFICATION, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }

    private void copyTextToCB(final String clipText){

        handler.post(new Runnable() {
            @Override
            public void run() {
                //copy clips to clipboard
                ClipData clipData = ClipData.newPlainText("text", clipText);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clipData );
                Log.e(TAG,"Clip >"+clipText+"< copied to clipboard.");

                Toast.makeText(ClipActionBridge.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openAddActivity() {

        Intent i = new Intent(this, AddEditActivity.class)
                .putExtra(MainActivity.EXTRA_IS_FROM_ADD, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void openEditActivity(String id) {

        Intent i = new Intent(this, AddEditActivity.class)
                .putExtra("CLIP_ID",id)
                .putExtra(MainActivity.EXTRA_IS_FROM_ADD, false);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void openPlayStore(){

        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void openFeedback(){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/email");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"ruthwikwarrier@live.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+" Feedback");
        // intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
        startActivity(Intent.createChooser(intent, "Send Feedback"));

    }

    private void showErrorLog(String log){
        Log.e(TAG,"\n"+log);
    }
}
