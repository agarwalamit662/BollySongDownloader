/*
 * Copyright 2012-2013 Andrea De Cesare
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.support.android.musicindia.dto;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.support.android.musicindia.data.UserProvider;
import com.support.android.musicindia.model.SONG;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class DTOProviderSONG {

    private final static String[] projection = {
            UserProvider._SONGID,
            UserProvider._SONG_NAME,
            UserProvider._SONG_URL,
            UserProvider._SONG_DWNLD_COMPLETED,
            UserProvider._SONG_DWNLD_RUNNING
    };


    private final static String[] projectionMaxId = {
            "MAX("+UserProvider._SONGID+")"
    };

    private final static String[] projectionIsRunning = {
            UserProvider._SONG_DWNLD_RUNNING
    };

    private final static String[] projectionIsCompleted = {
            UserProvider._SONG_DWNLD_COMPLETED
    };

    private static ContentResolver contentResolver;

    public static ArrayList<SONG> getSONGListinDatabase(Context mContext) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;

        String where = UserProvider._SONG_DWNLD_COMPLETED + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(0)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projection, where, selectionArgs, sortOrder);

        int songidindex = cursor.getColumnIndex(UserProvider._SONGID);
        int songnametextindex = cursor.getColumnIndex(UserProvider._SONG_NAME);
        int songurlindex = cursor.getColumnIndex(UserProvider._SONG_URL);
        int songcomindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_COMPLETED);
        int songrunningindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_RUNNING);


        if (cursor != null && cursor.moveToFirst()) {
            do {

                int songid = cursor.getInt(songidindex);
                String sname = cursor.getString(songnametextindex);
                String url = cursor.getString(songurlindex);
                int completed = cursor.getInt(songcomindex);
                int running = cursor.getInt(songrunningindex);
                songList.add(new SONG(songid, sname, url,completed,running));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return songList;
    }

    public static ArrayList<SONG> getSONGIteminDatabase(Context mContext,int sid,String songname) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;

        String where = UserProvider._SONGID + " =? AND "+UserProvider._SONG_NAME + " =? AND "
                +UserProvider._SONG_DWNLD_COMPLETED +" =? AND "+UserProvider._SONG_DWNLD_RUNNING + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(sid),songname,String.valueOf(0),String.valueOf(0)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projection, where, selectionArgs, sortOrder);

        int songidindex = cursor.getColumnIndex(UserProvider._SONGID);
        int songnameindex = cursor.getColumnIndex(UserProvider._SONG_NAME);
        int songurlindex = cursor.getColumnIndex(UserProvider._SONG_URL);
        int songcomindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_COMPLETED);
        int songrunningindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_RUNNING);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int songid = cursor.getInt(songidindex);
                String sname = cursor.getString(songnameindex);
                String url = cursor.getString(songurlindex);
                int completed = cursor.getInt(songcomindex);
                int running = cursor.getInt(songrunningindex);
                songList.add(new SONG(songid, sname, url,completed,running));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return songList;
    }

    public static ArrayList<SONG> getInCompletedSONGIteminDatabase(Context mContext) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;

        String where = UserProvider._SONG_DWNLD_COMPLETED +" =? AND "
                +UserProvider._SONG_DWNLD_RUNNING + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(0),String.valueOf(0)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projection, where, selectionArgs, sortOrder);

        int songidindex = cursor.getColumnIndex(UserProvider._SONGID);
        int songnameindex = cursor.getColumnIndex(UserProvider._SONG_NAME);
        int songurlindex = cursor.getColumnIndex(UserProvider._SONG_URL);
        int songcomindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_COMPLETED);
        int songrunningindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_RUNNING);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int songid = cursor.getInt(songidindex);
                String sname = cursor.getString(songnameindex);
                String url = cursor.getString(songurlindex);
                int completed = cursor.getInt(songcomindex);
                int running = cursor.getInt(songrunningindex);
                songList.add(new SONG(songid, sname, url,completed,running));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return songList;
    }


    public static ArrayList<SONG> getNewSONGIteminDatabase(Context mContext,int sid,String songname) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;

        String where = UserProvider._SONGID + " =? AND "+UserProvider._SONG_NAME + " =? ";


        String[] selectionArgs = new String[]{String.valueOf(sid),songname};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projection, where, selectionArgs, sortOrder);

        int songidindex = cursor.getColumnIndex(UserProvider._SONGID);
        int songnameindex = cursor.getColumnIndex(UserProvider._SONG_NAME);
        int songurlindex = cursor.getColumnIndex(UserProvider._SONG_URL);
        int songcomindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_COMPLETED);
        int songrunningindex = cursor.getColumnIndex(UserProvider._SONG_DWNLD_RUNNING);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int songid = cursor.getInt(songidindex);
                String sname = cursor.getString(songnameindex);
                String url = cursor.getString(songurlindex);
                int completed = cursor.getInt(songcomindex);
                int running = cursor.getInt(songrunningindex);
                songList.add(new SONG(songid, sname, url,completed,running));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return songList;
    }

    public static int getMAXSONGIteminDatabase(Context mContext) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;
        int maxValue = 0;
        ContentResolver resolver = contexts.getContentResolver();

        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projectionMaxId, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int songid = cursor.getInt(0);
                maxValue =songid;

            } while (cursor.moveToNext());
        }
        cursor.close();

        return maxValue;
    }

    public static int getIsRunningIteminDatabase(Context mContext,int id) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;
        int isrunning = 0;
        String where = UserProvider._SONGID + " =? ";
        String[] selArgs = new String[]{String.valueOf(id)};
        ContentResolver resolver = contexts.getContentResolver();

        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projectionIsRunning, where, selArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int running = cursor.getInt(0);
                isrunning =running;

            } while (cursor.moveToNext());
        }
        cursor.close();

        return isrunning;
    }

    public static int getIsCompletedIteminDatabase(Context mContext,int id) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;
        int iscompleted = 0;
        String where = UserProvider._SONGID + " =? ";
        String[] selArgs = new String[]{String.valueOf(id)};
        ContentResolver resolver = contexts.getContentResolver();

        Uri uri = UserProvider.CONTENT_URI_SONG;

        Cursor cursor = resolver.query(uri, projectionIsCompleted, where, selArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int completed = cursor.getInt(0);
                iscompleted =completed;

            } while (cursor.moveToNext());
        }
        cursor.close();

        return iscompleted;
    }

    public static void insertSONGIteminDatabase(Context mContext,ContentValues values) {

        Context contexts = mContext;
        ContentResolver resolver = contexts.getContentResolver();
        Uri uri = UserProvider.CONTENT_URI_SONG;

        Uri uris;
        try{

            uris = resolver.insert(uri, values);
        }
        catch (Exception e){
            Log.e("Exception Updating",e.toString());

        }


    }

    public static int updateSONGIteminDatabase(Context mContext,int id,ContentValues values) {

        Context contexts = mContext;

        String where = UserProvider._SONGID + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(id)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        int recUpdated=0;
        try{
            recUpdated = resolver.update(uri, values, where, selectionArgs);
        }
        catch (Exception e){
            Log.e("Exception Updating",e.toString());

        }

        return recUpdated;
    }


    public static int updateALLRUNNINGSONGIteminDatabase(Context mContext) {

        Context contexts = mContext;

        String where = " 1 ";
        ContentValues cv = new ContentValues();
        cv.put(UserProvider._SONG_DWNLD_RUNNING,0);
        //String[] selectionArgs = new String[]{String.valueOf(id)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        int recUpdated=0;
        try{
            recUpdated = resolver.update(uri, cv, where, null);
        }
        catch (Exception e){
            Log.e("Exception Updating",e.toString());

        }

        return recUpdated;
    }


    public static int updateIsRunningToOneinDatabase(Context mContext,int id,ContentValues values) {

        Context contexts = mContext;

        String where = UserProvider._SONGID + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(id)};

        ContentResolver resolver = contexts.getContentResolver();

        String sortOrder = UserProvider._SONGID + " DESC ";
        Uri uri = UserProvider.CONTENT_URI_SONG;

        int recUpdated=0;
        try{
            recUpdated = resolver.update(uri, values, where, selectionArgs);
        }
        catch (Exception e){
            Log.e("Exception Updating",e.toString());

        }

        return recUpdated;
    }

    public static ContentValues getContentValues(SONG song){

        ContentValues values = new ContentValues();

        values.put(UserProvider._SONGID,song.getSongId());
        values.put(UserProvider._SONG_NAME,song.getSongName());
        values.put(UserProvider._SONG_URL,song.getSongUrl());
        values.put(UserProvider._SONG_DWNLD_COMPLETED,song.getCompleted());
        values.put(UserProvider._SONG_DWNLD_RUNNING,song.getRunning());
        return values;

    }

    public static ContentValues putContentValues(SONG song){

        ContentValues values = new ContentValues();

        values.put(UserProvider._SONGID,song.getSongId());
        values.put(UserProvider._SONG_NAME,song.getSongName());
        values.put(UserProvider._SONG_URL,song.getSongUrl());
        values.put(UserProvider._SONG_DWNLD_COMPLETED,song.getCompleted());
        values.put(UserProvider._SONG_DWNLD_RUNNING,song.getRunning());
        return values;

    }

    public static int deleteSONGfromDatabase(Context mContext,int id,String songname) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;

        String where = UserProvider._SONGID + " =? AND "+UserProvider._SONG_NAME + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(id),songname};

        ContentResolver resolver = contexts.getContentResolver();


        Uri uri = UserProvider.CONTENT_URI_SONG;

        int deleted = 0;
        try{
            deleted = resolver.delete(uri, where, selectionArgs);
        }
        catch (Exception e){
            Log.e("Error Deleting",e.toString());
        }
        return deleted;

    }

    public static int deleteCompletedSongsfromDatabase(Context mContext) {
        ArrayList<SONG> songList = new ArrayList<>();
        Context contexts = mContext;

        String where = UserProvider._SONG_DWNLD_COMPLETED + " =? ";

        String[] selectionArgs = new String[]{String.valueOf(1)};

        ContentResolver resolver = contexts.getContentResolver();


        Uri uri = UserProvider.CONTENT_URI_SONG;

        int deleted = 0;
        try{
            deleted = resolver.delete(uri, where, selectionArgs);
        }
        catch (Exception e){
            Log.e("Error Deleting",e.toString());
        }
        return deleted;

    }


}

