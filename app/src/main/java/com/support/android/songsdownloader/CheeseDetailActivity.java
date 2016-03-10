package com.support.android.songsdownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheeseDetailActivity extends AppCompatActivity {

    private RecyclerView rv;
    public static final String EXTRA_NAME = "cheese_name";
    private String mName;
    private String mUrl;
    private ProgressDialog dialog;
    private static TextView noSongs;
    private static Button otezip;
    private static Button ttzip;
    private String linkOne;
    private String linkTwo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        rv = (RecyclerView) findViewById(R.id.recyclerviewMovies);
        noSongs = (TextView) findViewById(R.id.noSongsFound);
        otezip = (Button) findViewById(R.id.otekbzip);
        ttzip = (Button) findViewById(R.id.ttkbzip);
        Intent intent = getIntent();
        mName = intent.getStringExtra("mName");
        mUrl = intent.getStringExtra("mUrl");

        dialog = new ProgressDialog(CheeseDetailActivity.this);

        /*final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSongs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        loadBackdrop();
        //getSupportActionBar().setTitle(mName);
        (new AsyncListZipViewLoader()).execute("http://www.songsmp3.com" + mUrl);
        (new AsyncListViewLoader()).execute("http://www.songsmp3.com" + mUrl);

        otezip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), linkOne, Toast.LENGTH_SHORT);

            }
        });
        ttzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), linkTwo, Toast.LENGTH_SHORT);

            }
        });




    }
    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    private class AsyncListViewLoader extends AsyncTask<String, Void, List<MoviesSongs>> {


        @Override
        protected void onPostExecute(List<MoviesSongs> result) {
            super.onPostExecute(result);

            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();



                final LinearLayoutManager layoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(CheeseDetailActivity.this, LinearLayoutManager.VERTICAL, false);
                rv.setNestedScrollingEnabled(false);
                rv.setHasFixedSize(false);
                rv.addItemDecoration(new DividerItemDecoration(CheeseDetailActivity.this, LinearLayoutManager.VERTICAL));
                rv.setLayoutManager(layoutManager);
                //rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                rv.setAdapter(new SimpleStringRecyclerViewAdapter(CheeseDetailActivity.this, result));
            }
            }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading Songs List...");
            dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            dialog.setCancelable(false);
            dialog.show();
        }

        private int getScreenHeight(Context context) {
            int measuredHeight;
            Point size = new Point();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                wm.getDefaultDisplay().getSize(size);
                measuredHeight = size.y;
            } else {
                Display d = wm.getDefaultDisplay();
                measuredHeight = d.getHeight();
            }

            return measuredHeight;
        }

        @Override
        protected List<MoviesSongs> doInBackground(String... params) {
            List<MoviesSongs> result = new ArrayList<MoviesSongs>();
            Document doc = null;
            try {
                doc = Jsoup.connect(params[0]).get();
                String title = doc.title();
                String href = doc.text();

                Elements e = doc.getElementsByAttributeValueContaining("class", "sinlge_link_item");

                for (Element src : e) {

                    // Elements f = src.select("b");
                    if (src.tagName().equals("div")) {

                        Elements f = src.getElementsByAttributeValueContaining("class","link-item");
                        for(Element srcs : f)
                        {
                            if(srcs.tagName().equals("div"))
                            {
                                Elements k = srcs.getElementsByAttributeValueContaining("href","/download/");
                                for(Element srcsSongs : k) {

                                    if(srcsSongs.tagName().equals("a")) {
                                        String songName = srcsSongs.text();


                                        Document docSongs = Jsoup.connect("http://www.songsmp3.com"+srcsSongs.attr("href")).get();
                                        String otekb=null;
                                        String ttkb = null;
                                        Elements eleSongs = docSongs.getElementsByAttributeValueContaining("href", "http://dl.songsmp3.com/fileDownload/Songs/128");
                                        for(Element eleSize : eleSongs){

                                            otekb = eleSize.attr("href");

                                        }
                                        Elements eleSongstt = docSongs.getElementsByAttributeValueContaining("href","http://dl.songsmp3.com/fileDownload/Songs/0");
                                        for(Element eleSize : eleSongstt){

                                            ttkb = eleSize.attr("href");

                                        }


                                        MoviesSongs m = new MoviesSongs(srcsSongs.text(),srcsSongs.attr("href"),otekb,ttkb);
                                        result.add(m);

                                    }
                                }

                            }
                        }


                    } else {
                        String theatrename = "hello";

                    }
                }
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }



    }


    private class AsyncListZipViewLoader extends AsyncTask<String, Void, List<MoviesZipSongs>> {


        @Override
        protected void onPostExecute(final List<MoviesZipSongs> result) {
            super.onPostExecute(result);
            if(result != null && result.size() > 0) {
                String oTEKBname = result.get(0).getmOTEKBNAME();
                if (oTEKBname != null) {
                    otezip.setVisibility(View.VISIBLE);
                    otezip.setTextSize(8);
                    linkOne = result.get(0).getmOTEKB();
                    otezip.setText(oTEKBname);
                    /*otezip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(),result.get(0).getmOTEKB(),Toast.LENGTH_SHORT);
                        }
                    });*/
                }
                String oTTKBName = result.get(0).getmTTKBNAME();
                if (oTTKBName != null) {
                    ttzip.setVisibility(View.VISIBLE);
                    ttzip.setTextSize(8);
                    ttzip.setText(oTTKBName);
                    linkTwo = result.get(0).getmTTKB();
                    /*ttzip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(),result.get(0).getmTTKB(),Toast.LENGTH_SHORT);
                        }
                    });*/
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        private int getScreenHeight(Context context) {
            int measuredHeight;
            Point size = new Point();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                wm.getDefaultDisplay().getSize(size);
                measuredHeight = size.y;
            } else {
                Display d = wm.getDefaultDisplay();
                measuredHeight = d.getHeight();
            }

            return measuredHeight;
        }

        @Override
        protected List<MoviesZipSongs> doInBackground(String... params) {
            List<MoviesZipSongs> result = new ArrayList<MoviesZipSongs>();
            String mOTELink = null;
            String mOTEName = null;
            String mTTLink = null;
            String mTTName = null;
            try {
                Document oteLink =  Jsoup.connect("http://www.songsmp3.com" + mUrl).get();

                Elements oteLinkZip = oteLink.getElementsByAttributeValueContaining("href", "http://www.songsmp3.com/files/zip/128/");
                for(Element link : oteLinkZip)
                {
                    mOTELink = link.attr("href");
                    mOTEName = link.text();

                }

                Elements ttLinkZip = oteLink.getElementsByAttributeValueContaining("href","http://www.songsmp3.com/files/zip/original/");
                for(Element link : ttLinkZip)
                {

                    mTTLink = link.attr("href");
                    mTTName = link.text();

                }
                MoviesZipSongs mZip = new MoviesZipSongs(mOTELink,mTTLink,mOTEName,mTTName);
                result.add(mZip);

                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }



    }


    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
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
        private List<MoviesSongs> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;

            public final TextView mTextView;
            public final Button mOTELink;
            public final Button mTTLink;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTextView = (TextView) view.findViewById(R.id.textMovieSongsName);
                mOTELink = (Button) view.findViewById(R.id.otekbSongButton);
                mTTLink = (Button) view.findViewById(R.id.ttkbSongButton);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public MoviesSongs getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<MoviesSongs> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            if(items == null){
                noSongs.setVisibility(View.VISIBLE);
            }
            else if(items.size() == 0){
                noSongs.setVisibility(View.VISIBLE);
            }
            else
            {
                noSongs.setVisibility(View.GONE);
            }
            mValues = items;
            mContext = context;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_movie_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holds = holder;

            holds.mBoundString = mValues.get(position).getmSongsName();
            //holds.mTextView.setText(String.valueOf(position));
            holds.mTextView.setText(mValues.get(position).getmSongsName().toString());
            holds.mOTELink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String songLink = mValues.get(position).getmOTEKB().toString();
                    String songName = mValues.get(position).getmSongsName().toString();

                    //Start MyIntentService
                    Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                    intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, songLink);
                    intentMyIntentService.putExtra("songName",songName +"128kb");
                    intentMyIntentService.putExtra("songId",(int)(position+1));
                    mContext.startService(intentMyIntentService);
                   // Toast.makeText(mContext,"Song added to download queue",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("EXTRA_PAGE", "1");
                    mContext.startActivity(intent);

                }
            });
            holds.mTTLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String songLink = mValues.get(position).getmTTKB().toString();
                    String songName = mValues.get(position).getmSongsName().toString();

                    //Start MyIntentService
                    Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                    intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, songLink);
                    intentMyIntentService.putExtra("songId",(int)(position+1));
                    intentMyIntentService.putExtra("songName",songName +"320kb");
                   // Toast.makeText(mContext,"Song added to download queue",Toast.LENGTH_SHORT).show();
                    mContext.startService(intentMyIntentService);

                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("EXTRA_PAGE", "1");
                    mContext.startActivity(intent);

                }
            });


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }



    }

}
