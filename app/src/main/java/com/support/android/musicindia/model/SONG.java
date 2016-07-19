package com.support.android.musicindia.model;

import java.util.Date;

/**
 * Created by amitagarwal3 on 7/17/2016.
 */
public class SONG {

    private int songId;
    private String songName;
    private String songUrl;
    private int completed;
    private int running;

    public SONG(int songId, String songName, String songUrl, int completed, int running) {
        this.songId = songId;
        this.songName = songName;
        this.songUrl = songUrl;
        this.completed = completed;
        this.running = running;
    }

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public SONG() {
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
