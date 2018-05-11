package com.ruthwikwarrier.cbmanager.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.data.StringData;
import com.ruthwikwarrier.cbmanager.database.AppDatabase;
import com.ruthwikwarrier.cbmanager.model.ClipObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    public static JSONArray getBackupData(AppDatabase db, boolean isFavOnly){

        List<ClipObject> list = isFavOnly ? db.clipDAO().readFavClipHistory() : db.clipDAO().readAllClipHistory();
        JSONArray array = new JSONArray();
        for(ClipObject clip : list){
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put(StringData.JSON_CLIP_TEXT, clip.getText());
                jsonObject.put(StringData.JSON_CLIP_DATE, clip.getDate().getTime());
                jsonObject.put(StringData.JSON_CLIP_FAV, clip.isStar());

            } catch (JSONException e) {
                Log.e("DATABASE", "Write JSON error: getBackupData()");
                e.printStackTrace();
            }

            array.put(jsonObject);
        }
        return array;
    }

    public static boolean importBackupData(AppDatabase db, JSONArray array){

        for(int i=0;i<array.length();i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                long dateString = jsonObject.getLong(StringData.JSON_CLIP_DATE);
                ClipObject clipObject = new ClipObject(jsonObject.getString(StringData.JSON_CLIP_TEXT), new Date(dateString), jsonObject.getBoolean(StringData.JSON_CLIP_FAV));
                db.clipDAO().insertClipToHistory(clipObject);
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return context.getString(R.string.about_version) + " " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
