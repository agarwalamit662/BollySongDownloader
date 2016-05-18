package com.support.android.tinyplayer;

/**
 * Created by amitagarwal3 on 12/7/2015.
 */
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.support.android.tinyplayer.model.PlayableItem;
import com.support.android.tinyplayer.model.Songs;
import com.support.android.tinyplayer.model.SongsFilesData;
import com.support.android.tinyplayer.model.SongsIndiPop;

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

public class PopSongsFragment extends Fragment implements SearchView.OnQueryTextListener {


    private static BrowserSong searchSong;
    public static MusicServiceTinyPlayer musicService; // The application service*/
    private static final int POLLING_INTERVAL = 450; // Refresh time of the seekbar
    private boolean pollingThreadRunning; // true if thread is active, false otherwise
    private boolean startPollingThread = true;
    private static Intent serviceIntent;
    private boolean showRemainingTime = false;
    private boolean isLengthAvailable = false;
    private String songDurationString = "";
    private String intentFile;
    private TextView textViewArtist, textViewTitle, textViewTime;
    private CheckableImageButton imageButtonPlayPause;
    private ImageButton imageButtonPrevious, imageButtonNext, imageButtonShowSeekbar2;
    private SeekBar seekBar1, seekBar2;
    private ImageView imageViewSongImage;
    private BroadcastReceiver broadcastReceiver;
    private MusicPlayerApplication app;

    private ArrayList<String> MyFiles;
    private RecyclerView rv;
    static MainActivity ma;
    public static List<SongsIndiPop> onRefresh;
    public static String driveURL = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestindi";
            //"https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/";

    private int cancelId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ConnectionDetector conDet = new ConnectionDetector(getActivity());
        View view =  inflater.inflate(R.layout.fragment_tracks_details, container, false);
        app = (MusicPlayerApplication)this.getActivity().getApplication();
        rv = (RecyclerView) view.findViewById(R.id.rViewTracks);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        //rv.setAdapter(new SimpleStringRecyclerViewAdapter(this.getActivity(), BrowserSong.getSongsInDirectory("date"),PopSongsFragment.this));

        if(onRefresh == null ) {
            /*Log.e("OnRefresh Null","On refresh Null");
            Log.e("OnRefresh Null", "On refresh Null");*/
            //(new AsyncListViewLoaderTiled()).execute("https://7db034b7a5c1141914d840db300fd46be9227dd3.googledrive.com/host/0B_KqRgJQ-c2MM3pMb3kyRXNZeGs/latestSongs.txt");
            //(new AsyncListViewLoaderTiled()).execute("http://"+deloitteURL+"/useraccount/rest/songs/latest","songs00");


            if (conDet.isConnectingToInternet()){


                (new AsyncListViewLoaderTiled()).execute(driveURL,"indi00");
                //(new AsyncListViewLoaderTiled()).execute(driveURL+"latestIndiSongs.txt","indi00");
                onRefresh = SongsFilesData.myIndiMap.get("indi00");

            }
            else{
                Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                SongsFilesData.myIndiMap.put("indi00", null);
                onRefresh = null;
                rv.setAdapter(new SimpleStringRecyclerViewAdapter(this.getActivity(), null,PopSongsFragment.this));

            }

        }
        else
        {

            if (conDet.isConnectingToInternet()){

                rv.setAdapter(new SimpleStringRecyclerViewAdapter(this.getActivity(), onRefresh,PopSongsFragment.this));
            }
            else{
                Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                SongsFilesData.myIndiMap.put("indi00", null);
                onRefresh = null;
                rv.setAdapter(new SimpleStringRecyclerViewAdapter(this.getActivity(), null,PopSongsFragment.this));

            }


           // rView.setAdapter(rcAdapter);
        }

        setHasOptionsMenu(true);
        //initialize(view);

        //updateListView(true);
        //loadSongFromIntent();
        //updateListView(true);

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public boolean onQueryTextChange(String query) {

        //mov = findMoviesLast;
        List<SongsIndiPop> temp = onRefresh;
        ArrayList<SongsIndiPop> sample = (ArrayList<SongsIndiPop>) temp;
        final List<SongsIndiPop> filteredModelList = filter(sample, query);

        SimpleStringRecyclerViewAdapter adap = new SimpleStringRecyclerViewAdapter(getActivity(),
                filteredModelList,this);

        adap.animateTo(filteredModelList);
        rv.setAdapter(adap);
        rv.scrollToPosition(0);
        return true;


        //  return false;
    }

