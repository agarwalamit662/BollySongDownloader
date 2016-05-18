package com.support.android.tinyplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class NowPlaying extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        baseurlNowPlaying = (ImageView) findViewById(R.id.backdrop);
        toolbar.setTitle("");
        /*AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) baseurlNowPlaying.getLayoutParams();
        params.setScrollFlags(0);

        AppBarLayout.LayoutParams paramsIV = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);*/


        CollapsingToolbarLayout ctl =(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedSongs);
        nsv.setNestedScrollingEnabled(false);
       //ctl.setNestedScrollingEnabled(false);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*if(BaseActivity.musicService != null && BaseActivity.musicService.getCurrentPlayingItem() != null){

        }*/

        textViewArtistTandI = null;
        imageViewSongImageTandI = null;

        textViewArtist = (TextView)findViewById(R.id.textViewArtist);
        textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        //textViewTime = (TextView)findViewById(R.id.textViewTime);
        //imageViewSongImage = (ImageView)findViewById(R.id.imageViewSongImage);
        imageButtonPrevious = (ImageButton)findViewById(R.id.imageButtonPrevious);
        imageButtonPlayPause = (CheckableImageButton)findViewById(R.id.imageButtonPlayPause);
        imageButtonNext = (ImageButton)findViewById(R.id.imageButtonNext);
        seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
       // seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        /*imageButtonShowSeekbar2 = (ImageButton)findViewById(R.id.imageButtonShowSeekbar2);

        imageButtonShowSeekbar2.setOnClickListener(this);*/
        imageButtonPrevious.setOnClickListener(this);
        imageButtonPlayPause.setOnClickListener(this);
        imageButtonNext.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar1.setClickable(false);
       // seekBar2.setOnSeekBarChangeListener(this);
        //textViewTime.setOnClickListener(this);


    }

    @Override
    protected void onResume() {


        super.onResume();
        bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onStart() {
        super.onStart();

        //*bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);*//*


    }






    @Override
    protected void onPause() {
        super.onPause();
        unbindService(BaseActivity.musicConnection);

    }

    @Override
    public void onStop() {
        super.onStop();

        //*//*unbindService(BaseActivity.musicConnection);*//*



    }


    /*@Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy", "In on destroy");
      //  unbindService(BaseActivity.musicConnection);
        *//*if(BaseActivity.musicService != null  && !BaseActivity.musicService.isPlaying()){
            // musicService.onDestroy();
            Log.e("IN onDestroy IF","IN onDestroy IF");
            Log.e("IN onDestroy IF","IN onDestroy IF");
            BaseActivity.musicService.destroyService();
            BaseActivity.musicService = null;
            BaseActivity.musicConnection = null;


        }*//*

        *//*SharedPreferences settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //boolean firstRun=settings.getBoolean("firstRun",false);

        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("firstRun",true);
        editor.commit();

        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy","In on destroy");*//*


    }*/

    @Override
    public void onClick(View view) {

        if (view.equals(imageButtonPlayPause)) {
            imageButtonPlayPauseOnClick();

        } else if(view.equals(imageButtonNext)) {
            imageButtonNextOnClick();
        } else if(view.equals(imageButtonPrevious))  {
            imageButtonPreviousOnClick();
        } /*else if(view.equals(textViewTime)) {
            textViewTimeOnClick();
        } else if(view.equals(imageButtonShowSeekbar2)) {
            imageButtonShowSeekbar2OnClick();
        }*/
    }

}
