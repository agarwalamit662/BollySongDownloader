
package com.support.android.musicindia;


import android.content.Context;
import android.content.Intent;
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

import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.MovieLyrics;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsFilesData;
import com.support.android.musicindia.model.SongsIndiPop;
import com.support.android.musicindia.model.SongsLyrics;

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
    private static long SLEEP_TIME = 2;    // Sleep for some time
    /*public static boolean splashLoadedTiny = false;*/
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
            (new AsyncListViewLoaderTiledFirst()).execute(deviceId, "punjabi0");
        }

        if(conDet.isConnectingToInternet()){
            (new AsyncListViewLoaderTiledShare()).execute("", "");
            (new AsyncListViewLoaderTiledUpdate()).execute("", "");
        }



        Log.e("SplashLoaded Pehle: ", String.valueOf(MusicPlayerApplication.splashLoadedTiny));
        Log.e("SplashLoaded: Pehle ", String.valueOf(MusicPlayerApplication.splashLoadedTiny));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String splashflag = extras.getString("splashflag");
            if(splashflag != null && splashflag.equals("false"))
                MusicPlayerApplication.splashLoadedTiny = false;
            Log.e("SplashLoaded: ",String.valueOf(MusicPlayerApplication.splashLoadedTiny));
            Log.e("SplashLoaded: ",String.valueOf(MusicPlayerApplication.splashLoadedTiny));
        }

        // Start timer and launch main activity
        pgBar = (ProgressBar) findViewById(R.id.splashProgressBar);
        startAsPlayer = (Button) findViewById(R.id.startAsPlayer);
        tryAgain = (Button) findViewById(R.id.tryAgain);
        loadingDataText = (TextView) findViewById(R.id.loadingDataSplash);
        if (!MusicPlayerApplication.splashLoadedTiny) {

             Log.e("IN IF","IN IF");
             Log.e("IN IF","IN IF");
            /*(new AsyncListViewLoaderTiled()).execute(driveURL + "latestPunjabiSongs.txt", "punjabi0");*/

        }
        else {
            Log.e("IN ELSE","In ELSE");
            Log.e("IN ELSE","In ELSE");
            Log.e("IN ELSE","In ELSE");
            MusicPlayerApplication.splashLoadedTiny = true;
            Intent goToMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }
        if(startAsPlayer != null){
        startAsPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.splashLoadedTiny = true;
                openAsPlayer = true;
                indexOfDownloads = 1;
                MusicPlayerApplication.splashLoadedTiny = true;
                loadingDataText.setVisibility(View.VISIBLE);
                Intent goToMainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(goToMainActivity);
                finish();

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
                    MainActivity.splashLoadedTiny = false;
                    (new AsyncListViewLoaderTiled()).execute(driveURL + "latestPunjabiSongs.txt", "punjabi0");
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, Void> {
        //private final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                MainActivity.splashLoadedTiny = false;
                //splashLoadedTiny = true;
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();

            }
            else
            {
                Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                openAsPlayer = false;
                MainActivity.splashLoadedTiny = true;
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
            /*Log.e("ListName is : ",listName);
            Log.e("ListName is : ",listName);*/

            URL u;
            try {
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);

                //u = new URL(params[0]);
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestpunjabi";
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
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);

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

                            mid = jObject.getInt("mid");
                            schar = jObject.getString("char");
                            mname = jObject.getString("mname");
                            date = jObject.getString("date");
                            mdirector = jObject.getString("mdirector");
                            actors = jObject.getString("actors");
                            singers = jObject.getString("singers");
                            director = jObject.getString("director");
                            url = jObject.getString("url");

                            //int mOVIENUMBER, String mOVIESTARTCHAR, String mOVIENAME, String rELEASE_DATE , String mUSIC_DIRECTOR, String aCTORS, String sINGERS, String dIRECTOR, String uRLS,List<Songs> SONGS
                            movieObject = new Movie(mid,schar,mname,date,mdirector,actors,singers,director,url,null,null);
                            List<Songs> songsObj = new ArrayList<Songs>();
                            JSONArray jSongArray = jObject.getJSONArray("msongs");
                            for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                            {
                                JSONObject jObjectSong = jSongArray.getJSONObject(j);
                                sname = jObjectSong.getString("sname");
                                sid = jObjectSong.getInt("sid");
                                singersSong = jObjectSong.getString("singers");
                                orglink = jObjectSong.getString("orglink");
                                working = jObjectSong.getString("working");
                                workinglink= jObjectSong.getString("workinglink");
                                //Movie movie, String sONGNAME, String sINGERS, String sONGLINK_128KBPS, int sONG_ID,String SONGLINK_128KBPS_CONV,String WORKING_LINK
                                Songs sObj = new Songs(movieObject,sname,singersSong,orglink,sid,workinglink,working);
                                songsObj.add(sObj);


                            }
                            movieObject.setSONGS(songsObj);
                            mReturnPunjabi.add(movieObject);


                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnPunjabi = null;
                        //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    mReturnPunjabi = null;
                    //Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                mReturnPunjabi = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                return null;
            } catch (ProtocolException e) {
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                mReturnPunjabi = null;
                return null;
            } catch (IOException e) {
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();

                mReturnPunjabi = null;
                e.printStackTrace();
                return null;

            }


            URL uBolly;
            try {
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);
                String latestbollywood = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestbollywood";
                //uBolly  = new URL(driveURL+"latestSongs.txt");
                uBolly  = new URL(latestbollywood);
                HttpURLConnection urlConnection = (HttpURLConnection) uBolly.openConnection();
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
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);

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

                            mid = jObject.getInt("mid");
                            schar = jObject.getString("char");
                            mname = jObject.getString("mname");
                            date = jObject.getString("date");
                            mdirector = jObject.getString("mdirector");
                            actors = jObject.getString("actors");
                            singers = jObject.getString("singers");
                            director = jObject.getString("director");
                            url = jObject.getString("url");

                            //int mOVIENUMBER, String mOVIESTARTCHAR, String mOVIENAME, String rELEASE_DATE , String mUSIC_DIRECTOR, String aCTORS, String sINGERS, String dIRECTOR, String uRLS,List<Songs> SONGS
                            movieObject = new Movie(mid,schar,mname,date,mdirector,actors,singers,director,url,null,null);
                            List<Songs> songsObj = new ArrayList<Songs>();
                            JSONArray jSongArray = jObject.getJSONArray("msongs");
                            for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                            {
                                JSONObject jObjectSong = jSongArray.getJSONObject(j);
                                sname = jObjectSong.getString("sname");
                                sid = jObjectSong.getInt("sid");
                                singersSong = jObjectSong.getString("singers");
                                orglink = jObjectSong.getString("orglink");
                                working = jObjectSong.getString("working");
                                workinglink= jObjectSong.getString("workinglink");
                                //Movie movie, String sONGNAME, String sINGERS, String sONGLINK_128KBPS, int sONG_ID,String SONGLINK_128KBPS_CONV,String WORKING_LINK
                                Songs sObj = new Songs(movieObject,sname,singersSong,orglink,sid,workinglink,working);
                                songsObj.add(sObj);


                            }
                            movieObject.setSONGS(songsObj);
                            mReturnBollywood.add(movieObject);


                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnBollywood = null;
                        //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    //Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    mReturnBollywood = null;
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                mReturnBollywood = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                return null;
            } catch (ProtocolException e) {
                mReturnBollywood = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                mReturnBollywood = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
                return null;

            }


            URL uPop;
            try {
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);
                String popUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestindi";
                uPop = new URL(popUrl);
                //uPop = new URL(driveURL+"latestIndiSongs.txt");
                HttpURLConnection urlConnection = (HttpURLConnection) uPop.openConnection();
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
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);

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
                            //Movie movie, String sONGNAME, String sINGERS, String sONGLINK_128KBPS, int sONG_ID,String SONGLINK_128KBPS_CONV,String WORKING_LINK
                            SongsIndiPop sObj = new SongsIndiPop(null,sname,singersSong,orglink,sid,workinglink,working,urls);
                            //songsObj.add(sObj);
                            mReturnPop.add(sObj);

                        }
                        //movieObject.setSONGS(songsObj);



                        // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnPop = null;
                        //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return null;
                    }


                } catch (Exception e) {
                    mReturnPop = null;
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    //Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return null;
                }

            } catch (MalformedURLException e) {
                mReturnPop = null;
                e.printStackTrace();
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                return null;
            } catch (ProtocolException e) {
                mReturnPop = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                mReturnPop = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;

            }

            URL uBollyLyrics;
            try {
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);
                String latestbollywoodlyrics = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestmovielyrics";
                //uBolly  = new URL(driveURL+"latestSongs.txt");
                uBollyLyrics  = new URL(latestbollywoodlyrics);
                HttpURLConnection urlConnectionLyrics = (HttpURLConnection) uBollyLyrics.openConnection();
                urlConnectionLyrics.setConnectTimeout(3000);
                urlConnectionLyrics.setRequestMethod("GET");

                // urlConnection.setDoOutput(true);
                //  urlConnection.setChunkedStreamingMode(100);

                urlConnectionLyrics.connect();
                int code = urlConnectionLyrics.getResponseCode();
                // Log.e("Response Code is : ",String.valueOf(code));
                // Log.e("Response Code is : ",String.valueOf(code));
                // Log.e("Response Code is : ",String.valueOf(code));
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
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);
                    //  Log.e("Result is : ",result);

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

                            mid = jObject.getInt("mid");
                            schar = jObject.getString("char");
                            mname = jObject.getString("mname");
                            url = jObject.getString("url");

                            //int mOVIENUMBER, String mOVIESTARTCHAR, String mOVIENAME, String rELEASE_DATE , String mUSIC_DIRECTOR, String aCTORS, String sINGERS, String dIRECTOR, String uRLS,List<Songs> SONGS
                            movieObject = new MovieLyrics(mid,schar,mname,null,url);
                            List<SongsLyrics> songsObj = new ArrayList<SongsLyrics>();
                            JSONArray jSongArray = jObject.getJSONArray("msongs");
                            for(int j = 0; jSongArray != null && jSongArray.length() > 0 && j < jSongArray.length(); j++)
                            {
                                JSONObject jObjectSong = jSongArray.getJSONObject(j);
                                sname = jObjectSong.getString("sname");
                                sid = jObjectSong.getInt("sid");
                                lyrics = jObjectSong.getString("lyrics");
                                //Movie movie, String sONGNAME, String sINGERS, String sONGLINK_128KBPS, int sONG_ID,String SONGLINK_128KBPS_CONV,String WORKING_LINK
                                SongsLyrics sObj = new SongsLyrics(movieObject,sname,sid,lyrics);
                                songsObj.add(sObj);


                            }
                            movieObject.setSONGS(songsObj);
                            mReturnBollywoodLyrics.add(movieObject);


                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                        mReturnBollywoodLyrics = null;
                        //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        return null;
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                    //Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    mReturnBollywoodLyrics = null;
                    return null;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                mReturnBollywoodLyrics = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                return null;
            } catch (ProtocolException e) {
                mReturnBollywoodLyrics = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                mReturnBollywoodLyrics = null;
                //Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();

                e.printStackTrace();
                return null;

            }


            return null;

        }
    }



    private class AsyncListViewLoaderTiledFirst extends AsyncTask<String, Void, Void> {
        //private final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);

                //u = new URL(params[0]);
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/device?did=";
                punjabiUrl = punjabiUrl+params[0];
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





            return null;

        }
    }

    private class AsyncListViewLoaderTiledShare extends AsyncTask<String, Void, Void> {
        //private final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);

                //u = new URL(params[0]);
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/sharelink";

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

                    int mid = 0,sid=0;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        sharelink = jObj.getString("link");



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





            return null;

        }
    }

    private class AsyncListViewLoaderTiledUpdate extends AsyncTask<String, Void, Void> {
        //private final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                //Log.e("Params is : ",params[0]);

                //u = new URL(params[0]);
                String punjabiUrl = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/updatelink";
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

                    int mid = 0,sid=0;

                    Songs songsObject;
                    try {

                        JSONObject jObj = new JSONObject(result);
                        updatelink = jObj.getString("link");
                        versionCode = jObj.getInt("version");



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





            return null;

        }
    }


}