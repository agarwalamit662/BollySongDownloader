package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 12/7/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DownloadListFragment extends Fragment {

    private TextView currDown;
    private ProgressBar currProg;
    private Button cancelDownloadButton;
    private CardView actionDow;
    private ArrayList<String> MyFiles;
    private RecyclerView rv;

    private MyBroadcastReceiver myBroadcastReceiver;
    private MyBroadcastReceiver_Update myBroadcastReceiver_Update;

    private int cancelId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_downloads_list, container, false);

        rv = (RecyclerView) view.findViewById(R.id.recyclerviewTest);
        actionDow = (CardView) view.findViewById(R.id.actionDownloads);
        setupRecyclerView(rv);
        currDown = (TextView) view.findViewById(R.id.currDwnld);
        currProg = (ProgressBar) view.findViewById(R.id.currPrg);
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
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(MyIntentService.EXTRA_KEY_OUT);
            if(result.equals("Download Cancelled")){
                Toast.makeText(getActivity(),"Download Cancelled",Toast.LENGTH_SHORT).show();


            }
            else {
                Toast.makeText(getActivity(), "Download Complete", Toast.LENGTH_SHORT).show();
        }
            actionDow.setVisibility(View.GONE);
            currDown.setText(result);
            currProg.setVisibility(View.GONE);
            cancelDownloadButton.setVisibility(View.GONE);
            setupRecyclerView(rv);

        }
    }

    public class MyBroadcastReceiver_Update extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            actionDow.setVisibility(View.VISIBLE);
            currProg.setVisibility(View.VISIBLE);
            cancelDownloadButton.setVisibility(View.VISIBLE);
            int update = intent.getIntExtra(MyIntentService.EXTRA_KEY_UPDATE, 0);

            Bundle extras = intent.getExtras();
            cancelId = extras.getInt("songCanId");
            currDown.setText(extras.getString("songName"));

            currProg.setProgress(update);
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), GetFiles("/sdcard/SongsDownloader")));
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
        private List<String> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatarDwnld);
                mTextView = (TextView) view.findViewById(R.id.textDwnld);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_downloads_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues.get(position);
            holder.mTextView.setText(mValues.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                   // Intent intent = new Intent(context, CheeseDetailActivity.class);
                   // intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);

                  //  context.startActivity(intent);
                }
            });

            Glide.with(holder.mImageView.getContext())
                    .load(Cheeses.getRandomCheeseDrawable())
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            if(mValues != null && mValues.size() > 0)
            return mValues.size();
            else
                return 0;
        }
    }



}

