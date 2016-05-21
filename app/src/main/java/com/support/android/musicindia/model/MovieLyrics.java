package com.support.android.musicindia.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amitagarwal3 on 3/7/2016.
 */
@SuppressWarnings("serial")
public class MovieLyrics implements Serializable {

    public String MOVIESTARTCHAR;
    public String MOVIENAME;
    public int MOVIENUMBER;
    public String URLS;
    public List<SongsLyrics> SONGS;


    public MovieLyrics() {
        super();
        // TODO Auto-generated constructor stub
    }
    public MovieLyrics(int mOVIENUMBER, String mOVIESTARTCHAR, String mOVIENAME ,List<SongsLyrics> SONGS,String uRLS) {
        super();
        this.MOVIESTARTCHAR = mOVIESTARTCHAR;
        this.MOVIENAME = mOVIENAME;
        this.MOVIENUMBER = mOVIENUMBER;

        this.URLS = uRLS;
        this.SONGS = SONGS;

    }

    public List<SongsLyrics> getSONGS() {
        return this.SONGS;
    }

    public void setSONGS(List<SongsLyrics> SONGS) {
        this.SONGS = SONGS;
    }

    public String getMOVIESTARTCHAR() {
        return this.MOVIESTARTCHAR;
    }
    public void setMOVIESTARTCHAR(String mOVIESTARTCHAR) {
        this.MOVIESTARTCHAR = mOVIESTARTCHAR;
    }
    public String getMOVIENAME() {
        return this.MOVIENAME;
    }
    public void setMOVIENAME(String mOVIENAME) {
        this.MOVIENAME = mOVIENAME;
    }
    public int getMOVIENUMBER() {
        return this.MOVIENUMBER;
    }
    public void setMOVIENUMBER(int mOVIENUMBER) {
        this.MOVIENUMBER = mOVIENUMBER;
    }
    public String getURLS() {
        return this.URLS;
    }
    public void setURLS(String uRLS) {
        this.URLS = uRLS;
    }

}

