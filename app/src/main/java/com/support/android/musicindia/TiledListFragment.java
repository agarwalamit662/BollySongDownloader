
package com.support.android.musicindia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
import android.support.v7.widget.GridLayoutManager;
import android.widget.Toast;

public class TiledListFragment extends Fragment {

    private ProgressDialog dialog;
    public static List<Movie> onRefresh;
    private static int posSpinnerOne = -1;
    private static int posSpinnerTwo = -1;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    List<String> listSongs = new ArrayList<String>();

    public static String deloitteURL = "usmumamitagarw1:8080";
    public static String emulatorURL = "10.0.2.2:8080";
    public static String driveURL = "https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/";
    public static String latestbollywood = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestbollywood";
    final String[] alphabets = {"Latest","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    final String[] str1={"Latest Movies Songs","A MOVIE SONGS","B MOVIE SONGS","C MOVIE SONGS","D MOVIE SONGS","E MOVIE SONGS","F MOVIE SONGS","G MOVIE SONGS","H MOVIE SONGS","I MOVIE SONGS","J MOVIE SONGS"
            ,"K MOVIE SONGS","L MOVIE SONGS","M MOVIE SONGS","N MOVIE SONGS","O MOVIE SONGS","P MOVIE SONGS","Q MOVIE SONGS","R MOVIE SONGS","S MOVIE SONGS","T MOVIE SONGS","U MOVIE SONGS","V MOVIE SONGS","W MOVIE SONGS","X MOVIE SONGS","Y MOVIE SONGS","Z MOVIE SONGS"};

    final String[] str2={"2011-2015","2001-2010","1991-2000","1981-1990","1971-1980","1961-1970","1950-1960"};
    final String[] years={"2011","2001","1991","1981","1971","1961","1950"};

    public Spinner sp1;
    public Spinner sp2;
    public ArrayAdapter<String> adp1;
    public ArrayAdapter<String> adp2;
    private RecyclerView rView;
    private GridLayoutManager lLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ConnectionDetector conDet = new ConnectionDetector(getActivity());
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



        adp2 =
                new ArrayAdapter<String>(getActivity(), R.layout.my_spinner_layout, str2)
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

        adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adp2);
        //sp2.setVisibility(View.GONE);
        //sp2.setSelection(str2.length-1);
        if(posSpinnerOne != -1)
            sp1.setSelection(posSpinnerOne);
        if(posSpinnerOne == -1)
            sp1.setSelection(0);
        if(posSpinnerTwo != -1)
            sp2.setSelection(posSpinnerTwo);
        if(posSpinnerTwo == -1)
            sp2.setSelection(0);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(sp1.getSelectedItemPosition() == posSpinnerOne && sp2.getSelectedItemPosition() == posSpinnerTwo)
                {
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = sp2.getSelectedItemPosition();

                    String name = "songs"+String.valueOf(posSpinnerOne)+String.valueOf(posSpinnerTwo);
                    /*Log.e("Name is : ",name);
                    Log.e("Name is : ",name);*/
                    if (conDet.isConnectingToInternet()){

                        if(SongsFilesData.myMap.get(name) == null) {
                        //(new AsyncListViewLoaderTiled()).execute("http://"+deloitteURL+"/useraccount/rest/songs/charwise?schar=" + str1[posSpinnerOne].charAt(0) + "&year=" + str2[posSpinnerTwo].substring(0, 4), name);


                             String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                            (new AsyncListViewLoaderTiled()).execute(tempurl,name);
                            //(new AsyncListViewLoaderTiled()).execute(driveURL+name+".txt",name);


                        /*Log.e("In if : ", "In if null");
                        Log.e("In if : ","In if null");*/
                        }
                        else {
                        /*Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));
                        Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));*/

                        onRefresh = SongsFilesData.myMap.get(name);
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);
                        if(onRefresh != null){
                            RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Movie item) {

                                    Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                    i.putExtra("MovieObject", item);
                                    i.putExtra("MovieFragment","MovieFragment");
                                    startActivity(i);
                                    /*getActivity().finish();*/

                                    //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
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
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                                //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);

                    }

                }
                else if(sp1.getSelectedItemPosition() == 0)
                {
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = -1;
                    sp2.setVisibility(View.GONE);
                    if (conDet.isConnectingToInternet()) {
                    if(SongsFilesData.myMap.get("songs00") == null) {



                        //String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                        (new AsyncListViewLoaderTiled()).execute(latestbollywood,"songs00");

                                //(new AsyncListViewLoaderTiled()).execute("http://" + deloitteURL + "/useraccount/rest/songs/latest", "songs00");
                                //(new AsyncListViewLoaderTiled()).execute(driveURL + "latestSongs.txt", "songs00");


                          //

                    }else {
                        onRefresh = SongsFilesData.myMap.get("songs00");
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);
                        if(onRefresh != null){
                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Movie item) {

                                Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                              //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);
                        }
                    }
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Check your Internet connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMap.put("songs00",null);
                        onRefresh = null;
                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Movie item) {

                                Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                                //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }

                            //"https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/latestSongs.txt");

                }
                else if(sp1.getSelectedItemPosition() != 0)
                {
                    sp2.setVisibility(View.VISIBLE);
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = sp2.getSelectedItemPosition();
                    String name = "songs"+String.valueOf(posSpinnerOne)+String.valueOf(posSpinnerTwo);
                    /*Log.e("Name is : ",name);
                    Log.e("Name is : ",name);*/
                    if (conDet.isConnectingToInternet()){
                    if(SongsFilesData.myMap.get(name) == null) {
                        //(new AsyncListViewLoaderTiled()).execute("http://"+deloitteURL+"/useraccount/rest/songs/charwise?schar=" + str1[posSpinnerOne].charAt(0) + "&year=" + str2[posSpinnerTwo].substring(0, 4), name);


                        String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                        (new AsyncListViewLoaderTiled()).execute(tempurl,name);

                                //(new AsyncListViewLoaderTiled()).execute(driveURL+name+".txt",name);


                        /*Log.e("In if : ", "In if null");
                        Log.e("In if : ","In if null");*/
                    }
                    else {
                        /*Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));
                        Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));*/

                        onRefresh = SongsFilesData.myMap.get(name);
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);
                        if(onRefresh != null){
                            RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Movie item) {

                                    Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                    i.putExtra("MovieObject", item);
                                    i.putExtra("MovieFragment","MovieFragment");
                                    startActivity(i);
                                    /*getActivity().finish();*/

                                  //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
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
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                                //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }

                    /*Log.e("Size of onRefersh is : ",String.valueOf(onRefresh.size()));
                    Log.e("Size of onRefersh is : ",String.valueOf(onRefresh.size()));*/
                }

                //

            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(sp1.getSelectedItemPosition() == posSpinnerOne && sp2.getSelectedItemPosition() == posSpinnerTwo)
                {
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = sp2.getSelectedItemPosition();

                    String name = "songs"+String.valueOf(posSpinnerOne)+String.valueOf(posSpinnerTwo);
                    /*Log.e("Name is : ",name);
                    Log.e("Name is : ",name);*/
                    if (conDet.isConnectingToInternet()){
                    if(SongsFilesData.myMap.get(name) == null) {
                        //(new AsyncListViewLoaderTiled()).execute("http://"+deloitteURL+"/useraccount/rest/songs/charwise?schar=" + str1[posSpinnerOne].charAt(0) + "&year=" + str2[posSpinnerTwo].substring(0, 4), name);


                        String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                        (new AsyncListViewLoaderTiled()).execute(tempurl,name);

                            //(new AsyncListViewLoaderTiled()).execute(driveURL+name+".txt",name);


                        /*Log.e("In if : ", "In if null");
                        Log.e("In if : ","In if null");*/
                    }
                    else {
                        /*Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));
                        Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));*/

                        onRefresh = SongsFilesData.myMap.get(name);
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);
                        if(onRefresh != null){
                            RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Movie item) {

                                    Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                    i.putExtra("MovieObject", item);
                                    i.putExtra("MovieFragment","MovieFragment");
                                    startActivity(i);
                                    /*getActivity().finish();*/

                                    //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
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
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                                //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }

                }
                else if(sp1.getSelectedItemPosition() == 0)
                {
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = -1;
                    sp2.setVisibility(View.GONE);

                    if (conDet.isConnectingToInternet()){
                    if(SongsFilesData.myMap.get("songs00") == null) {


                        //String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                        (new AsyncListViewLoaderTiled()).execute(latestbollywood,"songs00");

                        //(new AsyncListViewLoaderTiled()).execute("http://" + deloitteURL + "/useraccount/rest/songs/latest", "songs00");
                        //(new AsyncListViewLoaderTiled()).execute(driveURL+"latestSongs.txt","songs00");


                    }
                    else{
                        onRefresh = SongsFilesData.myMap.get("songs00");
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);
                        if(onRefresh != null){
                            RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Movie item) {

                                    Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                    i.putExtra("MovieObject", item);
                                    i.putExtra("MovieFragment","MovieFragment");
                                    startActivity(i);
                                    /*getActivity().finish();*/

                                  //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            rView.setAdapter(rcAdapter);
                        }
                    }
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myMap.put("songs00",null);
                        onRefresh = null;
                        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Movie item) {

                                Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                                //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }


                }
                else
                {
                    sp2.setVisibility(View.VISIBLE);
                    posSpinnerOne = sp1.getSelectedItemPosition();
                    posSpinnerTwo = sp2.getSelectedItemPosition();

                    String name = "songs"+String.valueOf(posSpinnerOne)+String.valueOf(posSpinnerTwo);

                    /*Log.e("Name is : ",name);
                    Log.e("Name is : ",name);*/
                    if (conDet.isConnectingToInternet()){
                    if(SongsFilesData.myMap.get(name) == null) {

                        String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                        (new AsyncListViewLoaderTiled()).execute(tempurl,name);

                        //(new AsyncListViewLoaderTiled()).execute("http://"+deloitteURL+"/useraccount/rest/songs/charwise?schar=" + str1[posSpinnerOne].charAt(0) + "&year=" + str2[posSpinnerTwo].substring(0, 4), name);
                        //(new AsyncListViewLoaderTiled()).execute(driveURL+name+".txt",name);


                        /*Log.e("In if : ", "In if null");
                        Log.e("In if : ","In if null");*/
                    }
                    else {
                        /*Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));
                        Log.e("In else : ","In else not null "+String.valueOf(SongsFilesData.myMap.get(name).size()));*/

                        onRefresh = SongsFilesData.myMap.get(name);
                        rView.setHasFixedSize(true);
                        rView.setLayoutManager(lLayout);
                        if(onRefresh != null){
                            RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Movie item) {

                                    Intent i = new Intent(getContext(), MovieDetailActivity.class);
                                    i.putExtra("MovieObject", item);
                                    i.putExtra("MovieFragment","MovieFragment");
                                    startActivity(i);
                                    /*getActivity().finish();*/
                                    //Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
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
                                i.putExtra("MovieObject", item);
                                i.putExtra("MovieFragment","MovieFragment");
                                startActivity(i);

                                //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        rView.setAdapter(rcAdapter);
                    }

                    /*Log.e("Size of onRefersh is : ",String.valueOf(onRefresh.size()));
                    Log.e("Size of onRefersh is : ",String.valueOf(onRefresh.size()));*/

                }

            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(onRefresh == null){
            Log.e("onRefresh is null","null");
            Log.e("onRefresh is null","null");
            if(sp1.getSelectedItemPosition() == 0)
                onRefresh = SongsFilesData.myMap.get("songs00");
            else
                onRefresh = SongsFilesData.myMap.get("songs"+String.valueOf(sp1.getSelectedItemPosition())+String.valueOf(sp2.getSelectedItemPosition()));
        }
        else if(onRefresh != null){
            Log.e("OnRefresh Not null size",String.valueOf(onRefresh.size()));
            Log.e("OnRefresh Not null size",String.valueOf(onRefresh.size()));
        }


        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        if(onRefresh == null ) {
            /*Log.e("OnRefresh Null","On refresh Null");
            Log.e("OnRefresh Null", "On refresh Null");*/
            //(new AsyncListViewLoaderTiled()).execute("https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/latestSongs.txt");
            //(new AsyncListViewLoaderTiled()).execute("http://"+deloitteURL+"/useraccount/rest/songs/latest","songs00");


                if (conDet.isConnectingToInternet()){


                    //String tempurl =    "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/bollywoodcharwiseyear?schar="+alphabets[posSpinnerOne]+"&year="+years[posSpinnerTwo];
                    (new AsyncListViewLoaderTiled()).execute(latestbollywood,"songs00");
            //(new AsyncListViewLoaderTiled()).execute(driveURL+"latestSongs.txt","songs00");
                    onRefresh = SongsFilesData.myMap.get("songs00");
                }
                else{
                    Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                    SongsFilesData.myMap.put("songs00", null);
                    onRefresh = null;
                    RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Movie item) {

                            Intent i = new Intent(getContext(), MovieDetailActivity.class);
                            i.putExtra("MovieObject", item);
                            i.putExtra("MovieFragment","MovieFragment");
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

                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item);
                        i.putExtra("MovieFragment","MovieFragment");
                        startActivity(i);

                        //  Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                    }
                });
                rView.setAdapter(rcAdapter);


            }
            else{
                Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                SongsFilesData.myMap.put("songs00", null);
                onRefresh = null;
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(getActivity(), onRefresh, new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Movie item) {

                        Intent i = new Intent(getContext(), MovieDetailActivity.class);
                        i.putExtra("MovieObject", item);
                        i.putExtra("MovieFragment","MovieFragment");
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
                Intent i = new Intent(getActivity(), SearchSongActivity.class);
                i.putExtra("typesearch","bollywood");
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

        /*switch (item.getItemId()) {
            case R.id.action_search:
                Intent i = new Intent(getActivity(),SearchSongActivity.class);
              //  i.putExtra("splashflag",false);
                startActivity(i);


        }*/

        //return false;
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, List<Movie>> {
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
                            i.putExtra("MovieObject", item);
                            i.putExtra("MovieFragment","MovieFragment");
                            startActivity(i);

                            //     Toast.makeText(getContext(), "Item Clicked : " + item.getMOVIENAME(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    rView.setAdapter(rcAdapter);
                    onRefresh = result;
                    SongsFilesData.myMap.put(listName, result);

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
        protected List<Movie> doInBackground(String... params) {

            List<Movie> mReturn = new ArrayList<Movie>();


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
