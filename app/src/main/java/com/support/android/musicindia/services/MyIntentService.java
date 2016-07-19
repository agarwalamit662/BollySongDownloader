package com.support.android.musicindia.services;

/**
 * Created by amitagarwal3 on 12/9/2015.
 */

import com.support.android.musicindia.data.UserProvider;
import com.support.android.musicindia.dto.DTOProviderSONG;
import com.support.android.musicindia.helper.ConnectionDetector;
import com.support.android.musicindia.model.Songs;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyIntentService extends CheckIntentService {


    public static final String ACTION_MyIntentService = "com.support.android.musicindiadownloader.RESPONSE";
    public static final String ACTION_MyUpdate = "com.support.android.musicindiadownloader.UPDATE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    public static final String EXTRA_KEY_LENGTH = "EXTRA_KEY_LENGTH";
    String lengthCal;
    String msgFromActivity;
    String songName;
    String extraOut;
    Songs objSong;
    Songs testSongObj;

    private int songId;
    private boolean flag=true;
    static ConnectionDetector conDet;

    public MyIntentService() {
        super("com.support.android.musicindiadownloader.MyIntentService");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Bundle extras = intent.getExtras();

        conDet = new ConnectionDetector(getApplicationContext());
        int songCancelId = extras.getInt("songCanecelId");
        int songIncompleteId = extras.getInt("songIncompleteId");
        int songRetryDownloadId = extras.getInt("songRetryDownloadId");

        if(songCancelId != 0) {

            if(songCancelId == songId) {
                flag = false;
            }
            else {
                clearTask(songCancelId);
            }
        }

        if(songIncompleteId != 0) {
            if(songIncompleteId == songId) {
                Toast.makeText(getApplicationContext(),"Already Running In Downloads",Toast.LENGTH_SHORT).show();
                clearTask(songIncompleteId);

            }
            else{
                Toast.makeText(getApplicationContext(), "Song added to Downloads", Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put(UserProvider._SONG_DWNLD_RUNNING,1);
                DTOProviderSONG.updateIsRunningToOneinDatabase(getApplicationContext(), songIncompleteId, cv);

            }
        }

        if(songRetryDownloadId != 0) {
            if(songRetryDownloadId == songId) {
                Toast.makeText(getApplicationContext(),"Already Running In Downloads",Toast.LENGTH_SHORT).show();
                clearTask(songIncompleteId);

            }
            else{

                Toast.makeText(getApplicationContext(), "Song added to Downloads", Toast.LENGTH_SHORT).show();
                ContentValues cv = new ContentValues();
                cv.put(UserProvider._SONG_DWNLD_RUNNING,1);
                DTOProviderSONG.updateIsRunningToOneinDatabase(getApplicationContext(), songRetryDownloadId, cv);

            }
        }

        super.onStart(intent, startId);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        boolean tempFlag = true;
        lengthCal = intent.getStringExtra(EXTRA_KEY_LENGTH);
        msgFromActivity = intent.getStringExtra(EXTRA_KEY_IN);
        songName = intent.getStringExtra("songName");
        Bundle extras = intent.getExtras();
        songId = extras.getInt("songId");
        int isrunning = DTOProviderSONG.getIsRunningIteminDatabase(getApplicationContext(),songId);
        int iscompleted = DTOProviderSONG.getIsCompletedIteminDatabase(getApplicationContext(), songId);

        if (songId != 0){

        }
        extraOut = "Download Complete: " +  songName;

        int count;
        try {
            if(msgFromActivity != null && songName != null && songId != 0 && isrunning == 1 && iscompleted == 0) {

                int lenghtOfFileCal = 0;
                URL url2 = null;
                URL url1 = convertToURLEscapingIllegalCharacters(msgFromActivity);
                HttpURLConnection conn;
                conn = (HttpURLConnection) url1.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

                Long lenghtOfFile = Long.parseLong(conn.getHeaderField("Content-Length"))-128;
                double checkDone = lenghtOfFile;
                double fileLMB = lenghtOfFile;
                double mb = fileLMB/(1024*1024);
                String resultMB = String.format("%.2f", mb);
                InputStream input = conn.getInputStream();
                File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia"));
                if(!mydir.exists())
                    mydir.mkdirs();
                else
                    Log.d("error", "dir. already exists");

                File file = new File(mydir, songName + ".mp3");
                if(file.exists()){
                    file.delete();

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                    Uri uri = Uri.fromFile(mydir);
                    mediaScanIntent.setData(uri);
                    beforeFourFourIntent.setData(uri);

                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                    }
                    else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    {
                        sendBroadcast(mediaScanIntent);
                    }

                    file = new File(mydir, songName + ".mp3");
                }
                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                byte d;

                long total = 0;
                StringBuilder b = new StringBuilder();
                float filedwn = (float)((float)lenghtOfFile)/1024*8;
                while (checkDone > 0) {
                    if (flag) {
                        count = input.read(data,0,Math.min((int)checkDone, data.length));

                        total += count;
                        double compMB = total;
                        double compMBDW = compMB/(1024*1024);
                        String resultCOMPMB = String.format("%.2f", compMBDW);
                        Intent intentUpdate = new Intent();
                        intentUpdate.setAction(ACTION_MyUpdate);
                        intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                        intentUpdate.putExtra("songCanId", (int) songId);
                        intentUpdate.putExtra("songName",songName);
                        int prog = (int) ((total / (float) lenghtOfFile) * 100);
                        intentUpdate.putExtra(EXTRA_KEY_UPDATE, (int) ((total / (float) lenghtOfFile) * 100));
                        intentUpdate.putExtra("MBDOWNLOADED", resultCOMPMB+"MB / "+resultMB+"MB");
                        sendBroadcast(intentUpdate);

                        output.write(data, 0, count);
                        checkDone = checkDone - count;

                    } else {

                        tempFlag = false;
                        File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + songName + ".mp3");
                        if (f.exists()) {
                            f.delete();

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                            Uri uri = Uri.fromFile(mydir);
                            mediaScanIntent.setData(uri);
                            beforeFourFourIntent.setData(uri);

                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                            }
                            else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            {
                                sendBroadcast(mediaScanIntent);
                            }

                        }
                        Intent intentResponse = new Intent();
                        intentResponse.setAction(ACTION_MyIntentService);
                        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                        intentResponse.putExtra(EXTRA_KEY_OUT, "Download Cancelled");
                        sendBroadcast(intentResponse);
                        // DOWNLOAD CANCELLED FOR THIS FILE DELETE FROM DB
                        DTOProviderSONG.deleteSONGfromDatabase(getApplicationContext(), songId, songName);
                        break;
                    }
                    if(!conDet.isConnectingToInternet()){
                        flag = false;
                        break;
                    }
                }

                output.flush();
                output.close();
                input.close();

                if(!conDet.isConnectingToInternet() && tempFlag) {
                    flag = false;
                    //break;
                }

                if(total != lenghtOfFile && tempFlag){
                    flag = false;
                }

                if (flag && (total == lenghtOfFile) && tempFlag){
                    TagOptionSingleton.getInstance().setId3v2PaddingWillShorten(true);
                    AudioFile f = AudioFileIO.read(file);
                    Tag tag = f.getTag();
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    if(artist != null){
                        artist = artist.toUpperCase();
                        artist = artist.replace("SONGSMP3.COM", "");
                        artist = artist.replace("SONGSMP3.INFO","");
                        artist = artist.replace("SONGSMP3.NET","");
                        artist = getOnlyStrings(artist);
                        tag.setField(FieldKey.ARTIST, artist);

                    }
                    String album = tag.getFirst(FieldKey.ALBUM);
                    if(album != null){
                        album = album.toUpperCase();
                        album = album.replace("SONGSMP3.COM", "");
                        album = album.replace("SONGSMP3.INFO", "");
                        album = album.replace("SONGSMP3.NET", "");
                        album = getOnlyStrings(album);
                        tag.setField(FieldKey.ALBUM, album);

                    }
                    String title = tag.getFirst(FieldKey.TITLE);
                    if(title != null ) {
                        title = title.toUpperCase();
                        title = title.replace("SONGSMP3.COM", "");
                        title = title.replace("SONGSMP3.INFO", "");
                        title = title.replace("SONGSMP3.NET", "");
                        title = getOnlyStrings(title);
                        tag.setField(FieldKey.TITLE, title);


                    }
                    f.commit();
                    Intent intentResponse = new Intent();
                    intentResponse.setAction(ACTION_MyIntentService);
                    intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                    intentResponse.putExtra(EXTRA_KEY_OUT, extraOut);
                    intentResponse.putExtra("PATHFILE",file.getAbsolutePath());
                    sendBroadcast(intentResponse);


                    try {

                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                        Uri uri = Uri.fromFile(file);
                        mediaScanIntent.setData(uri);
                        beforeFourFourIntent.setData(uri);

                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            sendBroadcast(beforeFourFourIntent);
                            Log.e("Before Kitkat","In Kitkat");
                            Log.e("Before Kitkat", "In Kitkat");// only for gingerbread and newer versions
                        }
                        else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {

                            Log.e("In Kitkat","In Kitkat");
                            Log.e("In Kitkat","In Kitkat");
                            sendBroadcast(mediaScanIntent);

                        }
                        ContentValues cv = new ContentValues();
                        cv.put(UserProvider._SONG_DWNLD_COMPLETED,1);
                        cv.put(UserProvider._SONG_DWNLD_RUNNING,0);
                        // DOWNLOAD COMPLETED FOR THIS FILE DELETE FROM DB
                        DTOProviderSONG.updateSONGIteminDatabase(getApplicationContext(),songId,cv);
                        //DTOProviderSONG.deleteSONGfromDatabase(getApplicationContext(),songId,songName);


                    } catch(Exception e) {

                        File ff = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + songName + ".mp3");
                        if (ff.exists()) {

                            ff.delete();
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                            Uri uri = Uri.fromFile(mydir);
                            mediaScanIntent.setData(uri);
                            beforeFourFourIntent.setData(uri);

                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                Log.e("Before Kitkat","In Kitkat");
                                Log.e("Before Kitkat","In Kitkat");
                                sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                            }
                            else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                            {
                                Log.e("In Kitkat","In Kitkat");
                                Log.e("In Kitkat","In Kitkat");
                                sendBroadcast(mediaScanIntent);
                            }
                        }
                        Intent intentResponseCheck = new Intent();
                        intentResponseCheck.setAction(ACTION_MyIntentService);
                        intentResponseCheck.addCategory(Intent.CATEGORY_DEFAULT);
                        intentResponseCheck.putExtra(EXTRA_KEY_OUT, "Download Cancelled.. Problem in Network Connectivity");
                        sendBroadcast(intentResponseCheck);

                        ContentValues cv = new ContentValues();
                        cv.put(UserProvider._SONG_DWNLD_COMPLETED, 0);
                        cv.put(UserProvider._SONG_DWNLD_RUNNING, 0);
                        // DOWNLOAD COMPLETED FOR THIS FILE DELETE FROM DB
                        DTOProviderSONG.updateSONGIteminDatabase(getApplicationContext(), songId, cv);

                    }


                }
                else if(!flag && tempFlag){
                    flag = true;
                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + songName + ".mp3");
                    if (f.exists()) {

                        f.delete();
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                        Uri uri = Uri.fromFile(mydir);
                        mediaScanIntent.setData(uri);
                        beforeFourFourIntent.setData(uri);

                        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                        }
                        else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {
                            sendBroadcast(mediaScanIntent);
                        }
                    }
                    Intent intentResponseCheck = new Intent();
                    intentResponseCheck.setAction(ACTION_MyIntentService);
                    intentResponseCheck.addCategory(Intent.CATEGORY_DEFAULT);
                    intentResponseCheck.putExtra(EXTRA_KEY_OUT, "Download Cancelled.. Problem in Network Connectivity");
                    sendBroadcast(intentResponseCheck);

                    ContentValues cv = new ContentValues();
                    cv.put(UserProvider._SONG_DWNLD_COMPLETED, 0);
                    cv.put(UserProvider._SONG_DWNLD_RUNNING, 0);
                    // DOWNLOAD COMPLETED FOR THIS FILE DELETE FROM DB
                    DTOProviderSONG.updateSONGIteminDatabase(getApplicationContext(), songId, cv);


                }
                else if(!tempFlag){
                    flag = true;
                }
            }
        } catch (Exception e) {


            File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia/") + songName + ".mp3");
            if (f.exists()) {

                f.delete();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/MusicIndia"));
                Uri uri = Uri.fromFile(mydir);
                mediaScanIntent.setData(uri);
                beforeFourFourIntent.setData(uri);

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                }
                else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    sendBroadcast(mediaScanIntent);
                }
            }
            Intent intentResponseCheck = new Intent();
            intentResponseCheck.setAction(ACTION_MyIntentService);
            intentResponseCheck.addCategory(Intent.CATEGORY_DEFAULT);
            intentResponseCheck.putExtra(EXTRA_KEY_OUT, "Download Cancelled.. Problem in Network Connectivity");
            sendBroadcast(intentResponseCheck);

            ContentValues cv = new ContentValues();
            cv.put(UserProvider._SONG_DWNLD_COMPLETED, 0);
            cv.put(UserProvider._SONG_DWNLD_RUNNING, 0);
            // DOWNLOAD COMPLETED FOR THIS FILE DELETE FROM DB
            DTOProviderSONG.updateSONGIteminDatabase(getApplicationContext(), songId, cv);


        }


    }

    public URL convertToURLEscapingIllegalCharacters(String string){
        try {
            String decodedURL = string;

            URL url = new URL(decodedURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            decodedURL=uri.toASCIIString();
            return new URL(decodedURL);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        number = number.replaceAll("-","");
        return number;
    }


}
