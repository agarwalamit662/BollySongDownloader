
package com.support.android.songsdownloader;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {



    private DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Context context;
    Adapter adapter;// = new Adapter(getSupportFragmentManager());

    private List<String> friendsList = new ArrayList<String>();

    public MainActivity ma;
    private ProgressDialog proDialog;
    protected void startLoading() {
        proDialog = new ProgressDialog(this);
        proDialog.setMessage("Loading Data");
        proDialog.setProgressStyle(ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        proDialog.setCancelable(false);
        proDialog.show();
    }

    protected void stopLoading() {
        proDialog.dismiss();
        proDialog = null;
    }
    public void call(String type)
    {
        setupViewPager(viewPager,"Downloads");
    }
    public void onDataFetch()
    {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, "Movies");


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager,"Files");
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("EXTRA_PAGE");
            if(value != null){
                int position= Integer.parseInt(value );
                //viewPager.setCurrentItem(position);
                selectFragment(position);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager viewPager, String caseNumber) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new Adapter(getSupportFragmentManager());

        if(caseNumber.equals("Files")){
            // Friends Reviews

            adapter.addFragment(new CheeseListFragment(), "Files", 0);

            adapter.addFragment(new DownloadListFragment(), "Downloads", 1);

            adapter.addFragment(new TiledListFragment(),"Tiled",2);

        }
        if(caseNumber.equals("Downloads"))
        {

        }

        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void selectFragment(int position){

       // TabLayout.Tab tab = tabLayout.getTabAt(i);
        //tab.setCustomView(pageAdapter.getTabView(i));

        viewPager.setCurrentItem(position, true);

// true is to animate the transaction
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
                            case R.id.nav_downloads:
                            {
                               Intent i = new Intent(MainActivity.this,DownloadsLists.class);
                               i.putExtra("hello","hello");
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

// Async Task Class



