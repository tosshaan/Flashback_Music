package test1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import comf.example.tydia.cse_110_team_project_team_15_1.FlashbackActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.FlashbackList;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.SongInfoActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.database;

import static android.support.test.InstrumentationRegistry.getContext;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Cory Liang on 2/17/2018.
 */

public class GPXUnitTest {
    /*@Rule
    public ActivityTestRule<SongInfoActivity> database = new ActivityTestRule<SongInfoActivity>(SongInfoActivity.class);*/
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    @Rule
    public ActivityTestRule<FlashbackActivity> flashbackActivity = new ActivityTestRule<FlashbackActivity>(FlashbackActivity.class);

    database db;
    LocationManager manager;
    Location currLocation;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currLocation = location;
            try {
                Log.d("tag1", db.getAddress(currLocation,getContext()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Before
    public void setup(){
        //sets up database and location manager
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }
        Context context;
        db = new database();
        manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = manager.GPS_PROVIDER;
        manager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }
    @Test
    public void testInsertSongAtLocation() {
        try {
            db.startSongInfoRequest("afterthestorm.mp3", getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.finishSongInfoRequest();
        String address = "";
        try {
            address = db.getAddress(currLocation, getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("testInsert", address);
        assertTrue(db.isSongHere("afterthestorm"));
        assertTrue(db.isLocation(address));
    }

}
