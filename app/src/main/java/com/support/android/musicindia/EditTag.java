package com.support.android.musicindia;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTag extends BaseActivity {
    private AudioFile f;
    private String title;
    private String artist;
    private EditText editTitle;
    private EditText editArtist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);
        editTitle =(EditText) findViewById(R.id.editTitle);
        editArtist =(EditText) findViewById(R.id.editArtist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Music Tags");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String value = extras.getString("FileName");
            if(value != null){


                try {
                    final File file = new File(value);
                    f = AudioFileIO.read(file);
                    final Tag tag = f.getTag();

                    editTitle.setText(tag.getFirst(FieldKey.TITLE));
                    editArtist.setText(tag.getFirst(FieldKey.ARTIST));

                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(editTitle.getText().toString().length() == 0 || editArtist.getText().toString().length() == 0){
                                Snackbar.make(view, "Please fill at least 1 character", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            else {
                                try {
                                    tag.setField(FieldKey.TITLE, editTitle.getText().toString());
                                    tag.setField(FieldKey.ARTIST, editArtist.getText().toString());

                                    ContentValues localContentValues = new ContentValues();
                                    ContentResolver localContentResolver = getContentResolver();
                                    Uri localUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                    localContentValues.put("title",editTitle.getText().toString() );

                                    localContentValues.put("artist", editArtist.getText().toString());
                                    localContentResolver.update(localUri, localContentValues, "_data LIKE \"" + file.getAbsolutePath() + "\"", null);

                                    f.commit();
                                    Snackbar.make(view, "Saved Successfully", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Intent beforeFourFourIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                                    Uri uri = Uri.fromFile(new File(value));
                                    mediaScanIntent.setData(uri);
                                    beforeFourFourIntent.setData(uri);

                                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                        sendBroadcast(beforeFourFourIntent);    // only for gingerbread and newer versions
                                    }
                                    else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                    {
                                        sendBroadcast(mediaScanIntent);
                                    }
                                    /*Intent intent=new Intent();
                                    intent.putExtra("MESSAGE","SAVED");

                                    setResult(2,intent);*/

                                    finish();

                                } catch (FieldDataInvalidException e) {
                                    e.printStackTrace();
                                } catch (CannotWriteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TagException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                }


            }
        }

        textViewArtistTandI = (TextView)findViewById(R.id.textViewArtistTandI);
        imageViewSongImageTandI = (CircleImageView)findViewById(R.id.songurl);
        textViewArtistTandI.setOnClickListener(this);
        imageViewSongImageTandI.setOnClickListener(this);

        /*textViewArtist = (TextView)findViewById(R.id.textViewArtist);
        textViewTitle = (TextView)findViewById(R.id.textViewTitle);
        textViewTime = (TextView)findViewById(R.id.textViewTime);
        imageViewSongImage = (ImageView)findViewById(R.id.imageViewSongImage);
        imageButtonPrevious = (ImageButton)findViewById(R.id.imageButtonPrevious);
        imageButtonPlayPause = (CheckableImageButton)findViewById(R.id.imageButtonPlayPause);
        imageButtonNext = (ImageButton)findViewById(R.id.imageButtonNext);
        seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
        imageButtonShowSeekbar2 = (ImageButton)findViewById(R.id.imageButtonShowSeekbar2);

        imageButtonShowSeekbar2.setOnClickListener(this);
        imageButtonPrevious.setOnClickListener(this);
        imageButtonPlayPause.setOnClickListener(this);
        imageButtonNext.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar1.setClickable(false);
        seekBar2.setOnSeekBarChangeListener(this);
        textViewTime.setOnClickListener(this);*/


    }

    @Override
    public void onClick(View view) {

        /*if (view.equals(imageButtonPlayPause)) {
            imageButtonPlayPauseOnClick();

        } else if(view.equals(imageButtonNext)) {
            imageButtonNextOnClick();
        } else if(view.equals(imageButtonPrevious))  {
            imageButtonPreviousOnClick();
        } else if(view.equals(textViewTime)) {
            textViewTimeOnClick();
        } else if(view.equals(imageButtonShowSeekbar2)) {
            imageButtonShowSeekbar2OnClick();
        }*/

        if(view.equals(textViewArtistTandI)){
            Intent i = new Intent(this,NowPlaying.class);
            startActivity(i);
        }
        else if(view.equals(imageViewSongImageTandI)){

            Intent i = new Intent(this,NowPlaying.class);
            startActivity(i);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /*bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);*/


    }




    @Override
    protected void onResume() {


        super.onResume();
        bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(BaseActivity.musicConnection);
    }

    @Override
    public void onStop() {
        super.onStop();

        /*unbindService(BaseActivity.musicConnection);*/



    }


    /*@Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy", "In on destroy");
        //  unbindService(BaseActivity.musicConnection);
        if(BaseActivity.musicService != null  && !BaseActivity.musicService.isPlaying()){
            // musicService.onDestroy();
            Log.e("IN onDestroy IF","IN onDestroy IF");
            Log.e("IN onDestroy IF","IN onDestroy IF");
            BaseActivity.musicService.destroyService();
            BaseActivity.musicService = null;
            BaseActivity.musicConnection = null;


        }

        *//*SharedPreferences settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //boolean firstRun=settings.getBoolean("firstRun",false);

        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("firstRun",true);
        editor.commit();

        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy","In on destroy");*//*


    }*/

}
