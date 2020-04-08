package com.faraaf.tictacdev.avmusicplayer.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.faraaf.tictacdev.avmusicplayer.Song;

import java.util.List;

@Dao
public interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Song song);

    @Query("DELETE FROM songs_table")
    void deleteAllSongsFromFavourites();

    @Query("SELECT * from songs_table ORDER BY ID ASC")
    List<Song> getAllSongs();

    @Query("DELETE FROM songs_table WHERE songs_table.id == :songId")
    void deleteSongFromFavourites(long songId);

    @Query("SELECT * FROM songs_table WHERE songs_table.id == :songId")
    Song getSongById(long songId);

}
