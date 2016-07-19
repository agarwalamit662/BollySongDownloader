package com.support.android.musicindia.adapters;

/**
 * Created by amitagarwal3 on 3/5/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.musicindia.activities.MovieDetailActivityLyrics;
import com.support.android.musicindia.R;
import com.support.android.musicindia.viewholders.RecyclerViewHoldersLyrics;
import com.support.android.musicindia.model.MovieLyrics;

import java.util.List;

public class RecyclerViewAdapterLyrics extends RecyclerView.Adapter<RecyclerViewHoldersLyrics> {
    private List<MovieLyrics> movie;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(MovieLyrics item);
    }

    private final OnItemClickListener listener;

    ImageLoaderConfiguration config;

    DisplayImageOptions imgDisplayOptions;

    static ImageLoader imageLoader = ImageLoader.getInstance();

    public RecyclerViewAdapterLyrics(Context context, List<MovieLyrics> movie, OnItemClickListener listener) {
        this.movie = movie;
        this.context = context;
        this.listener = listener;
        config  = new ImageLoaderConfiguration.Builder(context).memoryCacheSize(41943040).discCacheSize(104857600).threadPoolSize(10).build();
        imgDisplayOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_musicindia).showImageOnFail(R.drawable.ic_musicindia).showImageOnLoading(R.drawable.ic_musicindia).cacheInMemory().cacheOnDisc().build();
        imageLoader.init(config);
    }
    @Override
    public RecyclerViewHoldersLyrics onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHoldersLyrics rcv = new RecyclerViewHoldersLyrics(layoutView);

        return rcv;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHoldersLyrics holder, final int position) {

        holder.bind(movie.get(position), listener);

        if(movie.get(position).getMOVIENAME().contains("Mp3 Songs"))
            holder.countryName.setText(movie.get(position).getMOVIENAME().replace("Mp3 Songs", ""));
        else if(movie.get(position).getMOVIENAME().contains("Mp3 Song"))
            holder.countryName.setText(movie.get(position).getMOVIENAME().replace("Mp3 Song", ""));
        else
            holder.countryName.setText(movie.get(position).getMOVIENAME());

        imageLoader.displayImage("" , holder.countryPhoto); //clears previous one

        imageLoader.displayImage(movie.get(position).getURLS(), holder.countryPhoto,
                    imgDisplayOptions
        );

        holder.countryPhoto.setVisibility(View.VISIBLE);

        holder.countryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MovieDetailActivityLyrics.class);
                i.putExtra("MovieObjectLyrics", movie.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

        holder.countryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MovieDetailActivityLyrics.class);
                i.putExtra("MovieObjectLyrics", movie.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(movie == null || movie.size() == 0)
            return 0;
        return this.movie.size();
    }
}
