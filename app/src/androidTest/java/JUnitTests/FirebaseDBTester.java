
package test1;

import android.app.Application;
import android.location.Location;
import android.support.test.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;


import java.sql.Timestamp;

import comf.example.tydia.cse_110_team_project_team_15_1.FirebaseDB;
import comf.example.tydia.cse_110_team_project_team_15_1.LocationService;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;

/**
 * Created by tosshaan on 3/5/2018.
 */

public class FirebaseDBTester {

    FirebaseDB  dbFunc;

    @Before
    public void setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext());
        dbFunc = new FirebaseDB();
    }

    @Test
    public void testSubmit() {
        String address = "My House";
        Timestamp currTime = new Timestamp( System.currentTimeMillis());
        String userName = "TOSH and I";
        String songName = "Beautiful Pain";
        //dbFunc.submit(userName, address, songName, currTime);
    }
}

