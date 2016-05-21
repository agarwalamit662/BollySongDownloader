/*
 * Copyright 2014-2015 Andrea De Cesare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.support.android.musicindia;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;


import com.support.android.musicindia.model.PlayableItem;

public class ImagesCache {
    private int imagesSize;
    final private LruCache<String,Bitmap> cache;

    public ImagesCache(Context context) {
        imagesSize = (int)context.getResources().getDimension(R.dimen.songImageSize);
        cache = new LruCache<>(Constants.IMAGES_CACHE_SIZE);
    }

    public void clearCache() {
        synchronized(cache) {
            cache.evictAll();
        }
    }

    public void getImageAsync(PlayableItem item, ImageView imageView) {
        Bitmap image;
        synchronized(cache) {
            image = cache.get(item.getPlayableUri());
        }
        if(image==null) {
            ImageLoaderTask imageLoader = new ImageLoaderTask(item, imageView);
            imageLoader.execute();
        } else {
            imageView.setImageBitmap(image);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap getImageSync(PlayableItem item) {
        synchronized(cache) {
            Bitmap image = cache.get(item.getPlayableUri());
            if(image==null) {
                Bitmap originalImage = item.getImage();
                if(originalImage==null) return null;
                image = Bitmap.createScaledBitmap(originalImage, imagesSize, imagesSize, true);
                cache.put(item.getPlayableUri(), image);
            }
            return image.copy(image.getConfig(), true); // Necessary to avoid recycled bitmap to be used.
        }
    }

    private class ImageLoaderTask extends AsyncTask<Void, Void, Bitmap> {
        private PlayableItem item;
        private ImageView imageView;

        public ImageLoaderTask(PlayableItem item, ImageView imageView) {
            this.item = item;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap originalImage = item.getImage();
            if(originalImage==null) return null;
            Bitmap image = Bitmap.createScaledBitmap(originalImage, imagesSize, imagesSize, true);
            synchronized(cache) {
                cache.put(item.getPlayableUri(), image);
            }
            image = image.copy(image.getConfig(), true); // Necessary to avoid recycled bitmap to be used.
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if(image!=null) {
                imageView.setImageBitmap(image);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }
}

