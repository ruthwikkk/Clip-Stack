package com.ruthwikwarrier.cbmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ruthwikwarrier.cbmanager.data.StringData;
import com.ruthwikwarrier.cbmanager.model.ClipObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 * Created by Ruthwik on 23-Mar-18.
 * -Ooo-ooO--Ooo-ooO--Ooo-ooO--Ooo-
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "clipdb";
    Context context;
    SQLiteDatabase db;

    private static final String TABLE_NAME = "cliphistory";

    private static final String ID = "id";
    private static final String CLIP_STRING = "history";
    private static final String CLIP_DATE = "date";
    private static final String CLIP_IS_STAR = "star";

    public DBHelper(Context con) {
        super(con, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=con;
    }

    private void openDB() {
        if (db == null) {
            db = this.getWritableDatabase();
        } else if (!db.isOpen()) {
            db = this.getWritableDatabase();
        }
    }

    private void closeDB() {
        if (db != null) {
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create clip history table
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + CLIP_DATE + " TIMESTAMP,"
                + CLIP_STRING + " TEXT,"
                + CLIP_IS_STAR + " BOOLEAN)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.e("DATABASE","Table "+TABLE_NAME+" created.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Creating tables again
        onCreate(sqLiteDatabase);
        //   Log.e("DATABASE","Database Upgraded");

    }

    public boolean insertClipToHistory(ClipObject clipObject){

        if(isClipExists(clipObject)){
            Log.e("DATABASE", "Clip already exists. Updated");
            updateClipWithText(clipObject);
            return true;
        }else{
            long timeStamp = clipObject.getDate().getTime();
            ContentValues values = new ContentValues();
            values.put(CLIP_DATE, timeStamp);
            values.put(CLIP_STRING, clipObject.getText());
            values.put(CLIP_IS_STAR, clipObject.isStarred());

            openDB();

            long row_id = db.insert(TABLE_NAME, null, values);
            if (row_id == -1) {
                Log.e("DATABASE", "Write db error: insertClipToHistory() => " + clipObject.getText());
                return false;
            }
            closeDB();
            return true;
        }

    }

    public boolean isClipExists(ClipObject clipObject){

        openDB();
        String[] COLUMNS = {CLIP_STRING, CLIP_DATE, CLIP_IS_STAR};
        String WHERE = CLIP_STRING +"=?";
        String[] WHERE_ARGS = {clipObject.getText()};
        Cursor c = db.query(TABLE_NAME, COLUMNS, WHERE, WHERE_ARGS, null, null, null);
        if(c.moveToFirst())
            return true;
        c.close();
        closeDB();
        return  false;

    }

    public boolean updateClip(ClipObject clipObject){

        long timeStamp = clipObject.getDate().getTime();
        ContentValues values = new ContentValues();
        values.put(CLIP_DATE, timeStamp);
        values.put(CLIP_STRING, clipObject.getText());
        values.put(CLIP_IS_STAR, clipObject.isStarred());
        String WHERE = ID +"=?";
        String[] WHERE_ARGS = {Integer.toString(clipObject.getId())};
        openDB();

        long row_id = db.update(TABLE_NAME, values, WHERE, WHERE_ARGS);
        if (row_id == -1) {
            Log.e("DATABASE", "Update db error: updateClip() => " + clipObject.getText());
            return false;
        }
        closeDB();
        return true;
    }

    public boolean updateClipWithText(ClipObject clipObject){

        long timeStamp = clipObject.getDate().getTime();
        ContentValues values = new ContentValues();
        values.put(CLIP_DATE, timeStamp);
        values.put(CLIP_STRING, clipObject.getText());
        values.put(CLIP_IS_STAR, clipObject.isStarred());
        String WHERE = CLIP_STRING +"=?";
        String[] WHERE_ARGS = {clipObject.getText()};
        openDB();

        long row_id = db.update(TABLE_NAME, values, WHERE, WHERE_ARGS);
        if (row_id == -1) {
            Log.e("DATABASE", "Update db error: updateClipWithText() => " + clipObject.getText());
            return false;
        }
        closeDB();
        return true;
    }

    public boolean updateClipFav(ClipObject clipObject){


        ContentValues values = new ContentValues();
        values.put(CLIP_IS_STAR, clipObject.isStarred());
        String WHERE = ID +"=?";
        String[] WHERE_ARGS = {Integer.toString(clipObject.getId())};
        openDB();

        long row_id = db.update(TABLE_NAME, values, WHERE, WHERE_ARGS);
        if (row_id == -1) {
            Log.e("DATABASE", "Update db error: updateClipFav() => " + clipObject.getText());
            return false;
        }
        closeDB();
        return true;
    }

    public ArrayList<ClipObject> readAllClipHistory(){

        ArrayList<ClipObject> list = new ArrayList<>();
        openDB();
        String sortOrder = CLIP_DATE + " DESC";
        String[] COLUMNS = {ID, CLIP_STRING, CLIP_DATE, CLIP_IS_STAR};
        Cursor c = db.query(TABLE_NAME, COLUMNS, null, null, null, null, sortOrder);
        while(c.moveToNext()){
            ClipObject clipObject = new ClipObject(c.getInt(0), c.getString(1),new Date( c.getLong(2)), c.getInt(3) > 0);
            list.add(clipObject);
            Log.e("DATABASE", ""+clipObject.getText()+"-"+clipObject.isStarred());
        }
        c.close();
        closeDB();
        return list;
    }

    public ArrayList<ClipObject> readFavClipHistory(){

        ArrayList<ClipObject> list = new ArrayList<>();
        openDB();
        String sortOrder = CLIP_DATE + " DESC";
        String[] COLUMNS = {ID, CLIP_STRING, CLIP_DATE, CLIP_IS_STAR};
        String WHERE = CLIP_IS_STAR +"=?";
        String[] WHERE_ARGS = {"1"};
        Cursor c = db.query(TABLE_NAME, COLUMNS, WHERE, WHERE_ARGS, null, null, sortOrder);
        while(c.moveToNext()){
            ClipObject clipObject = new ClipObject(c.getInt(0), c.getString(1),new Date( c.getLong(2)), c.getInt(3) > 0);
            list.add(clipObject);
        }
        c.close();
        closeDB();
        return list;
    }

    public ClipObject readSingleClip(String clipId){

        openDB();
        String[] COLUMNS = {ID, CLIP_STRING, CLIP_DATE, CLIP_IS_STAR};
        String WHERE = ID +"=?";
        String[] WHERE_ARGS = {clipId};
        Cursor c = db.query(TABLE_NAME, COLUMNS, WHERE, WHERE_ARGS, null, null, null);
        c.moveToFirst();
        ClipObject clipObject = new ClipObject(c.getInt(0), c.getString(1),new Date( c.getLong(2)), c.getInt(3) > 0);
        c.close();
        closeDB();
        return  clipObject;
    }



    public boolean deleteClip(int id){

        openDB();

        int row_id = db.delete(TABLE_NAME, ID + "=" + sqliteEscape(Integer.toString(id)), null);
        if (row_id == -1) {
            Log.e("DATABASE", "Write db error: deleteClip() => " + id);
            return false;
        }
        closeDB();
        return true;
    }

    public boolean deleteAllClips(){

        openDB();

        int row_id = db.delete(TABLE_NAME, CLIP_IS_STAR + "='" + 0 + "'", null);
        if(row_id == -1){
            Log.e("DATABASE", "Write db error: deleteAllClips()");
            return false;
        }
        closeDB();
        return true;
    }

    public JSONArray getBackupData(boolean isFavOnly){

        ArrayList<ClipObject> list = isFavOnly ? readFavClipHistory() : readAllClipHistory();
        JSONArray array = new JSONArray();
        for(ClipObject clip : list){
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put(StringData.JSON_CLIP_TEXT, clip.getText());
                jsonObject.put(StringData.JSON_CLIP_DATE, clip.getDate().getTime());
                jsonObject.put(StringData.JSON_CLIP_FAV, clip.isStarred());

            } catch (JSONException e) {
                Log.e("DATABASE", "Write JSON error: getBackupData()");
                e.printStackTrace();
            }

            array.put(jsonObject);
        }
        return array;
    }

    public boolean importBackupData(JSONArray array){

        for(int i=0;i<array.length();i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                long dateString = jsonObject.getLong(StringData.JSON_CLIP_DATE);
                ClipObject clipObject = new ClipObject(jsonObject.getString(StringData.JSON_CLIP_TEXT), new Date(dateString), jsonObject.getBoolean(StringData.JSON_CLIP_FAV));
                insertClipToHistory(clipObject);
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    private String sqliteEscape(String keyWord) {
        return DatabaseUtils.sqlEscapeString(keyWord);
    }
}
