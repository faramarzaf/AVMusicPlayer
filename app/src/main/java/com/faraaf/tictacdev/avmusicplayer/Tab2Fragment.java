package com.faraaf.tictacdev.avmusicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class Tab2Fragment extends Fragment {

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

    int idPl;

    public Tab2Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        receiveData();

        mRecyclerViewSongs = view.findViewById(R.id.recycler_view_playlist);
        mMediaPlayer = new MediaPlayer();
        timeUtil = new TimeUtil();
        mCoordinatorLayout = view.findViewById(R.id.coordinator_layout_playlist);
        mMediaLayout = view.findViewById(R.id.layout_media);
        mIvArtwork = view.findViewById(R.id.iv_artwork);
        mIvPlay = view.findViewById(R.id.iv_play);
        mIvPrevious = view.findViewById(R.id.iv_previous);
        mIvNext = view.findViewById(R.id.iv_next);
        mTvTitle = view.findViewById(R.id.tv_title);
        setUpAdapter();
        // get();

        return view;
    }


    private void setUpAdapter() {
        mAdapter = new PlayListAdapter(getActivity(), mSongList, listener);
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
        }
    }


}

