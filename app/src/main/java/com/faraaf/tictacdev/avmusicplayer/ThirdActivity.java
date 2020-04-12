package com.faraaf.tictacdev.avmusicplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.faraaf.tictacdev.avmusicplayer.lable_page_run.ControllerPageRun;

public class ThirdActivity extends AppCompatActivity {

    ControllerPageRun controllerPageRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        controllerPageRun  = findViewById(R.id.controller);

    }
}
