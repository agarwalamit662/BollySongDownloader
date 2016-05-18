
package com.support.android.tinyplayer;


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
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.support.android.tinyplayer.model.PlayableItem;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{


    boolean mBound = false;
    public static boolean splashLoadedTiny = true;

    public TextView textViewArtist, textViewTitle, textViewTime,textViewArtistTandI;
    public CheckableImageButton imageButtonPlayPause;
    public CircleImageView imageViewSongImageTandI;
    public ImageButton imageButtonPrevious, imageButtonNext;/* imageButtonShowSeekbar2;*/
    public SeekBar seekBar1;
    //, seekBar2;
    public ImageView baseurlNowPlaying;
    /*public ImageView imageViewSongImage;*/

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
    public void imageButtonShowSeekbar2OnClick(){

        /*if (seekBar2.getVisibility() == View.VISIBLE) {
          //  imageButtonShowSeekbar2.setImageResource(R.drawable.expand);
            seekBar2.setVisibility(View.GONE);
        } else {
            //imageButtonShowSeekbar2.setImageResource(R.drawable.collapse);
            seekBar2.setVisibility(View.VISIBLE);
        }*/

    }





    @Override
    public void onClick(View view) {


    }



    @Override
    public void onStart() {
        super.onStart();

        Log.e("IN ON START", "IN ON START");
        Log.e("IN ON START","IN ON START");
        Log.e("IN ON START","IN ON START");

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
                //mBound = true;
                startRoutine();
            }
            @Override
            public void onServiceDisconnected(ComponentName className) {
                musicService = null;
               // mBound = false;
                Log.e("In Disconn","In Disc");
                Log.e("In Disconn","In Disc");
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
                /*if(duration>Constants.SECOND_SEEKBAR_DURATION) {
                    int progress2 = progress%Constants.SECOND_SEEKBAR_DURATION;

                    int parts = duration/Constants.SECOND_SEEKBAR_DURATION;
                    if(progress>parts*Constants.SECOND_SEEKBAR_DURATION) {
                        seekBar2.setMax(duration-parts*Constants.SECOND_SEEKBAR_DURATION);
                    } else {
                        seekBar2.setMax(Constants.SECOND_SEEKBAR_DURATION);
                    }
                    seekBar2.setProgress(progress2);
                }*/
                String time;
                if(showRemainingTime && isLengthAvailable) {
                    time = "-" + Utils.formatTime(musicService.getDuration()-progress);
                } else {
                    time = Utils.formatTime(progress);
                }
                time += songDurationString;
        //textViewTime.setText(time);
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

        /*if(musicService == null) {
            Log.e("IN ON RESUME","IN ON RESUME");
            Log.e("IN ON RESUME","IN ON RESUME");
                startService(serviceIntent); // Starts the service if it is not running
                createMusicConnection();
                bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }*/


        //bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        if (musicService!=null) startRoutine();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.support.android.tinyplayer.musicplayer.newsong");
        intentFilter.addAction("com.support.android.tinyplayer.musicplayer.playpausechanged");
        intentFilter.addAction("com.support.android.tinyplayer.musicplayer.podcastdownloadcompleted");
        intentFilter.addAction("com.support.android.tinyplayer.musicplayer.quitactivity");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("com.support.android.tinyplayer.musicplayer.newsong")) {
                    updatePlayingItem();
                } else if(intent.getAction().equals("com.support.android.tinyplayer.musicplayer.playpausechanged")) {
                    updatePlayPauseButton();
                }

            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        updatePlayPauseButton();


    }

    public void playItem(PlayableItem item) {
        Log.e("in play item","in play item");
        Log.e("in play item","in play item");
        if(musicService == null) {
            Log.e("Music Service was null","IN ON RESUME");
            Log.e("Music Service was null","IN ON RESUME");
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
            //  gotoPlayingItemPosition();
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
        //unbindService(musicConnection);
        // Disable broadcast receiver

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("On Stop service called", "On Stop Called");
        Log.e("On Stop service called", "On Stop Called");

        if(musicService != null && musicService.getCurrentPlayingItem() == null){
             //musicService.onDestroy();
           // unbindService(musicConnection);
            musicService.destroyService();
            musicService = null;
            Log.e("In dest", "In dest");
            Log.e("In dest","In dest");


        }
        //unbindService(musicConnection);

        /*if(mBound)
            getApplicationContext().unbindService(musicConnection);
        *///getApplicationContext().unbindService(musicConnection);
        /*if (mBound && musicConnection != null) {
            unbindService(musicConnection);
            mBound = false;

        }*/

    }

    private void updatePlayingItem() {
        if(musicService != null) {
            PlayableItem playingItem = musicService.getCurrentPlayingItem();

            if (playingItem != null) {
                // Song loaded


                String path = playingItem.getPlayableUri();
                Bitmap p = Utils.getMusicFileImage(path);


                if (imageViewSongImageTandI != null && textViewArtistTandI != null) {
                    imageViewSongImageTandI.setImageBitmap(p);
                    textViewArtistTandI.setTypeface(null, Typeface.ITALIC);
                    textViewArtistTandI.setText(playingItem.getTitle());
                }
                if (textViewTitle != null && textViewArtist != null && seekBar1 != null
                         && baseurlNowPlaying != null) {
                    textViewTitle.setText(playingItem.getTitle());
                    textViewArtist.setText(playingItem.getArtist());
                    if (p != null)
                        baseurlNowPlaying.setImageBitmap(p);


                    seekBar1.setMax(musicService.getDuration());
                    seekBar1.setProgress(musicService.getCurrentPosition());
                    seekBar1.setClickable(true);
                    isLengthAvailable = playingItem.isLengthAvailable();
                    if (isLengthAvailable) {
                        int duration = musicService.getDuration();
                        songDurationString = "/" + Utils.formatTime(duration);
                        seekBar1.setVisibility(View.VISIBLE);
                        /*if (duration > Constants.SECOND_SEEKBAR_DURATION) {
                            // imageButtonShowSeekbar2.setVisibility(View.VISIBLE);
                            // imageButtonShowSeekbar2.setImageResource(R.drawable.expand);
                        } else {
                            seekBar2.setVisibility(View.GONE);
                            //imageButtonShowSeekbar2.setVisibility(View.GONE);
                        }*/
                    } else {
                        songDurationString = "";
                        //seekBar1.setVisibility(View.GONE);
                        //seekBar2.setVisibility(View.GONE);
                        // imageButtonShowSeekbar2.setVisibility(View.GONE);
                    }

                    //imageViewSongImage.setVisibility(View.GONE);
                    //imageViewSongImage.setImageBitmap(p);
                }

                //((MusicPlayerApplication)getActivity().getApplication()).imagesCache.getImageAsync(playingItem, imageViewSongImage);
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
                    //seekBar1.setVisibility(SeekBar.GONE);
                    //imageViewSongImage.setVisibility(View.GONE);
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
        //currentFragment.updateListView();
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
                }/* else if(seekBar.equals(seekBar2)) {
                    int progress2 = (seekBar1.getProgress()/Constants.SECOND_SEEKBAR_DURATION)*Constants.SECOND_SEEKBAR_DURATION;
                    musicService.seekTo(progress2+progress);
                }*/
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
        // getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


}