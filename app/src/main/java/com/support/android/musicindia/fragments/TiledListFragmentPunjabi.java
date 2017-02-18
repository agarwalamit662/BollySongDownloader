
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.musicindia.Constants.Constants;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.R;
import com.support.android.musicindia.activities.MovieDetailActivity;
import com.support.android.musicindia.activities.SearchSongActivity;
import com.support.android.musicindia.adapters.RecyclerViewAdapter;
import com.support.android.musicindia.model.Movie;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsFilesData;

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

public class TiledListFragmentPunjabi extends Fragment {

    private ProgressDialog dialog;
    public static List<Movie> onRefresh;
    public static int posSpinnerOne = -1;
    public static int posSpinnerTwo = -1;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    List<String> listSongs = new ArrayList<String>();
    public static String latestpunjabi = Constants.BASE_URL+"rest/songs/latestpunjabi";
    final String[] alphabets = {"Latest","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static String deloitteURL = "usmumamitagarw1:8080";
    public static String emulatorURL = "10.0.2.2:8080";
    public static String driveURL = "https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/";
    public ConnectionDetector conDet;
    final String[] str1={"Latest Punjabi Songs","A PUNJABI SONGS","B PUNJABI SONGS","C PUNJABI SONGS","D PUNJABI SONGS","E PUNJABI SONGS","F PUNJABI SONGS","G PUNJABI SONGS","H PUNJABI SONGS","I PUNJABI SONGS","J PUNJABI SONGS"
            ,"K PUNJABI SONGS","L PUNJABI SONGS","M PUNJABI SONGS","N PUNJABI SONGS","O PUNJABI SONGS","P PUNJABI SONGS","Q PUNJABI SONGS","R PUNJABI SONGS","S PUNJABI SONGS","T PUNJABI SONGS","U PUNJABI SONGS","V PUNJABI SONGS","W PUNJABI SONGS","X PUNJABI SONGS","Y PUNJABI SONGS","Z PUNJABI SONGS"};
    final String[] str2={"2011-2015","2001-2010","1991-2000","1981-1990","1971-1980","1961-1970","1950-1960"};

    public Spinner sp1;
    public Spinner sp2;
    public ArrayAdapter<String> adp1;
    public ArrayAdapter<String> adp2;
    private RecyclerView rView;
    private GridLayoutManager lLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        conDet = new ConnectionDetector(getActivity());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.getActivity()));
        View view =  inflater.inflate(R.layout.tiled_list, container, false);
        rView = (RecyclerView)view.findViewById(R.id.recycler_view_tiled);

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
        sp1 = (Spinner) view.findViewById(R.id.selectChar);
        sp2= (Spinner) view.findViewById(R.id.selectYear);
        sp2.setVisibility(View.GONE);
       adp1 =
                new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_layout, str1)
                {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        ((TextView) v).setTextSize(15);
                        ((TextView) v).setTextColor(
                                getResources().getColorStateList(R.color.white));


                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView, parent);

                        ((TextView) v).setTextSize(20);
                        ((TextView) v).setTextColor(
                                getResources().getColorStateList(R.color.black)
                        );

                        return v;
                    }
                };
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp1.setAdapter(adp1);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(TiledListFragmentPunjabi.onRefresh != null ){
            ArrayList<Movie> arrList = new ArrayList<Movie>(TiledListFragmentPunjabi.onRefresh);
            savedInstanceState.putSerializable("punjabi", arrList);
        }

        savedInstanceState.putInt("spinnerOnePunjabi", TiledListFragmentPunjabi.posSpinnerOne);
        savedInstanceState.putInt("spinnerTwoPunjabi", TiledListFragmentPunjabi.posSpinnerTwo);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null){
            commonMethod();
        }
        else if (savedInstanceState != null) {

            // Restore last state for checked position.
            if(savedInstanceState.getSerializable("punjabi") != null){
                TiledListFragmentPunjabi.posSpinnerOne = savedInstanceState.getInt("spinnerOnePunjabi");
                TiledListFragmentPunjabi.posSpinnerTwo = savedInstanceState.getInt("spinnerTwoPunjabi");
                TiledListFragmentPunjabi.onRefresh =  (List<Movie>) savedInstanceState.getSerializable("punjabi");

                String name = "punjabi" + String.valueOf(posSpinnerOne);
                TiledListFragmentPunjabi.onRefresh =  (List<Movie>)savedInstanceState.getSerializable("punjabi");


                SongsFilesData.myMap.put(name,onRefresh);
                rView.setHasFixedSize(true);
                rView.setLayoutManager(lLayout);

                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
                rView.setAdapter(rcAdapter);
                commonMethod();
            }
            else{

                rView.setHasFixedSize(true);
                rView.setLayoutManager(lLayout);
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    }
                });
                rView.setAdapter(rcAdapter);

                commonMethod();


            }

        }

    }


    public void commonMethod(){

        if(posSpinnerOne != -1)
            sp1.setSelection(posSpinnerOne,false);
        if(posSpinnerOne == -1)
            sp1.setSelection(0,false);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(sp1.getSelectedItemPosition() == posSpinnerOne)
                {
                    posSpinnerOne = sp1.getSelectedItemPosition();


                    String name = "punjabi"+String.valueOf(posSpinnerOne);
                    if (conDet.isConnectingToInternet()){

                        if(SongsFilesData.myMap.get(name) == null) {

                            String temp = Constants.BASE_URL+"rest/songs/punjabistartchar?schar="+alphabets[posSpinnerOne];
                            (new AsyncListViewLoaderTiledPunjabi()).execute(temp,name);
                        }
                        else {
                            onRefresh = SongsFilesData.myMap.get(name);
                            rView.setHasFixedSize(true);
                            rView.setLayoutManager(lLayout);
                            if(onRefresh != null){
                                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Movie item) {

                                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                });
                                rView.setAdapter(rcAdapter);
                            }

                        }
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMap.put(name, null);

                        onRefresh = null;
                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Movie item) {

                                Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                        }
                        });
                        rView.setAdapter(rcAdapter);

                    }

                }
                else if(sp1.getSelectedItemPosition() == 0)
                {
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = -1;
                    if (conDet.isConnectingToInternet()) {
                        if(SongsFilesData.myMap.get("punjabi0") == null) {
                            (new AsyncListViewLoaderTiledPunjabi()).execute(latestpunjabi,"punjabi0");

                        }else {
                            onRefresh = SongsFilesData.myMap.get("punjabi0");
                            rView.setHasFixedSize(true);
                            rView.setLayoutManager(lLayout);
                            if(onRefresh != null){
                                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Movie item) {

                                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                });
                                rView.setAdapter(rcAdapter);
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Check your Internet connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMap.put("punjabi0",null);
                        onRefresh = null;
                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Movie item) {

                                Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }
                }
                else if(sp1.getSelectedItemPosition() != 0)
                {

                    posSpinnerOne = sp1.getSelectedItemPosition();
                    String name = "punjabi"+String.valueOf(posSpinnerOne);
                    if (conDet.isConnectingToInternet()){
                        if(SongsFilesData.myMap.get(name) == null) {
                            String temp = Constants.BASE_URL+"rest/songs/punjabistartchar?schar="+alphabets[posSpinnerOne];
                            (new AsyncListViewLoaderTiledPunjabi()).execute(temp,name);

                        }
                        else {
                            onRefresh = SongsFilesData.myMap.get(name);
                            rView.setHasFixedSize(true);
                            rView.setLayoutManager(lLayout);
                            if(onRefresh != null){
                                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Movie item) {

                                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                });
                                rView.setAdapter(rcAdapter);
                            }

                        }
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMap.put(name,null);
                        onRefresh = null;
                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Movie item) {

                                Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        if(onRefresh == null ) {
            if (conDet.isConnectingToInternet()){
                (new AsyncListViewLoaderTiledPunjabi()).execute(latestpunjabi,"punjabi0");
                onRefresh = SongsFilesData.myMap.get("punjabi0");
            }
            else{
                Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                SongsFilesData.myMap.put("punjabi0", null);
                onRefresh = null;
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
                rView.setAdapter(rcAdapter);
            }
        }
        else
        {
            if (conDet.isConnectingToInternet()){
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    }
                });
                rView.setAdapter(rcAdapter);


            }
            else{
                Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                SongsFilesData.myMap.put("punjabi0", null);
                onRefresh = null;
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    }
                });
                rView.setAdapter(rcAdapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchSongActivity.class);
                i.putExtra("typesearch", "punjabi");
                startActivity(i);
                searchView.setIconified(true);

            }
        });


    }

    private class AsyncListViewLoaderTiledPunjabi extends AsyncTask<String, Void, List<Movie>> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(List<Movie> result) {
            super.onPostExecute(result);

            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();

                if(result == null){
                    Toast.makeText(getActivity(),"Something went wrong! Check your network connection or Try Later",Toast.LENGTH_SHORT).show();
                }
                else if (result != null && result.size() > 0 & getActivity() != null) {

                    RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), result, new RecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Movie item) {

                            Intent i = new Intent(getContext(), MovieDetailActivity.class);
                            i.putExtra("MovieObject", item); i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            //     Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    rView.setAdapter(rcAdapter);
                    onRefresh = result;
                    SongsFilesData.myMap.put(listName, result);

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
        protected List<Movie> doInBackground(String... params) {

            List<Movie> mReturn = new ArrayList<Movie>();


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
