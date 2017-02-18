package com.support.android.musicindia.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.musicindia.Constants.Constants;
import com.support.android.musicindia.data.UserProvider;
import com.support.android.musicindia.dto.DTOProviderSONG;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.helper.DividerItemDecoration;
import com.support.android.musicindia.R;
import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.SONG;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsIndiPop;
import com.support.android.musicindia.services.MyIntentService;

import org.jaudiotagger.audio.AudioFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchSongActivity extends BaseActivity {
    private AudioFile f;
    private String title;
    private String artist;
    private EditText searchSongEditText;
    private Button searchButton;
    private RecyclerView rv;
    private CardView cardView;
    public String urltosend;
    public static List<SongsIndiPop> onRefresh;
    public static String driveURL = Constants.BASE_URL+"rest/songs/latestindi";
    public static String bollywood = Constants.BASE_URL+"rest/songs/bollywoodsearchsong?sname=";
    public static String punjabi = Constants.BASE_URL+"rest/songs/punjabisearchsong?sname=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ConnectionDetector conDet = new ConnectionDetector(this);
        setContentView(R.layout.activity_search_songs_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_moviedetail);
        toolbar.setTitle("Search Song");
        setSupportActionBar(toolbar);
        searchSongEditText = (EditText) findViewById(R.id.songNameEditText);
        searchButton = (Button) findViewById(R.id.searchSongButton);
        Bundle extras = getIntent().getExtras();
        urltosend="";
        if (extras != null) {
             urltosend = getIntent().getStringExtra("typesearch");
                if(urltosend.equals("bollywood"))
                    urltosend = bollywood;
                else
                    urltosend = punjabi;
        }
        cardView = (CardView) findViewById(R.id.cardRecyclerView);
        rv = (RecyclerView) findViewById(R.id.recycler_view_movie_songs);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setNestedScrollingEnabled(false);
        cardView.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchSongEditText.getText().toString() != null && searchSongEditText.getText().toString().length() > 0){

                    if (conDet.isConnectingToInternet()){

                        String ssong = searchSongEditText.getText().toString().toUpperCase();

                        String url = urltosend;
                        url = url+ssong;
                        (new AysncTaskSearchSong()).execute(url, "indi00");

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Please Check Network Connectivity",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Enter Song Name",Toast.LENGTH_SHORT).show();
                    if(cardView != null)
                        cardView.setVisibility(View.GONE);
                    if(rv != null)
                        rv.setVisibility(View.GONE);
                }

            }
        });

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
    public void onStart() {
        super.onStart();

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

    }

    private class AysncTaskSearchSong extends AsyncTask<String, Void, List<SongsIndiPop>> {
        
        private final ProgressDialog dialog = new ProgressDialog(SearchSongActivity.this);
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(List<SongsIndiPop> result) {
            super.onPostExecute(result);

            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();

                if(result == null){

                    Toast.makeText(SearchSongActivity.this,"No Results Found",Toast.LENGTH_SHORT).show();

                    onRefresh = null;

                    rv.setAdapter(new RecyclerViewSearchSong(getApplicationContext(), null));
                    rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    rv.setNestedScrollingEnabled(false);
                    cardView.setVisibility(View.GONE);
                }
                else if(result != null && result.size() == 0){

                    Toast.makeText(SearchSongActivity.this,"No Results Found",Toast.LENGTH_SHORT).show();

                    onRefresh = null;

                    rv.setAdapter(new RecyclerViewSearchSong(getApplicationContext(), null));
                    rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    rv.setNestedScrollingEnabled(false);
                    cardView.setVisibility(View.GONE);
                }
                else if (result != null && result.size() > 0 ) {

                    onRefresh = result;
                    cardView.setVisibility(View.VISIBLE);
                    rv.setAdapter(new RecyclerViewSearchSong(getApplicationContext(), onRefresh));
                    rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    rv.setNestedScrollingEnabled(false);

                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching Movies");
            dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected List<SongsIndiPop> doInBackground(String... params) {

            List<SongsIndiPop> mReturn = new ArrayList<SongsIndiPop>();
            listName = params[1];
            URL u;
            try {

                String encodedURL= params[0].replaceAll(" ","%20");
                u = new URL(encodedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                inputStream = new InputStreamReader(urlConnection.getInputStream());

                try {
                    BufferedReader bReader = new BufferedReader(inputStream);
                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sBuilder.append(line + "\n");
                    }

                    inputStream.close();
                    result = sBuilder.toString();
                    int mid = 0,sid=0;
                    String schar,mname,date,mdirector,actors,singers,director,url;
                    String working,workinglink,orglink,singersSong,sname,urls;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);

                        JSONArray jSongArray = jObj.getJSONArray("msongs");
                        for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                        {
                            JSONObject jObjectSong = jSongArray.getJSONObject(j);
                            sname = jObjectSong.getString("sname");

                            sid = jObjectSong.getInt("sid");
                            singersSong = jObjectSong.getString("singers");
                            orglink = jObjectSong.getString("orglink");
                            working = jObjectSong.getString("working");
                            workinglink= jObjectSong.getString("workinglink");
                            urls = jObjectSong.getString("urls");
                            mname = jObjectSong.getString("mname");
                            Movie m = new Movie();
                            m.setMOVIENAME(mname);
                            m.setURLS(urls);

                            SongsIndiPop sObj = new SongsIndiPop(m,sname,singersSong,orglink,sid,workinglink,working,null);

                            mReturn.add(sObj);

                        }

                    } catch (JSONException e) {
                        Log.e("JSONException", e.toString());
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("MalformedURL", e.toString());
                return null;
            } catch (ProtocolException e) {
                Log.e("ProtocolException", e.toString());
                return null;
            } catch (IOException e) {
                Log.e("IOException", e.toString());
                return null;
            }
            return mReturn;
        }
    }

    public static class RecyclerViewSearchSong
            extends RecyclerView.Adapter<RecyclerViewSearchSong.ViewHolder> {

        private Context mContext;

        ImageLoaderConfiguration config;

        DisplayImageOptions imgDisplayOptions;

        static ImageLoader imageLoader = ImageLoader.getInstance();

        private List<SongsIndiPop> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

            public final ImageView mMenu;
            public final View mView;
            public final CircleImageView mPopImage;
            public final TextView mTextView;
            public final TextView mTextMNAMEView;
            public final CircleButton mPlayIcon;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mMenu = (ImageView) view.findViewById(R.id.buttonMenu);
                mTextView = (TextView) view.findViewById(R.id.trackname);
                mPlayIcon = (CircleButton) view.findViewById(R.id.playIcon);
                mPopImage = (CircleImageView) view.findViewById(R.id.popImage);
                mTextMNAMEView = (TextView) view.findViewById(R.id.trackmoviename);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            }
        }

        public SongsIndiPop getValueAt(int position) {
            return mValues.get(position);
        }

        public RecyclerViewSearchSong(Context context, List<SongsIndiPop> items) {
            mValues = items;
            mContext = context;

            config  = new ImageLoaderConfiguration.Builder(context).memoryCacheSize(41943040).discCacheSize(104857600).threadPoolSize(10).build();
            imgDisplayOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_musicindia).showImageOnFail(R.drawable.ic_musicindia).showImageOnLoading(R.drawable.ic_musicindia).cacheInMemory().cacheOnDisc().build();
            imageLoader.init(config);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_tracks_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holds = holder;
            holds.mMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_popsongs, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

                                case R.id.menu_watchonyoutube:

                                    String url = "https://www.youtube.com/results?search_query=";
                                    String snameCheck = getOnlyStrings(mValues.get(position).getSONGNAME());
                                    String singers = mValues.get(position).getMovie().getMOVIENAME().toString();

                                    String mname = singers;

                                    url = url + mname + " - " + snameCheck;
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
                            listener.getPopup().setVerticalOffset(-holds.mMenu.getHeight());
                            listener.getPopup().show();
                        }
                    }


                }
            });

            holds.mTextMNAMEView.setVisibility(View.VISIBLE);
            holds.mTextMNAMEView.setText(mValues.get(position).getMovie().getMOVIENAME().toString());
            holds.mTextView.setText(mValues.get(position).getSONGNAME().toString());

            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_download_test);
            holds.mPlayIcon.setImageDrawable(d);

            imageLoader.displayImage("", holds.mPopImage); //clears previous one

            imageLoader.displayImage(mValues.get(position).getMovie().getURLS().toString(), holds.mPopImage,
                    imgDisplayOptions
            );

            holds.mPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + mValues.get(position).getSONGNAME().toString() + ".mp3");
                    if(f.exists()){


                        String alreadyInDb = mValues.get(position).getSONGNAME().toString();
                        int sid = mValues.get(position).getSONG_ID();

                        ArrayList<SONG> isExits = DTOProviderSONG.getSONGIteminDatabase(mContext, sid, alreadyInDb);
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

                            Toast.makeText(mContext,"Song Already in Downloads",Toast.LENGTH_SHORT).show();
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

                        SONG songObj = new SONG(mValues.get(position).getSONG_ID(),mValues.get(position).getSONGNAME(),mValues.get(position).getSONGLINK_128KBPS_CONV(),0,1);
                        ContentValues cv = getContentValues(songObj);
                        DTOProviderSONG.insertSONGIteminDatabase(mContext, cv);

                    }


                }
            });

        }

        public static String getOnlyStrings(String s) {
            Pattern pattern = Pattern.compile("[^a-z A-Z]");
            Matcher matcher = pattern.matcher(s);
            String number = matcher.replaceAll("");
            number = number.replaceAll("-","");
            return number;
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

        public void animateTo(List<SongsIndiPop> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<SongsIndiPop> newModels) {
            for (int i = mValues.size() - 1; i >= 0; i--) {
                final SongsIndiPop model = mValues.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<SongsIndiPop> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final SongsIndiPop model = newModels.get(i);
                if (!mValues.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<SongsIndiPop> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final SongsIndiPop model = newModels.get(toPosition);
                final int fromPosition = mValues.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public  SongsIndiPop removeItem(int position) {
            final SongsIndiPop model = mValues.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, SongsIndiPop model) {
            mValues.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final SongsIndiPop model = mValues.remove(fromPosition);
            mValues.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }

    }

}
