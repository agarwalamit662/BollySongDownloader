package com.support.android.musicindia.activities;

/**
 * Created by amitagarwal3 on 3/8/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.support.android.musicindia.Constants.Constants;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.R;

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

import at.markushi.ui.CircleButton;

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
    private CircleButton pstore;
    private TextView totalInstalls;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme_details);

        fb = (ImageButton) findViewById(R.id.fblink);
        twitter = (ImageButton) findViewById(R.id.twitterlink);
        gplus = (ImageButton) findViewById(R.id.googlepluslink);
        pstore = (CircleButton) findViewById(R.id.playstore);
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

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        number = number.replaceAll("-","");
        return number;
    }


    private class AsyncListViewLoaderTiledFirst extends AsyncTask<String, Void, Integer> {

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
                String punjabiUrl = Constants.BASE_URL+"rest/songs/totalinstalls";
                u  = new URL(punjabiUrl);
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
                    try {

                        JSONObject jObj = new JSONObject(result);
                        success = jObj.getInt("totalinstall");

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        return 0;
                    }
                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    return 0;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return 0;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;

            }
            return success;
        }
    }

}

