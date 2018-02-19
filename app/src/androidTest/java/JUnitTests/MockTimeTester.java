package JUnitTests;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.MetadataGetter;
import comf.example.tydia.cse_110_team_project_team_15_1.SongsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.TimeMachine;
import comf.example.tydia.cse_110_team_project_team_15_1.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by tosshaan on 2/14/2018.
 */

public class MockTimeTester {

    private int[] idsTest;
    private MetadataGetter metadataGetter;
    private MainActivity mainActivity;
    private database db;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup () throws IOException {
        idsTest = SongsActivity.getSongIDs();
        mainActivity  = mainActivityActivityTestRule.getActivity();
        metadataGetter = new MetadataGetter(mainActivity.getApplicationContext());
        db = new database();
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        try {
            Timestamp morning = new Timestamp(2018,2,7,9,13,0,0);
            db.startSongInfoRequest("After The Storm", mainActivity.getApplicationContext(), morning);
            db.finishSongInfoRequest(true, false);

            db.startSongInfoRequest("Beautiful-Pain", mainActivity.getApplicationContext(),morning);
            db.finishSongInfoRequest(true, false);

            Timestamp noon = new Timestamp(2018,2,7,14,13,0,0);
            db.startSongInfoRequest("Currently", mainActivity.getApplicationContext(),noon);
            db.finishSongInfoRequest(true, false);
            db.startSongInfoRequest("Dead Dove Do Not Eat", mainActivity.getApplicationContext(),noon);
            db.finishSongInfoRequest(true, false);

            Timestamp evening = new Timestamp(2018,2,7,23,13,0,0);
            db.startSongInfoRequest("123 Go", mainActivity.getApplicationContext(),evening);
            db.finishSongInfoRequest(true, false);
            db.startSongInfoRequest("Crane City", mainActivity.getApplicationContext(),evening);
            db.finishSongInfoRequest(true, false);

            Timestamp sunday = new Timestamp(2018,1,31,9,13,0,0);
            db.startSongInfoRequest("Lift Me Up", mainActivity.getApplicationContext(), sunday);
            db.finishSongInfoRequest(true,false);

            Timestamp friday = new Timestamp(2018,2,1,9,13,0,0);
            db.startSongInfoRequest("Can't You Be Mine", mainActivity.getApplicationContext(), friday);
            db.finishSongInfoRequest(true,false);

            Timestamp saturday = new Timestamp(2018,2,2,9,13,0,0);
            db.startSongInfoRequest("I Fell In Love", mainActivity.getApplicationContext(), saturday);
            db.finishSongInfoRequest(true,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testGetSongsAtTime () {
        // test if correct song is in morning list
        ArrayList<String> mlist = db.getSongsAtTime(9);
        assertEquals(mlist.contains("After The Storm"), true);
        assertEquals(mlist.contains("Beautiful-Pain"), true);

        // test if correct song is in noon list
        ArrayList<String> nlist = db.getSongsAtTime(14);
        assertEquals(nlist.contains("Currently"), true);
        assertEquals(nlist.contains("Dead Dove Do Not Eat"), true);

        // test if correct song is in evening list
        ArrayList<String> elist = db.getSongsAtTime(22);
        assertEquals(elist.contains("Crane City"), true);
        assertEquals(elist.contains("123 Go"), true);
    }

    @Test
    public void testGetSongsByDay () {
        ArrayList<String> sunList = db.getSongsByDay(GregorianCalendar.SUNDAY);
        Log.d("day: ", sunList.toString());
        assertEquals(sunList.contains("Lift Me Up"), true);

        ArrayList<String> friList = db.getSongsByDay(GregorianCalendar.FRIDAY);
        Log.d("day: ", friList.toString());
        assertEquals(friList.contains("Can't You Be Mine"), true);

        ArrayList<String> satList = db.getSongsByDay(GregorianCalendar.SATURDAY);
        Log.d("day: ", satList.toString());
        assertEquals(satList.contains("I Fell In Love"), true);
    }

    @Test
    public void testMockTime() {
        // pick a song id
        int currentResource = idsTest[1];

        // specify a time range
        LocalTime intervalStart = LocalTime.parse("11:00:00");
        LocalTime intervalEnd = LocalTime.parse("16:00:00");

        // declare two dummy times, one within expected range, the other not
        LocalDateTime dummyTime1 = LocalDateTime.of(2018,2,7,12,23);
        LocalDateTime dummyTime2 = LocalDateTime.of(2018,1,7,9,23);

        // test within the given time range
        TimeMachine.useFixedClockAt(dummyTime1);
        assertEquals(currentResource, metadataGetter.getCurrentResources(idsTest[1],intervalStart,intervalEnd));

        // test not in the time range
        TimeMachine.useFixedClockAt(dummyTime2);
        assertNotEquals(currentResource, metadataGetter.getCurrentResources(idsTest[1],intervalStart,intervalEnd));
    }

}
