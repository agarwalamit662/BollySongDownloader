package com.support.android.musicindia.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.support.android.musicindia.services.MyIntentService;

import java.util.HashMap;

public class UserProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.support.android.musicindia.data";


    static final String URL_SONG = "content://" + PROVIDER_NAME + "/SONG";
    public static final Uri CONTENT_URI_SONG = Uri.parse(URL_SONG);

    public static final String _SONGID = "_id";
    public static final String _SONG_NAME = "songName";
    public static final String _SONG_URL = "songUrl";
    public static final String _SONG_DWNLD_COMPLETED = "completed";
    public static final String _SONG_DWNLD_RUNNING = "running";

    private static HashMap<String, String> USER_SONG_PROJECTION_MAP;

    static final int USER_SONG  = 1;
    static final int USER_SONG_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PROVIDER_NAME, "SONG", USER_SONG);
        uriMatcher.addURI(PROVIDER_NAME, "SONG/#", USER_SONG_ID);


    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;

    public static final String DATABASE_NAME = "SONGDATABASE";
    public static final String USER_SONG_TABLE_NAME = "SONG";
    static final int DATABASE_VERSION = 1;

    static final String CREATE_DB_TABLE_SONG =
            " CREATE TABLE " + USER_SONG_TABLE_NAME +
                    " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " songName TEXT NOT NULL, " +
                    " songUrl TEXT NOT NULL, " +
                    " completed INTEGER NOT NULL, " +
                    " running INTEGER NOT NULL); ";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE_SONG);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  USER_SONG_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {

            case USER_SONG: {
                long rowID = db.insert(USER_SONG_TABLE_NAME, "", values);

                /**
                 * If record is added successfully
                 */

                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_SONG, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }

                throw new SQLException("Failed to add a record into " + uri);
                //break;
            }
            default:{
               // return _uri;
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

       // return _uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //qb.setTables(USER_TABLE_NAME);

        switch (uriMatcher.match(uri)) {

            case USER_SONG:
                qb.setTables(USER_SONG_TABLE_NAME);
                qb.setProjectionMap(USER_SONG_PROJECTION_MAP);
                break;

            case USER_SONG_ID:
                qb.setTables(USER_SONG_TABLE_NAME);
                qb.appendWhere( _SONGID + "=" + uri.getPathSegments().get(1));
                break;


            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){

            case USER_SONG:
                count = db.delete(USER_SONG_TABLE_NAME, selection, selectionArgs);
                break;

            case USER_SONG_ID:
                String mid = uri.getPathSegments().get(1);
                count = db.delete( USER_SONG_TABLE_NAME, _SONGID +  " = " + mid +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;



            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){

            case USER_SONG: {
                count = db.update(USER_SONG_TABLE_NAME, values, selection, selectionArgs);

                break;
            }
            case USER_SONG_ID:
                count = db.update(USER_SONG_TABLE_NAME, values, _SONGID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){

            case USER_SONG:
                // return "vnd.android.cursor.dir/vnd.example.students";
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "SONG";
            case USER_SONG_ID:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PROVIDER_NAME + "/" + "SONG";


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}