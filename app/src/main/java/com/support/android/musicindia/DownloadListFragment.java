package com.support.android.musicindia;

/**
 * Created by amitagarwal3 on 12/7/2015.
 */
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.support.android.musicindia.model.PlayableItem;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.markushi.ui.CircleButton;

public class DownloadListFragment extends Fragment implements YourFragmentInterface{

    private Button refreshDownloads;
    private TextView currDown;
    private TextView progPercent;
    private ProgressBar currProg;
    private Button cancelDownloadButton;
    private CardView actionDow;
    private ArrayList<String> MyFiles;
    private RecyclerView rv;
    private SimpleStringRecyclerViewAdapter adapter;
    private MyBroadcastReceiver myBroadcastReceiver;
    private MyBroadcastReceiver_Update myBroadcastReceiver_Update;

    private int cancelId;
    private ProgressDialog dialog;
    private static ViewGroup contCheck;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_downloads_list, container, false);
        contCheck = container;
        setHasOptionsMenu(true);
        dialog = new ProgressDialog(this.getActivity());
        rv = (RecyclerView) view.findViewById(R.id.recyclerviewTest);

        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        adapter = new SimpleStringRecyclerViewAdapter(this.getActivity(), BrowserSong.getSongsInDirectoryDownload("date",getActivity()));
        rv.setAdapter(adapter);

        actionDow = (CardView) view.findViewById(R.id.actionDownloads);
        progPercent = (TextView) view.findViewById(R.id.progPercent);
        //setupRecyclerView(rv);
        currDown = (TextView) view.findViewById(R.id.currDwnld);
        currProg = (ProgressBar) view.findViewById(R.id.currPrg);
        refreshDownloads = (Button) view.findViewById(R.id.refreshDownloads);
        cancelDownloadButton = (Button) view.findViewById(R.id.cancelDwnldRunning);
        cancelDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentMyIntentService = new Intent(getActivity(), MyIntentService.class);
                intentMyIntentService.putExtra("songCanecelId", (int)cancelId);
                getActivity().startService(intentMyIntentService);
            }
        });


        myBroadcastReceiver = new MyBroadcastReceiver();
        myBroadcastReceiver_Update = new MyBroadcastReceiver_Update();

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(MyIntentService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(myBroadcastReceiver, intentFilter);

        IntentFilter intentFilter_update = new IntentFilter(MyIntentService.ACTION_MyUpdate);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        getActivity().registerReceiver(myBroadcastReceiver_Update, intentFilter_update);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        item.setVisible(false);
        //searchView.setOnQueryTextListener(this);

    }

    @Override
    public void onResume(){
        /*MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onResume();*/

        super.onResume();
        setupRecyclerView(rv);
        if(refreshDownloads != null)
            refreshDownloads.setVisibility(View.GONE);
        //update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //un-register BroadcastReceiver
        getActivity().unregisterReceiver(myBroadcastReceiver);
        getActivity().unregisterReceiver(myBroadcastReceiver_Update);
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if(files == null)
        {
            return null;
        }
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

    @Override
    public void update() {
        (new AsyncListViewLoaderTiled()).execute("");
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(MyIntentService.EXTRA_KEY_OUT);
            String pathfile = intent.getStringExtra("PATHFILE");
            if(result != null)
                Toast.makeText(getActivity(),result,Toast.LENGTH_SHORT).show();
            /*if(result.equals("Download Cancelled")){
                Toast.makeText(getActivity(),"Download Cancelled",Toast.LENGTH_SHORT).show();


            }
            else {
                Toast.makeText(getActivity(), "Download Complete", Toast.LENGTH_SHORT).show();
        }*/
            actionDow.setVisibility(View.GONE);
            currDown.setText(result);
            currProg.setVisibility(View.GONE);
            progPercent.setVisibility(View.GONE);
            cancelDownloadButton.setVisibility(View.GONE);

            refreshDownloads.setVisibility(View.VISIBLE);
            refreshDownloads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity ma = (MainActivity) getActivity();
                    ma.update();
                }
            });

            sendIntentToTemp(pathfile);


        }
    }



    public void sendIntentToTemp(String filepath)
    {



    }

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, List<BrowserSong>> {

        @Override
        protected void onPostExecute(List<BrowserSong> result) {
            super.onPostExecute(result);
            if(result != null){

            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
                onResume();
                /*setupRecyclerView(rv);
                if(refreshDownloads != null)
                    refreshDownloads.setVisibility(View.GONE);*/
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Refreshing Songs List");
            dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected List<BrowserSong> doInBackground(String... params) {


            /*File mydirs = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia"));
            if(!mydirs.exists())
                mydirs.mkdirs();
            String path = mydirs.getAbsolutePath();
            if(!path.endsWith("/")) path+="/";


            String where = MediaStore.MediaColumns.DATA + " LIKE \"" + path + "%\" AND (title LIKE '%SongsMp3.Com%' OR title LIKE '%SongsMp3.CoM%' OR title LIKE '%SongsMp3.com%' OR album LIKE '%SongsMp3.com%' OR album LIKE '%SongsMp3.Com%' OR album LIKE '%SongsMp3.CoM%') AND " + MediaStore.MediaColumns.DATA + " NOT LIKE \"" + path + "%/%\"";

            Cursor abc =  getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "title", "artist", "album", "year", "_data" }, where, null, " _id desc");
            Log.e("Count is : ",String.valueOf(abc.getCount()));
            Log.e("Count is : ",String.valueOf(abc.getCount()));
            while(abc.moveToNext()){

                int id = abc.getInt(0);
                String titless = abc.getString(1);
                titless = titless.replace("SongsMp3.com", "");
                titless = titless.replace("SongsMp3.Com", "");
                titless = titless.replace("SongsMp3.CoM", "");
                titless = titless.replace("Songsmp3.com", "");
                titless = titless.replace("Songsmp3.CoM", "");
                titless = titless.replace("Songsmp3.Com", "");
                titless = getOnlyStrings(titless);

                String album = abc.getString(3);
                album = album.replace("SongsMp3.com", "");
                album = album.replace("SongsMp3.Com", "");
                album = album.replace("SongsMp3.CoM", "");
                album = album.replace("Songsmp3.com", "");
                album = album.replace("Songsmp3.CoM", "");
                album = album.replace("Songsmp3.Com", "");
                album = getOnlyStrings(album);

                String artist = abc.getString(2);
                artist = artist.replace("SongsMp3.com", "");
                artist = artist.replace("SongsMp3.Com", "");
                artist = artist.replace("SongsMp3.CoM", "");
                artist = artist.replace("Songsmp3.com", "");
                artist = artist.replace("Songsmp3.CoM", "");
                artist = artist.replace("Songsmp3.Com", "");
                artist = getOnlyStrings(artist);

                ContentValues localContentValues = new ContentValues();
                ContentResolver localContentResolver = getActivity().getContentResolver();
                Uri localUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                localContentValues.put("title", (String)titless);
                localContentValues.put("album", (String)album);
                localContentValues.put("artist", (String) artist);
                localContentResolver.update(localUri, localContentValues, "_id LIKE \"" + id + "\"", null);
                Log.e("Fiel Path is, ", abc.getString(5));
                Log.e("Fiel Path is, ",abc.getString(5));
                File fs = new File(abc.getString(5));
                AudioFile f = null;
                try {
                    f = AudioFileIO.read(fs);
                    Tag tag = f.getTag();
                    tag.setField(FieldKey.ARTIST, artist);
                    tag.setField(FieldKey.ALBUM, album);

                    tag.setField(FieldKey.TITLE, titless);


                    try {
                        f.commit();
                    } catch (CannotWriteException e) {
                        e.printStackTrace();
                    }
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TagException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                }

            }*/


            return bs;
        }
    }
    static List<BrowserSong> bs;
    private String dwnldCheck;
    public class MyBroadcastReceiver_Update extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            actionDow.setVisibility(View.VISIBLE);
            currProg.setVisibility(View.VISIBLE);
            progPercent.setVisibility(View.VISIBLE);
            cancelDownloadButton.setVisibility(View.VISIBLE);
            int update = intent.getIntExtra(MyIntentService.EXTRA_KEY_UPDATE, 0);

            Bundle extras = intent.getExtras();
            cancelId = extras.getInt("songCanId");
            dwnldCheck = extras.getString("MBDOWNLOADED");
            currDown.setText(extras.getString("songName"));
            //progPercent.setText("Progress: "+String.valueOf(update)+"%");
            progPercent.setText("Downloaded: "+dwnldCheck);
            currProg.setProgress(update);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        if(rv != null){
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
            rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
            rv.setAdapter(new SimpleStringRecyclerViewAdapter(this.getActivity(), BrowserSong.getSongsInDirectoryDownload("date",getActivity())));
        }

    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
            while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<BrowserSong> mValues;
        private Context mContext;
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            public String mBoundString;

            public final View mView;

            public final TextView mTextView;
            public final CircleButton mPlayButton;
            private ImageButton menu;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTextView = (TextView) view.findViewById(R.id.textDwnld);
                mPlayButton = (CircleButton) view.findViewById(R.id.playIconDownloads);
                menu = (ImageButton)view.findViewById(R.id.buttonMenuDwnld);
                menu.setFocusable(false);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            }
        }

        public PlayableItem getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<BrowserSong> items) {
            mValues = items;
            mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_downloads_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mTextView.setText(mValues.get(position).getTitle());

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("in onclick", "in onclick");
                    Log.e("in onclick", "in onclick");
                    final PopupMenu popup = new PopupMenu(mContext, v);
                    popup.getMenuInflater().inflate(R.menu.contextmenu_tracks, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_share:

                                    Uri uri = Uri.parse(mValues.get(position).getUri());
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("audio/*");
                                    share.putExtra(Intent.EXTRA_STREAM, uri);
                                    mContext.startActivity(Intent.createChooser(share, "Share Audio File"));

                                    return true;

                                case R.id.menu_delete:

                                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);


                                    //adb.setView(alertDialogView);


                                    adb.setTitle("Delete this song?");


                                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {


                                            File file = new File(mValues.get(position).getUri().toString());
                                            file.delete();
                                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(new File(mValues.get(position).getUri().toString()))));
                                            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mValues.get(position).getUri().toString()))));
                                            }


                                            dialog.dismiss();

                                            removeAt(position);


                                        }
                                    });


                                    adb.show();

                                    return true;

                                case R.id.menu_editTag:

                                    Intent n = new Intent(mContext, EditTag.class);
                                    n.putExtra("FileName", mValues.get(position).getUri());
                                    mContext.startActivity(n);

                                    return true;


                            }
                            return true;
                        }
                    });

                    popup.show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (popup.getDragToOpenListener() instanceof ListPopupWindow.ForwardingListener) {
                            ListPopupWindow.ForwardingListener listener = (ListPopupWindow.ForwardingListener) popup.getDragToOpenListener();
                            listener.getPopup().setVerticalOffset(-holder.menu.getHeight());
                            listener.getPopup().show();
                        }
                    }


                }
            });

            holder.mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity m = (MainActivity) mContext;
                    //m.selectFragment(2);
                    m.playItem(mValues.get(position));

                }
            });


        }

        @Override
        public int getItemCount() {
            if(mValues != null && mValues.size() > 0)
            return mValues.size();
            else
                return 0;
        }

        public void removeAt(int position) {
            mValues.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mValues.size());
        }

    }


    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        number = number.replaceAll("-","");
        return number;
    }



}

