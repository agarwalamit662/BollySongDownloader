package com.support.android.musicindia.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.support.android.musicindia.fragments.DownloadListFragment;
import com.support.android.musicindia.application.MusicPlayerApplication;
import com.support.android.musicindia.fragments.IncompleteDownloadListFragment;
import com.support.android.musicindia.fragments.MusicPlayerFragments;
import com.support.android.musicindia.fragments.PopSongsFragment;
import com.support.android.musicindia.R;
import com.support.android.musicindia.fragments.TiledListFragment;
import com.support.android.musicindia.fragments.TiledListFragmentLyrics;
import com.support.android.musicindia.fragments.TiledListFragmentPunjabi;
import com.support.android.musicindia.model.PlayableItem;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

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
        if(musicConnection != null)
        unbindService(BaseActivity.musicConnection);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            super.onNewIntent(intent);
            setIntent(intent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(BaseActivity.musicService != null  && !BaseActivity.musicService.isPlaying()){
            BaseActivity.musicService.destroyService();
            BaseActivity.musicService = null;
            BaseActivity.musicConnection = null;
        }
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

            boolean splashLoadedTinyMainActivity = extras.getBoolean("splashLoadedTinyMainActivity");
            MusicPlayerApplication.splashLoadedTinyMainActivity = splashLoadedTinyMainActivity;

        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("sharelink", SplashScreenActivity.sharelink);
        savedInstanceState.putString("updatelink", SplashScreenActivity.updatelink);
        savedInstanceState.putInt("version", SplashScreenActivity.versionCode);
        savedInstanceState.putBoolean("splashTinyMain", MusicPlayerApplication.splashLoadedTinyMainActivity);
        savedInstanceState.putBoolean("splashTinySplash",MusicPlayerApplication.splashLoadedTiny);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        SplashScreenActivity.sharelink = savedInstanceState.getString("sharelink");
        SplashScreenActivity.updatelink = savedInstanceState.getString("updatelink");
        SplashScreenActivity.versionCode = savedInstanceState.getInt("version");
        MusicPlayerApplication.splashLoadedTinyMainActivity = savedInstanceState.getBoolean("splashTinyMain");
        MusicPlayerApplication.splashLoadedTiny = savedInstanceState.getBoolean("splashTinySplash");
    }

    @Override
    public void onClick(View view) {

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

                    BaseActivity.musicService.destroyService();
                    BaseActivity.musicService = null;

                }
                i.putExtra("splashflag","false");
                i.putExtra("splashLoadedTinyMainActivity",String.valueOf(MusicPlayerApplication.splashLoadedTinyMainActivity));
                startActivity(i);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager viewPager, String caseNumber) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new Adapter(getSupportFragmentManager());

        if(caseNumber.equals("Files")){
            if(!MusicPlayerApplication.splashLoadedTinyMainActivity){

                adapter.addFragment(new TiledListFragment(),"Movies",0);
                adapter.addFragment(new PopSongsFragment(),"Pop Songs",1);
                adapter.addFragment(new TiledListFragmentPunjabi(),"Punjabi",2);
                adapter.addFragment(new TiledListFragmentLyrics(), "Lyrics", 3);
                adapter.addFragment(new MusicPlayerFragments(),"Tracks",4);
                adapter.addFragment(new DownloadListFragment(), "Downloads", 5);
                adapter.addFragment(new IncompleteDownloadListFragment(), "Incomplete", 6);

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
                if (adapter.getItem(position) instanceof MusicPlayerFragments) {
                    MusicPlayerFragments mpf = (MusicPlayerFragments) adapter.getItem(position);
                    mpf.onResume();
                }
                else if(adapter.getItem(position) instanceof DownloadListFragment){
                                                   DownloadListFragment dlf = (DownloadListFragment) adapter.getItem(position);
                                                   dlf.onResume();
                }
                else if(adapter.getItem(position) instanceof IncompleteDownloadListFragment){
                    IncompleteDownloadListFragment dlf = (IncompleteDownloadListFragment) adapter.getItem(position);
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
        tabLayout.setScrollPosition(position,0f,true);
        viewPager.setCurrentItem(position, true);


    }

    public void change()
    {
        if (!(adapter == null)) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupDrawerContent(final NavigationView navigationView) {
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
                            case R.id.nav_shareapp:
                            {
                                mDrawerLayout.closeDrawers();
                                navigationView.clearFocus();

                                String sname = SplashScreenActivity.sharelink;

                                if(sname != null && sname.length() > 0 && !sname.equals("NA") ) {
                                    sname = "Get MusicIndia Android App! Download Indian Music and Songs Lyrics for free. To Install Click on this link. "+" \n"+" \n"+sname;
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, sname);
                                    startActivity(Intent.createChooser(share, "MusicIndia App Install"));
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Something is wrong, Link not available",Toast.LENGTH_SHORT).show();
                                }
                                return true;

                            }
                            case R.id.nav_appupdate:
                            {
                                String sname = SplashScreenActivity.updatelink;
                                int version = SplashScreenActivity.versionCode;
                                try {
                                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    int versionCode = pInfo.versionCode;
                                    if(sname != null && sname.length() > 0 && !sname.equals("NA") && version > versionCode) {

                                        Uri uriLyrics = Uri.parse(sname);
                                        Intent videoIntentLyrics = new Intent(Intent.ACTION_VIEW);
                                        videoIntentLyrics.setData(uriLyrics);
                                        videoIntentLyrics.setPackage("com.android.chrome");
                                        //videoIntent.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
                                        startActivity(videoIntentLyrics);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,"No New Updates",Toast.LENGTH_SHORT).show();
                                    }

                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }


                                return true;

                            }



                            default:
                                return true;

                        }

                    };
                });
    }

    public static void openAppRating(Context context) {

        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:BollyMusicDeveloper"));
        boolean marketFound = false;

        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            //Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=BollyMusicDeveloper"));

            context.startActivity(webIntent);
        }
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