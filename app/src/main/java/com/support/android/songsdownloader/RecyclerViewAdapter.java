package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 3/5/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.support.android.songsdownloader.model.Movie;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {
    private List<Movie> movie;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Movie item);
    }

    //private final List<ContentItem> items;
    private final OnItemClickListener listener;

    ImageLoaderConfiguration config;

    DisplayImageOptions imgDisplayOptions;

    static ImageLoader imageLoader = ImageLoader.getInstance();

    public RecyclerViewAdapter(Context context, List<Movie> movie,OnItemClickListener listener) {
        this.movie = movie;
        this.context = context;
        this.listener = listener;
        config  = new ImageLoaderConfiguration.Builder(context).memoryCacheSize(41943040).discCacheSize(104857600).threadPoolSize(10).build();
        imgDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc().build();
        //imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }
    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);

        return rcv;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {

        holder.bind(movie.get(position), listener);

        if(movie.get(position).getMOVIENAME().contains("Mp3 Songs"))
            holder.countryName.setText(movie.get(position).getMOVIENAME().replace("Mp3 Songs", ""));
        else if(movie.get(position).getMOVIENAME().contains("Mp3 Song"))
            holder.countryName.setText(movie.get(position).getMOVIENAME().replace("Mp3 Song", ""));
        else
            holder.countryName.setText(movie.get(position).getMOVIENAME());

        //holder.countryName.setText(movie.get(position).getMOVIENAME());

        imageLoader.displayImage("", holder.countryPhoto); //clears previous one

        imageLoader.displayImage(movie.get(position).getURLS(),holder.countryPhoto,
                    imgDisplayOptions
            );
            holder.countryPhoto.setVisibility(View.VISIBLE);


        //imageLoader.displayImage(movie.get(position).getURLS(), holder.countryPhoto);
       // holder.countryPhoto.setImageResource(movie.get(position).getURLS());
    }
    @Override
    public int getItemCount() {
        if(movie == null || movie.size() == 0)
            return 0;
        return this.movie.size();
    }
}
