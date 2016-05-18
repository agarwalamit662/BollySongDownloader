/*
 * Copyright 2012-2013 Andrea De Cesare
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


package com.support.android.tinyplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.support.android.tinyplayer.model.Information;
import com.support.android.tinyplayer.model.PlayableItem;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class BrowserSong implements PlayableItem, Serializable {
    private static final long serialVersionUID = 1L;
    private final static String[] projection = {
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATA

    };
    private static ContentResolver mediaResolver;
    private String title, artist;
    private int trackNumber = 0;
    private String uri;
    private Bitmap bitSong;

    public BrowserSong(String uri, String artist, String title, String trackNumberString, Bitmap bitSong) {
        this.uri = uri;
        this.artist = artist;
        this.title = title;
        this.bitSong = bitSong;
        try {
            this.trackNumber = Integer.parseInt(trackNumberString);
        } catch (Exception e) {
        }

        Context context = MusicPlayerApplication.getContext();

        mediaResolver = context.getContentResolver();


    }

    public BrowserSong(String uri) {
        this.uri = uri;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(uri);
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (title == null || title.equals("")) title = new File(uri).getName();
            if (artist == null) artist = "";
            try {
                trackNumber = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
            } catch (Exception ex) {
            }
        } catch (Exception e) {
            title = new File(uri).getName();
            artist = "";
        } finally {
            mmr.release();
        }
    }

    public static ArrayList<BrowserSong> getSongsInDirectory(String sortingMethod) {
        ArrayList<BrowserSong> songs = new ArrayList<>();
        Context contexts = MusicPlayerApplication.getContext();
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0  " ;

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");

        String[] selectionArgsMp3 = new String[]{mimeType};
        //"AND " + MediaStore.Audio.Media.MIME_TYPE + " = \"audio/mp3\"" ;
        mediaResolver = contexts.getContentResolver();
        //android.provider.MediaStore.Audio.Media.getContentUriForPath(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = mediaResolver.query(musicUri, projection, selectionMimeType, selectionArgsMp3, getSortOrder(sortingMethod));
        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int uriColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

        int trackColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String uri = musicCursor.getString(uriColumn);
              //  Bitmap bitmap = Utils.getMusicFileImage(uri);
                String trackNumber = musicCursor.getString(trackColumn);
                songs.add(new BrowserSong(uri, artist, title, trackNumber, null));
            } while (musicCursor.moveToNext());
        }
        musicCursor.close();

        return songs;
    }

    public static ArrayList<BrowserSong> getSongsInDirectoryDownload(String sortingMethod, Context con) {

        ArrayList<BrowserSong> songs = new ArrayList<>();
        //Context contexts = con;


        Context contexts = MusicPlayerApplication.getContext();
        mediaResolver = contexts.getContentResolver();
        //File mydir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
        File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory() + "/SongsDownloader"));
        if (!mydir.exists())
            mydir.mkdirs();
        String path = mydir.getAbsolutePath();
        if (!path.endsWith("/")) path += "/";


        String where = MediaStore.MediaColumns.DATA + " LIKE \"" + path + "%\" AND " + MediaStore.MediaColumns.DATA + " NOT LIKE \"" + path + "%/%\"";

        Cursor musicCursor = mediaResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, where, null, getSortOrder(sortingMethod));
        //Cursor musicCursor = mediaResolver.query(Uri.fromFile(mydir), projection, null, null, getSortOrder(sortingMethod));

        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int uriColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int trackColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String uri = musicCursor.getString(uriColumn);
                String trackNumber = musicCursor.getString(trackColumn);
                //Bitmap bitmap = Utils.getMusicFileImage(uri);
                songs.add(new BrowserSong(uri, artist, title, trackNumber, null));
            } while (musicCursor.moveToNext());
        }
        musicCursor.close();

		/*FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        MediaMetadataRetriever ret = new MediaMetadataRetriever();

		File f = new File(path);
		if (f.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : f.listFiles(new FileExtensionFilter())) {
				//retriever.setDataSource(file.getAbsolutePath());
				ret.setDataSource(file.getAbsolutePath());
				String title = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
				String artist = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
				String uri = Uri.fromFile(file).toString();
				String trackNumber = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
				songs.add(new BrowserSong(uri, artist, title, trackNumber));

			}
		}*/
        return songs;


    }

    private static String getSortOrder(String sortingMethod) {
        switch (sortingMethod) {
            case "nat":
                return MediaStore.Audio.Media.TRACK + "," + MediaStore.Audio.Media.ARTIST + "," + MediaStore.Audio.Media.TITLE;
            case "at":
                return MediaStore.Audio.Media.ARTIST + "," + MediaStore.Audio.Media.TITLE;
            case "ta":
                return MediaStore.Audio.Media.TITLE + "," + MediaStore.Audio.Media.ARTIST;
            case "f":
                return MediaStore.Audio.Media.DATA;
            case "t":
                return MediaStore.Audio.Media.TITLE;
            case "date":
                return MediaStore.Audio.Media.DATE_ADDED + " desc ";

        }
        return null;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean hasImage() {
        return true;
    }

    @Override
    public Bitmap getImage() {
        return Utils.getMusicFileImage(uri);
    }

    @Override
    public boolean equals(Object o) {
        BrowserSong s2 = (BrowserSong) o;
        return uri.equals(s2.uri);
    }

    @Override
    public String getPlayableUri() {
        return uri;
    }

    @Override
    public PlayableItem getNext(boolean repeatAll) {
        ArrayList<BrowserSong> songs = getSongsInDirectory("ta");
        int index = songs.indexOf(this);
        if (index < songs.size() - 1) {
            return songs.get(index + 1);
        } else {
            if (repeatAll) return songs.get(0);
        }
        return null;
    }

    @Override
    public PlayableItem getPrevious() {
        ArrayList<BrowserSong> songs = getSongsInDirectory("nat");
        int index = songs.indexOf(this);
        if (index > 0) {
            return songs.get(index - 1);
        } else {
            return null;
        }
    }

    @Override
    public PlayableItem getRandom(Random random) {
        ArrayList<BrowserSong> songs = getSongsInDirectory("nat");
        return songs.get(random.nextInt(songs.size()));
    }

    @Override
    public boolean isLengthAvailable() {
        return true;
    }

    @Override
    public ArrayList<Information> getInformation() {
        String bitrate = null, album = null, year = null;

        // Get additional information from file
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(uri);
            bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        } catch (Exception e) {
        } finally {
            mmr.release();
        }

        ArrayList<Information> info = new ArrayList<>();
        info.add(new Information(R.string.artist, artist));
        info.add(new Information(R.string.title, title));
        if (year != null) info.add(new Information(R.string.year, year));
        if (album != null) info.add(new Information(R.string.album, album));
        if (trackNumber > 0) {
            info.add(new Information(R.string.trackNumber, trackNumber + ""));
        } else {
            info.add(new Information(R.string.trackNumber, "-"));
        }
        info.add(new Information(R.string.fileName, uri));
        info.add(new Information(R.string.fileSize, Utils.getFileSize(uri)));
        if (bitrate != null) {
            try {
                int kbps = Integer.parseInt(bitrate) / 1000;
                info.add(new Information(R.string.bitrate, kbps + " kbps"));
            } catch (Exception e) {
            }
        }

        return info;
    }

    static class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}

