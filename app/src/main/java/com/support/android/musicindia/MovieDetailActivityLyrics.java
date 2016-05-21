package com.support.android.musicindia;

/**
 * Created by amitagarwal3 on 3/8/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.support.android.musicindia.model.MovieLyrics;
import com.support.android.musicindia.model.SongsLyrics;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class MovieDetailActivityLyrics extends BaseActivity {

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
    private static RecyclerView rView;
    private static CardView cardMovies;
    private String linkOne;
    private String linkTwo;
    private Button goToDownloads;
    private ImageView movieUrl;
    public static MovieLyrics movObject;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_details);

        Intent i = getIntent();
        movObject = (MovieLyrics)i.getSerializableExtra("MovieObjectLyrics");
        final String movorpunjab = i.getStringExtra("MovieFragment");
        /*AdView ad = (AdView) findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_moviedetail);
        toolbar.setTitle(movObject.getMOVIENAME().toString());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*movieUrl = (ImageView) findViewById(R.id.movieurl);
        if(movObject.getURLS() != null){
            movieUrl.setVisibility(View.VISIBLE);
            Glide.with(this).load(movObject.getURLS()).into(movieUrl);
        }
        else{
            movieUrl.setVisibility(View.GONE);
        }*/
        cardMovies = (CardView) findViewById(R.id.cardMovies);
        cardMovies.setVisibility(View.GONE);
        /*actors = (TextView) findViewById(R.id.actors);
        singers = (TextView)findViewById(R.id.singers);
        mdirector = (TextView) findViewById(R.id.mdirector);
        director = (TextView)findViewById(R.id.director);
        mname = (TextView)findViewById(R.id.mname);*/
        goToDownloads = (Button) findViewById(R.id.gotoDownloads);
        /*goToDownloads = (Button) findViewById(R.id.gotoDownloads);
        *//*if(movorpunjab == null){
            goToDownloads.setVisibility(View.GONE);
        }*//*

        goToDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String urlLyrics = "http://www.imdb.com/find?ref_=nv_sr_fn&s=all&q=";
                String snameCheckLyrics = movObject.getMOVIENAME();
                urlLyrics = urlLyrics + snameCheckLyrics;
                Uri uriLyrics = Uri.parse(urlLyrics);
                Intent videoIntentLyrics = new Intent(Intent.ACTION_VIEW);
                videoIntentLyrics.setData(uriLyrics);
                videoIntentLyrics.setPackage("com.android.chrome");
                //videoIntent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                startActivity(videoIntentLyrics);

                *//*Intent intent = new Intent(MovieDetailActivityLyrics.this, MainActivity.class);
                intent.putExtra("EXTRA_PAGE", String.valueOf(5));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                *//**//*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*//**//*
                startActivity(intent);
                finish();*//*


            }
        });*/
        goToDownloads.setVisibility(View.GONE);



            rView = (RecyclerView) findViewById(R.id.recycler_view_movie_songs);
            List<SongsLyrics> sList = movObject.getSONGS();


            LinearLayoutManager lm = new LinearLayoutManager(MovieDetailActivityLyrics.this,LinearLayoutManager.VERTICAL,false);
            rView.setNestedScrollingEnabled(false);
            rView.setHasFixedSize(false);
            rView.setFocusable(false);
            rView.setLayoutManager(lm);
            rView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            rView.setAdapter(new SimpleStringRecyclerViewAdapter(MovieDetailActivityLyrics.this, sList));


        textViewArtistTandI = (TextView)findViewById(R.id.textViewArtistTandI);
        imageViewSongImageTandI = (CircleImageView)findViewById(R.id.songurl);
        textViewArtistTandI.setOnClickListener(this);
        imageViewSongImageTandI.setOnClickListener(this);


    }



    @Override
    public void onClick(View view) {

        /*if (view.equals(imageButtonPlayPause)) {
            imageButtonPlayPauseOnClick();

        } else if(view.equals(imageButtonNext)) {
            imageButtonNextOnClick();
        } else if(view.equals(imageButtonPrevious))  {
            imageButtonPreviousOnClick();
        } else if(view.equals(textViewTime)) {
            textViewTimeOnClick();
        } else if(view.equals(imageButtonShowSeekbar2)) {
            imageButtonShowSeekbar2OnClick();
        }*/
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
    /*

    @Override
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
    /*private void loadBackdrop(String url) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop_movie);
        Glide.with(this).load(url).into(imageView);
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private static Context mContext;

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<SongsLyrics> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            public String mBoundString;

            public final View mView;
            public final CircleButton mDwnldButton;
            public final TextView mSongName;
            private ImageButton menu;
            //public final TextView mSingerName;
            //public final ImageView mDwnldClick;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mSongName = (TextView) view.findViewById(R.id.songname);
              //  mDwnldClick = (ImageView) view.findViewById(R.id.dwnldIcon);
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

        public SongsLyrics getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<SongsLyrics> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            //mBackground = mTypedValue.resourceId;
            mValues = items;
            mContext = context;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_songs_details, parent, false);
            //view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holds = holder;
            holds.mDwnldButton.setVisibility(View.GONE);
            holds.mSongName.setText(mValues.get(position).getSONGNAME().toString());
            holds.mSongName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(mContext,LyricsActivity.class);
                    i.putExtra("lyrics",mValues.get(position).getLYRICS().toString());
                    i.putExtra("sname",mValues.get(position).getSONGNAME().toString());
                    i.putExtra("url",movObject.getURLS().toString());
                    mContext.startActivity(i);

                }
            });
            //holds.mDwnldButton.setOnC
            /*holds.mDwnldButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //MyIntentService.queuedDownloads.add(mValues.get(position));
                    //MyIntentService.myMapQueue.put(mValues.get(position).getSONG_ID(),mValues.get(position));

                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + mValues.get(position).getSONGNAME().toString() + ".mp3");
                    if(f.exists()){
                        Toast.makeText(mContext, "Song already in Downloads", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);

                        //intentMyIntentService.putExtra(MyIntentService.OBJECT_SONG, mValues.get(position));
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSONGLINK_128KBPS_CONV().toString());
                        intentMyIntentService.putExtra("songName", mValues.get(position).getSONGNAME().toString());
                        intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSONG_ID());
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSONGLINK_128KBPS().toString());
                        mContext.startService(intentMyIntentService);
                        Toast.makeText(mContext, "Song added to Downloads", Toast.LENGTH_SHORT).show();
                    }


                }
            });*/

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("in onclick", "in onclick");
                    Log.e("in onclick", "in onclick");
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_moviedetail_lyrics, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                /*case R.id.menu_asksong:

                                    String sname = mValues.get(position).getSONGNAME().toString();
                                    sname = getOnlyStrings(sname);
                                    String mnameAsk = mValues.get(position).getMovieLyrics().getMOVIENAME();
                                    mnameAsk = getOnlyStrings(mnameAsk);
                                    String finalSng = "Can you provide me song : "+sname+" from the movie/album "+mnameAsk;
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text*//*");
                                    share.putExtra(Intent.EXTRA_TEXT, finalSng);
                                    mContext.startActivity(Intent.createChooser(share, "Ask for Song"));

                                    return true;*/

                                case R.id.menu_watchonyoutube:

                                    String url = "https://www.youtube.com/results?search_query=";
                                    String snameCheck = getOnlyStrings(mValues.get(position).getSONGNAME());
                                    String mname = getOnlyStrings(mValues.get(position).getMovieLyrics().getMOVIENAME());
                                    url = url + mname + " - "+snameCheck;
                                    Uri uri = Uri.parse(url);
                                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                                    videoIntent.setData(uri);
                                    videoIntent.setPackage("com.google.android.youtube");
                                    //videoIntent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                                    mContext.startActivity(videoIntent);

                                    /*Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://www.youtube.com*//**YOURCHANNEL**"));

                                    startActivity(intent);*/

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
            //holds.mSingerName.setText(mValues.get(position).getSINGERS().toString());
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

