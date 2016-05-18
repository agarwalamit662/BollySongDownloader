package com.support.android.tinyplayer;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.support.android.tinyplayer.model.PlayableItem;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity{



    public static boolean splashLoadedTiny = true;

    private static int positionRe=0;
    private MusicPlayerApplication app;
    private DrawerLayout mDrawerLayout;
    public static ViewPager viewPager;
    private TabLayout tabLayout;
    private Context context;
    Adapter adapter;// = new Adapter(getSupportFragmentManager());
    private NavigationView navigationView;
    public MainActivity ma;



    @Override
    public void onStart() {
        super.onStart();




        /*bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);*/


    }




    @Override
    protected void onResume() {


        super.onResume();
        bindService(BaseActivity.serviceIntent, BaseActivity.musicConnection, Context.BIND_AUTO_CREATE);
        if(navigationView != null){
            navigationView.setCheckedItem(R.id.nav_files);
        }

    }


    public void playItem(PlayableItem item) {
        super.playItem(item);
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



    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            super.onNewIntent(intent);
            setIntent(intent);
        }
        //now getIntent() should always return the last received intent
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy", "In on destroy");
       // unbindService(BaseActivity.musicConnection);
        if(BaseActivity.musicService != null  && !BaseActivity.musicService.isPlaying()){
            // musicService.onDestroy();
            Log.e("IN onDestroy IF","IN onDestroy IF");
            Log.e("IN onDestroy IF","IN onDestroy IF");
            BaseActivity.musicService.destroyService();
            BaseActivity.musicService = null;
            BaseActivity.musicConnection = null;


        }

        /*SharedPreferences settings= getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        //boolean firstRun=settings.getBoolean("firstRun",false);

        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("firstRun",true);
        editor.commit();

        Log.e("In on destroy", "In on destroy");
        Log.e("In on destroy","In on destroy");*/


    }



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
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



        if (viewPager != null) {
            setupViewPager(viewPager,"Files");
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = tabLayout.getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int deviceWidth = metrics.widthPixels;

                if (tabLayoutWidth < deviceWidth) {
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else {
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String call = extras.getString("CALLMETHOD");
            String value = extras.getString("EXTRA_PAGE");
            if(value != null){
                int position= Integer.parseInt(value );

                selectFragment(position);
            }
            /*if(call != null){
                MainActivity m = (MainActivity) this;
                m.update();
            }*/
        }



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
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu);




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this,SplashScreenActivity.class);

                if(BaseActivity.musicService != null ){
                    // musicService.onDestroy();
                    BaseActivity.musicService.destroyService();
                    BaseActivity.musicService = null;
                   // BaseActivity.musicConnection = null;


                }
                i.putExtra("splashflag",false);
                startActivity(i);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager viewPager, String caseNumber) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new Adapter(getSupportFragmentManager());

        if(caseNumber.equals("Files")){

            if(!splashLoadedTiny){

                adapter.addFragment(new TiledListFragment(),"Movies",0);
                adapter.addFragment(new PopSongsFragment(),"Pop Songs",1);
                adapter.addFragment(new TiledListFragmentPunjabi(),"Punjabi",2);
                adapter.addFragment(new TiledListFragmentLyrics(), "Lyrics", 3);
                adapter.addFragment(new MusicPlayerFragments(),"Tracks",4);
                adapter.addFragment(new DownloadListFragment(), "Downloads", 5);


            }
            else{

                adapter.addFragment(new MusicPlayerFragments(),"Tracks",0);
                adapter.addFragment(new DownloadListFragment(), "Downloads", 1);
            }



        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);


        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                                               /*Log.e("here in onPageSelected","onPageSelected");
                                               Log.e("here in onPageSelected","onPageSelected");*/
                if (adapter.getItem(position) instanceof MusicPlayerFragments) {
                    MusicPlayerFragments mpf = (MusicPlayerFragments) adapter.getItem(position);
                    mpf.onResume();
                }
                else if(adapter.getItem(position) instanceof DownloadListFragment){
                                                   DownloadListFragment dlf = (DownloadListFragment) adapter.getItem(position);
                                                   dlf.onResume();
                }


            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = tabLayout.getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int deviceWidth = metrics.widthPixels;

                if (tabLayoutWidth < deviceWidth) {
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else {
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });

    }

    public void update()
    {
        adapter.notifyDataSetChanged();
    }


    public void selectFragment(int position){
        viewPager.setCurrentItem(position, true);

    }

    public void change()
    {
        if (!(adapter == null)) {

            adapter.notifyDataSetChanged();


        }
    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        viewPager = (ViewPager) findViewById(R.id.viewpager);



                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_files:
                            {
                                setupViewPager(viewPager,"Files");
                                return true;
                            }
                            case R.id.nav_aboutus:
                            {
                                Intent i = new Intent(getBaseContext(),AboutMeActitvity.class);
                                startActivity(i);
                                return true;
                            }


                            default:
                                return true;

                        }

                    };
                });
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private  final List<Fragment> mFragments = new ArrayList<>();
        private  final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);


        }

        public void removeAllFragments()
        {
            mFragments.clear();

        }
        public void addFragment(Fragment fragment, String title,int index) {
            //mFragments.add(index);
            if(mFragments.size() == 0 )
            {
                mFragments.add(index, fragment);
                mFragmentTitles.add(index, title);
            }
            else if( index > (mFragments.size()-1)  ) {
                mFragments.add(index, fragment);
                mFragmentTitles.add(index, title);
            }
            else if(index <= (mFragments.size()-1)){
                mFragments.set(index, fragment);
                mFragmentTitles.set(index, title);
            }
        }

        @Override
        public int getItemPosition(Object object) {

            if(object instanceof DownloadListFragment){

                ((DownloadListFragment) object).update();
            }
            if(object instanceof MusicPlayerFragments){

                ((MusicPlayerFragments) object).update();
            }
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}