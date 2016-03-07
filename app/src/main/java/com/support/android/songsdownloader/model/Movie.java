package com.support.android.songsdownloader.model;

import java.util.List;

/**
 * Created by amitagarwal3 on 3/7/2016.
 */

public class Movie {

    public String MOVIESTARTCHAR;
    public String MOVIENAME;
    public int MOVIENUMBER;
    public String RELEASE_DATE;
    public String MUSIC_DIRECTOR;
    public String ACTORS;
    public String SINGERS;
    public String DIRECTOR;
    public String URLS;
    public List<Songs> SONGS;

    public Movie() {
        super();
        // TODO Auto-generated constructor stub
    }
    public Movie(int mOVIENUMBER, String mOVIESTARTCHAR, String mOVIENAME, String rELEASE_DATE , String mUSIC_DIRECTOR, String aCTORS, String sINGERS, String dIRECTOR, String uRLS,List<Songs> SONGS) {
        super();
        this.MOVIESTARTCHAR = mOVIESTARTCHAR;
        this.MOVIENAME = mOVIENAME;
        this.MOVIENUMBER = mOVIENUMBER;
        this.RELEASE_DATE = rELEASE_DATE;
        this.MUSIC_DIRECTOR = mUSIC_DIRECTOR;
        this.ACTORS = aCTORS;
        this.SINGERS = sINGERS;
        this.DIRECTOR = dIRECTOR;
        this.URLS = uRLS;
        this.SONGS = SONGS;
    }

    public List<Songs> getSONGS() {
        return this.SONGS;
    }

    public void setSONGS(List<Songs> SONGS) {
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
    public String getRELEASE_DATE() {
        return this.RELEASE_DATE;
    }
    public void setRELEASE_DATE(String rELEASE_DATE) {
        this.RELEASE_DATE = rELEASE_DATE;
    }
    public String getMUSIC_DIRECTOR() {
        return this.MUSIC_DIRECTOR;
    }
    public void setMUSIC_DIRECTOR(String mUSIC_DIRECTOR) {
        this.MUSIC_DIRECTOR = mUSIC_DIRECTOR;
    }
    public String getACTORS() {
        return this.ACTORS;
    }
    public void setACTORS(String aCTORS) {
        this.ACTORS = aCTORS;
    }
    public String getSINGERS() {
        return this.SINGERS;
    }
    public void setSINGERS(String sINGERS) {
        this.SINGERS = sINGERS;
    }
    public String getDIRECTOR() {
        return this.DIRECTOR;
    }
    public void setDIRECTOR(String dIRECTOR) {
        this.DIRECTOR = dIRECTOR;
    }
    public String getURLS() {
        return this.URLS;
    }
    public void setURLS(String uRLS) {
        this.URLS = uRLS;
    }

}

