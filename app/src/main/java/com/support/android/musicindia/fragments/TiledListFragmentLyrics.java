
package com.support.android.musicindia.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.musicindia.Constants.Constants;
import com.support.android.musicindia.activities.MovieDetailActivity;
import com.support.android.musicindia.adapters.RecyclerViewAdapter;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.R;
import com.support.android.musicindia.activities.MovieDetailActivityLyrics;
import com.support.android.musicindia.activities.SearchSongLyricsActivity;
import com.support.android.musicindia.adapters.RecyclerViewAdapterLyrics;
import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.MovieLyrics;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsFilesData;
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

public class TiledListFragmentLyrics extends Fragment {

    private ProgressDialog dialog;
    public static List<MovieLyrics> onRefresh;
    private static int posSpinnerOne = -1;
    private static int posSpinnerTwo = -1;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    List<String> listSongs = new ArrayList<String>();
    public static String latestpunjabi = Constants.BASE_URL+"rest/songs/latestmovielyrics";
    final String[] alphabets = {"Latest","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static String deloitteURL = "usmumamitagarw1:8080";
    public static String emulatorURL = "10.0.2.2:8080";
    public static String driveURL = "https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/";
    private ConnectionDetector conDet;

    private RecyclerView rView;
    private GridLayoutManager lLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        conDet = new ConnectionDetector(getActivity());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.getActivity()));
        View view =  inflater.inflate(R.layout.tiled_list_lyrics, container, false);
        rView = (RecyclerView)view.findViewById(R.id.recycler_view_tiled_lyrics);
        setHasOptionsMenu(true);
        int numberOfColumns = 3;
        if(getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0 ||
                getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_180 )
        {lLayout = new GridLayoutManager(this.getActivity(),3);}

        else if(getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 ||
                getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270 )
        {
            lLayout = new GridLayoutManager(this.getActivity(),5);
        }

        dialog = new ProgressDialog(this.getActivity());

        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);


        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(TiledListFragmentLyrics.onRefresh != null){
            ArrayList<MovieLyrics> arrListLyrics = new ArrayList<MovieLyrics>(TiledListFragmentLyrics.onRefresh);
            savedInstanceState.putSerializable("lyrics", arrListLyrics);
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null) {

            //commonMethod();
            if(onRefresh == null ) {


                if (conDet.isConnectingToInternet()){
                    (new AsyncListViewLoaderTiledLyrics()).execute(latestpunjabi,"lyrics00");
                    onRefresh = SongsFilesData.myMapLyrics.get("lyrics00");
                }
                else{
                    Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                    SongsFilesData.myMapLyrics.put("lyrics00", null);
                    onRefresh = null;
                    RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                        @Override
                        public void onItemClick(MovieLyrics item) {

                            Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                            i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
                    rView.setAdapter(rcAdapter);
                }

            }
            else
            {

                if (conDet.isConnectingToInternet()){

                    RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                        @Override
                        public void onItemClick(MovieLyrics item) {

                            Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                            i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
                    rView.setAdapter(rcAdapter);


                }
                else{
                    Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                    SongsFilesData.myMapLyrics.put("lyrics00", null);
                    onRefresh = null;
                    RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                        @Override
                        public void onItemClick(MovieLyrics item) {

                            Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                            i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
                    rView.setAdapter(rcAdapter);
                }

            }

        }
        else if (savedInstanceState != null) {
            if(savedInstanceState.getSerializable("lyrics") != null){
                TiledListFragmentLyrics.onRefresh =  (List<MovieLyrics>)savedInstanceState.getSerializable("lyrics");

                RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                    @Override
                    public void onItemClick(MovieLyrics item) {

                        Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                        i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
                rView.setAdapter(rcAdapter);


            }else{

                rView.setHasFixedSize(true);
                rView.setLayoutManager(lLayout);
                RecyclerViewAdapterLyrics rcAdapternew = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                    @Override
                    public void onItemClick(MovieLyrics item) {

                        Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                        i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
                rView.setAdapter(rcAdapternew);

                if(onRefresh == null ) {


                    if (conDet.isConnectingToInternet()){
                        (new AsyncListViewLoaderTiledLyrics()).execute(latestpunjabi,"lyrics00");
                        onRefresh = SongsFilesData.myMapLyrics.get("lyrics00");
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMapLyrics.put("lyrics00", null);
                        onRefresh = null;
                        RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                            @Override
                            public void onItemClick(MovieLyrics item) {

                                Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                                i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }

                }
                else
                {

                    if (conDet.isConnectingToInternet()){

                        RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                            @Override
                            public void onItemClick(MovieLyrics item) {

                                Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                                i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                        rView.setAdapter(rcAdapter);


                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMapLyrics.put("lyrics00", null);
                        onRefresh = null;
                        RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity(), onRefresh, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                            @Override
                            public void onItemClick(MovieLyrics item) {

                                Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                                i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }

                }

            }

        }



    }


            @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lLayout = new GridLayoutManager(this.getActivity(), 5);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            lLayout = new GridLayoutManager(this.getActivity(), 3);
        }
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchSongLyricsActivity.class);
                startActivity(i);
                searchView.setIconified(true);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private class AsyncListViewLoaderTiledLyrics extends AsyncTask<String, Void, List<MovieLyrics>> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(List<MovieLyrics> result) {
            super.onPostExecute(result);

            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();

                if(result == null){
                    Toast.makeText(getActivity(),"Something went wrong! Check your network connection or Try Later",Toast.LENGTH_SHORT).show();
                }
                else if (result != null && result.size() > 0 & getActivity() != null) {

                    RecyclerViewAdapterLyrics rcAdapter = new RecyclerViewAdapterLyrics(getActivity().getApplicationContext(), result, new RecyclerViewAdapterLyrics.OnItemClickListener() {
                        @Override
                        public void onItemClick(MovieLyrics item) {

                            Intent i = new Intent(getContext(), MovieDetailActivityLyrics.class);
                            i.putExtra("MovieObjectLyrics", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
                    rView.setAdapter(rcAdapter);
                    onRefresh = result;
                    SongsFilesData.myMapLyrics.put(listName, result);
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
        protected List<MovieLyrics> doInBackground(String... params) {

            List<MovieLyrics> mReturn = new ArrayList<MovieLyrics>();


                    listName = params[1];
                    URL u;
                    try {
                        u = new URL(params[0]);
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
                                        mReturn.add(movieObject);
                                    }

                                } // End Loop

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

            return mReturn;
        }
    }

}
