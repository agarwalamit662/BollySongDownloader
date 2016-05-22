package com.support.android.musicindia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;


import org.jaudiotagger.audio.AudioFile;

import de.hdodenhof.circleimageview.CircleImageView;

public class LyricsActivity extends BaseActivity {
    private AudioFile f;
    private String title;
    private String artist;
    private TextView lyrics;
    private String songLyrics;
    private String url;
    private String songname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_song);
        lyrics =(TextView) findViewById(R.id.songLyricsId);
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            songLyrics = extras.getString("lyrics");
            url = extras.getString("url");
            songname = extras.getString("sname");
            songLyrics = songLyrics.replaceAll("\r ","\n");
            songLyrics = songLyrics+"\n"+" "+"\n"+" "+"\n";

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(songname);
        setSupportActionBar(toolbar);

        lyrics.setText(songLyrics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        textViewArtistTandI = (TextView)findViewById(R.id.textViewArtistTandI);
        imageViewSongImageTandI = (CircleImageView)findViewById(R.id.songurl);
        textViewArtistTandI.setOnClickListener(this);
        imageViewSongImageTandI.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.equals(textViewArtistTandI)){
            Intent i = new Intent(this,NowPlaying.class);
            startActivity(i);
        }
        else if(view.equals(imageViewSongImageTandI)){

            Intent i = new Intent(this,NowPlaying.class);
            startActivity(i);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_share, menu);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.aboutButton);

        // Now get the ShareActionProvider from the item
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);


        mShareActionProvider.setShareIntent(doShare());
        return true;

    }

    public Intent doShare() {

        String sname = SplashScreenActivity.sharelink;
        String sendData = "MusicIndia: Lyrics of "+ songname+ "\n" +" \n" + " \n" +songLyrics;

        if(sname != null && sname.length() > 0 && !sname.equals("NA") ){
            sendData = sendData +" \n" + "Link to install MusicIndia: " +" \n"+" \n"+sname;
        }


        // populate the share intent with data
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sendData);

        return intent;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);*/


    }




    @Override
    protected void onResume() {


        super.onResume();
        bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);

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

        /*unbindService(BaseActivity.musicConnection);*/



    }


    /*@Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy", "In on destroy");
        //  unbindService(BaseActivity.musicConnection);
        if(BaseActivity.musicService != null  && !BaseActivity.musicService.isPlaying()){
            // musicService.onDestroy();
            Log.e("IN onDestroy IF","IN onDestroy IF");
            Log.e("IN onDestroy IF","IN onDestroy IF");
            BaseActivity.musicService.destroyService();
            BaseActivity.musicService = null;
            BaseActivity.musicConnection = null;


        }

        *//*SharedPreferences settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //boolean firstRun=settings.getBoolean("firstRun",false);

        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("firstRun",true);
        editor.commit();

        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy","In on destroy");*//*


    }*/

}
