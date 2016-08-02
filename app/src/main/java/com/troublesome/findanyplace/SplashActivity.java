package com.troublesome.findanyplace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        ReadFile();

        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(1 * 1000);
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                } finally {
                    //Remove activity from stack
                    finish();
                }
            }
        };
        background.start();
    }

    public void ReadFile() {
        try {
            FileInputStream is = openFileInput("settings.txt");
            Scanner in = new Scanner(is);
            SettingsActivity.map = in.nextLine();
            SettingsActivity.radius = in.nextLine();
        } catch (Exception e) {
            SettingsActivity.map = "0";
            SettingsActivity.radius = "1";
        }
    }
}