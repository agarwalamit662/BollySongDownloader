package com.support.android.musicindia.fragments;

/**
 * Created by amitagarwal3 on 12/7/2015.
 */

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.support.android.musicindia.R;
import com.support.android.musicindia.activities.EditTag;
import com.support.android.musicindia.activities.MainActivity;
import com.support.android.musicindia.data.UserProvider;
import com.support.android.musicindia.dto.DTOProviderSONG;
import com.support.android.musicindia.helper.BrowserSong;
import com.support.android.musicindia.helper.DividerItemDecoration;
import com.support.android.musicindia.interfaces.YourFragmentInterface;
import com.support.android.musicindia.model.PlayableItem;
import com.support.android.musicindia.model.SONG;
import com.support.android.musicindia.services.MyIntentService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.markushi.ui.CircleButton;

public class IncompleteDownloadListFragment extends Fragment implements YourFragmentInterface {


    private TextView completed;
    private CardView actionDow;
    private ArrayList<String> MyFiles;
    private RecyclerView rv;
    private RecyclerViewAdapterDownloads adapter;

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
        completed = (TextView) view.findViewById(R.id.completed);
        completed.setText("Incomplete and Running Downloads");
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        adapter = new RecyclerViewAdapterDownloads(this.getActivity(), DTOProviderSONG.getInCompletedSONGIteminDatabase(getActivity()));
        rv.setAdapter(adapter);

        actionDow = (CardView) view.findViewById(R.id.actionDownloads);
        actionDow.setVisibility(View.GONE);
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
        super.onResume();
        setupRecyclerView(rv);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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

    private class AsyncListViewLoaderTiled extends AsyncTask<String, Void, List<BrowserSong>> {

        @Override
        protected void onPostExecute(List<BrowserSong> result) {
            super.onPostExecute(result);
            if(result != null){

            }
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
                onResume();

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
            return bs;
        }
    }
    static List<BrowserSong> bs;


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
            rv.setAdapter(new RecyclerViewAdapterDownloads(this.getActivity(), DTOProviderSONG.getInCompletedSONGIteminDatabase(getActivity())));
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

    public static class RecyclerViewAdapterDownloads
            extends RecyclerView.Adapter<RecyclerViewAdapterDownloads.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<SONG> mValues;
        private Context mContext;
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            public String mBoundString;

            public final View mView;

            public final TextView mTextView;
            public final CircleButton mRetryButton;
            public final CircleButton mDeleteButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTextView = (TextView) view.findViewById(R.id.textDwnld);
                mRetryButton = (CircleButton) view.findViewById(R.id.retryIconDownloads);
                mDeleteButton = (CircleButton) view.findViewById(R.id.deleteIconDownloads);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            }
        }

        public SONG getValueAt(int position) {
            return mValues.get(position);
        }

        public RecyclerViewAdapterDownloads(Context context, List<SONG> items) {
            mValues = items;
            mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_incomplete_downloads_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mTextView.setText(mValues.get(position).getSongName());


            holder.mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + mValues.get(position).getSongName().toString() + ".mp3");
                    if(f.exists()){

                        Log.e("File exists", "File Exitsts");
                        Log.e("File exists","File Exitsts");
                        Log.e("File exists","File Exitsts");
                        String alreadyInDb = mValues.get(position).getSongName().toString();
                        int sid = mValues.get(position).getSongId();

                        ArrayList<SONG> isExits = DTOProviderSONG.getSONGIteminDatabase(mContext,sid,alreadyInDb);
                        if(isExits != null && isExits.size() > 0){

                            Log.e("Milgaya DBexists","File Exitsts");
                            Log.e("Milgaya DBexists","File Exitsts");
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
                                intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSongUrl().toString());
                                intentMyIntentService.putExtra("songName", mValues.get(position).getSongName().toString());
                                intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSongId());
                                intentMyIntentService.putExtra("songRetryDownloadId", (int) mValues.get(position).getSongId());
                                intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSongUrl().toString());
                                mContext.startService(intentMyIntentService);
                                // Toast.makeText(mContext, "Song added to Downloads", Toast.LENGTH_SHORT).show();

                                /*addedToDownload.setVisibility(View.VISIBLE);
                                addedToDownload.setText("Song added to Downloads");
                                addedToDownload.startAnimation(animSideDown);*/

                            }
                        }
                        else{
                            Toast.makeText(mContext, "Song already in Downloads", Toast.LENGTH_SHORT).show();

                            Log.e("PADA HUA HAI","File Exitsts");
                            Log.e("PADA HUA HAI","File Exitsts");
                            Log.e("PADA HUA HAI","File Exitsts");


                        }

                    }
                    else {

                        String alreadyInDb = mValues.get(position).getSongName().toString();
                        int sid = mValues.get(position).getSongId();

                        /*ArrayList<SONG> isExits = DTOProviderSONG.getNewSONGIteminDatabase(mContext, sid, alreadyInDb);
                        if(isExits != null && isExits.size() > 0){
                            DTOProviderSONG.deleteSONGfromDatabase(mContext, sid, alreadyInDb);
                        }*/

                        Log.e("NYA HAI ", "NAYA HAI");
                        Log.e("NYA HAI ", "NAYA HAI");
                        Log.e("NYA HAI ", "NAYA HAI");

                        Intent intentMyIntentService = new Intent(mContext, MyIntentService.class);
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, mValues.get(position).getSongUrl().toString());
                        intentMyIntentService.putExtra("songName", mValues.get(position).getSongName().toString());
                        intentMyIntentService.putExtra("songId", (int) mValues.get(position).getSongId());
                        intentMyIntentService.putExtra("songRetryDownloadId", (int) mValues.get(position).getSongId());
                        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_LENGTH, mValues.get(position).getSongUrl().toString());
                        mContext.startService(intentMyIntentService);
                        //Toast.makeText(mContext, "Song added to Downloads", Toast.LENGTH_SHORT).show();

                        ContentValues cv = new ContentValues();
                        cv.put(UserProvider._SONG_DWNLD_RUNNING,1);
                        DTOProviderSONG.updateIsRunningToOneinDatabase(mContext, mValues.get(position).getSongId(), cv);

                    }

                }
            });

            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);

                    adb.setTitle("Delete this song from Downloads?");


                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + mValues.get(position).getSongName().toString() + ".mp3");
                            if(f.exists()){
                                f.delete();

                            }


                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(f)));
                            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
                            }

                            dialog.dismiss();

                            DTOProviderSONG.deleteSONGfromDatabase(mContext, mValues.get(position).getSongId(), mValues.get(position).getSongName());
                            removeAt(position);


                        }
                    });


                    adb.show();


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

