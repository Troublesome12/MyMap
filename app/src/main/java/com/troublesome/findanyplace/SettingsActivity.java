package com.troublesome.findanyplace;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.GoogleMap;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    final ArrayList<String> map_type = new ArrayList<>();
    final ArrayList<String> radius_list = new ArrayList<>();
    Spinner mapSpinner, radiusSpinner;
    Toolbar toolbar;
    Button saveButton;
    public static String map, radius;
    public String tempMap, tempRadius;
    boolean atFirst = true;
    InterstitialAd mInterstitialAd;
    ImageView mapImageView;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        populatingSpinnerList();

        //Creating toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7903550813305755/8932079222");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();


        mapSpinner = (Spinner) findViewById(R.id.mapSpinner);
        ArrayAdapter<String> map_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, map_type);
        map_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapSpinner.setAdapter(map_adapter);
        mapSpinner.setSelection(Integer.parseInt(map));
        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempMap = position + "";
                if (!saveButton.isEnabled() && !atFirst)
                    saveButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        radiusSpinner = (Spinner) findViewById(R.id.radiusSpinner);
        ArrayAdapter<String> radius_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, radius_list);
        radius_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radiusSpinner.setAdapter(radius_adapter);
        radiusSpinner.setSelection(Integer.parseInt(radius));
        radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempRadius = position + "";
                if (!saveButton.isEnabled() && !atFirst)
                    saveButton.setEnabled(true);
                else if (atFirst)
                    atFirst = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
                saveButton.setEnabled(false);
                map = tempMap;
                radius = tempRadius;
                updateMap();
                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                finish();
            }
        });

        mapImageView = (ImageView) findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setView(R.layout.settings_map_dialog)
                        .show();
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void populatingSpinnerList() {
        map_type.add(new String("Normal"));
        map_type.add(new String("Hybrid"));
        map_type.add(new String("Terrain"));
        map_type.add(new String("Satellite"));

        radius_list.add(new String("300"));
        radius_list.add(new String("500"));
        radius_list.add(new String("700"));
        radius_list.add(new String("900"));
        radius_list.add(new String("1100"));
        radius_list.add(new String("1300"));
        radius_list.add(new String("1500"));
    }

    public void saveToFile() {
        try {
            FileOutputStream out = openFileOutput("settings.txt", MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(tempMap);
            writer.write("\n");
            writer.write(tempRadius);
            writer.flush();
            writer.close();
            Toast.makeText(getBaseContext(), "Settings has been updated", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMap() {
        switch (map) {
            case "0":
                MapsActivity.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "1":
                MapsActivity.mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "2":
                MapsActivity.mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "3":
                MapsActivity.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                MapsActivity.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }

        switch (radius) {
            case "0":
                MapsActivity.radiusValue = "300";
                break;
            case "1":
                MapsActivity.radiusValue = "500";
                break;
            case "2":
                MapsActivity.radiusValue = "700";
                break;
            case "3":
                MapsActivity.radiusValue = "900";
                break;
            case "4":
                MapsActivity.radiusValue = "1100";
                break;
            case "5":
                MapsActivity.radiusValue = "1300";
                break;
            case "6":
                MapsActivity.radiusValue = "1500";
                break;
            default:
                MapsActivity.radiusValue = "500";
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
    }
}