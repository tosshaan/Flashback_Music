package test1;

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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import comf.example.tydia.cse_110_team_project_team_15_1.AlbumsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.MetadataGetter;
import comf.example.tydia.cse_110_team_project_team_15_1.R;
import comf.example.tydia.cse_110_team_project_team_15_1.SongsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.TimeMachine;
import comf.example.tydia.cse_110_team_project_team_15_1.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by tosshaan on 2/14/2018.
 */

public class JUnitTest02 {

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
            LocalDateTime morning = LocalDateTime.of(2018,2,7,10,23);
            TimeMachine.useFixedClockAt(morning);
            db.startSongInfoRequest("After The Storm", mainActivity.getApplicationContext());
            db.startSongInfoRequest("Beautiful-Pain", mainActivity.getApplicationContext());

            LocalDateTime noon = LocalDateTime.of(2018,2,7,14,23);
            TimeMachine.useFixedClockAt(noon);
            db.startSongInfoRequest("Currently", mainActivity.getApplicationContext());
            db.startSongInfoRequest("Dead Dove Do Not Eat", mainActivity.getApplicationContext());

            LocalDateTime evening = LocalDateTime.of(2018,2,7,22,23);
            TimeMachine.useFixedClockAt(evening);
            db.startSongInfoRequest("123 Go", mainActivity.getApplicationContext());
            db.startSongInfoRequest("Crane City", mainActivity.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testGetSongsAtTime () {
        ArrayList<String> mlist = db.getSongsAtTime(9);
        assertEquals(mlist.contains("After The Storm"), true);
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
