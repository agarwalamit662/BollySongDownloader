
package com.support.android.musicindia.activities;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.support.android.musicindia.helper.BrowserSong;
import com.support.android.musicindia.helper.CheckableImageButton;
import com.support.android.musicindia.application.MusicPlayerApplication;
import com.support.android.musicindia.services.MusicServiceTinyPlayer;
import com.support.android.musicindia.R;
import com.support.android.musicindia.helper.Utils;
import com.support.android.musicindia.model.PlayableItem;

import java.net.URLDecoder;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{


    boolean mBound = false;
    public static boolean splashLoadedTiny = true;

    public TextView textViewArtist, textViewTitle, textViewTime,textViewArtistTandI;
    public CheckableImageButton imageButtonPlayPause;
    public CircleImageView imageViewSongImageTandI;
    public ImageButton imageButtonPrevious, imageButtonNext;/* imageButtonShowSeekbar2;*/
    public SeekBar seekBar1;
    public ImageView baseurlNowPlaying;
    private static BrowserSong searchSong;
    public static MusicServiceTinyPlayer musicService; // The application service*/
    private static final int POLLING_INTERVAL = 450; // Refresh time of the seekbar
    private boolean pollingThreadRunning; // true if thread is active, false otherwise
    private boolean startPollingThread = true;

    private boolean showRemainingTime = false;
    private boolean isLengthAvailable = false;
    private String songDurationString = "";
    private String intentFile;
    private static int positionRe=0;
    public static Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private MusicPlayerApplication app;
    private DrawerLayout mDrawerLayout;
    public static ViewPager viewPager;
    private TabLayout tabLayout;
    private Context context;


    public MainActivity ma;


    public void imageButtonPlayPauseOnClick(){
        if(musicService != null){
            musicService.playPause();
            updatePlayPauseButton();
        }
    }

    public void imageButtonNextOnClick(){
        if(musicService != null)
            musicService.nextItem();
    }
    public void imageButtonPreviousOnClick(){
        if(musicService != null)
            musicService.previousItem(false);
    }
    public void textViewTimeOnClick(){
        showRemainingTime = !showRemainingTime;
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onStart() {
        super.onStart();
        if(musicService == null) {
            startService(serviceIntent); // Starts the service if it is not running
            createMusicConnection();
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public static ServiceConnection musicConnection;
    private void createMusicConnection() {
        musicConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                musicService = ((MusicServiceTinyPlayer.MusicBinder)service).getService();
                startRoutine();
            }
            @Override
            public void onServiceDisconnected(ComponentName className) {
                musicService = null;
            }
        };
    }

    private void updatePosition() {
        if(musicService != null){
            int progress = musicService.getCurrentPosition();
            int duration = musicService.getDuration();
            if( textViewTitle != null && textViewArtist != null && seekBar1 != null
                    && baseurlNowPlaying != null){
                seekBar1.setProgress(progress);
                String time;
                if(showRemainingTime && isLengthAvailable) {
                    time = "-" + Utils.formatTime(musicService.getDuration() - progress);
                } else {
                    time = Utils.formatTime(progress);
                }
                time += songDurationString;
            }
        }
    }



    private void startPollingThread() {
        pollingThreadRunning = true;
        new Thread() {
            public void run() {
                while(pollingThreadRunning) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(musicService!=null) {
                                updatePosition();
                            }
                        }
                    });
                    try{ Thread.sleep(POLLING_INTERVAL); } catch(Exception e) {}
                }
            }
        }.start();
    }
    private void stopPollingThread() {
        pollingThreadRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (musicService!=null) startRoutine();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.support.android.musicindia.musicplayer.newsong");
        intentFilter.addAction("com.support.android.musicindia.musicplayer.playpausechanged");
        intentFilter.addAction("com.support.android.musicindia.musicplayer.podcastdownloadcompleted");
        intentFilter.addAction("com.support.android.musicindia.musicplayer.quitactivity");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("com.support.android.musicindia.musicplayer.newsong")) {
                    updatePlayingItem();
                } else if(intent.getAction().equals("com.support.android.musicindia.musicplayer.playpausechanged")) {
                    updatePlayPauseButton();
                }

            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        updatePlayPauseButton();

    }

    public void playItem(PlayableItem item) {
        if(musicService == null) {
            startService(serviceIntent); // Starts the service if it is not running
            createMusicConnection();
            bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
        if(musicService != null){

            boolean ok = musicService.playItem(item);
            if(!ok) Utils.showMessageDialog(this, R.string.errorSong, R.string.errorSongMessage);
        }
    }
    private void loadSongFromIntent() {
        Intent intent = getIntent();
        if(intent!=null && intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            try {
                intentFile = URLDecoder.decode(intent.getDataString(), "UTF-8");
                intentFile = intentFile.replace("file://", "");
            } catch (Exception e) {}
        }
    }


    private void startRoutine() {


        // Opens the song from the search, if any
        if(searchSong!=null) {
            playItem(searchSong);
            searchSong = null;
        } else {
            updatePlayingItem();
        }

        // Opens the song from the intent, if necessary
        if(intentFile!=null) {
            BrowserSong song = new BrowserSong(intentFile);
            playItem(song);
            intentFile = null;
        } else {
            updatePlayingItem();
        }

        // Starts the thread to update the seekbar and position information
        if(startPollingThread) startPollingThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPollingThread(); // Stop the polling thread
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void updatePlayingItem() {
        if(musicService != null) {
            PlayableItem playingItem = musicService.getCurrentPlayingItem();

            if (playingItem != null) {

                String path = playingItem.getPlayableUri();
                Bitmap p = Utils.getMusicFileImage(path);
                if (imageViewSongImageTandI != null && textViewArtistTandI != null) {
                    if (p != null){
                        imageViewSongImageTandI.setImageBitmap(p);
                    }
                    else if(p == null){
                        Drawable d = getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher);
                        imageViewSongImageTandI.setImageDrawable(d);
                    }
                    textViewArtistTandI.setTypeface(null, Typeface.ITALIC);
                    textViewArtistTandI.setText(playingItem.getTitle());
                }
                if (textViewTitle != null && textViewArtist != null && seekBar1 != null
                         && baseurlNowPlaying != null) {
                    textViewTitle.setText(playingItem.getTitle());
                    textViewArtist.setText(playingItem.getArtist());
                    if (p != null){
                        baseurlNowPlaying.setImageBitmap(p);
                    }
                    else if(p == null){
                        Drawable d = getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher);
                        baseurlNowPlaying.setImageDrawable(d);
                    }

                    seekBar1.setMax(musicService.getDuration());
                    seekBar1.setProgress(musicService.getCurrentPosition());
                    seekBar1.setClickable(true);
                    isLengthAvailable = playingItem.isLengthAvailable();
                    if (isLengthAvailable) {
                        int duration = musicService.getDuration();
                        songDurationString = "/" + Utils.formatTime(duration);
                        seekBar1.setVisibility(View.VISIBLE);

                    } else {
                        songDurationString = "";

                    }
                }


            } else {
                // No song loaded

                if (textViewTitle != null && textViewArtist != null && seekBar1 != null
                        && baseurlNowPlaying != null) {
                    Drawable d = getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher);
                    baseurlNowPlaying.setImageDrawable(d);

                    textViewTitle.setText(R.string.noSong);
                    textViewArtist.setText("");
                    seekBar1.setMax(10);
                    seekBar1.setProgress(0);
                    seekBar1.setClickable(false);
                    //seekBar2.setVisibility(View.GONE);
                    //imageButtonShowSeekbar2.setVisibility(View.GONE);
                    isLengthAvailable = true;
                    songDurationString = "";

                }
                if (imageViewSongImageTandI != null && textViewArtistTandI != null) {
                    Drawable d = getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher);
                    imageViewSongImageTandI.setImageDrawable(d);
                    textViewArtistTandI.setTypeface(null, Typeface.ITALIC);
                    textViewArtistTandI.setText("No Song Loaded");
                }
            }
            updatePlayPauseButton();
            updatePosition();
        }

    }
    private void updatePlayPauseButton() {
        if(musicService != null){
            if( textViewTitle != null && textViewArtist != null && seekBar1 != null
                    && baseurlNowPlaying != null){
                imageButtonPlayPause.setChecked(musicService != null && musicService.isPlaying());
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(musicService != null){
            if(fromUser) { // Event is triggered only if the seekbar position was modified by the user
                if(seekBar1 != null && seekBar.equals(seekBar1)) {
                    musicService.seekTo(progress);
                }
            updatePosition();
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            super.onNewIntent(intent);
            setIntent(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }



    @Override
    protected void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, MusicServiceTinyPlayer.class);
        loadSongFromIntent();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}