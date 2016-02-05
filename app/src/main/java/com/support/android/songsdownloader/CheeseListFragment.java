
package com.support.android.songsdownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheeseListFragment extends Fragment {
    private static ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    List<String> listSongs = new ArrayList<String>();
    private static RetryPolicy retryPolicy;
    private RecyclerView rv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_cheese_list, container, false);
        (new AsyncListViewLoader()).execute("http://www.songsmp3.com/");
              //  execute("http://10.0.2.2:8080/JSONServer/rest/ContactWS/get");
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        retryPolicy = new DefaultRetryPolicy();


        return rv;
    }


    private class AsyncListViewLoader extends AsyncTask<String, Void, List<Movies>> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(List<Movies> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
            rv.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), result));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading Movies List...");
            dialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected List<Movies> doInBackground(String... params) {
            List<Movies> result = new ArrayList<Movies>();
            Document doc = null;
            try {
                doc = Jsoup.connect(params[0]).get();
                String title = doc.title();
                String href = doc.text();

                Elements e = doc.getElementsByAttributeValueContaining("class", "list_box_2");

                for (Element src : e) {

                   // Elements f = src.select("b");
                    if (src.tagName().equals("div")) {

                        Elements f = src.getElementsByAttributeValueContaining("class","list_box_1_head");
                        for(Element srcs : f)
                        {
                            if(srcs.tagName().equals("div"))
                            {
                                String latBollyMovies = srcs.text();
                                if(latBollyMovies.equals("Latest Bollywood Music"))
                                {
                                    Elements h = src.select("a");

                                    for(Element movies : h)
                                    {

                                        Movies m = new Movies(movies.text(),movies.attr("href"));

                                        listSongs.add(movies.text());
                                        result.add(m);

                                    }

                                }
                            }
                        }
                        return result;

                    } else {
                        String theatrename = "hello";

                    }
                }
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }



    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private Context mContext;

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Movies> mValues;
        private ViewHolder holds;
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;

            public final TextView mTextView;
            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTextView = (TextView) view.findViewById(R.id.text1);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public Movies getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<Movies> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
            mContext = context;


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holds = holder;
            holds.mBoundString = mValues.get(position).getmName();
            //holds.mTextView.setText(String.valueOf(position));
            holds.mTextView.setText(mValues.get(position).getmName().toString());
            holds.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CheeseDetailActivity.class);
                    intent.putExtra("mName", mValues.get(position).getmName());
                    intent.putExtra("mUrl",mValues.get(position).getmUrl());

                    context.startActivity(intent);
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



    }



}
