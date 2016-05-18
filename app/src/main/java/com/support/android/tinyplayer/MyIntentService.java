package com.support.android.tinyplayer;

/**
 * Created by amitagarwal3 on 12/9/2015.
 */


import com.support.android.tinyplayer.model.Songs;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;


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


    public static final String ACTION_MyIntentService = "com.support.android.songsdownloader.RESPONSE";
    public static final String ACTION_MyUpdate = "com.support.android.songsdownloader.UPDATE";
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
        super("com.support.android.songsdownloader.MyIntentService");

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Bundle extras = intent.getExtras();

        conDet = new ConnectionDetector(getApplicationContext());
        int songCancelId = extras.getInt("songCanecelId");

        if(songCancelId != 0) {

            if(songCancelId == songId) {
                flag = false;
            }
            else {
                clearTask(songCancelId);
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



        if (songId != 0){

        }
        extraOut = "Download Complete: " +  songName;

        int count;
        try {
            if(msgFromActivity != null && songName != null && songId != 0 ) {


                int lenghtOfFileCal = 0;
                URL url2 = null;
                URL url1 = convertToURLEscapingIllegalCharacters(msgFromActivity);

                /*HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setConnectTimeout(2000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                urlConnection.setChunkedStreamingMode(100);
                urlConnection.connect();*/

                HttpURLConnection conn;
                conn = (HttpURLConnection) url1.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                Long lenghtOfFile = Long.parseLong(conn.getHeaderField("Content-Length"))-128;
                double checkDone = lenghtOfFile;
                double fileLMB = lenghtOfFile;
                double mb = fileLMB/(1024*1024);
                String resultMB = String.format("%.2f", mb);
                //int lenghtOfFile = urlConnection.getContentLength();
                /*if(lenghtOfFileCal != 0 && lengthCal != null)
                    lenghtOfFile = lenghtOfFileCal;*/

                InputStream input = conn.getInputStream();
                        //new BufferedInputStream(url1.openStream());


                //File mydir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
                File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader"));
                if(!mydir.exists())
                    mydir.mkdirs();
                else
                    Log.d("error", "dir. already exists");

                File file = new File(mydir, songName + ".mp3");
                if(file.exists()){
                    //getApplicationContext().getContentResolver().delete(Uri.fromFile(file),null,null);
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

                //System.out.println("downloading.............");
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
                        //intentUpdate.putExtra("objCancelId",testSongObj);
                        intentUpdate.putExtra("songName",songName);
                        int prog = (int) ((total / (float) lenghtOfFile) * 100);
                        intentUpdate.putExtra(EXTRA_KEY_UPDATE, (int) ((total / (float) lenghtOfFile) * 100));
                        //float dwn = (float)((float)total)/1024;
                        intentUpdate.putExtra("MBDOWNLOADED", resultCOMPMB+"MB / "+resultMB+"MB");
                        sendBroadcast(intentUpdate);

                        output.write(data, 0, count);
                        checkDone = checkDone - count;

                    } else {

                        tempFlag = false;
                        //File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader"));
                        File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader/") + songName + ".mp3");
                        if (f.exists()) {
                            //getApplicationContext().getContentResolver().delete(Uri.fromFile(f),null,null);
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

                    /*tag.deleteField(FieldKey.COVER_ART);
                    tag.deleteField(FieldKey.COMMENT);
                    tag.deleteField(FieldKey.GENRE);
                    tag.deleteField(FieldKey.COMPOSER);
                    tag.deleteField(FieldKey.COUNTRY);
                    tag.deleteField(FieldKey.ENCODER);
                    tag.deleteField(FieldKey.ALBUM_ARTIST);*/

                    String artist = tag.getFirst(FieldKey.ARTIST);
                    if(artist != null){
                        artist = artist.toUpperCase();
                        artist = artist.replace("SONGSMP3.COM","");
                        /*artist = artist.replace("SongsMp3.Com", "");
                        artist = artist.replace("SongsMp3.CoM", "");
                        artist = artist.replace("Songsmp3.com", "");
                        artist = artist.replace("Songsmp3.CoM", "");
                        artist = artist.replace("Songsmp3.Com", "");*/
                        artist = getOnlyStrings(artist);
                        tag.setField(FieldKey.ARTIST, artist);

                    }
                    String album = tag.getFirst(FieldKey.ALBUM);
                    if(album != null){
                        album = album.toUpperCase();
                        album = album.replace("SONGSMP3.COM", "");
                        /*album = album.replace("SongsMp3.Com","");
                        album = album.replace("SongsMp3.CoM", "");
                        album = album.replace("Songsmp3.com", "");
                        album = album.replace("Songsmp3.CoM", "");
                        album = album.replace("Songsmp3.Com", "");*/
                        album = getOnlyStrings(album);
                        tag.setField(FieldKey.ALBUM, album);

                    }
                    String title = tag.getFirst(FieldKey.TITLE);
                    if(title != null ) {
                        title = title.toUpperCase();
                        title = title.replace("SONGSMP3.COM", "");
                        /*title = title.replace("SongsMp3.Com", "");
                        title = title.replace("SongsMp3.CoM", "");
                        title = title.replace("Songsmp3.com", "");
                        title = title.replace("Songsmp3.CoM", "");
                        title = title.replace("Songsmp3.Com", "");*/
                        title = getOnlyStrings(title);
                        tag.setField(FieldKey.TITLE, title);


                    }
                    tag.setField(FieldKey.ALBUM_ARTIST,"");
                    tag.setField(FieldKey.COUNTRY,"");
                    tag.setField(FieldKey.YEAR, "");
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
                            sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                        }
                        else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {

                            sendBroadcast(mediaScanIntent);

                        }



                    } catch(Exception e) {

                        File ff = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader/") + songName + ".mp3");
                        if (ff.exists()) {
                           // getApplicationContext().getContentResolver().delete(Uri.fromFile(f),null,null);
                            ff.delete();
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

                    }


                }
                else if(!flag && tempFlag){
                    flag = true;
                    File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader/") + songName + ".mp3");
                    if (f.exists()) {
                        //getApplicationContext().getContentResolver().delete(Uri.fromFile(f),null,null);
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


                }
                else if(!tempFlag){
                    flag = true;
                }
            }
        } catch (Exception e) {


            File f = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader/") + songName + ".mp3");
            if (f.exists()) {
              //  getApplicationContext().getContentResolver().delete(Uri.fromFile(f),null,null);
                f.delete();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                File mydir = new File(String.valueOf(Environment.getExternalStorageDirectory()+"/SongsDownloader"));
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


        }


    }

    public URL convertToURLEscapingIllegalCharacters(String string){
        try {
            String decodedURL = string;
            //URLDecoder.decode(string, "UTF-8");
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
