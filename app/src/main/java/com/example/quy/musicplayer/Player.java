package com.example.quy.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Nguyen Toan Gia Quy on 5/6/2016.
 */
public class Player extends AppCompatActivity implements View.OnClickListener{
    static MediaPlayer musicPlayer;
    ArrayList<File> mySongs;
    int position;
    Uri uri;
    Thread updateSeekBar;
    SeekBar seekBar;
    Button btnPlay,btnFF, btnFB,btnPrv,btnNxt;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnFB = (Button) findViewById(R.id.btnFB);
        btnFF = (Button) findViewById(R.id.btnFF);
        btnPrv = (Button) findViewById(R.id.btnPrv);
        btnNxt = (Button) findViewById(R.id.btnNxt);
        btnPlay.setOnClickListener(this);
        btnFB.setOnClickListener(this);
        btnFF.setOnClickListener(this);
        btnPrv.setOnClickListener(this);
        btnNxt.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = musicPlayer.getDuration();
                int currentPosition = 0;
                seekBar.setMax(totalDuration );
                while(currentPosition < totalDuration){
                    try{
                        sleep(500);
                        currentPosition = musicPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
                //super.run();
            }
        };

        if(musicPlayer != null){
            musicPlayer.stop();
            musicPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songlist");
        position = bundle.getInt("pos", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        musicPlayer = musicPlayer.create(getApplicationContext(),uri);
        musicPlayer.start();
        seekBar.setMax(musicPlayer.getDuration());

        updateSeekBar.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnPlay:
                if(musicPlayer.isPlaying()){
                    btnPlay.setText(">");
                    musicPlayer.pause();
                }
                else{
                    musicPlayer.start();
                    btnPlay.setText("||");
                }
                break;
            case R.id.btnFF:
                musicPlayer.seekTo(musicPlayer.getCurrentPosition()+5000);
                break;
            case R.id.btnFB:
                musicPlayer.seekTo(musicPlayer.getCurrentPosition()-5000);
                break;
            case R.id.btnNxt:
                musicPlayer.stop();
                musicPlayer.release();
                position = (position +1)%mySongs.size();
                Uri uri = Uri.parse(mySongs.get(position).toString());
                musicPlayer = musicPlayer.create(getApplicationContext(),uri);
                musicPlayer.start();
                seekBar.setMax(musicPlayer.getDuration());

                break;
            case R.id.btnPrv:
                musicPlayer.stop();
                musicPlayer.release();
                position = (position -1 <0)? mySongs.size()-1: position -1;
                /*if(position -1 <0){
                    position = mySongs.size()-1;
                }
                else{
                    position = position -1;
                }*/
                uri = Uri.parse(mySongs.get(position).toString());
                musicPlayer = musicPlayer.create(getApplicationContext(), uri);
                musicPlayer.start();
                seekBar.setMax(musicPlayer.getDuration());
                break;
        }
    }
}
