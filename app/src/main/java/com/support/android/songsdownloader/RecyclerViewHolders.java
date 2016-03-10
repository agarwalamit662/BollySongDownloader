package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 3/5/2016.
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.support.android.songsdownloader.model.Movie;

public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView countryName;
    public ImageView countryPhoto;
    public RecyclerViewHolders(View itemView) {
        super(itemView);
        //itemView.setOnClickListener(this);

        countryName = (TextView)itemView.findViewById(R.id.country_name);
        countryPhoto = (ImageView)itemView.findViewById(R.id.country_photo);
    }
    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition(), Toast.LENGTH_SHORT).show();
    }

    public void bind(final Movie item, final RecyclerViewAdapter.OnItemClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}
