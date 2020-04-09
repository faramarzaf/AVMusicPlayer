package com.faraaf.tictacdev.avmusicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class ServiceMusic extends Service implements
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener , AudioManager.OnAudioFocusChangeListener {


    private MediaPlayer player;
    private ArrayList<Song> songsList;
    private final IBinder musicBind = new MusicBinder();
    private int currentSongIndex;
    private AudioManager mAudioManager;
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }


    //    player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
 //  player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs) {
        songsList = theSongs;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        // handling calls
        if (focusChange <= 0) {
            //LOSS -> PAUSE
            player.pause();
        } else {
            //GAIN -> PLAY
            player.start();
        }
    }

    //binder
    public class MusicBinder extends Binder {
        ServiceMusic getService() {
            return ServiceMusic.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }


    //play a song
    public void playSong(Song song) {
        try {
            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(getApplicationContext(), Uri.parse(song.getSongLink()));
            player.prepare();
            player.start();
          } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    //    player.prepareAsync();
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
   /*     if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }*/

        if (currentSongIndex < (songsList.size() - 1)) {
            playSong(songsList.get(currentSongIndex + 1));
            currentSongIndex = currentSongIndex + 1;
        } else {
            playSong(songsList.get(0));
            currentSongIndex = 0;
        }


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }


    //playback methods
    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }



    public void playPrev() {
        if (currentSongIndex > 0) {
            Song song = songsList.get(currentSongIndex - 1);
            playSong(song);
            currentSongIndex = currentSongIndex - 1;
        } else {
            Song song = songsList.get(songsList.size() - 1);
            playSong(song);
            currentSongIndex = songsList.size() - 1;
        }

    }

    public void playNext() {
        if (currentSongIndex < (songsList.size() - 1)) {
            Song song = songsList.get(currentSongIndex + 1);
            playSong(song);
            currentSongIndex = currentSongIndex + 1;
        } else {
            playSong(songsList.get(0));
            currentSongIndex = 0;
        }
    }

    @Override
    public void onDestroy() {
        mAudioManager.abandonAudioFocus(this);
        stopForeground(true);
    }

}
