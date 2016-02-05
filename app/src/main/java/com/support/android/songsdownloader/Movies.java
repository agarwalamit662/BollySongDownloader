package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 12/26/2015.
 */
public class Movies {

    private String mName;
    private String mUrl;

    public Movies(String mName,String mUrl)
    {
        this.mName = mName;
        this.mUrl = mUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
