/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.support.android.songsdownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsLists extends AppCompatActivity {
    public MyDownloadStatusListener myDownloadStatusListener;
    private ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;

    private RecyclerView rv;
    public static final String EXTRA_NAME = "cheese_name";
    private String mSongName;
    private String mSongUrl;
    private String mSongId;
    private ProgressDialog dialog;
    private ProgressBar cur;
    private String linkOne;
    private String linkTwo;
    private ArrayList<MoviesDownloadSongs> listDwnlds;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download_detail);
        myDownloadStatusListener = new MyDownloadStatusListener();
        rv = (RecyclerView) findViewById(R.id.recyclerviewDownloads);
        cur = (ProgressBar) findViewById(R.id.currPrgCheck);
        Intent intent = getIntent();
        mSongName = intent.getStringExtra("mSongName");
        mSongUrl = intent.getStringExtra("mSongUrl");
        mSongId = intent.getStringExtra("mSongId");
        if(mSongName != null && mSongUrl != null && mSongId != null)
        {
            listDwnlds = new ArrayList<MoviesDownloadSongs>();
            MoviesDownloadSongs m = new MoviesDownloadSongs(mSongName,mSongUrl,mSongId);
            listDwnlds.add(m);

            downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
            RetryPolicy retryPolicy = new DefaultRetryPolicy();

            File filesDir = getExternalFilesDir("");

            Uri downloadUri = Uri.parse(mSongUrl);
            Uri destinationUri = Uri.parse("/sdcard"+"/"+mSongName+".mp3");
            final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
                    .setRetryPolicy(retryPolicy)
                    .setDownloadListener(myDownloadStatusListener);

            int downloadId1 = downloadManager.add(downloadRequest1);


        }




        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSongs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Downloads");



    }
    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    class MyDownloadStatusListener implements DownloadStatusListener {

        @Override
        public void onDownloadComplete(int id) {

            // mProgress5Txt.setText("Download5 id: " + id + " Completed");

        }

        @Override
        public void onDownloadFailed(int id, int errorCode, String errorMessage) {

            // mProgress5Txt.setText("Download5 id: " + id + " Failed: ErrorCode " + errorCode + ", " + errorMessage);
            // mProgress5.setProgress(0);

        }

        @Override
        public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

            // mProgress1Txt.setText("Download1 id: " + id + ", " + progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
            // mProgress1.setProgress(progress);
            cur.setProgress(progress);
        }


        private String getBytesDownloaded(int progress, long totalBytes) {
            //Greater than 1 MB
            long bytesCompleted = (progress * totalBytes) / 100;
            if (totalBytes >= 1000000) {
                return ("" + (String.format("%.1f", (float) bytesCompleted / 1000000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000000)) + "MB");
            }
            if (totalBytes >= 1000) {
                return ("" + (String.format("%.1f", (float) bytesCompleted / 1000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000)) + "Kb");

            } else {
                return ("" + bytesCompleted + "/" + totalBytes);
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        public MyDownloadStatusListener myDownloadStatusListener;
        private Context mContext;
        private ThinDownloadManager downloadManager;
        private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<MoviesDownloadSongs> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;

            public final TextView mTextView;
            public final ProgressBar mCurrentProgress;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTextView = (TextView) view.findViewById(R.id.textDwnldSongName);
                mCurrentProgress = (ProgressBar) view.findViewById(R.id.currPrgDownloads);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public MoviesDownloadSongs getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<MoviesDownloadSongs> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;

            mValues = items;
            mContext = context;
            myDownloadStatusListener = new MyDownloadStatusListener();


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_alldownloads_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holds = holder;

            holds.mBoundString = mValues.get(position).getmSongsName();
            //holds.mTextView.setText(String.valueOf(position));
            holds.mTextView.setText(mValues.get(position).getmSongsName().toString());
            downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
            RetryPolicy retryPolicy = new DefaultRetryPolicy();

           // File filesDir = getExternalFilesDir("");

            Uri downloadUri = Uri.parse(mValues.get(position).getmSongsUrl().toString());
            Uri destinationUri = Uri.parse("/sdcard"+"/"+mValues.get(position).getmSongsName().toString()+".mp3");
            final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
                    .setRetryPolicy(retryPolicy)
                    .setDownloadListener(myDownloadStatusListener);

            int downloadId1 = downloadManager.add(downloadRequest1);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        class MyDownloadStatusListener implements DownloadStatusListener {

            @Override
            public void onDownloadComplete(int id) {

               // mProgress5Txt.setText("Download5 id: " + id + " Completed");

            }

            @Override
            public void onDownloadFailed(int id, int errorCode, String errorMessage) {

               // mProgress5Txt.setText("Download5 id: " + id + " Failed: ErrorCode " + errorCode + ", " + errorMessage);
               // mProgress5.setProgress(0);

            }

            @Override
            public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

               // mProgress1Txt.setText("Download1 id: " + id + ", " + progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
               // mProgress1.setProgress(progress);
               holds.mCurrentProgress.setProgress(progress);
            }


            private String getBytesDownloaded(int progress, long totalBytes) {
                //Greater than 1 MB
                long bytesCompleted = (progress * totalBytes) / 100;
                if (totalBytes >= 1000000) {
                    return ("" + (String.format("%.1f", (float) bytesCompleted / 1000000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000000)) + "MB");
                }
                if (totalBytes >= 1000) {
                    return ("" + (String.format("%.1f", (float) bytesCompleted / 1000)) + "/" + (String.format("%.1f", (float) totalBytes / 1000)) + "Kb");

                } else {
                    return ("" + bytesCompleted + "/" + totalBytes);
                }
            }
        }


    }

}
