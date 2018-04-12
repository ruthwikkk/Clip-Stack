package com.ruthwikwarrier.cbmanager.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AppUtils {

    public static String getFormatDate(Context context, Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
        return dateFormat.format(date);
    }

    public static String getFormatTime(Context context, Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(date);
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static String getAppName(String packageName, Context context){

        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo( packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        final String applicationName = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown App");
        return applicationName;
    }

    public static boolean saveJSONToFile(JSONArray array){

        try {
            Writer output = null;
            File file = new File(createFile());
            output = new BufferedWriter(new FileWriter(file));
            output.write(array.toString());
            output.close();
            return true;

        } catch (Exception e) {
            return false;
        }

    }

    private static String createFile(){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ClipMan");
        if(!myDir.exists()){
            myDir.mkdirs();
        }

        long n = System.currentTimeMillis();
        String fname = "Backup_"+ n +".json";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath().toString();
    }

    public static void showToast(Context context, String text, int length){
        Toast.makeText(context,text,length).show();
    }
}
