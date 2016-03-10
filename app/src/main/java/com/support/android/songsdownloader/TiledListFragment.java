
package com.support.android.songsdownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.songsdownloader.model.Movie;
import com.support.android.songsdownloader.model.Songs;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.GridLayoutManager;
import android.widget.Toast;

public class TiledListFragment extends Fragment {
    private static ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    List<String> listSongs = new ArrayList<String>();
    private static RetryPolicy retryPolicy;
    private RecyclerView rView;
    private GridLayoutManager lLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.getActivity()));
        View view =  inflater.inflate(R.layout.tiled_list, container, false);
        rView = (RecyclerView)view.findViewById(R.id.recycler_view_tiled);

        int numberOfColumns = 4;
        lLayout = new GridLayoutManager(this.getActivity(),numberOfColumns);
        //lLayout = new WrappableGridLayoutManager(this.getActivity(), numberOfColumns);

        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
       // (new AsyncListViewLoader()).execute("http://usmumamitagarw1:8080/useraccount/rest/songs/latest");
        (new AsyncListViewLoader()).execute("http://10.0.2.2:8080/useraccount/rest/songs/latest");

        return view;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lLayout = new GridLayoutManager(this.getActivity(), 6);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            lLayout = new GridLayoutManager(this.getActivity(), 4);
        }
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncListViewLoader extends AsyncTask<String, Void, List<Movie>> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        InputStreamReader inputStream = null;
        String result = "";


        @Override
        protected void onPostExecute(List<Movie> result) {
            super.onPostExecute(result);

            if(result != null && result.size() > 0 ) {
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), result, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item);
                        startActivity(i);

                        Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                    }
                });
                rView.setAdapter(rcAdapter);
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            List<Movie> mReturn = new ArrayList<Movie>();
            URL u;
            try {
                Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);
                Log.e("Params is : ",params[0]);
                u = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                urlConnection.setConnectTimeout(1000);
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
                            mReturn.add(movieObject);


                        } // End Loop

                    } catch (JSONException e) {
                        Log.e("JSONException", "Error: " + e.toString());
                    }


                } catch (Exception e) {
                    Log.e("StringBuilding ", "Error converting result " + e.toString());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("In IO Exception ","In IO Exception");
                Log.e("In IO Exception ","In IO Exception");
                e.printStackTrace();
                return null;

            }

            return mReturn;
        }
    }

}
