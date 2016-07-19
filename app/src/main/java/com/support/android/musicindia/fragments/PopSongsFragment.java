package com.support.android.musicindia.fragments;

/**
 * Created by amitagarwal3 on 12/7/2015.
 */
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.musicindia.data.UserProvider;
import com.support.android.musicindia.dto.DTOProviderSONG;
import com.support.android.musicindia.helper.BrowserSong;
import com.support.android.musicindia.helper.CheckableImageButton;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.helper.DividerItemDecoration;
import com.support.android.musicindia.application.MusicPlayerApplication;
import com.support.android.musicindia.R;
import com.support.android.musicindia.activities.MainActivity;
import com.support.android.musicindia.model.PlayableItem;
import com.support.android.musicindia.model.SONG;
import com.support.android.musicindia.model.Songs;
import com.support.android.musicindia.model.SongsFilesData;
import com.support.android.musicindia.model.SongsIndiPop;
import com.support.android.musicindia.services.MusicServiceTinyPlayer;
import com.support.android.musicindia.services.MyIntentService;

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

public class PopSongsFragment extends Fragment implements SearchView.OnQueryTextListener,Animation.AnimationListener {

    private MusicPlayerApplication app;
    public ConnectionDetector conDet;
    private ArrayList<String> MyFiles;
    private RecyclerView rv;
    static MainActivity ma;
    public static List<SongsIndiPop> onRefresh;
    public static String driveURL = "http://ec2-52-36-80-134.us-west-2.compute.amazonaws.com:8080/useraccount/rest/songs/latestindi";
    private int cancelId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        conDet = new ConnectionDetector(getActivity());
        View view =  inflater.inflate(R.layout.fragment_tracks_details, container, false);
        app = (MusicPlayerApplication)this.getActivity().getApplication();
        rv = (RecyclerView) view.findViewById(R.id.rViewTracks);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));

        setHasOptionsMenu(true);
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(PopSongsFragment.onRefresh != null){
            ArrayList<SongsIndiPop> arrListPop = new ArrayList<SongsIndiPop>(PopSongsFragment.onRefresh);
            savedInstanceState.putSerializable("pop", arrListPop);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null){

            if(onRefresh == null ) {

                if (conDet.isConnectingToInternet()){
                    (new AsyncListViewLoaderTiled()).execute(driveURL,"indi00");
                    onRefresh = SongsFilesData.myIndiMap.get("indi00");

                }
                else{
                    Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                    SongsFilesData.myIndiMap.put("indi00", null);
                    onRefresh = null;
                    rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), null,PopSongsFragment.this));

                }

            }
            else
            {

                if (conDet.isConnectingToInternet()){

                    rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), onRefresh,PopSongsFragment.this));
                }
                else{
                    Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                    SongsFilesData.myIndiMap.put("indi00", null);
                    onRefresh = null;
                    rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), null,PopSongsFragment.this));

                }

            }


        }
        else if (savedInstanceState != null) {
            // Restore last state for checked position.
            if(savedInstanceState.getSerializable("pop") != null){
                PopSongsFragment.onRefresh =  (List<SongsIndiPop>)savedInstanceState.getSerializable("pop");
                rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), onRefresh,PopSongsFragment.this));

            }
            else{

                if(onRefresh == null ) {

                    if (conDet.isConnectingToInternet()){
                        (new AsyncListViewLoaderTiled()).execute(driveURL,"indi00");
                        onRefresh = SongsFilesData.myIndiMap.get("indi00");

                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myIndiMap.put("indi00", null);
                        onRefresh = null;
                        rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), null,PopSongsFragment.this));

                    }

                }
                else
                {

                    if (conDet.isConnectingToInternet()){

                        rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), onRefresh,PopSongsFragment.this));
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Internet Connection",Toast.LENGTH_SHORT).show();
                        SongsFilesData.myIndiMap.put("indi00", null);
                        onRefresh = null;
                        rv.setAdapter(new RecyclerViewAdapterPopSongs(this.getActivity(), null,PopSongsFragment.this));

                    }

                }

            }

        }
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

        List<SongsIndiPop> temp = onRefresh;
        ArrayList<SongsIndiPop> sample = (ArrayList<SongsIndiPop>) temp;
        final List<SongsIndiPop> filteredModelList = filter(sample, query);

        RecyclerViewAdapterPopSongs adap = new RecyclerViewAdapterPopSongs(getActivity(),
                filteredModelList,this);

        adap.animateTo(filteredModelList);
        rv.setAdapter(adap);
        rv.scrollToPosition(0);
        return true;
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
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

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
                    rv.setAdapter(new RecyclerViewAdapterPopSongs(getActivity(), onRefresh,PopSongsFragment.this));
                    SongsFilesData.myIndiMap.put(listName, result);
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
                                mReturn.add(sObj);

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


            return mReturn;
        }
    }

    public static class RecyclerViewAdapterPopSongs
            extends RecyclerView.Adapter<RecyclerViewAdapterPopSongs.ViewHolder> {



        private Context mContext;
        private PopSongsFragment mpf;
        private List<SongsIndiPop> mValues;
        private ViewHolder holds;

        ImageLoaderConfiguration config;

        DisplayImageOptions imgDisplayOptions;

        static ImageLoader imageLoader = ImageLoader.getInstance();

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

        public RecyclerViewAdapterPopSongs(Context context, List<SongsIndiPop> items,PopSongsFragment mpf) {


            mValues = items;
            mContext = context;
            this.mpf = mpf;

            config  = new ImageLoaderConfiguration.Builder(context).memoryCacheSize(41943040).discCacheSize(104857600).threadPoolSize(10).build();
            imgDisplayOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_musicindia).showImageOnFail(R.drawable.ic_musicindia).showImageOnLoading(R.drawable.ic_musicindia).cacheInMemory().cacheOnDisc().build();
            imageLoader.init(config);

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
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_popsongs, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {

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
                                    mContext.startActivity(videoIntent);
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
            holds.mTextView.setText(mValues.get(position).getSONGNAME().toString());
            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_download_test);
            holds.mPlayIcon.setImageDrawable(d);
            imageLoader.displayImage("", holds.mPopImage); //clears previous one

            imageLoader.displayImage(mValues.get(position).getURLS().toString(), holds.mPopImage,
                    imgDisplayOptions
            );


            holds.mPlayIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + mValues.get(position).getSONGNAME().toString() + ".mp3");
                    if(f.exists()){

                        String alreadyInDb = mValues.get(position).getSONGNAME().toString();
                        int sid = mValues.get(position).getSONG_ID();

                        ArrayList<SONG> isExits = DTOProviderSONG.getSONGIteminDatabase(mContext, sid, alreadyInDb);
                        if(isExits != null && isExits.size() > 0){

                            File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia"));
                            if(!mydir.exists())
                                mydir.mkdirs();
                            else
                                Log.d("error", "dir. already exists");
                            File file = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + alreadyInDb + ".mp3");
                            if (file.exists()) {
                                file.delete();

                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                                Uri uri = Uri.fromFile(mydir);
                                mediaScanIntent.setData(uri);
                                beforeFourFourIntent.setData(uri);

                                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                    mContext.sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                                }
                                else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                {
                                    mContext.sendBroadcast(mediaScanIntent);
                                }

                                Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                                intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSONGLINK_128KBPS_CONV().toString());
                                intentMyIntentService.putExtra("songName", mValues.get(position).getSONGNAME().toString());
                                intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSONG_ID());
                                intentMyIntentService.putExtra("songIncompleteId", (int) mValues.get(position).getSONG_ID());
                                intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSONGLINK_128KBPS().toString());
                                mContext.startService(intentMyIntentService);

                            }
                        }
                        else{

                        }

                    }
                    else {

                        String alreadyInDb = mValues.get(position).getSONGNAME().toString();
                        int sid = mValues.get(position).getSONG_ID();

                        ArrayList<SONG> isExits = DTOProviderSONG.getNewSONGIteminDatabase(mContext, sid, alreadyInDb);
                        if(isExits != null && isExits.size() > 0){
                            DTOProviderSONG.deleteSONGfromDatabase(mContext, sid, alreadyInDb);
                        }


                        Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSONGLINK_128KBPS_CONV().toString());
                        intentMyIntentService.putExtra("songName", mValues.get(position).getSONGNAME().toString());
                        intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSONG_ID());
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSONGLINK_128KBPS().toString());
                        mContext.startService(intentMyIntentService);
                        Toast.makeText(mContext, "Song added to Downloads", Toast.LENGTH_SHORT).show();


                        SONG songObj = new SONG(mValues.get(position).getSONG_ID(),mValues.get(position).getSONGNAME(),mValues.get(position).getSONGLINK_128KBPS_CONV(),0,1);
                        ContentValues cv = getContentValues(songObj);
                        DTOProviderSONG.insertSONGIteminDatabase(mContext,cv);

                    }

                }
            });

        }

        public ContentValues getContentValues(SONG songObj){

            ContentValues contentValues = new ContentValues();
            contentValues.put(UserProvider._SONGID,songObj.getSongId());
            contentValues.put(UserProvider._SONG_URL, songObj.getSongUrl());
            contentValues.put(UserProvider._SONG_NAME, songObj.getSongName());
            contentValues.put(UserProvider._SONG_DWNLD_COMPLETED, songObj.getCompleted());
            contentValues.put(UserProvider._SONG_DWNLD_RUNNING, songObj.getRunning());
            return contentValues;
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


