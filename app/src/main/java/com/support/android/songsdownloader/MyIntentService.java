package com.support.android.songsdownloader;

/**
 * Created by amitagarwal3 on 12/9/2015.
 */
import android.app.IntentService;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.RetryPolicy;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MyIntentService extends CheckIntentService {

    public static final String ACTION_MyIntentService = "com.support.android.songsdownloader.RESPONSE";
    public static final String ACTION_MyUpdate = "com.support.android.songsdownloader.UPDATE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    String msgFromActivity;
    String songName;
    String extraOut;
    Thread t;
    private int songId;
    private boolean flag=true;


    private static ThinDownloadManager downloadManager;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 1;
    private static RetryPolicy retryPolicy;
    public MyIntentService() {
        super("com.support.android.songsdownloader.MyIntentService");
        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
        retryPolicy = new DefaultRetryPolicy();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Bundle extras = intent.getExtras();

        int songCancelId = extras.getInt("songCanecelId");
        if(songCancelId != 0) {
            //Login clears messages in the queue
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

        //get input
        msgFromActivity = intent.getStringExtra(EXTRA_KEY_IN);
        songName = intent.getStringExtra("songName");
        Bundle extras = intent.getExtras();
        songId = extras.getInt("songId");
        if (songId != 0){
            Log.e("songId ki value hai", String.valueOf(songId));
            Log.e("songId ki value hai", String.valueOf(songId));
            Log.e("songId ki value hai", String.valueOf(songId));
        }
        extraOut = "Download Complete: " +  songName;

        int count;
        try {
            if(msgFromActivity != null && songName != null && songId != 0 ) {
                URL url1 = new URL(msgFromActivity);
               // HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
              //  urlConnection.setRequestMethod("GET");
              //  urlConnection.setDoOutput(true);
              //  urlConnection.setChunkedStreamingMode(100);
              //  urlConnection.connect();
              //  final String contentLengthStr=urlConnection.getHeaderField("content-length");
             //   Log.e("content Lenght Str ",contentLengthStr);
             //   Log.e("content Lenght Str ",contentLengthStr);
             //   Log.e("content Lenght Str ",contentLengthStr);


                URLConnection conexion = url1.openConnection();
                //conexion.setRequestProperty("GET");
               // conexion.setRequestProperty("Accept-Encoding", "identity");
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url1.openStream());
                OutputStream output = new FileOutputStream("/sdcard/SongsDownloader/" + songName + ".mp3");
                byte data[] = new byte[1024];
                long total = 0;
                System.out.println("downloading.............");

                while ((count = input.read(data)) != -1) {
                    if (flag) {
                        total += count;
                        Intent intentUpdate = new Intent();
                        intentUpdate.setAction(ACTION_MyUpdate);
                        intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                        intentUpdate.putExtra("songCanId", (int) songId);
                        intentUpdate.putExtra("songName",songName);
                        int prog = (int) ((total / (float) lenghtOfFile) * 100);
                        intentUpdate.putExtra(EXTRA_KEY_UPDATE, (int) ((total / (float) lenghtOfFile) * 100));
                       // intentUpdate.putExtra("progressSong",lenghtOfFile);
                       // intentUpdate.putExtra("totalProgress",total);
                        sendBroadcast(intentUpdate);
                        //publishProgress((int)((total/(float)lenghtOfFile)*100));
                        output.write(data, 0, count);
                    } else {
                        File f = new File("/sdcard/SongsDownloader/" + songName + ".mp3");
                        if (f.exists()) {
                            f.delete();
                        }
                        Intent intentResponse = new Intent();
                        intentResponse.setAction(ACTION_MyIntentService);
                        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                        intentResponse.putExtra(EXTRA_KEY_OUT, "Download Cancelled");
                        sendBroadcast(intentResponse);

                        break;

                    }
                }
                output.flush();
                output.close();
                input.close();
                if (flag){
                    Intent intentResponse = new Intent();
                    intentResponse.setAction(ACTION_MyIntentService);
                    intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                    intentResponse.putExtra(EXTRA_KEY_OUT, extraOut);
                    sendBroadcast(intentResponse);
                }
                else{
                    flag = true;
                }
            }
        } catch (Exception e) {
        }


    }

    class MyDownloadStatusListener implements DownloadStatusListener {

        int position;
        int[] progress;


        @Override
        public void onDownloadComplete(int id) {

            Intent intentResponse = new Intent();
            intentResponse.setAction(ACTION_MyIntentService);
            intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
            intentResponse.putExtra(EXTRA_KEY_OUT, extraOut);
            sendBroadcast(intentResponse);
            t.stop();
        }

        @Override
        public void onDownloadFailed(int id, int errorCode, String errorMessage) {

        }

        @Override
        public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

            //send update
            Intent intentUpdate = new Intent();
            intentUpdate.setAction(ACTION_MyUpdate);
            intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
            intentUpdate.putExtra(EXTRA_KEY_UPDATE, progress);
            sendBroadcast(intentUpdate);

             }
    }
}
