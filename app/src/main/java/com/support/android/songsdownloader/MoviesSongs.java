package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 12/26/2015.
 */
public class MoviesSongs {

    private String mSongsName;
    private String mSongsUrl;
    private String mOTEKB;
    private String mTTKB;

    public MoviesSongs(String mSongsName, String mSongsUrl,String mOTEKB,String mTTKB)
    {
        this.mSongsName = mSongsName;
        this.mSongsUrl = mSongsUrl;
        this.mOTEKB = mOTEKB;
        this.mTTKB = mTTKB;
    }

    public String getmOTEKB() {
        return mOTEKB;
    }

    public void setmOTEKB(String mOTEKB) {
        this.mOTEKB = mOTEKB;
    }

    public String getmTTKB() {
        return mTTKB;
    }

    public void setmTTKB(String mTTKB) {
        this.mTTKB = mTTKB;
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
}
