
package com.support.android.musicindia;

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
    public static String latestpunjabi = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestmovielyrics";
    final String[] alphabets = {"Latest","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static String deloitteURL = "usmumamitagarw1:8080";
    public static String emulatorURL = "10.0.2.2:8080";
    public static String driveURL = "https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/";

    final String[] str1={"Latest Punjabi Songs","A PUNJABI SONGS","B PUNJABI SONGS","C PUNJABI SONGS","D PUNJABI SONGS","E PUNJABI SONGS","F PUNJABI SONGS","G PUNJABI SONGS","H PUNJABI SONGS","I PUNJABI SONGS","J PUNJABI SONGS"
            ,"K PUNJABI SONGS","L PUNJABI SONGS","M PUNJABI SONGS","N PUNJABI SONGS","O PUNJABI SONGS","P PUNJABI SONGS","Q PUNJABI SONGS","R PUNJABI SONGS","S PUNJABI SONGS","T PUNJABI SONGS","U PUNJABI SONGS","V PUNJABI SONGS","W PUNJABI SONGS","X PUNJABI SONGS","Y PUNJABI SONGS","Z PUNJABI SONGS"};
    final String[] str2={"2011-2015","2001-2010","1991-2000","1981-1990","1971-1980","1961-1970","1950-1960"};

    private RecyclerView rView;
    private GridLayoutManager lLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ConnectionDetector conDet = new ConnectionDetector(getActivity());
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
        if(onRefresh == null ) {


                if (conDet.isConnectingToInternet()){


                    //String temp = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/punjabistartchar?schar="+alphabets[posSpinnerOne];
                    (new AsyncListViewLoaderTiled()).execute(latestpunjabi,"lyrics00");
            //(new AsyncListViewLoaderTiled()).execute(driveURL+"latestPunjabiSongs.txt","punjabi0");
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
                            i.putExtra("MovieObjectLyrics", item);
                            startActivity(i);

                            //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
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
                        i.putExtra("MovieObjectLyrics", item);
                        startActivity(i);

                        //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
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
                        i.putExtra("MovieObjectLyrics", item);
                        startActivity(i);

                        //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                    }
                });
                rView.setAdapter(rcAdapter);
            }

            /*Log.e("OnRefresh Not Null","On refresh Not Null");
            Log.e("OnRefresh Not Null","On refresh Not Null");
            Log.e("OnRefresh Not Null","On refresh Not Null");*/

        }

               // ("http://usmumamitagarw1:8080/useraccount/rest/songs/latest");
        //(new AsyncListViewLoaderTiled()).execute("http://10.0.2.2:8080/useraccount/rest/songs/latest");

        return view;
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
                Log.e("IN THIS METHOD", "in this method");
                Log.e("IN THIS METHOD", "in this method");
                Intent i = new Intent(getActivity(), SearchSongLyricsActivity.class);
                //i.putExtra("typesearch", "bollywood");
                startActivity(i);
                searchView.setIconified(true);
                //searchView.clearFocus();
                /*item.collapseActionView();*/
            }
        });


        // item.setVisible(false);
        //searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
    }

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, List<MovieLyrics>> {
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
                            i.putExtra("MovieObjectLyrics", item);
                            startActivity(i);

                            //     Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    rView.setAdapter(rcAdapter);
                    onRefresh = result;
                    SongsFilesData.myMapLyrics.put(listName, result);

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
        protected List<MovieLyrics> doInBackground(String... params) {

            List<MovieLyrics> mReturn = new ArrayList<MovieLyrics>();


                    listName = params[1];
            /*Log.e("ListName is : ",listName);
            Log.e("ListName is : ",listName);*/

                    URL u;
                    try {
                /*Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);*/
                        //Log.e("Params is : ",params[0]);

                        u = new URL(params[0]);
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
                                    mReturn.add(movieObject);


                                } // End Loop

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
                        Log.e("In IO Exception ","In IO Exception");
                        Log.e("In IO Exception ","In IO Exception");
                        e.printStackTrace();
                        return null;

                    }


            return mReturn;
        }
    }

}
