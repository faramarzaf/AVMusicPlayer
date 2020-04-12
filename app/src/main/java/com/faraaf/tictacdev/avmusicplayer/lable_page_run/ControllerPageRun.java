package com.faraaf.tictacdev.avmusicplayer.lable_page_run;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faraaf.tictacdev.avmusicplayer.R;

public class ControllerPageRun extends RelativeLayout {

    private View rootView;
    private TextView txtSongTitle;
    private TextView txtArtist;

    private ImageView imgGoToSongs;
    private ImageView imgAvatarMusic;
    private ImageView imgPlaySong;
    private Context globalContext;

    public ControllerPageRun(Context context) {
        super(context);
        this.globalContext = context;
        init(context);

        imgGoToSongs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        setSaveEnabled(true);
        rootView = inflate(context, R.layout.layout_music_player_run, this);
        txtSongTitle = rootView.findViewById(R.id.txtSongTitle);
        txtSongTitle = rootView.findViewById(R.id.txtSongTitle);
        txtArtist = rootView.findViewById(R.id.txtArtist);
        imgGoToSongs = rootView.findViewById(R.id.imgGoToSongs);
        imgAvatarMusic = rootView.findViewById(R.id.imgAvatarMusic);
        imgPlaySong = rootView.findViewById(R.id.imgPlaySong);


    }

}
