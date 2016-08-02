package com.troublesome.findanyplace;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;


public class MapsActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    public static ArrayList<String> searchList = new ArrayList<String>();
    public static ArrayList<String> searchListPlaceId = new ArrayList<String>();
    static MyAutoCompleteTextView autoCompleteTextView;
    static Double mLatitude, mLongitude;
    static Marker myPositionMarker, searchPositionMarker;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public boolean mapJustOpened = true;
    public static Polyline line;
    static ProgressDialog mapsActivityDialog;
    public static boolean mapTextViewPressed;
    static FloatingActionsMenu floatingActionsMenu;
    public static String searchPlaceId;
    public static String radiusValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable())
            Toast.makeText(getBaseContext(), "Google Play Service is not available", Toast.LENGTH_LONG).show();
        else
            createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_maps);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        if (!isNetworkAvailable()) {
            Toast.makeText(getBaseContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
        }
        setMap();

        AdView adView = (AdView) findViewById(R.id.mapActivityAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mapTextViewPressed = false;

        populatingFAB();

        mapsActivityDialog = new ProgressDialog(this);
        mapsActivityDialog.setMessage("Loading...");
        mapsActivityDialog.setCancelable(false);

        //Creating toolbar
        toolbar = (Toolbar) findViewById(R.id.search_bar);

        //Creating drawerFragment
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        autoCompleteTextView = (MyAutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        autoCompleteTextView.setCompoundDrawablePadding(10);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                char a[] = autoCompleteTextView.getText().toString().toCharArray();
                for (int i = 0; i < a.length; i++) {
                    if (a[i] == ' ')
                        a[i] = '+';
                }
                AutoCompleteRequest.autoCompleteRequest(new String(a), getBaseContext());
                ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, searchList);
                autoCompleteTextView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
                    autoCompleteTextView.setCompoundDrawablePadding(10);
                } else {
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchButtonClicked();
                    InputMethodManager mgr = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                    autoCompleteTextView.clearFocus();
                }

                return false;
            }
        });
    }

    private void SearchButtonClicked() {
        if (isNetworkAvailable()) {
            String temp = autoCompleteTextView.getText().toString();
            if (temp.length() > 0) {
                SearchRequest.searchRequest(searchPlaceId);
                autoCompleteTextView.setText("");
            }
        } else
            Toast.makeText(getBaseContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
    }

    private void populatingFAB() {
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.floatingMenu);
        FloatingActionButton detailsFAB = (FloatingActionButton) findViewById(R.id.detailsFAB);
        FloatingActionButton clearFAB = (FloatingActionButton) findViewById(R.id.clearFAB);

        detailsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);
                    intent.putExtra("placeId", searchPlaceId);
                    startActivity(intent);
                    floatingActionsMenu.collapse();
                } else
                    Toast.makeText(getBaseContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
            }
        });

        clearFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPositionMarker != null)
                    searchPositionMarker.setVisible(false);

                if (line != null)
                    line.remove();

                //animating the camera to my recent location
                LatLng myCoordinates = new LatLng(mLatitude, mLongitude);
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(myCoordinates)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                floatingActionsMenu.setVisibility(View.GONE);
                floatingActionsMenu.collapse();
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 60);
        mLocationRequest.setFastestInterval(1000 * 30);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void setMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();
        if (mMap != null) {

            // Enable MyLocation Layer of Google Map
            mMap.setMyLocationEnabled(true);

            SettingsActivity.updateMap();
        }
    }

    private void setUpMap() {
        // Create a LatLng object for the current location
        LatLng myCoordinates = new LatLng(mLatitude, mLongitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));

        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 17);
        mMap.animateCamera(yourLocation);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to LatLng (refer to previous snippet)
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        myPositionMarker = mMap.addMarker(new MarkerOptions().position(myCoordinates)
                .title("You are here!")
                .snippet("Consider yourself located")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

    }

    public static void setMarkerOnLocation(Location location, String name) {
        floatingActionsMenu.setVisibility(View.VISIBLE);
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        DrawRouteRequest.getRouteRequest(location);


        if (searchPositionMarker == null) {
            searchPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(myCoordinates)
                    .title("Search Point!")
                    .snippet(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            searchPositionMarker.showInfoWindow();
            searchPositionMarker.setVisible(true);
        } else {
            searchPositionMarker.setVisible(true);
            searchPositionMarker.setPosition(myCoordinates);
            searchPositionMarker.setSnippet(name);
            searchPositionMarker.hideInfoWindow();
            searchPositionMarker.showInfoWindow();
        }

        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myCoordinates)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

        if (mapJustOpened) {
            //showing my location on the map at beginning
            setUpMap();
            mapJustOpened = false;
        } else {
            myPositionMarker.setPosition(new LatLng(mLatitude, mLongitude));
        }

    }

    @Override
    public void onBackPressed() {

        AlertDialog alertBox = new AlertDialog.Builder(this)
                .setTitle("My Map")
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
        alertBox.setCancelable(false);
    }
}