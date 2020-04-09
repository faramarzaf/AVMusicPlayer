package com.faraaf.tictacdev.avmusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faraaf.tictacdev.avmusicplayer.db.SongsDao;
import com.faraaf.tictacdev.avmusicplayer.db.SongsRepositoryDb;
import com.faraaf.tictacdev.avmusicplayer.db.SongsRoomDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
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

import static android.content.Context.MODE_PRIVATE;


public class Tab1Fragment extends Fragment implements
        SongAdapter.SongAdapterListener,
        SongAdapter.AddToPlayListListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener, SeekBar.OnSeekBarChangeListener {


    Tab2Fragment tab2Fragment;
    private TabLayout tabhost;
    Bundle bundle;
    Song song1;

    private static Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private List<Song> mSongList = new ArrayList<>();
    private RecyclerView mRecyclerViewSongs;
    private SongAdapter mAdapter;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mMediaLayout;
    private TextView mTvTitle;

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
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private AudioManager mAudioManager;

    // repeat
    boolean repeat = false;
    boolean shuffle = false;

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


    public Tab1Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        tabhost = view.findViewById(R.id.tabLayout);

        // init db


        mMediaPlayer = new MediaPlayer();
        timeUtil = new TimeUtil();
        mRecyclerViewSongs = view.findViewById(R.id.recycler_view);
        mCoordinatorLayout = view.findViewById(R.id.coordinator_layout);
        mMediaLayout = view.findViewById(R.id.layout_media);
        mIvArtwork = view.findViewById(R.id.iv_artwork);
        mIvPlay = view.findViewById(R.id.iv_play);
        mIvPrevious = view.findViewById(R.id.iv_previous);
        mIvNext = view.findViewById(R.id.iv_next);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvCurrentDuration = view.findViewById(R.id.songCurrentDurationLabel);
        mTvTotalDuration = view.findViewById(R.id.songTotalDurationLabel);
        songProgressBar = view.findViewById(R.id.songProgressBar);
        img_shuffle = view.findViewById(R.id.img_shuffle);
        img_repeat = view.findViewById(R.id.img_repeat);
        img_fav = view.findViewById(R.id.img_fav);

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




        return view;
    }

    void initShuffleRepeat() {
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
                        if (mSongList.size() > 0) {
                            playSong(mSongList.get(0));
                            currentSongIndex = 0;
                        }
                    }
                }
            }
        });


    }

    private void checkIncomingCalls() {
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

    }

    private void setUpAdapter() {
        mAdapter = new SongAdapter(getActivity(), mSongList, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewSongs.setLayoutManager(mLayoutManager);
        mRecyclerViewSongs.setItemAnimator(new DefaultItemAnimator());

        // add divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewSongs.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerViewSongs.addItemDecoration(dividerItemDecoration);

        mRecyclerViewSongs.setAdapter(mAdapter);
    }

    private void setUpListeners() {
        mIvPlay.setOnClickListener(this);
        mIvPrevious.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        songProgressBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    void getPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            getSongList();
                            initShuffleRepeat();
                        } else
                            Toast.makeText(getActivity(), "Sorry! You denied the permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void getSongList() {
        //retrieve item_song info
        ContentResolver musicResolver = getActivity().getContentResolver();


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
    public void onSongSelected(Song song) {
        playSong(song);
        currentSongIndex = mSongList.indexOf(song);
    }

    @Override
    public void onAddToPlayListClicked(Song song) {
        Toast.makeText(getActivity(), song.getTitle(), Toast.LENGTH_SHORT).show();
        this.song1 = song;
        tab2Fragment = new Tab2Fragment();
        bundle = new Bundle();
        bundle.putSerializable("song", song1);
        tab2Fragment.setArguments(bundle);
        if (getActivity().getFragmentManager() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame2, tab2Fragment).commit();
        }


        //   createPlaylist(getActivity(), song1.getTitle());

    }

    public static final long createPlaylist(final Context context, final String name) {
        if (name != null && name.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();
            final String[] projection = new String[]{MediaStore.Audio.PlaylistsColumns.NAME};
            final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, selection, null, null);
            if (cursor.getCount() <= 0) {
                final ContentValues values = new ContentValues(1);
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                final Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                return Long.parseLong(uri.getLastPathSegment());
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            return -1;
        }
        return -1;
    }

    private void addItem(Song item) {
        mSongList.add(item);
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

    private void playMusic() {
        if (!mMediaPlayer.isPlaying()) {
            mIvPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
            mMediaPlayer.start();
        } else {
            mIvPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
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

            mMediaPlayer.setDataSource(getActivity(), Uri.parse(song.getSongLink()));
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
     /*   if (currentSongIndex < (mSongList.size() - 1)) {
            playSong(mSongList.get(currentSongIndex + 1));
            currentSongIndex = currentSongIndex + 1;
        } else {
            if (mSongList.size()>0){

                playSong(mSongList.get(0));
                currentSongIndex = 0;
            }
        }*/
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
        /**AudioManager.abandonAudioFocus(this);
         * Added in API level 8
         * Deprecated in API level 26
         * use abandonAudioFocusRequest(android.media.AudioFocusRequest)
         */
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();

    }


}
