package test1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.ActivityCompat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;

import comf.example.tydia.cse_110_team_project_team_15_1.FlashbackList;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.MetadataGetter;
import comf.example.tydia.cse_110_team_project_team_15_1.R;
import comf.example.tydia.cse_110_team_project_team_15_1.database;

import static android.support.test.InstrumentationRegistry.getContext;
import static junit.framework.Assert.assertEquals;

/**
 * Created by Cory Liang on 2/18/2018.
 */

public class FlashBackListTester {
    FlashbackList fbList;
    Timestamp mockTime;
    Timestamp tempMockTime;
    database db;
    MainActivity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

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
        mainActivity = mainActivityRule.getActivity();
        db = new database();
        mockTime = new Timestamp(2018, 2, 11, 18, 45, 0, 0);
        tempMockTime = mockTime;
        try {
            db.startSongInfoRequest("After The Storm", mainActivity.getApplicationContext(), tempMockTime);
            db.finishSongInfoRequest(true,false);
            // Same day of week but different time of day as original
            tempMockTime = new Timestamp(2018, 2, 4, 10, 45, 0, 0);
            db.startSongInfoRequest("123 Go", mainActivity.getApplicationContext(), tempMockTime);
            db.finishSongInfoRequest(true,false);
            // Only location similar to original
            tempMockTime = new Timestamp(2018, 2, 3, 10, 45, 0, 0);
            db.startSongInfoRequest("Blood On Your Bootheels", mainActivity.getApplicationContext(), tempMockTime);
            db.finishSongInfoRequest(true,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = "";
        try {
            address = db.getAddress(MainActivity.getCurrLoc(), mainActivity.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        fbList = new FlashbackList(address, mockTime, db, mainActivity.getApplicationContext());
    }

    @Test
    public void testGenerateList() {
        fbList.generateList();
        int[] flashBackListIDs = fbList.getFlashbackSongIDs();
        assertEquals(3, flashBackListIDs.length);
        assertEquals(flashBackListIDs[0], R.raw.afterthestorm);
        assertEquals(flashBackListIDs[1], R.raw.aaaaonetwothreego);
        assertEquals(flashBackListIDs[2], R.raw.bloodonyourbootheels);
        // After liking, order should not change
        db.setLikedStatus("Blood On Your Bootheels", true);
        fbList.generateList();
        //  fbList.setDatabase(db);
        assertEquals(flashBackListIDs[0], R.raw.afterthestorm);
        assertEquals(flashBackListIDs[1], R.raw.aaaaonetwothreego);
        assertEquals(flashBackListIDs[2], R.raw.bloodonyourbootheels);
        // new song with same time but liked, should go to top
        tempMockTime = new Timestamp(2018, 2, 11, 18, 45, 0, 0);
        try {
            db.startSongInfoRequest("Crane City", mainActivity.getApplicationContext(), tempMockTime);
            db.finishSongInfoRequest(true,false);
            db.setLikedStatus("Crane City", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fbList.generateList();
        flashBackListIDs = fbList.getFlashbackSongIDs();
        assertEquals(4, flashBackListIDs.length);
        assertEquals(flashBackListIDs[0], R.raw.cranecity);
        assertEquals(flashBackListIDs[1], R.raw.afterthestorm);
        assertEquals(flashBackListIDs[2], R.raw.aaaaonetwothreego);
        assertEquals(flashBackListIDs[3], R.raw.bloodonyourbootheels);
        // new song with same points as afterthestorm but played more recently
        tempMockTime = new Timestamp(2018, 2, 11, 18, 55, 0, 0);
        try {
            db.startSongInfoRequest("At Midnight", mainActivity.getApplicationContext(), tempMockTime);
            db.finishSongInfoRequest(true,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fbList.generateList();
        flashBackListIDs = fbList.getFlashbackSongIDs();
        assertEquals(5, flashBackListIDs.length);
        assertEquals(flashBackListIDs[0], R.raw.cranecity);
        assertEquals(flashBackListIDs[1], R.raw.atmidnight);
        assertEquals(flashBackListIDs[2], R.raw.afterthestorm);
        assertEquals(flashBackListIDs[3], R.raw.aaaaonetwothreego);
        assertEquals(flashBackListIDs[4], R.raw.bloodonyourbootheels);
    }
}
