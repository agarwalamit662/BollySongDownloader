package com.support.android.songsdownloader.model;

public class Songs {

	
	public Movie movie;
	public String SONGNAME;
	public String SINGERS;
	public String SONGLINK_128KBPS;
	public int SONG_ID;
	public String SONGLINK_128KBPS_CONV;
	public String WORKING_LINK;
	
	
	public String getSONGLINK_128KBPS_CONV() {
		return this.SONGLINK_128KBPS_CONV;
	}


	public void setSONGLINK_128KBPS_CONV(String sONGLINK_128KBPS_CONV) {
		this.SONGLINK_128KBPS_CONV = sONGLINK_128KBPS_CONV;
	}


	public String getWORKING_LINK() {
		return this.WORKING_LINK;
	}


	public void setWORKING_LINK(String wORKING_LINK) {
		this.WORKING_LINK = wORKING_LINK;
	}


	public Movie getMovie() {
		return this.movie;
	}


	public void setMovie(Movie movie) {
		this.movie = movie;
	}


	public String getSONGNAME() {
		return this.SONGNAME;
	}


	public void setSONGNAME(String sONGNAME) {
		this.SONGNAME = sONGNAME;
	}


	public String getSINGERS() {
		return this.SINGERS;
	}


	public void setSINGERS(String sINGERS) {
		this.SINGERS = sINGERS;
	}


	public String getSONGLINK_128KBPS() {
		return this.SONGLINK_128KBPS;
	}


	public void setSONGLINK_128KBPS(String sONGLINK_128KBPS) {
		this.SONGLINK_128KBPS = sONGLINK_128KBPS;
	}


	public int getSONG_ID() {
		return this.SONG_ID;
	}


	public void setSONG_ID(int sONG_ID) {
		this.SONG_ID = sONG_ID;
	}


	public Songs() {
		// TODO Auto-generated constructor stub
	}


	public Songs(Movie movie, String sONGNAME, String sINGERS, String sONGLINK_128KBPS, int sONG_ID,String SONGLINK_128KBPS_CONV,String WORKING_LINK) {
		super();
		this.movie = movie;
		this.SONGNAME = sONGNAME;
		this.SINGERS = sINGERS;
		this.SONGLINK_128KBPS = sONGLINK_128KBPS;
		this.SONG_ID = sONG_ID;
		this.SONGLINK_128KBPS_CONV = SONGLINK_128KBPS_CONV;
		this.WORKING_LINK = WORKING_LINK;
		
	}
	
	

}
