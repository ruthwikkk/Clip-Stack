package com.ruthwikwarrier.cbmanager.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ruthwikwarrier.cbmanager.model.ClipObject;

import java.util.Date;
import java.util.List;

@Dao
public interface ClipDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClipToHistory(ClipObject clipObject);

    @Query("SELECT * FROM cliphistory WHERE history = :text")
    ClipObject isExists(String text);

    @Update
    void updateClip(ClipObject clipObject);

    @Query("UPDATE cliphistory SET date = :date, star = :isStarred WHERE history = :text")
    void updateClipWithText(String text, Date date, boolean isStarred);

    @Query("SELECT * FROM cliphistory ORDER BY date DESC")
    List<ClipObject> readAllClipHistory();

    @Query("SELECT * FROM cliphistory WHERE star ORDER BY date DESC")
    List<ClipObject> readFavClipHistory();

    @Query("SELECT * FROM cliphistory WHERE id = :clipId")
    ClipObject readSingleClip(String clipId);

    @Delete
    void deleteClip(ClipObject clipObject);

    @Query("DELETE FROM cliphistory")
    void deleteAllClips();


}
