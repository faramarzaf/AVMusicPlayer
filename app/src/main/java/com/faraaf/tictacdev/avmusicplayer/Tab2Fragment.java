package com.faraaf.tictacdev.avmusicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class Tab2Fragment extends Fragment implements

        View.OnClickListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener, SeekBar.OnSeekBarChangeListener, PlayListAdapter.SongAdapterListener {

    Song receivedSong;
    Bundle bundle;
    private List<Song> mSongList = new ArrayList<>();
    private PlayListAdapter mAdapter;
    private RecyclerView mRecyclerViewSongs;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mMediaLayout;
    PlayListAdapter.SongAdapterListener listener;

    private ImageView mIvArtwork;
    private ImageView mIvPlay;
    private ImageView mIvPrevious;
    private ImageView mIvNext;

    private ImageView img_repeat;
    private ImageView img_shuffle;
    private ImageView img_fav;
    private SeekBar songProgressBar;
    private MediaPlayer mMediaPlayer;
    private TextView mTvCurrentDuration;
    private TextView mTvTotalDuration;
    private TimeUtil timeUtil;
    private int currentSongIndex;
    private TextView mTvTitle;


    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private AudioManager mAudioManager;

    // repeat
    boolean repeat = false;
    boolean shuffle = false;

    Context context;

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayer == null) return;
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();
            mTvTotalDuration.setText(String.format("%s", timeUtil.milliSecondsToTimer(totalDuration)));
            mTvCurrentDuration.setText(String.format("%s", timeUtil.milliSecondsToTimer(currentDuration)));
            int progress = (timeUtil.getProgressPercentage(currentDuration, totalDuration));
            songProgressBar.setProgress(progress);
            mHandler.postDelayed(this, 100);
        }
    };


    public Tab2Fragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context  = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);



        mRecyclerViewSongs = view.findViewById(R.id.recycler_view_playlist);
        mMediaPlayer = new MediaPlayer();
        timeUtil = new TimeUtil();
        mCoordinatorLayout = view.findViewById(R.id.coordinator_layout_playlist);
        mMediaLayout = view.findViewById(R.id.layout_media);
        mIvArtwork = view.findViewById(R.id.iv_artwork);
        mIvPlay = view.findViewById(R.id.iv_play);
        songProgressBar = view.findViewById(R.id.songProgressBar);
        mIvPrevious = view.findViewById(R.id.iv_previous);
        mIvNext = view.findViewById(R.id.iv_next);
        mTvCurrentDuration = view.findViewById(R.id.songCurrentDurationLabel);
        mTvTotalDuration = view.findViewById(R.id.songTotalDurationLabel);
        mTvTitle = view.findViewById(R.id.tv_title);
        img_shuffle = view.findViewById(R.id.img_shuffle);
        img_repeat = view.findViewById(R.id.img_repeat);
        setUpAdapter();
        setUpListeners();
        checkIncomingCalls();
        receiveData();
        retrive();

        img_repeat.setAlpha(.5f);
        img_shuffle.setAlpha(.5f);


        img_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (repeat) {
                    repeat = false;
                    img_repeat.setAlpha(.5f);
                } else {
                    repeat = true;
                    img_repeat.setAlpha(1f);
                    shuffle = false;
                    img_shuffle.setAlpha(.5f);
                }

            }
        });

        img_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffle) {

                    shuffle = false;

                    img_shuffle.setAlpha(.5f);


                } else {
                    shuffle = true;
                    img_shuffle.setAlpha(1f);
                    repeat = false;
                    img_repeat.setAlpha(.5f);
                }
            }
        });


 /*       mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (repeat) {

                 *//*   playSong(globalSong);
                    currentSongIndex = mSongList.indexOf(globalSong);*//*
                    playSong(mSongList.get(currentSongIndex));
                } else if (shuffle) {
                    // shuffle
                    Random random = new Random();
                    currentSongIndex = random.nextInt((mSongList.size() - 1) + 1);
                    //      currentSongIndex = random.nextInt(mSongList.size());
                    playSong(mSongList.get(currentSongIndex));

                } else {
                    // no repeat no shuffle
                    if (currentSongIndex < (mSongList.size() - 1)) {
                        playSong(mSongList.get(currentSongIndex + 1));
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        if (mSongList.size() > 0) {
                            playSong(mSongList.get(0));
                            currentSongIndex = 0;
                        }
                    }
                }
            }
        });*/


        return view;
    }


    private void setUpAdapter() {
        mAdapter = new PlayListAdapter(getActivity(), mSongList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewSongs.setLayoutManager(mLayoutManager);
        mRecyclerViewSongs.setItemAnimator(new DefaultItemAnimator());

        // add divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewSongs.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerViewSongs.addItemDecoration(dividerItemDecoration);

        mRecyclerViewSongs.setAdapter(mAdapter);
    }


    private void receiveData() {
        bundle = this.getArguments();
        if (bundle != null) {
            receivedSong = (Song) bundle.getSerializable("song");
            Log.d("TAG0", "receiveData: " + receivedSong.getTitle());

            //To save
            SharedPreferences mPrefs = getActivity().getPreferences(MODE_PRIVATE);
            // Song myObject = new Song();
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(receivedSong);
            prefsEditor.putString("MyObject", json);
            prefsEditor.commit();

        }


    }

    void retrive() {
        SharedPreferences mPrefs = getActivity().getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        receivedSong = gson.fromJson(json, Song.class);
        if (receivedSong != null) {
            Log.d("TAG1", "receiveData: " + receivedSong.getTitle());
            mSongList.add(new Song(receivedSong.getId(), receivedSong.getTitle(), receivedSong.getArtist(), receivedSong.getThumbnail(), receivedSong.getSongLink()));
            Collections.sort(mSongList, new Comparator<Song>() {
                public int compare(Song a, Song b) {
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
            mAdapter.notifyDataSetChanged();
        } else
            return;
    }

    private void checkIncomingCalls() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

    }

    private void setUpListeners() {
        mIvPlay.setOnClickListener(this);
        mIvPrevious.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        songProgressBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onSongSelected(Song song) {
        playSong(song);
        currentSongIndex = mSongList.indexOf(song);
    }

    private void playMusic() {
        if (!mMediaPlayer.isPlaying()) {
            mIvPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            mMediaPlayer.start();
        } else {
            mIvPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                playMusic();
                break;
            case R.id.iv_previous:
                playPreviousSong();
                break;
            case R.id.iv_next:
                playNextSong();
                break;
            default:
                break;
        }
    }

    public void playSong(Song song) {
        try {
            mMediaPlayer.reset();
      /*      @Deprecated
      * use this
      *            mp.setAudioAttributes(new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
            */

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setDataSource(context, Uri.parse(song.getSongLink()));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            // Displaying Song title
            //      isPlaying = true;
            mIvPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            mMediaLayout.setVisibility(View.VISIBLE);
            mTvTitle.setText(song.getTitle());
            Glide.with(getActivity()).load(song.getThumbnail()).placeholder(R.drawable.play).error(R.drawable.play).crossFade().centerCrop().into(mIvArtwork);
            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            songProgressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            songProgressBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        if (currentSongIndex < (mSongList.size() - 1)) {
            Song song = mSongList.get(currentSongIndex + 1);
            playSong(song);
            currentSongIndex = currentSongIndex + 1;
        } else {
            playSong(mSongList.get(0));
            currentSongIndex = 0;
        }
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            Song song = mSongList.get(currentSongIndex - 1);
            playSong(song);
            currentSongIndex = currentSongIndex - 1;
        } else {
            Song song = mSongList.get(mSongList.size() - 1);
            playSong(song);
            currentSongIndex = mSongList.size() - 1;
        }
    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        // handling calls
        if (focusChange <= 0) {
            //LOSS -> PAUSE
            mMediaPlayer.pause();
        } else {
            //GAIN -> PLAY
            mMediaPlayer.start();
        }
    }

    // auto go to the next song
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentSongIndex < (mSongList.size() - 1)) {
            playSong(mSongList.get(currentSongIndex + 1));
            currentSongIndex = currentSongIndex + 1;
        } else {
            if (mSongList.size()>0){

                playSong(mSongList.get(0));
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mMediaPlayer.getDuration();
        int currentPosition = timeUtil.progressToTimer(seekBar.getProgress(), totalDuration);
        mMediaPlayer.seekTo(currentPosition);
        updateProgressBar();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioManager.abandonAudioFocus(this);
/*        *AudioManager.abandonAudioFocus(this);
     * Added in API level 8
     * Deprecated in API level 26
     * use abandonAudioFocusRequest(android.media.AudioFocusRequest)*/

        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

}

