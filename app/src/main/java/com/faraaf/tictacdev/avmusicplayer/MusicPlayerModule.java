package com.faraaf.tictacdev.avmusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MusicPlayerModule extends RelativeLayout implements
        SongAdapter.SongAdapterListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener, SeekBar.OnSeekBarChangeListener {


    private View rootView;
    private TypedArray typedArray;
    CoordinatorLayout coordinatorLayout;
    RecyclerView recycler_view;
    LinearLayout layout_media;
    SeekBar songProgressBar;
    LinearLayout timerDisplay;
    TextView songCurrentDurationLabel;
    TextView songTotalDurationLabel;
    ImageView iv_artwork;
    TextView tv_title;
    ImageView img_repeat;
    ImageView img_shuffle;
    ImageView iv_previous;
    ImageView iv_play;
    ImageView iv_next;

    private static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private List<Song> mSongList = new ArrayList<>();
    private SongAdapter mAdapter;
    private TimeUtil timeUtil;
    private int currentSongIndex;
    private Handler mHandler = new Handler();
    private AudioManager mAudioManager;
    boolean repeat = false;
    boolean shuffle = false;
    private MediaPlayer mMediaPlayer;

    Context globalContext;


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayer == null) return;
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();
            songTotalDurationLabel.setText(String.format("%s", timeUtil.milliSecondsToTimer(totalDuration)));
            songCurrentDurationLabel.setText(String.format("%s", timeUtil.milliSecondsToTimer(currentDuration)));
            int progress = (timeUtil.getProgressPercentage(currentDuration, totalDuration));
            songProgressBar.setProgress(progress);
            mHandler.postDelayed(this, 100);
        }
    };


    public MusicPlayerModule(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.globalContext = context;
        setUpAdapter();
        setUpListeners();
        getPermission();
        checkIncomingCalls();

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


        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (repeat) {

                 /*   playSong(globalSong);
                    currentSongIndex = mSongList.indexOf(globalSong);*/
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
                        if (mSongList.size()>0){
                            playSong(mSongList.get(0));
                            currentSongIndex = 0;
                        }
                    }
                }
            }
        });
    }

   /* public MusicPlayerModule(Context context) {
        super(context);
        init(context);
        this.globalContext = context;
        setUpAdapter();
        setUpListeners();
        getPermission();
        checkIncomingCalls();

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


        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
                        playSong(mSongList.get(0));
                        currentSongIndex = 0;
                    }
                }
            }
        });
    }*/

    private void checkIncomingCalls() {
        mAudioManager = (AudioManager) globalContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    void getPermission() {
        Dexter.withActivity((Activity) globalContext)
                .withPermissions(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            getSongList();
                        } else
                            Toast.makeText(globalContext, "Sorry! You denied the permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void setUpAdapter() {
        mAdapter = new SongAdapter(globalContext.getApplicationContext(), mSongList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(globalContext.getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);
    }

    private void setUpListeners() {
        //       ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        //  new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerViewSongs);
        iv_play.setOnClickListener(this);
        iv_previous.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        songProgressBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void getSongList() {
        //retrieve item_song info
        ContentResolver musicResolver = globalContext.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int albumID = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int songLink = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Uri thisSongLink = Uri.parse(musicCursor.getString(songLink));
                long some = musicCursor.getLong(albumID);
                Uri uri = ContentUris.withAppendedId(sArtworkUri, some);
                mSongList.add(new Song(thisId, thisTitle, thisArtist, uri.toString(), thisSongLink.toString()));
            }
            while (musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();

        // Sort music alphabetically
        Collections.sort(mSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onSongSelected(Song song) {
        playSong(song);
        currentSongIndex = mSongList.indexOf(song);
    }

    private void playMusic() {
        if (!mMediaPlayer.isPlaying()) {
            iv_play.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            mMediaPlayer.start();
        } else {
            iv_play.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
            mMediaPlayer.pause();
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

            mMediaPlayer.setDataSource(globalContext.getApplicationContext(), Uri.parse(song.getSongLink()));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            // Displaying Song title
            //      isPlaying = true;
            iv_play.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            layout_media.setVisibility(View.VISIBLE);
            tv_title.setText(song.getTitle());
            Glide.with(globalContext).load(song.getThumbnail()).placeholder(R.drawable.play).error(R.drawable.play).crossFade().centerCrop().into(iv_artwork);
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
            playSong(mSongList.get(0));
            currentSongIndex = 0;
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed
        mAudioManager.abandonAudioFocus(this);
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        setSaveEnabled(true);
        rootView = inflate(context, R.layout.activity_module, this);
        coordinatorLayout = rootView.findViewById(R.id.coordinator_layout);
        recycler_view = rootView.findViewById(R.id.recycler_view);
        layout_media = rootView.findViewById(R.id.layout_media);
        songProgressBar = rootView.findViewById(R.id.songProgressBar);
        timerDisplay = rootView.findViewById(R.id.timerDisplay);
        songCurrentDurationLabel = rootView.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = rootView.findViewById(R.id.songTotalDurationLabel);
        iv_artwork = rootView.findViewById(R.id.iv_artwork);
        tv_title = rootView.findViewById(R.id.tv_title);
        img_repeat = rootView.findViewById(R.id.img_repeat);
        img_shuffle = rootView.findViewById(R.id.img_shuffle);
        iv_previous = rootView.findViewById(R.id.iv_previous);
        iv_play = rootView.findViewById(R.id.iv_play);
        iv_next = rootView.findViewById(R.id.iv_next);
        mMediaPlayer = new MediaPlayer();
        timeUtil = new TimeUtil();
    }

    private void checkNullSet(AttributeSet set) {
        if (set == null) {
            return;
        }
    }
}
