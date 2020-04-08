package com.faraaf.tictacdev.avmusicplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.faraaf.tictacdev.avmusicplayer.Song;

@Database(entities = {Song.class}, version = 1)
public abstract class SongsRoomDatabase extends RoomDatabase {
    public abstract SongsDao songsDao();

    private static volatile SongsRoomDatabase INSTANCE;

    public static SongsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SongsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SongsRoomDatabase.class, "songs_database").fallbackToDestructiveMigration().allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
