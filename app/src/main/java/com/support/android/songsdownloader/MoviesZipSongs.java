package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 12/26/2015.
 */
public class MoviesZipSongs {


    private String mOTEKB;
    private String mTTKB;
    private String mOTEKBNAME;
    private String mTTKBNAME;

    public MoviesZipSongs(String mOTEKB, String mTTKB,String mOTEKBNAME,String mTTKBNAME)
    {

        this.mOTEKB = mOTEKB;
        this.mTTKB = mTTKB;
        this.mOTEKBNAME = mOTEKBNAME;
        this.mTTKBNAME = mTTKBNAME;
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

    public String getmOTEKBNAME() {
        return mOTEKBNAME;
    }

    public void setmOTEKBNAME(String mOTEKBNAME) {
        this.mOTEKBNAME = mOTEKBNAME;
    }

    public String getmTTKBNAME() {
        return mTTKBNAME;
    }

    public void setmTTKBNAME(String mTTKBNAME) {
        this.mTTKBNAME = mTTKBNAME;
    }

    public void setmTTKB(String mTTKB) {

        this.mTTKB = mTTKB;
    }


}
