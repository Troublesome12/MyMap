package com.troublesome.findanyplace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity implements View.OnTouchListener {

    static public TextView nameTextView, ratingTextView, reviewTextView, vicinityTextView;
    static public TextView carDurationTextView, carDistanceTextView, walkingDurationTextView, walkingDistanceTextView;
    static public TextView callTextView, mapTextView, webTextView;
    static public RatingBar ratingBar;
    static public ImageView imageView;
    static public Place selectedPlace;
    static ProgressDialog progressDialog;
    String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        reviewTextView = (TextView) findViewById(R.id.reviewTextView);
        vicinityTextView = (TextView) findViewById(R.id.vicinityTextView);
        carDurationTextView = (TextView) findViewById(R.id.carDurationTextView);
        carDistanceTextView = (TextView) findViewById(R.id.carDistanceTextView);
        walkingDurationTextView = (TextView) findViewById(R.id.walkingDurationTextView);
        walkingDistanceTextView = (TextView) findViewById(R.id.walkingDistanceTextView);
        callTextView = (TextView) findViewById(R.id.callTextView);
        mapTextView = (TextView) findViewById(R.id.mapTextView);
        webTextView = (TextView) findViewById(R.id.webTextView);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        imageView = (ImageView) findViewById(R.id.detailImageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        callTextView.setOnTouchListener(this);
        mapTextView.setOnTouchListener(this);
        webTextView.setOnTouchListener(this);

        placeId = getIntent().getStringExtra("placeId");
        DetailsRequest.detailsRequest(placeId, getBaseContext());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setAlpha((float) 0.4);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setAlpha(1);

            switch (v.getId()) {
                case R.id.callTextView:
                    if(selectedPlace.getPhoneNumber()!=null) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selectedPlace.getPhoneNumber()));
                        startActivity(callIntent);
                    }else
                        Toast.makeText(getBaseContext(),"Couldn't fetch data, Please Check your internet access",Toast.LENGTH_LONG).show();
                    break;
                case R.id.mapTextView:
                    if(selectedPlace.getLocation()!=null && selectedPlace.getName()!=null) {
                        MapsActivity.setMarkerOnLocation(selectedPlace.getLocation(), selectedPlace.getName());
                        MapsActivity.mapTextViewPressed = true;
                        MapsActivity.searchPlaceId = placeId;
                        finish();
                    }
                    else
                        Toast.makeText(getBaseContext(),"Couldn't fetch data, Please Check your internet access",Toast.LENGTH_LONG).show();
                    break;
                case R.id.webTextView:
                    if (selectedPlace.getWebSite()!=null) {
                        try {
                            Uri uri = Uri.parse(selectedPlace.getWebSite()); // missing 'https://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Sorry the web page is not secured.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(getBaseContext(),"Couldn't fetch data, Please Check your internet access",Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return false;
    }
}