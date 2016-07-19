package com.support.android.musicindia.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.support.android.musicindia.helper.CheckableImageButton;
import com.support.android.musicindia.R;

public class NowPlaying extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        baseurlNowPlaying = (ImageView) findViewById(R.id.backdrop);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedSongs);
        nsv.setNestedScrollingEnabled(false);
        textViewArtistTandI = null;
        imageViewSongImageTandI = null;

        textViewArtist = (TextView)findViewById(R.id.textViewArtist);
        textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        imageButtonPrevious = (ImageButton)findViewById(R.id.imageButtonPrevious);
        imageButtonPlayPause = (CheckableImageButton)findViewById(R.id.imageButtonPlayPause);
        imageButtonNext = (ImageButton)findViewById(R.id.imageButtonNext);
        seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
        imageButtonPrevious.setOnClickListener(this);
        imageButtonPlayPause.setOnClickListener(this);
        imageButtonNext.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar1.setClickable(false);

    }

    @Override
    protected void onResume() {
         super.onResume();
         bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(BaseActivity.musicConnection != null)
            unbindService(BaseActivity.musicConnection);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {

        if (view.equals(imageButtonPlayPause)) {
            imageButtonPlayPauseOnClick();

        } else if(view.equals(imageButtonNext)) {
            imageButtonNextOnClick();
        } else if(view.equals(imageButtonPrevious))  {
            imageButtonPreviousOnClick();
        }
    }

}
