package com.ruthwikwarrier.cbmanager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ruthwikwarrier.cbmanager.services.CBWatchService;


/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */

//Class to receive ACTION_BOOT_COMPLETED broadcast to start CBWatchService

public class LaunchServiceAtStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.e("LaunchServiceAtStartup", "LaunchServiceAtStartup");
            CBWatchService.startCBService(context);


        }

    }
}
