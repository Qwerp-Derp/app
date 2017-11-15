package com.weetbix.rabobank;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weetbix.rabobank.branches.generate.AllBranches;
import com.weetbix.rabobank.branches.generate.Branch;
import com.weetbix.rabobank.branches.generate.BranchListAdapter;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.amirs.JSON;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;




public class all_branches extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;


    public List<Branch> Branches     = new ArrayList<Branch>();
    public BranchListAdapter adapter = new BranchListAdapter(Branches);
    public FloatingSearchView searchBar;
    public FloatingSearchView searchBarMap;
    public RecyclerView recycler;
    public ViewFlipper flipper;
    public Boolean mapLoaded = false;
    public String flag = "inactive";



    // Handlers for updating the UI
    // Removes stress on the main thread
    public Handler updateUI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_branches);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        updateUI = new Handler();
        // Get the viewflipper
        flipper = (ViewFlipper) findViewById(R.id.flipper);






        // Load the branches through a http req
        // Ideally this will change to an actual domain name on a vps and not hosted locally
        try {
            String res = sendGet("http://35.201.8.34:8080/branches/get");
            // Parse the json
            JSON json = new JSON(res);
            // Begin reading it
            for (int i = 0; i < json.count(); i++) {
                // Branch at index i
                JSON branch = json.index(i);
                // Create the branch
                double[] latlng = new double[]{branch.key("Lat").doubleValue(), branch.key("Lng").doubleValue()};
                Branches.add(new Branch(branch.key("Name").stringValue(), branch.key("Country").stringValue(), branch.key("Addr").stringValue(), latlng, branch.key("ID").intValue()));
            }

            Log.d("Result", res);

        } catch (IOException e) {
            e.printStackTrace();
        }






        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        // Get the search view
        searchBar = (FloatingSearchView) findViewById(R.id.floating_search_view);


        // Load the recycler
        // Attain recycler and set layout manager
        LinearLayoutManager llm   = new LinearLayoutManager(all_branches.this);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        // Load layout into it
        AllBranches.LoadBranches(recycler, adapter);

        // Add some fancy animations
        recycler.setItemAnimator(new SlideInUpAnimator());









        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);


        // Stuff relating to the bottom nav drawer
        AHBottomNavigationItem listSearch = new AHBottomNavigationItem("List Search", R.drawable.list_search_icon);
        AHBottomNavigationItem mapSearch = new AHBottomNavigationItem("Map Search", R.drawable.map_search_icon);
        // Add it
        bottomNavigation.addItem(listSearch);
        bottomNavigation.addItem(mapSearch);



        // Set the current item
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setAccentColor(R.color.secondary);
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setInactiveColor(Color.parseColor("#adadad"));



        // Add a listener for the navbar
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // This means that the map search was clicked and wasnt already clicked
                if ((position == 1) && !wasSelected) {
                    flipper.setDisplayedChild(1);
                } else if ((position == 0) && !wasSelected) {
                    flipper.setDisplayedChild(0);
                }
                return true;
            }
        });















        // Need to make sure that the search bar appears and disappears when recycler view is scrolled
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            Boolean scrollingDown = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // Determine the scroll state
                if (((newState == RecyclerView.SCROLL_STATE_IDLE) && scrollingDown) || scrollingDown) {
                    searchBar.animate()
                            .setDuration(400)
                            .setInterpolator(new DecelerateInterpolator())
                            .translationY(-1000)
                            .setStartDelay(0)
                            .start();
                } else {
                    searchBar.animate()
                            .setDuration(400)
                            .setInterpolator(new AccelerateInterpolator())
                            .translationY(0)
                            .setStartDelay(0)
                            .start();
                }
            }

            // Determine scroll state
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    scrollingDown = true;
                } else {
                    scrollingDown = false;
                }
            }
        });








        // Handle Searches
        searchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                // Filter views
                final String query = newQuery;


                // Thread for searching
                Thread search = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (query.length() != 0) {
                            // Iterate and filter
                            List<Branch> newList = new ArrayList<Branch>();
                            for (Branch curr : Branches) {
                                // Check that it contains the query
                                if (curr.name.startsWith(query.toLowerCase().substring(0, 1).toUpperCase() + query.substring(1))) {
                                    // Append
                                    newList.add(curr);
                                }
                            }

                            final List<Branch> createdList = newList;
                            // Alter data
                            updateUI.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setData(createdList);
                                    adapter.notifyDataSetChanged();
                                }
                            });



                        } else {
                            updateUI.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setData(Branches);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
                search.start();

            }
        });










        // The rest of this class is dedicated to the maps


        // Set up the map for our second layout.... Do this at the end
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        searchBarMap = (FloatingSearchView) findViewById(R.id.floating_search_view_maps);




        // Create click handler for the suggestions

        searchBarMap.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, final SearchSuggestion item, int itemPosition) {
                final SearchSuggestion temp = item;
                suggestionView.setOnClickListener(new View.OnClickListener() {
                    SearchSuggestion suggestion = item;
                    @Override
                    public void onClick(View v) {
                        // Clear the map
                        mMap.clear();
                        // Get the branch
                        Branch clicked = Branches.get(suggestion.describeContents());
                        // Load the tag into the map
                        mMap.addMarker(new MarkerOptions().position(new LatLng(clicked.latLng[0], clicked.latLng[1])).title(clicked.name));
                        searchBarMap.clearSearchFocus();
                    }
                });
            }
        });









        // Search shit for the map
        // Attain the map
        searchBarMap = (FloatingSearchView) findViewById(R.id.floating_search_view_maps);
        // Load the default suggestions
        final List<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();

        // Iterate over branches
        for (int i = 0; i < 7; i++) {
            // Check that it exists if not the close the loop
            try {
                final Branch curr = Branches.get(i);
                // Add a new suggestion
                suggestions.add(new SearchSuggestion() {
                    // Just the library stuff
                    @Override
                    public String getBody() {
                        return curr.name;
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    public double[] getCoords() {
                        return curr.latLng;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                        dest.writeString(curr.name);
                        dest.writeInt(1);
                    }
                });
            } catch (Exception e) {
                break;
            }
        }







        // Create a search listener
        // Andddddd................... that is search completed
        searchBarMap.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                // Determine the length of the query
                // First get the query
                if (newQuery.length() == 0) {
                    // Push the suggestions
                    searchBarMap.swapSuggestions(suggestions);
                } else {
                    List<SearchSuggestion> suggestionList = new ArrayList<SearchSuggestion>();
                    // Iterate
                    for (int i = 0; i < Branches.size(); i++) {
                        final int index = i;
                        final Branch curr = Branches.get(i);
                        if (curr.name.startsWith(newQuery.toLowerCase().substring(0, 1).toUpperCase() + newQuery.substring(1))) {
                            suggestionList.add(new SearchSuggestion() {
                                @Override
                                public String getBody() {
                                    return curr.name;
                                }

                                @Override
                                public int describeContents() {
                                    return index;
                                }

                                @Override
                                public void writeToParcel(Parcel dest, int flags) {
                                    dest.writeString(curr.name);
                                    dest.writeInt(1);
                                }
                            });
                        }
                    }
                    searchBarMap.swapSuggestions(suggestionList);
                }
            }
        });










    }













    public String sendGet(String url) throws IOException {
        final String[] response = new String[1];
        final String query = url;

        Thread httpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(query)
                        .build();

                Response resp = null;
                try {
                    resp = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    response[0] = resp.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        httpThread.start();


        return response[0];
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





    // On Map ready for google maps
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Iterate through list
        for(final Branch curr: Branches) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(curr.latLng[0], curr.latLng[1])).title(curr.name));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-25.2744, 133.7751)));
            mMap.setMinZoomPreference(3.0f);
        }
    }
}
