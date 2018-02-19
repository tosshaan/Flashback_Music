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
import java.sql.Timestamp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import java.util.ArrayList;

import comf.example.tydia.cse_110_team_project_team_15_1.FlashbackActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.FlashbackList;
import comf.example.tydia.cse_110_team_project_team_15_1.LocationService;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.SongInfo;
import comf.example.tydia.cse_110_team_project_team_15_1.SongInfoActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.SongsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.database;

import static android.support.test.InstrumentationRegistry.getContext;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Cory Liang on 2/17/2018.
 */

public class GPXTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    database db;


    @Before
    public void setup() {
        //sets up database and location manager
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }
        db = new database();


    }

    @Test
    public void testInsertSongAtLocation() throws IOException {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        MainActivity mainClass = mainActivity.getActivity();
        LocationService service = mainClass.locationService;
        Location location = service.getCurrLoc();
        try {
            db.startSongInfoRequest("afterthestorm", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);

            db.startSongInfoRequest("struggles", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);

            db.startSongInfoRequest("cse110", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);

            db.startSongInfoRequest("toss", mainClass.getApplicationContext(),time );
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.finishSongInfoRequest(true, false);
        String address = "";

        try {
            address = db.getAddress(location, mainClass.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("testInsert", address);
        assertTrue(db.isSongHere("afterthestorm"));
        assertTrue(db.isSongHere("struggles"));
        assertTrue(db.isSongHere("cse110"));
        assertTrue(db.isSongHere("toss"));
        assertTrue(db.isLocation(address));
    }

    @Test
    public void testGetSongPlayedatLocation() throws IOException {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        MainActivity mainClass = mainActivity.getActivity();
        Location location = mainClass.getCurrLoc();
        try {
            db.startSongInfoRequest("afterthestorm", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);
            db.startSongInfoRequest("struggles", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);
            db.startSongInfoRequest("cse110", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);
            db.startSongInfoRequest("toss", mainClass.getApplicationContext(), time);
            db.finishSongInfoRequest(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.finishSongInfoRequest(true, false);
        String address = "";
        try {
            address = db.getAddress(location, mainClass.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> songs = db.getSongsPlayedAtLocation(address);
        assertEquals(songs.get(0), "afterthestorm");
        assertEquals(songs.get(1), "struggles");
        assertEquals(songs.get(2), "cse110");
        assertEquals(songs.get(3), "toss");
    }
    @Test
    public void testGetCurrentSongLocation() throws IOException {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        MainActivity mainClass = mainActivity.getActivity();
        Location location = mainClass.getCurrLoc();
        Location currLoc = new Location(LocationManager.GPS_PROVIDER);
        currLoc.setLatitude(32.715738);
        currLoc.setLongitude(-117.16108400000002);
        String changeAddress = db.getAddress(currLoc, mainClass.getApplicationContext());
        db.startSongInfoRequest("afterthestorm", mainClass.getApplicationContext(), time);
        db.finishSongInfoRequest(true, false);
        db.startSongInfoRequest("diffLocation", mainClass.getApplicationContext(), time);
        db.finishSongInfoRequest(true,false);
        SongInfo grab = db.getSongInfo("diffLocation");
        grab.LocationSetter(changeAddress);
        String address = db.getCurrentSongLastLocation("afterthestorm", mainClass.getApplicationContext());
        String test = db.getAddress(location, mainClass.getApplicationContext());
        String address2 = db.getCurrentSongLastLocation("diffLocation", mainClass.getApplicationContext());

        assertEquals(test, address);
        assertEquals(changeAddress,address2);
    }



}