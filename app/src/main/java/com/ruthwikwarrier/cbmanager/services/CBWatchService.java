package com.ruthwikwarrier.cbmanager.services;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;
import com.ruthwikwarrier.cbmanager.data.SharedPrefNames;
import com.ruthwikwarrier.cbmanager.database.DBHelper;
import com.ruthwikwarrier.cbmanager.model.ClipObject;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */

public class CBWatchService extends Service {


    private NotificationManager notificationManager;
    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

    private int pIntentId = 999;
    private boolean pinOnTop = false;
    private int notificationPriority = 0;
    private SharedPreferences preference;
    boolean allowService = true;
    boolean allowNotification = true;

    String TAG = "CB WatchService";
    ClipboardManager clipboardManager;

    DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();



        preference = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, getOreoNotification());
        }

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
        dbHelper = new DBHelper(this);

        readPreferences();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showSingleNotification();
        }


        Log.e(TAG," => onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG,"Showing Notification");


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (clipboardManager != null) {
            clipboardManager.removePrimaryClipChangedListener(clipChangedListener);
        }
        notificationManager.cancelAll();
        Log.e(TAG," => onDestroy()");
    }

    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck(getForegroundTask());
        }
    };

    private void performClipboardCheck(String sourceApp){

        if (!clipboardManager.hasPrimaryClip())
            return;
        String clipString;
        try {
            //Don't use CharSequence .toString()!
            CharSequence charSequence = clipboardManager.getPrimaryClip().getItemAt(0).getText();
            clipString = String.valueOf(charSequence);
        } catch (Error ignored) {
            return;
        }
        if (clipString.trim().isEmpty())
            return;
        if (clipString.equals("null"))
            return;
        Log.e(TAG,"Clipboard Data: "+clipString +" from "+ sourceApp);
        ClipObject clipObject = new ClipObject(clipString, new Date(), false);
        dbHelper.insertClipToHistory(clipObject);
    }

    public static void startCBService(Context context) {


       /* Intent intent = new Intent(context, CBWatchService.class);
        context.startService(intent);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, CBWatchService.class);
            context.startForegroundService(intent);
        } else {
            Intent intent = new Intent(context, CBWatchService.class);
            context.startService(intent);
        }

    }

    /*public static void startCBService(Context context) {

        Intent intent = new Intent(context, CBWatchService.class);
        context.startService(intent);

    }*/

    private void showSingleNotification() {

        if (!checkNotificationPermission()) {
            return;
        }

        Intent openMainDialogIntent = new Intent(this, ClipActionBridge.class).putExtra(ClipActionBridge.ACTION_CODE, ClipActionBridge.ACTION_OPEN_MAIN_DIALOG);
        PendingIntent pOpenMainDialogIntent = PendingIntent.getService(CBWatchService.this, pIntentId--, openMainDialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pOpenMainDialogIntent)
                .setContentTitle("Click to copy saved data")
                .setOngoing(pinOnTop)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_filter_none)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setColor(getResources().getColor(R.color.colorPrimaryLight));
               // .addAction(R.drawable.ic_action_add, getString(R.string.action_add), pOpenMainDialogIntent)


        switch (notificationPriority) {
            case 0:
                builder.setPriority(NotificationCompat.PRIORITY_MIN);
                break;
            case 1:
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                break;
            case 2:
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                break;
        }

        Notification notification = builder.build();

        notificationManager.notify(/*notification id*/0, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private Notification getOreoNotification(){

        Intent openMainDialogIntent = new Intent(this, ClipActionBridge.class).putExtra(ClipActionBridge.ACTION_CODE, ClipActionBridge.ACTION_OPEN_MAIN_DIALOG);
        PendingIntent pOpenMainDialogIntent = PendingIntent.getService(CBWatchService.this, pIntentId--, openMainDialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Channel description");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pOpenMainDialogIntent)
                .setContentTitle("Click to copy saved data")
                .setOngoing(pinOnTop)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_filter_none)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setColor(getResources().getColor(R.color.colorPrimaryLight));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();
        return notification;
    }

    private boolean checkNotificationPermission() {
        if (allowNotification && allowService) {
            return true;
        }
        notificationManager.cancelAll();
        return false;
    }

    private void readPreferences(){

        allowService = preference.getBoolean(SharedPrefNames.PREF_START_SERVICE, true);
        allowNotification = preference.getBoolean(SharedPrefNames.PREF_NOTIFICATION_SHOW, true);
        notificationPriority = Integer.parseInt(preference.getString(SharedPrefNames.PREF_NOTIFICATION_PRIORITY, "0"));
    }

    private String getForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        //Log.e(TAG, "Current App in foreground is: " + AppUtils.getAppName(currentApp, this));
        return AppUtils.getAppName(currentApp, this);
    }
}
