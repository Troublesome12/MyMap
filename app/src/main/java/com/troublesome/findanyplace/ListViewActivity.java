package com.troublesome.findanyplace;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    public static ArrayList<Place> placeArrayList = new ArrayList<Place>();
    public static ListView listView;
    public static ArrayAdapter<Place> adapter;
    public static SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //Creating toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        AdView adView = (AdView) findViewById(R.id.listActivityAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        listView = (ListView) findViewById(R.id.place_list_view);
        adapter = new placeAdapter();

        NearByPlaceRequest.nearByPlaceRequest(getIntent().getStringExtra("type"), false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(ListViewActivity.this, DetailsActivity.class);
                    intent.putExtra("placeId", placeArrayList.get(position).getPlaceId());
                    startActivity(intent);
                } else
                    Toast.makeText(getBaseContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                    }
                                }
        );
    }

    @Override
    public void onRefresh() {
        NearByPlaceRequest.nearByPlaceRequest(getIntent().getStringExtra("type"), true);
    }

    private class placeAdapter extends ArrayAdapter<Place> {
        public placeAdapter() {

            super(ListViewActivity.this, R.layout.listview_row, placeArrayList);
        }
        private int lastPosition=-1;

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_row, parent, false);
            Place current = placeArrayList.get(position);

            Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            view.startAnimation(animation);
            lastPosition = position;

            TextView placeName = (TextView) view.findViewById(R.id.placeName);
            placeName.setText(current.getName());

            TextView distance = (TextView) view.findViewById(R.id.distance);
            Location location = new Location("");
            location.setLatitude(MapsActivity.mLatitude);
            location.setLongitude(MapsActivity.mLongitude);
            distance.setText("Distance : " + current.getDistance(location) + "km");

            return view;
        }
    }

    //check if internet access available or not
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MapsActivity.mapTextViewPressed) {
            MapsActivity.mapTextViewPressed = false;
            finish();
        }
    }
}
