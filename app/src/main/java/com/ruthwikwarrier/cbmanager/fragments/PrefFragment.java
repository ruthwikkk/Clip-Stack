package com.ruthwikwarrier.cbmanager.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ruthwikwarrier.cbmanager.R;
import com.ruthwikwarrier.cbmanager.database.DBHelper;
import com.ruthwikwarrier.cbmanager.utils.AppUtils;
import com.ruthwikwarrier.cbmanager.data.SharedPrefNames;
import com.ruthwikwarrier.cbmanager.services.CBWatchService;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;
    Preference exportPref, importPref, rateAppPref, sourcePref;
    Context context;
    DBHelper dbHelper;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        context = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        exportPref = findPreference(SharedPrefNames.PREF_EXPORT_CLIP);
        importPref = findPreference(SharedPrefNames.PREF_IMPORT_CLIP);
        rateAppPref = findPreference(SharedPrefNames.PREF_RATE_APP);
        sourcePref = findPreference(SharedPrefNames.PREF_VIEW_SOURCE);

        dbHelper = new DBHelper(context);

        setListeners();


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case SharedPrefNames.PREF_NOTIFICATION_SHOW:
                Log.e("PrefFragment","Preference Notification Show clicked");
                restartCBService();
                break;
            case SharedPrefNames.PREF_NOTIFICATION_PRIORITY:
                Log.e("PrefFragment","Preference Notification Priority clicked");
                restartCBService();
                break;
            case SharedPrefNames.PREF_START_SERVICE:

                if (sharedPreferences.getBoolean(SharedPrefNames.PREF_START_SERVICE, true)) {
                    Log.e("PrefFragment","Preference Start Service clicked. Status:"+sharedPreferences.getBoolean(SharedPrefNames.PREF_START_SERVICE, true));
                    if( !AppUtils.isMyServiceRunning(CBWatchService.class, getActivity()))
                        CBWatchService.startCBService(getActivity());
                    else
                        Log.e("MainActivity","CBWatch Service already running.");
                }else{
                    Log.e("PrefFragment","Preference Start Service clicked. Status:"+sharedPreferences.getBoolean(SharedPrefNames.PREF_START_SERVICE, true));
                    getActivity().stopService(new Intent(getActivity(),CBWatchService.class));
                }
                break;
            /*case SharedPrefNames.PREF_SOURCE_APP:
                getStatsPermission();
                break;*/

        }

    }

    private void restartCBService(){

        if(AppUtils.isMyServiceRunning(CBWatchService.class, getActivity())) {
            getActivity().stopService(new Intent(getActivity(), CBWatchService.class));
            CBWatchService.startCBService(getActivity());
        }else{
            CBWatchService.startCBService(getActivity());
        }

    }

    private void setListeners(){

        exportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                requestStoragePermission();
                return true;
            }
        });

        importPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                requestReadPermission();
                return true;
            }
        });

        rateAppPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                openPlayStore();
                return true;
            }
        });
    }

    private void getStatsPermission(){

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_need_permission)
                .setMessage(getString(R.string.dialog_permission_stats))
                .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                )
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .create()
                .show();
    }

    private void requestStoragePermission(){

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted,
                        Log.e("PrefFragment","Storage Permission granted");
                        JSONArray array = dbHelper.getBackupData(false);
                        Log.e("PrefFragment","Data:"+array);
                        if(AppUtils.saveJSONToFile(array))
                            AppUtils.showToast(context, "Backup created in SD Card/ClipMan", Toast.LENGTH_SHORT);
                        else
                            AppUtils.showToast(context, "Backup not created", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                            Log.e("PrefFragment","Storage Permission denied");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void requestReadPermission(){

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted,
                        Log.e("PrefFragment","Read Permission granted");
                        chooseFileAndDoImport();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                            Log.e("PrefFragment","Read Permission denied");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void chooseFileAndDoImport(){

        final String[] filePath = new String[1];
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(context,properties);
        dialog.setTitle("Select backup File");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                filePath[0] =  files[0];
                String jsonData = getDataFromJSONFile(filePath[0]);
                JSONArray array = null;
                try {
                    array = new JSONArray(jsonData);
                } catch (JSONException e) {
                    Log.e("PrefFragment","JSON Array creation error");
                    AppUtils.showToast(context, "Backup file corrupted", Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
                if(dbHelper.importBackupData(array))
                    AppUtils.showToast(context, "Imported Successfully", Toast.LENGTH_SHORT);
                else
                    AppUtils.showToast(context, "Something went wrong", Toast.LENGTH_SHORT);
            }
        });
        dialog.show();
    }

    private String getDataFromJSONFile(String filePath){

        File file = new File(filePath);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            //You'll need to add proper error handling here
            Log.e("PrefFragment","File Not Found Exception");
        } catch (IOException e) {
            Log.e("PrefFragment","IO Exception");
            e.printStackTrace();
        }
        return text.toString();
    }

    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(getActivity())) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    private void openPlayStore(){

        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
