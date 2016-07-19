package com.support.android.musicindia.activities;

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

import com.support.android.musicindia.helper.DividerItemDecoration;
import com.support.android.musicindia.R;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_moviedetail);
        toolbar.setTitle(movObject.getMOVIENAME().toString());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        cardMovies = (CardView) findViewById(R.id.cardMovies);
        cardMovies.setVisibility(View.GONE);
        goToDownloads = (Button) findViewById(R.id.gotoDownloads);
        goToDownloads.setVisibility(View.GONE);
        rView = (RecyclerView) findViewById(R.id.recycler_view_movie_songs);
        List<SongsLyrics> sList = movObject.getSONGS();


        LinearLayoutManager lm = new LinearLayoutManager(MovieDetailActivityLyrics.this,LinearLayoutManager.VERTICAL,false);
        rView.setNestedScrollingEnabled(false);
        rView.setHasFixedSize(false);
        rView.setFocusable(false);
        rView.setLayoutManager(lm);
        rView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rView.setAdapter(new RecyclerViewAdapterMovieDetail(MovieDetailActivityLyrics.this, sList));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


    public static class RecyclerViewAdapterMovieDetail
            extends RecyclerView.Adapter<RecyclerViewAdapterMovieDetail.ViewHolder> {

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

        public SongsLyrics getValueAt(int position) {
            return mValues.get(position);
        }

        public RecyclerViewAdapterMovieDetail(Context context, List<SongsLyrics> items) {
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

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_moviedetail_lyrics, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.menu_watchonyoutube:

                                    String url = "https://www.youtube.com/results?search_query=";
                                    String snameCheck = getOnlyStrings(mValues.get(position).getSONGNAME());
                                    String mname = getOnlyStrings(mValues.get(position).getMovieLyrics().getMOVIENAME());
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

