package com.support.android.musicindia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsIndiPop;

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
    public static String driveURL = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestindi";
    public static String bollywood = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodsearchsong?sname=";
    public static String punjabi = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/punjabisearchsong?sname=";
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
        //rv.setVisibility(View.GONE);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        cardView.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchSongEditText.getText().toString() != null && searchSongEditText.getText().toString().length() > 0){

                    if (conDet.isConnectingToInternet()){

                        String ssong = searchSongEditText.getText().toString().toUpperCase();

                        String url = urltosend;
                                //"http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodsearchsong?sname=";
                        url = url+ssong;
                        Log.e("URL IS",url);
                        Log.e("URL IS",url);
                        (new AsyncListViewLoaderTiled()).execute(url, "indi00");

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
        unbindService(BaseActivity.musicConnection);
    }

    @Override
    public void onStop() {
        super.onStop();

        /*unbindService(BaseActivity.musicConnection);*/



    }

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, List<SongsIndiPop>> {
        //SearchSongActivity ssa = ( SearchSongActivity ) getApplicationContext();
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

                    rv.setAdapter(new SimpleStringRecyclerViewAdapter(getApplicationContext(), null));
                    rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    cardView.setVisibility(View.GONE);
                }
                else if(result != null && result.size() == 0){

                    Toast.makeText(SearchSongActivity.this,"No Results Found",Toast.LENGTH_SHORT).show();

                    onRefresh = null;

                    rv.setAdapter(new SimpleStringRecyclerViewAdapter(getApplicationContext(), null));
                    rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    cardView.setVisibility(View.GONE);
                }
                else if (result != null && result.size() > 0 ) {

                    onRefresh = result;
                    cardView.setVisibility(View.VISIBLE);
                    rv.setAdapter(new SimpleStringRecyclerViewAdapter(getApplicationContext(), onRefresh));
                    rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
                    rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

                /*Log.e("Size of Post Ex", String.valueOf(SongsFilesData.myMap.get(listName).size()));
                Log.e("Size of Post Ex",String.valueOf(SongsFilesData.myMap.get(listName).size()));*/
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

            Log.e("In doinbac", "doinback");
            Log.e("In doinbac","doinback");
            List<SongsIndiPop> mReturn = new ArrayList<SongsIndiPop>();


            listName = params[1];
            /*Log.e("ListName is : ",listName);
            Log.e("ListName is : ",listName);*/

            URL u;
            try {
                Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);
                //Log.e("Params is : ",params[0]);


                String encodedURL= params[0].replaceAll(" ","%20");
                        //java.net.URLEncoder.encode(params[0],"UTF-8");
                Log.e("Encoded URL Is ",encodedURL);
                Log.e("Encoded URL Is ",encodedURL);

                u = new URL(encodedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");

                // urlConnection.setDoOutput(true);
                //  urlConnection.setChunkedStreamingMode(100);

                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                 Log.e("Response Code is : ",String.valueOf(code));
                 Log.e("Response Code is : ",String.valueOf(code));
                 Log.e("Response Code is : ",String.valueOf(code));
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
                      Log.e("Result is : ",result);
                      Log.e("Result is : ",result);
                      Log.e("Result is : ",result);

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
                            Log.e("Song Name is",sname);
                            Log.e("Song Name is",sname);

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
                            //Movie movie, String sONGNAME, String sINGERS, String sONGLINK_128KBPS, int sONG_ID,String SONGLINK_128KBPS_CONV,String WORKING_LINK
                            SongsIndiPop sObj = new SongsIndiPop(m,sname,singersSong,orglink,sid,workinglink,working,null);
                            //songsObj.add(sObj);
                            mReturn.add(sObj);

                        }
                        //movieObject.setSONGS(songsObj);



                        // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    //Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                return null;
            } catch (ProtocolException e) {
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;

            }


            return mReturn;
        }
    }



    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private Context mContext;

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

        public SimpleStringRecyclerViewAdapter(Context context, List<SongsIndiPop> items) {


            mValues = items;
            mContext = context;


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
                    Log.e("in onclick", "in onclick");
                    Log.e("in onclick", "in onclick");
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_popsongs, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                /*case R.id.menu_asksong:

                                    String sname = mValues.get(position).getSONGNAME().toString();
                                    sname = getOnlyStrings(sname);
                                    String singersask = mValues.get(position).getSINGERS().replaceAll("NOT AVAILABLE ,", "");
                                    String finalSng = "Can you provide me song : " + sname + " by singer/artist " + singersask;
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text*//*");
                                    share.putExtra(Intent.EXTRA_TEXT, finalSng);
                                    mContext.startActivity(Intent.createChooser(share, "Ask for Song"));

                                    return true;*/

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
                            listener.getPopup().setVerticalOffset(-holds.mMenu.getHeight());
                            listener.getPopup().show();
                        }
                    }


                }
            });
            //holds.mSingerName.setText(mValues.get(position).getSINGERS().toString());
            holds.mTextMNAMEView.setVisibility(View.VISIBLE);
            holds.mTextMNAMEView.setText(mValues.get(position).getMovie().getMOVIENAME().toString());
            holds.mTextView.setText(mValues.get(position).getSONGNAME().toString());
            //holds.mPlayIcon.setVisibility(View.GONE);
            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_download_test);
            holds.mPlayIcon.setImageDrawable(d);
            /*holds.mPopImage.setVisibility(View.GONE);*/
            //holds.mPopImage.setVisibility(View.GONE);
            if(!mValues.get(position).getMovie().getURLS().toString().equals("NA"))
                Glide.with(mContext).load(mValues.get(position).getMovie().getURLS().toString()).into(holds.mPopImage);
            holds.mPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            });

        }

        public static String getOnlyStrings(String s) {
            Pattern pattern = Pattern.compile("[^a-z A-Z]");
            Matcher matcher = pattern.matcher(s);
            String number = matcher.replaceAll("");
            number = number.replaceAll("-","");
            return number;
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
