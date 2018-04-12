package com.ruthwikwarrier.cbmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ruthwik on 04-Jan-17.
 */

public class SharedPref {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Context context;
    String name;
    public SharedPref(Context con){
        this.context = con;
    }

    public void initSharedPreference (String name){

        sharedPreferences = context.getSharedPreferences(name, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public  void setSharedPreference (String key, String value){
        editor.putString(key, value);
        editor.commit();
    }
    public  void setSharedPreference (String key, int value){
        editor.putInt(key, value);
        editor.commit();
    }
    public  void setSharedPreference (String key, boolean value){
        editor.putBoolean(key, value);
        editor.commit();
    }



    public  String getStringSharedPreference (String key){
        String value;
        value = sharedPreferences.getString(key, null);
        return value;
    }

    public  int getIntSharedPreference (String key){
        int value;
        value = sharedPreferences.getInt(key, 0);
        return value;
    }

    public  Boolean getBoolSharedPreference (String key){
        Boolean value;
        value = sharedPreferences.getBoolean(key, false);
        return value;
    }

    public void removeSharedPreference (String key){

        editor.remove(key);
        editor.commit();
    }

    public void clearSharedPreference (){

        editor.clear();
        editor.commit();
    }

}
