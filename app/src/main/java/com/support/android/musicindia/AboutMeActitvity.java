package com.support.android.musicindia;

/**
 * Created by amitagarwal3 on 3/8/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.support.android.musicindia.model.Songs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AboutMeActitvity extends BaseActivity {

    private RecyclerView rv;
    public static final String EXTRA_NAME = "cheese_name";
    private String mName;
    private String mUrl;
    private ProgressDialog dialog;
    private String linkOne;
    private String linkTwo;

    private ImageButton fb;
    private ImageButton twitter;
    private ImageButton gplus;
    private ImageButton pstore;
    private TextView totalInstalls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_movie);*/

        /*textViewArtistTandI = (TextView)findViewById(R.id.textViewArtistTandI);
        imageViewSongImageTandI = (CircleImageView)findViewById(R.id.songurl);
        textViewArtistTandI.setOnClickListener(this);
        imageViewSongImageTandI.setOnClickListener(this);*/

        fb = (ImageButton) findViewById(R.id.fblink);
        twitter = (ImageButton) findViewById(R.id.twitterlink);
        gplus = (ImageButton) findViewById(R.id.googlepluslink);
        pstore = (ImageButton) findViewById(R.id.playstore);
        totalInstalls = (TextView) findViewById(R.id.totalInstalls);
        totalInstalls.setVisibility(View.GONE);
        final ConnectionDetector conDet = new ConnectionDetector(AboutMeActitvity.this);
        if(conDet.isConnectingToInternet()){
            (new AsyncListViewLoaderTiledFirst()).execute("", "punjabi0");
        }

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fburl = "https://www.facebook.com/amit.agarwal.3367";
                Uri uriLyrics = Uri.parse(fburl);
                Intent videoIntentLyrics = new Intent(Intent.ACTION_VIEW);
                videoIntentLyrics.setData(uriLyrics);
                videoIntentLyrics.setPackage("com.android.chrome");
                startActivity(videoIntentLyrics);


            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fburl = "https://twitter.com/agarwalamit662";
                Uri uriLyrics = Uri.parse(fburl);
                Intent videoIntentLyrics = new Intent(Intent.ACTION_VIEW);
                videoIntentLyrics.setData(uriLyrics);
                videoIntentLyrics.setPackage("com.android.chrome");
                startActivity(videoIntentLyrics);


            }
        });

        pstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String fburl = "https://play.google.com/store/apps/developer?id=BollyMusicDeveloper";
                Uri uriLyrics = Uri.parse(fburl);
                Intent videoIntentLyrics = new Intent(Intent.ACTION_VIEW);
                videoIntentLyrics.setData(uriLyrics);
                videoIntentLyrics.setPackage("com.android.chrome");
                startActivity(videoIntentLyrics);*/

                MainActivity.openAppRating(getApplicationContext());


            }
        });



        gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fburl = "https://plus.google.com/102426049325044641408";
                Uri uriLyrics = Uri.parse(fburl);
                Intent videoIntentLyrics = new Intent(Intent.ACTION_VIEW);
                videoIntentLyrics.setData(uriLyrics);
                videoIntentLyrics.setPackage("com.android.chrome");
                startActivity(videoIntentLyrics);


            }
        });

    }


    @Override
    public void onClick(View view) {


        /*if(view.equals(textViewArtistTandI)){
            Intent i = new Intent(this,NowPlaying.class);
            startActivity(i);
        }
        else if(view.equals(imageViewSongImageTandI)){

            Intent i = new Intent(this,NowPlaying.class);
            startActivity(i);

        }*/
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();

        //*//*bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);*//*


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

        //*unbindService(BaseActivity.musicConnection);*//*



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


    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        number = number.replaceAll("-","");
        return number;
    }


    private class AsyncListViewLoaderTiledFirst extends AsyncTask<String, Void, Integer> {
        //private final ProgressDialog dialog = new ProgressDialog(getActivity());
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(Integer xyz) {
            super.onPostExecute(xyz);
            totalInstalls.setVisibility(View.VISIBLE);
            totalInstalls.setText("Total Installs : "+String.valueOf(xyz) );

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Integer doInBackground(String... params) {

            int success = 0;

            URL u;
            try {
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);

                //u = new URL(params[0]);
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/totalinstalls";

                u  = new URL(punjabiUrl);
                //u  = new URL(driveURL+"latestPunjabiSongs.txt");
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");

                // urlConnection.setDoOutput(true);
                //  urlConnection.setChunkedStreamingMode(100);

                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                // Log.e("Response Code is : ",String.valueOf(code));
                // Log.e("Response Code is : ",String.valueOf(code));
                // Log.e("Response Code is : ",String.valueOf(code));
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




                    try {

                        JSONObject jObj = new JSONObject(result);
                        success = jObj.getInt("totalinstall");




                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());

                        //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return 0;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());

                    //Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return 0;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();

                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                return 0;
            } catch (ProtocolException e) {
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();

                return 0;
            } catch (IOException e) {
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();


                e.printStackTrace();
                return 0;

            }


            return success;

        }
    }

}

