
package com.support.android.musicindia.activities;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.application.MusicPlayerApplication;
import com.support.android.musicindia.fragments.PopSongsFragment;
import com.support.android.musicindia.R;
import com.support.android.musicindia.fragments.TiledListFragment;
import com.support.android.musicindia.fragments.TiledListFragmentLyrics;
import com.support.android.musicindia.fragments.TiledListFragmentPunjabi;
import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.MovieLyrics;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsFilesData;
import com.support.android.musicindia.model.SongsIndiPop;
import com.support.android.musicindia.model.SongsLyrics;
import com.support.android.musicindia.services.RegistrationService;
import com.support.android.musicindia.services.TokenRefreshListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class SplashScreenActivity extends AppCompatActivity {

    private static String TAG = SplashScreenActivity.class.getName();
    public static String driveURL = "https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/";
    public static String updatelink = "";
    public static String sharelink = "";
    public static int versionCode = 0;
    public static boolean openAsPlayer = false;
    public static int indexOfDownloads = 4;
    List<Movie> mReturnPunjabi = new ArrayList<Movie>();
    List<SongsIndiPop> mReturnPop = new ArrayList<SongsIndiPop>();
    List<Movie> mReturnBollywood = new ArrayList<Movie>();
    List<MovieLyrics> mReturnBollywoodLyrics = new ArrayList<MovieLyrics>();
    private Button startAsPlayer;
    public SharedPreferences settings;
    private Button tryAgain;
    private ProgressBar pgBar;
    private TextView loadingDataText;

    @Override
    protected void onResume(){
        super.onResume();
        final ConnectionDetector conDet = new ConnectionDetector(SplashScreenActivity.this);
        if(conDet.isConnectingToInternet()){
            (new AsyncTaskShareUrl()).execute("", "");
            (new AsyncTaskUpdateLink()).execute("", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        final ConnectionDetector conDet = new ConnectionDetector(SplashScreenActivity.this);
        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstRun=settings.getBoolean("firstRun", false);


        if(!firstRun && conDet.isConnectingToInternet()){
            (new AsyncTaskAppOpenFirstTime()).execute(deviceId, "punjabi0");
        }

        if(conDet.isConnectingToInternet()){
            (new AsyncTaskShareUrl()).execute("", "");
            (new AsyncTaskUpdateLink()).execute("", "");
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String splashflag = extras.getString("splashflag");
            String splashLoadedTinyMainActivityFlag = extras.getString("splashLoadedTinyMainActivity");
            if(splashflag != null && splashflag.equals("false") && splashLoadedTinyMainActivityFlag != null && splashLoadedTinyMainActivityFlag.equals("true")) {
                MusicPlayerApplication.splashLoadedTiny = false;
                MusicPlayerApplication.splashLoadedTinyMainActivity = true;
            }
            else if(splashflag != null && splashflag.equals("false") && splashLoadedTinyMainActivityFlag != null && splashLoadedTinyMainActivityFlag.equals("false")){
                MusicPlayerApplication.splashLoadedTiny = false;
                MusicPlayerApplication.splashLoadedTinyMainActivity = false;
            }

        }

        // Start timer and launch main activity
        pgBar = (ProgressBar) findViewById(R.id.splashProgressBar);
        startAsPlayer = (Button) findViewById(R.id.startAsPlayer);
        tryAgain = (Button) findViewById(R.id.tryAgain);
        loadingDataText = (TextView) findViewById(R.id.loadingDataSplash);
        if (!MusicPlayerApplication.splashLoadedTiny) {

        }
        else {
            MusicPlayerApplication.splashLoadedTiny = true;
            Intent goToMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
            goToMainActivity.putExtra("splashLoadedTiny",true);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(goToMainActivity);

        }
        if(startAsPlayer != null){
        startAsPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerApplication.splashLoadedTinyMainActivity = true;
                openAsPlayer = true;
                indexOfDownloads = 1;
                MusicPlayerApplication.splashLoadedTiny = true;
                loadingDataText.setVisibility(View.VISIBLE);
                Intent goToMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                goToMainActivity.putExtra("splashLoadedTinyMainActivity", true);
                goToMainActivity.putExtra("splashLoadedTiny",true);
                startActivity(goToMainActivity);


            }
        });
        }
        if(tryAgain != null) {
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pgBar.setVisibility(View.VISIBLE);
                    loadingDataText.setVisibility(View.VISIBLE);
                    openAsPlayer = false;
                    indexOfDownloads = 1;
                    MusicPlayerApplication.splashLoadedTinyMainActivity = false;
                    (new AsyncTaskLoadAllSongsAndLyrics()).execute(driveURL + "latestPunjabiSongs.txt", "punjabi0");
                }
            });
        }

        boolean isPlayServiceInstalled = isPlayServicesInstalled();
        if(isPlayServiceInstalled){

            boolean regToken = settings.getBoolean("regToken", false);
            if(!regToken){

                Log.e("PLAYSERVICEHAI","PLAYSERVICEAVAILABLE");
                Log.e("PLAYSERVICEHAI","PLAYSERVICEAVAILABLE");
                Log.e("PLAYSERVICEHAI","PLAYSERVICEAVAILABLE");
                Log.e("PLAYSERVICEHAI","PLAYSERVICEAVAILABLE");
                Intent i = new Intent(this, RegistrationService.class);
                startService(i);

            }
        }
        else{

            Log.e("PlayStoreNA","Play Sotre Not availble");
        }



    }

    //code to check Google play services availability.
    private boolean isPlayServicesInstalled() {
        GoogleApiAvailability getGoogleapiAvailability = GoogleApiAvailability.getInstance();
        int Code = getGoogleapiAvailability.isGooglePlayServicesAvailable(this);
        if (Code != ConnectionResult.SUCCESS) {
            if (getGoogleapiAvailability.isUserResolvableError(Code)) {
                getGoogleapiAvailability.getErrorDialog(this, Code, 9000)
                        .show();
            } else {
                Log.i("SplashActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("splashTinyMain", MusicPlayerApplication.splashLoadedTinyMainActivity);
        savedInstanceState.putBoolean("splashTinySplash",MusicPlayerApplication.splashLoadedTiny);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MusicPlayerApplication.splashLoadedTinyMainActivity = savedInstanceState.getBoolean("splashTinyMain");
        MusicPlayerApplication.splashLoadedTiny = savedInstanceState.getBoolean("splashTinySplash");
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    private class AsyncTaskLoadAllSongsAndLyrics extends AsyncTask<String, Void, Void> {

        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(Void xyz) {
            super.onPostExecute(xyz);

            TiledListFragmentPunjabi.onRefresh = mReturnPunjabi;
            SongsFilesData.myMap.put("punjabi0", mReturnPunjabi);

            TiledListFragment.onRefresh = mReturnBollywood;
            SongsFilesData.myMap.put("songs00", mReturnBollywood);

            PopSongsFragment.onRefresh = mReturnPop;
            SongsFilesData.myIndiMap.put("indi00", mReturnPop);

            TiledListFragmentLyrics.onRefresh = mReturnBollywoodLyrics;
            SongsFilesData.myMapLyrics.put("lyrics00",mReturnBollywoodLyrics);

            if(mReturnBollywood != null && mReturnPunjabi != null && mReturnPop != null) {
                pgBar.setVisibility(View.INVISIBLE);
                loadingDataText.setVisibility(View.INVISIBLE);
                indexOfDownloads = 4;
                openAsPlayer = false;
                MusicPlayerApplication.splashLoadedTiny = true;
                MusicPlayerApplication.splashLoadedTinyMainActivity = false;
                Intent goToMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                goToMainActivity.putExtra("splashLoadedTiny",true);
                goToMainActivity.putExtra("splashLoadedTinyMainActivity",false);
                startActivity(goToMainActivity);

            }
            else
            {
                Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                openAsPlayer = false;
                MusicPlayerApplication.splashLoadedTinyMainActivity = true;
                indexOfDownloads = 4;
                pgBar.setVisibility(View.INVISIBLE);
                loadingDataText.setVisibility(View.INVISIBLE);
                startAsPlayer.setVisibility(View.VISIBLE);
                tryAgain.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAsPlayer.setVisibility(View.INVISIBLE);
            tryAgain.setVisibility(View.INVISIBLE);
            pgBar.setVisibility(View.VISIBLE);
            loadingDataText.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... params) {


            mReturnPunjabi = new ArrayList<Movie>();
            mReturnPop = new ArrayList<SongsIndiPop>();
            mReturnBollywood = new ArrayList<Movie>();
            mReturnBollywoodLyrics = new ArrayList<MovieLyrics>();


            listName = params[1];
            URL u;
            try {
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestpunjabi";
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

                    int mid = 0,sid=0;
                    String schar,mname,date,mdirector,actors,singers,director,url;
                    String working,workinglink,orglink,singersSong,sname;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        JSONArray jArray = jObj.getJSONArray("movies");
                        for(int i=0; jArray != null && jArray.length() > 0 && i < jArray.length(); i++) {

                            Movie movieObject;
                            JSONObject jObject = jArray.getJSONObject(i);
                            if(jObject.isNull("mid"))
                                continue;
                            else{
                                mid = jObject.getInt("mid");
                            }
                            if(jObject.isNull("char"))
                                schar = "NA";
                            else{
                                schar = jObject.getString("char");
                            }
                            if(jObject.isNull("mname"))
                                mname = "NA";
                            else{
                                mname = jObject.getString("mname");
                            }
                            if(jObject.isNull("date"))
                                date = "NA";
                            else{
                                date = jObject.getString("date");
                            }
                            if(jObject.isNull("mdirector"))
                                mdirector = "NA";
                            else{
                                mdirector = jObject.getString("mdirector");
                            }
                            if(jObject.isNull("actors"))
                                actors = "NA";
                            else{
                                actors = jObject.getString("actors");
                            }
                            if(jObject.isNull("singers"))
                                singers = "NA";
                            else{
                                singers = jObject.getString("singers");
                            }
                            if(jObject.isNull("director"))
                                director = "NA";
                            else{
                                director = jObject.getString("director");
                            }
                            if(jObject.isNull("url"))
                                url = "NA";
                            else{
                                url = jObject.getString("url");
                            }
                            movieObject = new Movie(mid,schar,mname,date,mdirector,actors,singers,director,url,null,null);
                            List<Songs> songsObj = new ArrayList<Songs>();
                            JSONArray jSongArray = jObject.getJSONArray("msongs");
                            for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                            {
                                JSONObject jObjectSong = jSongArray.getJSONObject(j);
                                if(jObjectSong.isNull("sname"))
                                    sname = "NA";
                                else{
                                    sname = jObjectSong.getString("sname");
                                }
                                if(jObjectSong.isNull("sid"))
                                    continue;
                                else{
                                    sid = jObjectSong.getInt("sid");
                                }
                                if(jObjectSong.isNull("singers"))
                                    singersSong = "NA";
                                else{
                                    singersSong = jObjectSong.getString("singers");
                                }
                                if(jObjectSong.isNull("orglink"))
                                    orglink = "NA";
                                else{
                                    orglink = jObjectSong.getString("orglink");
                                }
                                if(jObjectSong.isNull("working"))
                                    continue;
                                else{
                                    working = jObjectSong.getString("working");
                                }
                                if(jObjectSong.isNull("workinglink"))
                                    workinglink = "NA";
                                else{
                                    workinglink = jObjectSong.getString("workinglink");
                                }
                                Songs sObj = new Songs(movieObject,sname,singersSong,orglink,sid,workinglink,working);
                                songsObj.add(sObj);


                            }
                            movieObject.setSONGS(songsObj);
                            if(songsObj != null && songsObj.size() > 0) {
                                mReturnPunjabi.add(movieObject);
                            }

                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnPunjabi = null;
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    mReturnPunjabi = null;
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("MalformedURL ", "Error converting result " + e.toString());
                mReturnPunjabi = null;
                return null;
            } catch (ProtocolException e) {
                Log.e("ProtocolException ", "Error converting result " + e.toString());
                mReturnPunjabi = null;
                return null;
            } catch (IOException e) {
                mReturnPunjabi = null;
                Log.e("IOException ", "Error converting result " + e.toString());
                return null;
            }

            URL uBolly;
            try {
                String latestbollywood = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestbollywood";
                uBolly  = new URL(latestbollywood);
                HttpURLConnection urlConnection = (HttpURLConnection) uBolly.openConnection();
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
                    String working,workinglink,orglink,singersSong,sname;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        JSONArray jArray = jObj.getJSONArray("movies");
                        for(int i=0; jArray != null && jArray.length() > 0 && i < jArray.length(); i++) {

                            Movie movieObject;
                            JSONObject jObject = jArray.getJSONObject(i);

                            if(jObject.isNull("mid"))
                                continue;
                            else{
                                mid = jObject.getInt("mid");
                            }
                            if(jObject.isNull("char"))
                                schar = "NA";
                            else{
                                schar = jObject.getString("char");
                            }
                            if(jObject.isNull("mname"))
                                mname = "NA";
                            else{
                                mname = jObject.getString("mname");
                            }
                            if(jObject.isNull("date"))
                                date = "NA";
                            else{
                                date = jObject.getString("date");
                            }
                            if(jObject.isNull("mdirector"))
                                mdirector = "NA";
                            else{
                                mdirector = jObject.getString("mdirector");
                            }
                            if(jObject.isNull("actors"))
                                actors = "NA";
                            else{
                                actors = jObject.getString("actors");
                            }
                            if(jObject.isNull("singers"))
                                singers = "NA";
                            else{
                                singers = jObject.getString("singers");
                            }
                            if(jObject.isNull("director"))
                                director = "NA";
                            else{
                                director = jObject.getString("director");
                            }
                            if(jObject.isNull("url"))
                                url = "NA";
                            else{
                                url = jObject.getString("url");
                            }

                            movieObject = new Movie(mid,schar,mname,date,mdirector,actors,singers,director,url,null,null);
                            List<Songs> songsObj = new ArrayList<Songs>();
                            JSONArray jSongArray = jObject.getJSONArray("msongs");
                            for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                            {
                                JSONObject jObjectSong = jSongArray.getJSONObject(j);
                                if(jObjectSong.isNull("sname"))
                                    sname = "NA";
                                else{
                                    sname = jObjectSong.getString("sname");
                                }
                                if(jObjectSong.isNull("sid"))
                                    continue;
                                else{
                                    sid = jObjectSong.getInt("sid");
                                }
                                if(jObjectSong.isNull("singers"))
                                    singersSong = "NA";
                                else{
                                    singersSong = jObjectSong.getString("singers");
                                }
                                if(jObjectSong.isNull("orglink"))
                                    orglink = "NA";
                                else{
                                    orglink = jObjectSong.getString("orglink");
                                }
                                if(jObjectSong.isNull("working"))
                                    continue;
                                else{
                                    working = jObjectSong.getString("working");
                                }
                                if(jObjectSong.isNull("workinglink"))
                                    workinglink = "NA";
                                else{
                                    workinglink = jObjectSong.getString("workinglink");
                                }
                                Songs sObj = new Songs(movieObject,sname,singersSong,orglink,sid,workinglink,working);
                                songsObj.add(sObj);


                            }
                            movieObject.setSONGS(songsObj);
                            if(songsObj != null && songsObj.size() > 0) {
                                mReturnBollywood.add(movieObject);
                            }

                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnBollywood = null;
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    mReturnBollywood = null;
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("MalformedURL ", "Error converting result " + e.toString());
                mReturnBollywood = null;
                return null;
            } catch (ProtocolException e) {
                mReturnBollywood = null;
                Log.e("ProtocolException ", "Error converting result " + e.toString());
                return null;
            } catch (IOException e) {
                mReturnBollywood = null;
                Log.e("ProtocolException ", "Error converting result " + e.toString());
                return null;

            }


            URL uPop;
            try {
                String popUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestindi";
                uPop = new URL(popUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) uPop.openConnection();
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
                            if(jObjectSong.isNull("sname"))
                                sname = "NA";
                            else{
                                sname = jObjectSong.getString("sname");
                            }
                            if(jObjectSong.isNull("sid"))
                                continue;
                            else{
                                sid = jObjectSong.getInt("sid");
                            }
                            if(jObjectSong.isNull("singers"))
                                singersSong = "NA";
                            else{
                                singersSong = jObjectSong.getString("singers");
                            }
                            if(jObjectSong.isNull("orglink"))
                                orglink = "NA";
                            else{
                                orglink = jObjectSong.getString("orglink");
                            }
                            if(jObjectSong.isNull("working"))
                                continue;
                            else{
                                working = jObjectSong.getString("working");
                            }
                            if(jObjectSong.isNull("workinglink"))
                                workinglink = "NA";
                            else{
                                workinglink = jObjectSong.getString("workinglink");
                            }
                            if(jObjectSong.isNull("urls"))
                                urls = "NA";
                            else{
                                urls = jObjectSong.getString("urls");
                            }
                            SongsIndiPop sObj = new SongsIndiPop(null,sname,singersSong,orglink,sid,workinglink,working,urls);
                            mReturnPop.add(sObj);

                        }

                    } catch (JSONException e) {
                        Log.e("JSONException","JSON Error.. "+e.toString());
                        mReturnPop = null;
                        return null;
                    }

                } catch (Exception e) {
                    Log.e("Exception","Exception Error.. "+e.toString());
                    mReturnPop = null;
                    return null;
                }

            } catch (MalformedURLException e) {
                mReturnPop = null;
                Log.e("MalformedURLException", "Exception Error.. " + e.toString());
                return null;
            } catch (ProtocolException e) {
                mReturnPop = null;
                Log.e("ProtocolException","ProtocolException Error.. "+e.toString());
                return null;
            } catch (IOException e) {
                mReturnPop = null;
                Log.e("IOException","Exception Error.. "+e.toString());
                return null;
            }

            URL uBollyLyrics;
            try {
                String latestbollywoodlyrics = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestmovielyrics";
                uBollyLyrics  = new URL(latestbollywoodlyrics);
                HttpURLConnection urlConnectionLyrics = (HttpURLConnection) uBollyLyrics.openConnection();
                urlConnectionLyrics.setConnectTimeout(3000);
                urlConnectionLyrics.setRequestMethod("GET");
                urlConnectionLyrics.connect();
                int code = urlConnectionLyrics.getResponseCode();
                inputStream = new InputStreamReader(urlConnectionLyrics.getInputStream());

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
                    String working,workinglink,orglink,singersSong,sname,lyrics;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        JSONArray jArray = jObj.getJSONArray("movies");
                        for(int i=0; jArray != null && jArray.length() > 0 && i < jArray.length(); i++) {

                            MovieLyrics movieObject;
                            JSONObject jObject = jArray.getJSONObject(i);
                            if(jObject.isNull("mid"))
                                continue;
                            else{
                                mid = jObject.getInt("mid");
                            }
                            if(jObject.isNull("schar"))
                                schar = "NA";
                            else{
                                schar = jObject.getString("schar");
                            }
                            if(jObject.isNull("mname"))
                                mname = "NA";
                            else{
                                mname = jObject.getString("mname");
                            }
                            if(jObject.isNull("url"))
                                url = "NA";
                            else{
                                url = jObject.getString("url");
                            }

                            movieObject = new MovieLyrics(mid,schar,mname,null,url);
                            List<SongsLyrics> songsObj = new ArrayList<SongsLyrics>();
                            JSONArray jSongArray = jObject.getJSONArray("msongs");
                            for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                            {
                                JSONObject jObjectSong = jSongArray.getJSONObject(j);
                                if(jObjectSong.isNull("sid"))
                                    continue;
                                else{
                                    sid = jObjectSong.getInt("sid");
                                }
                                if(jObjectSong.isNull("sname"))
                                    sname = "NA";
                                else{
                                    sname = jObjectSong.getString("sname");
                                }
                                if(jObjectSong.isNull("lyrics"))
                                    lyrics = "NA";
                                else{
                                    lyrics = jObjectSong.getString("lyrics");
                                }

                                SongsLyrics sObj = new SongsLyrics(movieObject,sname,sid,lyrics);
                                songsObj.add(sObj);


                            }
                            movieObject.setSONGS(songsObj);
                            if(songsObj != null && songsObj.size() > 0) {
                                mReturnBollywoodLyrics.add(movieObject);
                            }

                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnBollywoodLyrics = null;
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    mReturnBollywoodLyrics = null;
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("Malformed URL ", "Error converting result " + e.toString());
                mReturnBollywoodLyrics = null;
                return null;
            } catch (ProtocolException e) {
                Log.e("ProtocolExc", "Error converting result " + e.toString());
                mReturnBollywoodLyrics = null;
                return null;
            } catch (IOException e) {
                mReturnBollywoodLyrics = null;
                Log.e("IO EXCEPTION ", "Error converting result " + e.toString());
                return null;

            }
            return null;
        }
    }

    private class AsyncTaskAppOpenFirstTime extends AsyncTask<String, Void, Void> {
        
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(Void xyz) {
            super.onPostExecute(xyz);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            URL u;
            try {
                String regURL = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/device?did=";
                regURL = regURL+params[0];
                u  = new URL(regURL);
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

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        boolean success = jObj.getBoolean("success");

                        if(success){
                            SharedPreferences.Editor editor=settings.edit();
                            editor.putBoolean("firstRun",true);
                            editor.commit();
                        }

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("Malformed URL ", "Error converting result " + e.toString());
                return null;
            } catch (ProtocolException e) {
                Log.e("ProtocolExc", "Error converting result " + e.toString());
                return null;
            } catch (IOException e) {
                Log.e("IO EXCEPTION ", "Error converting result " + e.toString());
                return null;

            }
            return null;
        }
    }

    private class AsyncTaskShareUrl extends AsyncTask<String, Void, Void> {
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(Void xyz) {
            super.onPostExecute(xyz);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            URL u;
            try {
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/sharelink";

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

                    int mid = 0,sid=0;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        sharelink = jObj.getString("link");
                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("Malformed URL ", "Error converting result " + e.toString());
                return null;
            } catch (ProtocolException e) {
                Log.e("ProtocolExc", "Error converting result " + e.toString());
                return null;
            } catch (IOException e) {
                Log.e("IO EXCEPTION ", "Error converting result " + e.toString());
                return null;

            }
            return null;

        }
    }

    private class AsyncTaskUpdateLink extends AsyncTask<String, Void, Void> {
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(Void xyz) {
            super.onPostExecute(xyz);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {
            URL u;
            try {
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/updatelink";
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

                    int mid = 0,sid=0;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        updatelink = jObj.getString("link");
                        versionCode = jObj.getInt("version");



                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    return null;
                }

            } catch (MalformedURLException e) {
                Log.e("Malformed URL ", "Error converting result " + e.toString());
                return null;
            } catch (ProtocolException e) {
                Log.e("ProtocolExc", "Error converting result " + e.toString());
                return null;
            } catch (IOException e) {
                Log.e("IO EXCEPTION ", "Error converting result " + e.toString());
                return null;

            }
            return null;

        }
    }

}