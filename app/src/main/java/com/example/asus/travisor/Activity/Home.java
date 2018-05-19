package com.example.asus.travisor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.asus.travisor.CustomView.CircleTransform;
import com.example.asus.travisor.Model.Common;
import com.example.asus.travisor.R;
import com.example.asus.travisor.fragment.FavouriteFragment;
import com.example.asus.travisor.fragment.HotFragment;
import com.example.asus.travisor.fragment.MenuFragment;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView tvName, tvWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_HOT = "hotPlaces";
    private static final String TAG_COLLECTION = "myCollection";
    private static final String TAG_NEAR_ME = "nearMeMap";
    private static final String TAG_SIGN_OUT = "signOut";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private final String urlProfileImg="https://scontent.fsgn2-2.fna.fbcdn.net/v/t1.0-9/31408161_1810872695884526_4171014063096070144_n.jpg?_nc_cat=0&oh=33f7b0128ca4a35d2d7ae2fd13e148d8&oe=5B805A7B";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        mHandler = new Handler();
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "need an action?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // load nav menu header data
        loadNavHeader();
        //nav view
        setUpNavView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void setUpNavView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadNavHeader() {
        navHeader=navigationView.getHeaderView(0);
        tvName=navHeader.findViewById(R.id.tvUserName);
        tvName.setText(Common.currentUser.getName());
        imgProfile=navHeader.findViewById(R.id.img_profile);
        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (shouldLoadHomeFragOnBackPress){
            //checking if user is on other nav menu rather than home
            if (navItemIndex!=0)
            {
                navItemIndex=0;
                CURRENT_TAG=TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        //other
        super.onBackPressed();
    }

    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            toggleFab();
            return;
        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        // show or hide the fab button
        toggleFab();
        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        //set appropriate fragment to container
        switch (navItemIndex) {
            case 0:
                // home
                Fragment menuFragment=new MenuFragment();
                return menuFragment;
            case 1:
                // hot
                Fragment hotFragment=new HotFragment();
                return hotFragment;
            case 2:
                // Favorite
                return new FavouriteFragment();
            case 3:
                // Near me
                Toast.makeText(getApplicationContext(),"Map",Toast.LENGTH_SHORT).show();
            case 4:
                //Sign out
                Toast.makeText(getApplicationContext(),"signout",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_menu:
                navItemIndex=0;
                CURRENT_TAG=TAG_HOME;
                break;
            case R.id.nav_hot:
                navItemIndex=1;
                CURRENT_TAG=TAG_HOT;
                break;
            case R.id.nav_collection:
                navItemIndex=2;
                CURRENT_TAG=TAG_COLLECTION;
                break;
            case R.id.nav_nearme:
                navItemIndex=3;
                CURRENT_TAG=TAG_NEAR_ME;
                Intent intent=new Intent(this,MapNearMe.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_signOut:
                navItemIndex=4;
                CURRENT_TAG=TAG_SIGN_OUT;
                signOut();
                return true;
            default:
                navItemIndex=0;
        }
        //Checking if the item is in checked state or not, if not make it in checked state
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        item.setChecked(true);

        loadHomeFragment();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {

    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
}
