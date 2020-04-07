package com.faraaf.tictacdev.avmusicplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.Comparator;


public class Tab2Fragment extends Fragment {

    Song receivedSong;
    Bundle bundle;

    public Tab2Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        receiveData();


        return view;
    }

    public void getSongList() {
        // mSongList.add(new Song(thisId, thisTitle, thisArtist, uri.toString(), thisSongLink.toString()));

/*        mSongList.add(receivedSong);
        // Sort music alphabetically
        Collections.sort(mSongList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        mAdapter.notifyDataSetChanged();*/
    }


    private void receiveData() {
        bundle = this.getArguments();
        if (bundle != null) {
            receivedSong = (Song) bundle.getSerializable("song");
            Log.d("TAG0", "receiveData: " + receivedSong.getTitle());
        }
    }


}