    private List<SongsIndiPop> filter(List<SongsIndiPop> models, String query) {
        models = onRefresh;
        query = query.toLowerCase();

        final List<SongsIndiPop> filteredModelList = new ArrayList<>();
        for (SongsIndiPop model : models) {
            final String text = model.getSONGNAME().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }



    @Override
    public void onStart() {
        super.onStart();
        // The service is bound to this activity

    }




    /*private ListsClickListener clickListener = new ListsClickListener() {


        @Override
        public void onPlayableItemClick(PlayableItem item) {
            playItem(item);
        }

    };*/



    /*public void gotoPlayingItemPosition() {
        final PlayableItem playingItem = musicService.getCurrentPlayingItem();
        if(playingItem==null) return;
        if(playingItem instanceof BrowserSong) {
            //openPage(PAGE_BROWSER);
        }
        gotoPlayingItemPosition(playingItem);
    }*/

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void gotoPlayingItemPosition(PlayableItem playingItem) {
        BrowserSong song = (BrowserSong)playingItem;
        scrollToSong(song);

    }

    private void scrollToSong(BrowserSong song) {
        /*MusicPlayerAdapter adapter = (MusicPlayerAdapter)recyclerView.getAdapter();
        recyclerView.scrollToPosition(adapter.getPlayableItemPosition(song));*/
    }



    private void updateListView(boolean restoreOldPosition) {
        if(this.getActivity()==null) return;

        ArrayList<BrowserSong> browsingSongs = BrowserSong.getSongsInDirectory("t");
        ArrayList<Object> items = new ArrayList<>();
        items.addAll(browsingSongs);
        BrowserSong playingSong = null;

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, List<SongsIndiPop>> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        InputStreamReader inputStream = null;
        String result = "";
        String listName;

        @Override
        protected void onPostExecute(List<SongsIndiPop> result) {
            super.onPostExecute(result);

            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();

                if(result == null){
                    Toast.makeText(getActivity(),"Something went wrong! Check your network connection or Try Later",Toast.LENGTH_SHORT).show();
                }
                else if (result != null && result.size() > 0 & getActivity() != null) {

                    onRefresh = result;
                    rv.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), onRefresh,PopSongsFragment.this));
                    SongsFilesData.myIndiMap.put(listName, result);

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

            List<SongsIndiPop> mReturn = new ArrayList<SongsIndiPop>();


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
        private PopSongsFragment mpf;
        private List<SongsIndiPop> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

            public final ImageView mMenu;
            public final View mView;
            public final CircleImageView mPopImage;
            public final TextView mTextView;
            public final CircleButton mPlayIcon;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mMenu = (ImageView) view.findViewById(R.id.buttonMenu);
                mTextView = (TextView) view.findViewById(R.id.trackname);
                mPlayIcon = (CircleButton) view.findViewById(R.id.playIcon);
                mPopImage = (CircleImageView) view.findViewById(R.id.popImage);
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

        public SimpleStringRecyclerViewAdapter(Context context, List<SongsIndiPop> items,PopSongsFragment mpf) {


            mValues = items;
            mContext = context;
            this.mpf = mpf;

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
                                case R.id.menu_asksong:

                                    String sname = mValues.get(position).getSONGNAME().toString();
                                    sname = getOnlyStrings(sname);
                                    String singersask = mValues.get(position).getSINGERS().replaceAll("NOT AVAILABLE ,", "");
                                    String finalSng = "Can you provide me song : " + sname + " by singer/artist " + singersask;
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/*");
                                    share.putExtra(Intent.EXTRA_TEXT, finalSng);
                                    mContext.startActivity(Intent.createChooser(share, "Ask for Song"));

                                    return true;

                                case R.id.menu_watchonyoutube:

                                    String url = "https://www.youtube.com/results?search_query=";
                                    String snameCheck = getOnlyStrings(mValues.get(position).getSONGNAME());
                                    String singers = mValues.get(position).getSINGERS().replaceAll("NOT AVAILABLE ,", "");

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


            holds.mTextView.setText(mValues.get(position).getSONGNAME().toString());
            //holds.mPlayIcon.setVisibility(View.GONE);
            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_download_test);
            holds.mPlayIcon.setImageDrawable(d);
            /*holds.mPopImage.setVisibility(View.GONE);*/
            Glide.with(mContext).load(mValues.get(position).getURLS().toString()).into(holds.mPopImage);
            holds.mPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader/") + mValues.get(position).getSONGNAME().toString() + ".mp3");
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


