package com.faraaf.tictacdev.avmusicplayer.db;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.faraaf.tictacdev.avmusicplayer.Song;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongsRepositoryDb {
    private SongsDao mSongsDao;

    public static SongsRepositoryDb repository;

    public SongsRepositoryDb(Application application) {
        SongsRoomDatabase songsRoomDatabase = SongsRoomDatabase.getDatabase(application);
        mSongsDao = songsRoomDatabase.songsDao();
    }

    public static SongsRepositoryDb getInstance(Application application) {
        if (repository == null) {
            repository = new SongsRepositoryDb(application);
        }
        return repository;
    }


    public List<Song> getAllSongs() {
        return mSongsDao.getAllSongs();
    }

    public void addSongsToFavourites(Song song) {
        new insertFavouriteSong(mSongsDao).execute(song);
    }

    public boolean checkIfSongIsInFavourites(Song song) {
        boolean duplicate = false;
        AsyncTask<Song, Void, Boolean> asyncTask = new checkIfMovieIsInFavourites(mSongsDao).execute(song);
        try {
            duplicate = asyncTask.get();
        } catch (ExecutionException e) {
            Log.d(SongsRepositoryDb.class.toString(), e.toString());
        } catch (InterruptedException e) {
            Log.d(SongsRepositoryDb.class.toString(), e.toString());
        }
        return duplicate;
    }

    public void removeSongFromFavourites(Song song) {
        new deleteFavouriteSong(mSongsDao).execute(song);
    }

    public void removeAllSongsFromFavourites() {
        new deleteAllFavouriteSongs(mSongsDao).execute();
    }


    private static class insertFavouriteSong extends AsyncTask<Song, Void, Void> {
        private SongsDao mAsyncTaskDao;

        insertFavouriteSong(SongsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Song... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteFavouriteSong extends AsyncTask<Song, Void, Void> {
        private SongsDao mAsyncTaskDao;

        deleteFavouriteSong(SongsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Song... params) {
            mAsyncTaskDao.deleteSongFromFavourites(params[0].getId());
            return null;
        }
    }

    private static class deleteAllFavouriteSongs extends AsyncTask<Void, Void, Void> {
        private SongsDao mAsyncTaskDao;

        deleteAllFavouriteSongs(SongsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllSongsFromFavourites();
            return null;
        }
    }

    private static class checkIfMovieIsInFavourites extends AsyncTask<Song, Void, Boolean> {
        private SongsDao mAsyncTaskDao;

        checkIfMovieIsInFavourites(SongsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Boolean doInBackground(Song... songs) {
            if (mAsyncTaskDao.getSongById(songs[0].getId()) != null) {
                return true;
            }
            return false;
        }
    }

}
