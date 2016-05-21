package com.support.android.musicindia.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SongsLyrics implements Serializable {


	public MovieLyrics movie;
	public String SONGNAME;
	public int SONG_ID;
	public String LYRICS;

	public String getLYRICS() {
		return LYRICS;
	}

	public void setLYRICS(String LYRICS) {
		this.LYRICS = LYRICS;
	}

	public MovieLyrics getMovieLyrics() {
		return this.movie;
	}


	public void setMovieLyrics(MovieLyrics movie) {
		this.movie = movie;
	}


	public String getSONGNAME() {
		return this.SONGNAME;
	}


	public void setSONGNAME(String sONGNAME) {
		this.SONGNAME = sONGNAME;
	}



	public int getSONG_ID() {
		return this.SONG_ID;
	}


	public void setSONG_ID(int sONG_ID) {
		this.SONG_ID = sONG_ID;
	}


	public SongsLyrics() {
		// TODO Auto-generated constructor stub
	}


	public SongsLyrics(MovieLyrics movie, String sONGNAME,int sONG_ID,String LYRICS) {
		super();
		this.movie = movie;
		this.SONGNAME = sONGNAME;
		this.SONG_ID = sONG_ID;
		this.LYRICS = LYRICS;

	}
	
	

}
