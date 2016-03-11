package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 3/8/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.support.android.songsdownloader.model.Movie;
import com.support.android.songsdownloader.model.Songs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MovieDetailActivity extends AppCompatActivity {

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
    private String linkOne;
    private String linkTwo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_details);

        Intent i = getIntent();
        Movie movObject = (Movie)i.getSerializableExtra("MovieObject");


        actors = (TextView) findViewById(R.id.actors);
        singers = (TextView)findViewById(R.id.singers);
        mdirector = (TextView) findViewById(R.id.mdirector);
        director = (TextView)findViewById(R.id.director);
        mname = (TextView)findViewById(R.id.mname);


        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_movie);

        if(movObject != null ) {
            if (movObject.getACTORS().contentEquals("Not Available ,"))
                actors.setText("Not Available");
            else
                actors.setText(movObject.getACTORS().replace("Not Available , ", ""));
            if (movObject.getDIRECTOR().contentEquals("Not Available ,"))
                director.setText("Not Available");
            else
                director.setText(movObject.getDIRECTOR().replace("Not Available , ", ""));

            if (movObject.getSINGERS().contentEquals("Not Available ,"))
                singers.setText("Not Available");
            else
                singers.setText(movObject.getSINGERS().replace("Not Available , ", ""));

            if (movObject.getMUSIC_DIRECTOR().contentEquals("Not Available ,"))
                mdirector.setText("Not Available");
            else
                mdirector.setText(movObject.getMUSIC_DIRECTOR().replace("Not Available , ", ""));

            if (movObject.getMOVIENAME().contains("Mp3 Songs"))
                mname.setText(movObject.getMOVIENAME().replace("Mp3 Songs", ""));
            else if (movObject.getMOVIENAME().contains("Mp3 Song"))
                mname.setText(movObject.getMOVIENAME().replace("Mp3 Song", ""));
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
            rView.setAdapter(new SimpleStringRecyclerViewAdapter(MovieDetailActivity.this, sList));
            loadBackdrop(movObject.getURLS());

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadBackdrop(String url) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop_movie);
        Glide.with(this).load(url).into(imageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private Context mContext;

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Songs> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;

            public final TextView mSongName;
            //public final TextView mSingerName;
            public final ImageView mDwnldClick;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mSongName = (TextView) view.findViewById(R.id.songname);
                mDwnldClick = (ImageView) view.findViewById(R.id.dwnldIcon);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mSongName.getText();
            }
        }

        public Songs getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<Songs> items) {
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

            holds.mSongName.setText(mValues.get(position).getSONGNAME().toString());
            holds.mDwnldClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                    intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSONGLINK_128KBPS_CONV().toString());
                    intentMyIntentService.putExtra("songName",mValues.get(position).getSONGNAME().toString());
                    intentMyIntentService.putExtra("songId",(int)mValues.get(position).getSONG_ID());
                    intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH,mValues.get(position).getSONGLINK_128KBPS().toString());
                    mContext.startService(intentMyIntentService);
                    Toast.makeText(mContext,"Song added to download queue",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("EXTRA_PAGE", "1");
                    mContext.startActivity(intent);


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

}

