package com.varun.edwin.hanyuan.trent.rabobank;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.varun.edwin.hanyuan.trent.rabobank.branches.generate.AllBranches;
import com.varun.edwin.hanyuan.trent.rabobank.branches.generate.Branch;
import com.varun.edwin.hanyuan.trent.rabobank.branches.generate.BranchListAdapter;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class all_branches extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    RecyclerView recycler;
    // I have to do this becuase java is the most austistic language ever
    public static String flag = "inactive";
    public List<Branch> Branches = new ArrayList<Branch>();
    public FloatingSearchView searchBar;
    public BranchListAdapter adapter = new BranchListAdapter(Branches);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_branches);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);

        // Just for testing purposes
        Branches.add(new Branch("Test"));
        Branches.add(new Branch("Washington"));
        Branches.add(new Branch("Washington"));
        Branches.add(new Branch("Washington"));
        Branches.add(new Branch("Washington"));
        Branches.add(new Branch("Sydney"));
        Branches.add(new Branch("Wagga Wagga"));
        Branches.add(new Branch("Varun"));
        Branches.add(new Branch("Germany"));
        Branches.add(new Branch("Turramurra"));
        Branches.add(new Branch("Berlin"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));
        Branches.add(new Branch("Hurstvile"));



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Attain recycler and set layout manager
        recycler = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        // Load layout into it
        AllBranches.LoadBranches(recycler, adapter);

        // Add some fancy animations
        recycler.setItemAnimator(new SlideInUpAnimator());

        // Get the search view
        searchBar = (FloatingSearchView) findViewById(R.id.floating_search_view);


        // Need to make sure that the search bar appears when recycler view is scrolled
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // Check that we are not at the top first
                if (!(recycler.getLayoutManager().findViewByPosition(0) == recyclerView.getChildAt(0))) {
                    // Check scrollstate
                    if ((newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) && flag.equals("inactive")) {

                        // Perform action depending on scrollstate
                        Animation animation = AnimationUtils.loadAnimation(all_branches.this, R.anim.slide_in_the_dms);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        searchBar.startAnimation(animation);
                        flag = "active";

                    } else if (flag.equals("active")) {

                        // Reopen bar
                        Animation animation = AnimationUtils.loadAnimation(all_branches.this, R.anim.slide_out_the_dms);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        searchBar.startAnimation(animation);
                        flag = "inactive";

                    }
                }
            }
        });





        // Handle Searches
        searchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                // Filter views

                if (newQuery.length() != 0) {
                    // Iterate and filter
                    List<Branch> newList = new ArrayList<Branch>();
                    for (Branch curr : Branches) {
                        // Check that it contains the query
                        if (curr.name.startsWith(newQuery.toLowerCase().substring(0, 1).toUpperCase() + newQuery.substring(1))) {
                            // Append
                            newList.add(curr);
                        }
                    }

                    // Alter data
                    adapter.setData(newList);
                    adapter.notifyDataSetChanged();


                } else {
                    adapter.setData(Branches);
                    adapter.notifyDataSetChanged();
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_branches, menu);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
