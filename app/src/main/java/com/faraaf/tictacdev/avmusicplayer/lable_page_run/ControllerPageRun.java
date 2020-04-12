package com.faraaf.tictacdev.avmusicplayer.lable_page_run;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faraaf.tictacdev.avmusicplayer.MusicPlayerModule;
import com.faraaf.tictacdev.avmusicplayer.R;
import com.faraaf.tictacdev.avmusicplayer.SecondAct;
import com.faraaf.tictacdev.avmusicplayer.Song;

public class ControllerPageRun extends RelativeLayout implements View.OnClickListener {

    private View rootView;
    private TextView txtSongTitle;
    private TextView txtArtist;

    private ImageView imgGoToSongs;
    private ImageView imgAvatarMusic;
    private ImageView imgPlaySong;
    private Context globalContext;

    MusicPlayerModule musicPlayerModule;

    public ControllerPageRun(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.globalContext = context;
        init(context);
        imgGoToSongs.setOnClickListener(this);
        imgPlaySong.setOnClickListener(this);

    }

    public ControllerPageRun(Context context) {
        super(context);
        this.globalContext = context;
        init(context);
        imgGoToSongs.setOnClickListener(this);
        imgPlaySong.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgGoToSongs:
                globalContext.startActivity(new Intent(globalContext, SecondAct.class));
                break;

            case R.id.imgPlaySong:
                break;


            default:
                break;
        }
    }
}
