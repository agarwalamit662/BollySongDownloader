package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 12/26/2015.
 */
public class MoviesDownloadSongs {

    private String mSongsName;
    private String mSongsUrl;
    private String mSongsId;

    public MoviesDownloadSongs(String mSongsName, String mSongsUrl, String mSongsId) {
        this.mSongsName = mSongsName;
        this.mSongsUrl = mSongsUrl;
        this.mSongsId = mSongsId;
    }

    public String getmSongsName() {
        return mSongsName;
    }

    public void setmSongsName(String mSongsName) {
        this.mSongsName = mSongsName;
    }

    public String getmSongsUrl() {
        return mSongsUrl;
    }

    public void setmSongsUrl(String mSongsUrl) {
        this.mSongsUrl = mSongsUrl;
    }

    public String getmSongsId() {
        return mSongsId;
    }

    public void setmSongsId(String mSongsId) {
        this.mSongsId = mSongsId;
    }
}
