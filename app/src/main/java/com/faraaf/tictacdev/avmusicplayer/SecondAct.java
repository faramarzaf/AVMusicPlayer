package com.faraaf.tictacdev.avmusicplayer;

import android.os.Bundle;
import android.util.AttributeSet;

import androidx.appcompat.app.AppCompatActivity;

public class SecondAct extends AppCompatActivity {
    MusicPlayerModule musicPlayerModule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        musicPlayerModule = findViewById(R.id.music);
     //   musicPlayerModule = new MusicPlayerModule(this );
    }

}
