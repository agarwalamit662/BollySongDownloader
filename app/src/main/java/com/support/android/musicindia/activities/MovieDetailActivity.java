package com.support.android.musicindia.activities;

/**
 * Created by amitagarwal3 on 3/8/2016.
 */

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.support.android.musicindia.data.UserProvider;
import com.support.android.musicindia.dto.DTOProviderSONG;
import com.support.android.musicindia.helper.DividerItemDecoration;
import com.support.android.musicindia.R;
import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.SONG;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.services.MyIntentService;


import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class MovieDetailActivity extends BaseActivity implements Animation.AnimationListener {
    static Animation animSideDown;
    private RecyclerView rv;
    public static final String EXTRA_NAME = "cheese_name";
    private String mName;
    private String mUrl;
    private ProgressDialog dialog;
    private static TextView actors;
    private static TextView director;
    private static TextView singers;
    private static TextView mdirector;
    private static TextView mname;
    private static TextView addedToDownload;
    private static RecyclerView rView;
    private String linkOne;
    private String linkTwo;
    private Button goToDownloads;
    private boolean finishSwitch = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_details);

        animSideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        // set animation listener
        animSideDown.setAnimationListener(this);
        Intent i = getIntent();
        final Movie movObject = (Movie)i.getSerializableExtra("MovieObject");
        final String movorpunjab = i.getStringExtra("MovieFragment");
        addedToDownload = (TextView) findViewById(R.id.songAddedToDownload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_moviedetail);
        toolbar.setTitle(movObject.getMOVIENAME().toString());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actors = (TextView) findViewById(R.id.actors);
        singers = (TextView)findViewById(R.id.singers);
        mdirector = (TextView) findViewById(R.id.mdirector);
        director = (TextView)findViewById(R.id.director);
        mname = (TextView)findViewById(R.id.mname);
        goToDownloads = (Button) findViewById(R.id.gotoDownloads);


        if(movObject != null ) {
            if (movObject.getACTORS().contentEquals("NOT AVAILABLE ,"))
                actors.setText("NOT AVAILABLE");
            else
                actors.setText(movObject.getACTORS().replace("NOT AVAILABLE , ", ""));
            if (movObject.getDIRECTOR().contentEquals("NOT AVAILABLE ,"))
                director.setText("NOT AVAILABLE");
            else
                director.setText(movObject.getDIRECTOR().replace("NOT AVAILABLE , ", ""));

            if (movObject.getSINGERS().contentEquals("NOT AVAILABLE ,"))
                singers.setText("NOT AVAILABLE");
            else
                singers.setText(movObject.getSINGERS().replace("NOT AVAILABLE , ", ""));

            if (movObject.getMUSIC_DIRECTOR().contentEquals("NOT AVAILABLE ,"))
                mdirector.setText("NOT AVAILABLE");
            else
                mdirector.setText(movObject.getMUSIC_DIRECTOR().replace("NOT AVAILABLE , ", ""));

            if (movObject.getMOVIENAME().contains("Mp3 Songs"))
                mname.setText(movObject.getMOVIENAME().replace("Mp3 Songs", ""));
            else if (movObject.getMOVIENAME().contains("Mp3 Song"))
                mname.setText(movObject.getMOVIENAME().replace("Mp3 Song", ""));
            else
                mname.setText(movObject.getMOVIENAME());

            if (movObject.getMOVIENAME().contains("MP3 SONGS"))
                mname.setText(movObject.getMOVIENAME().replace("MP3 SONGS", ""));
            else if (movObject.getMOVIENAME().contains("MP3 SONG"))
                mname.setText(movObject.getMOVIENAME().replace("MP3 SONG", ""));
            else
                mname.setText(movObject.getMOVIENAME());

        }

            rView = (RecyclerView) findViewById(R.id.recycler_view_movie_songs);
            List<Songs> sList = movObject.getSONGS();


            LinearLayoutManager lm = new LinearLayoutManager(MovieDetailActivity.this,LinearLayoutManager.VERTICAL,false);
            rView.setNestedScrollingEnabled(false);
            rView.setHasFixedSize(false);
            rView.setFocusable(false);
            rView.setLayoutManager(lm);
            rView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            rView.setAdapter(new RecyclerViewAdapterMovieDetailActivity(MovieDetailActivity.this, sList));

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
    protected void onResume() {


        super.onResume();
        if(BaseActivity.musicService != null && BaseActivity.serviceIntent != null && BaseActivity.musicConnection != null )
        {
            bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);
        }

        goToDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        
                Intent intent = new Intent(MovieDetailActivity.this, MainActivity.class);
                intent.putExtra("EXTRA_PAGE", String.valueOf(5));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if(BaseActivity.musicConnection != null){
                    unbindService(BaseActivity.musicConnection);
                    finishSwitch = false;
                }
                finish();


            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(BaseActivity.musicConnection != null && finishSwitch){
            unbindService(BaseActivity.musicConnection);
        }
        
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        addedToDownload.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    public static class RecyclerViewAdapterMovieDetailActivity
            extends RecyclerView.Adapter<RecyclerViewAdapterMovieDetailActivity.ViewHolder> {

        private static Context mContext;

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Songs> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            public String mBoundString;

            public final View mView;
            public final CircleButton mDwnldButton;
            public final TextView mSongName;
            private ImageButton menu;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mSongName = (TextView) view.findViewById(R.id.songname);
                mDwnldButton = (CircleButton)view.findViewById(R.id.dwnldIcon);
                menu = (ImageButton)view.findViewById(R.id.buttonMenuDetail);
                menu.setFocusable(false);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSongName.getText();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            }
        }

        public Songs getValueAt(int position) {
            return mValues.get(position);
        }

        public RecyclerViewAdapterMovieDetailActivity(Context context, List<Songs> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mValues = items;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_songs_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holds = holder;

            holds.mSongName.setText(mValues.get(position).getSONGNAME().toString());
            holds.mDwnldButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + mValues.get(position).getSONGNAME().toString() + ".mp3");
                    if(f.exists()){


                        String alreadyInDb = mValues.get(position).getSONGNAME().toString();
                        int sid = mValues.get(position).getSONG_ID();

                        ArrayList<SONG> isExits = DTOProviderSONG.getSONGIteminDatabase(mContext,sid,alreadyInDb);
                        if(isExits != null && isExits.size() > 0){

                            File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia"));
                            if(!mydir.exists())
                                mydir.mkdirs();
                            else
                                Log.d("error", "dir. already exists");
                            File file = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + alreadyInDb + ".mp3");
                            if (file.exists()) {
                                ContentResolver contentResolver = mContext.getContentResolver();
                                contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                        MediaStore.Images.ImageColumns.DATA + " =? " , new String[]{ file.getAbsolutePath() });

                                file.delete();
                                /*file.delete();

                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                                Uri uri = Uri.fromFile(mydir);
                                mediaScanIntent.setData(uri);
                                beforeFourFourIntent.setData(uri);

                                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                    mContext.sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                                }
                                else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                {
                                    mContext.sendBroadcast(mediaScanIntent);
                                }*/

                                Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                                intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSONGLINK_128KBPS_CONV().toString());
                                intentMyIntentService.putExtra("songName", mValues.get(position).getSONGNAME().toString());
                                intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSONG_ID());
                                intentMyIntentService.putExtra("songIncompleteId", (int) mValues.get(position).getSONG_ID());
                                intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSONGLINK_128KBPS().toString());
                                mContext.startService(intentMyIntentService);

                            }
                        }
                        else{

                            addedToDownload.setVisibility(View.VISIBLE);
                            addedToDownload.setText("Song Already in Downloads");
                            addedToDownload.startAnimation(animSideDown);
                        }

                    }
                    else {

                        String alreadyInDb = mValues.get(position).getSONGNAME().toString();
                        int sid = mValues.get(position).getSONG_ID();

                        ArrayList<SONG> isExits = DTOProviderSONG.getNewSONGIteminDatabase(mContext, sid, alreadyInDb);
                        if(isExits != null && isExits.size() > 0){
                               DTOProviderSONG.deleteSONGfromDatabase(mContext, sid, alreadyInDb);
                        }

                        Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSONGLINK_128KBPS_CONV().toString());
                        intentMyIntentService.putExtra("songName", mValues.get(position).getSONGNAME().toString());
                        intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSONG_ID());
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSONGLINK_128KBPS().toString());
                        mContext.startService(intentMyIntentService);
                        Toast.makeText(mContext, "Song added to Downloads", Toast.LENGTH_SHORT).show();

                        addedToDownload.setVisibility(View.VISIBLE);
                        addedToDownload.setText("Song added to Downloads");
                        addedToDownload.startAnimation(animSideDown);

                        SONG songObj = new SONG(mValues.get(position).getSONG_ID(),mValues.get(position).getSONGNAME(),mValues.get(position).getSONGLINK_128KBPS_CONV(),0,1);
                        ContentValues cv = getContentValues(songObj);
                        DTOProviderSONG.insertSONGIteminDatabase(mContext,cv);

                    }


                }
            });



            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_moviedetail, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.menu_watchonyoutube:

                                    String url = "https://www.youtube.com/results?search_query=";
                                    String snameCheck = getOnlyStrings(mValues.get(position).getSONGNAME());
                                    String mname = getOnlyStrings(mValues.get(position).getMovie().getMOVIENAME());
                                    url = url + mname + " - "+snameCheck;
                                    Uri uri = Uri.parse(url);
                                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                                    videoIntent.setData(uri);
                                    videoIntent.setPackage("com.google.android.youtube");
                                    mContext.startActivity(videoIntent);

                                    return true;

                            }
                            return true;
                        }
                    });

                    popup.show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (popup.getDragToOpenListener() instanceof ListPopupWindow.ForwardingListener) {
                            ListPopupWindow.ForwardingListener listener = (ListPopupWindow.ForwardingListener) popup.getDragToOpenListener();
                            listener.getPopup().setVerticalOffset(-holder.menu.getHeight());
                            listener.getPopup().show();
                        }
                    }


                }
            });

        }

        public ContentValues getContentValues(SONG songObj){

            ContentValues contentValues = new ContentValues();
            contentValues.put(UserProvider._SONGID,songObj.getSongId());
            contentValues.put(UserProvider._SONG_URL,songObj.getSongUrl());
            contentValues.put(UserProvider._SONG_NAME,songObj.getSongName());
            contentValues.put(UserProvider._SONG_DWNLD_COMPLETED,songObj.getCompleted());
            contentValues.put(UserProvider._SONG_DWNLD_RUNNING,songObj.getRunning());
            return contentValues;
        }

        @Override
        public int getItemCount() {
            if(mValues != null && mValues.size() > 0)
                return mValues.size();
            else
                return 0;
        }
    }

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        number = number.replaceAll("-","");
        return number;
    }

}

