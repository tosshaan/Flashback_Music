
package JUnitTests;

import android.app.Application;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mortbay.jetty.servlet.Context;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import comf.example.tydia.cse_110_team_project_team_15_1.FirebaseDB;
import comf.example.tydia.cse_110_team_project_team_15_1.FirebaseQueryObserver;
import comf.example.tydia.cse_110_team_project_team_15_1.GoogleHelper;
import comf.example.tydia.cse_110_team_project_team_15_1.LocationService;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.R;

import static comf.example.tydia.cse_110_team_project_team_15_1.FirebaseDB.MILLISECODNS_IN_DAY;
import static junit.framework.Assert.assertEquals;

/**
 * Created by tosshaan on 3/5/2018.
 * This class unit tests the public methods in FirebaseDB
 */

public class FirebaseDBTester {

    private final static long MORE_MILLISECONDS_THAN_A_WEEK = 8 * MILLISECODNS_IN_DAY;

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    // Setting up test with TestDatabase

    FirebaseOptions options;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseDB  dbFunc;
    private static boolean setUpDone = false;
    private static int count = 0;
    private static final long someTime = 709010000;
    long moreThanOneWeekAgo = someTime - MORE_MILLISECONDS_THAN_A_WEEK;


    @Before
    public void setup() {
        if( setUpDone ) {
            return;
        }
        // Setting up test with TestDatabase
        options = new FirebaseOptions.Builder().setApplicationId("1:954527089143:android:10e8bbfa388bfab6")
                                                                .setDatabaseUrl("https://testdatabaseforfbm.firebaseio.com/")
                                                                .build();

        database = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(mainActivity.getActivity().getApplicationContext(), options, "secondary"+count));
        myRef = database.getReferenceFromUrl("https://testdatabaseforfbm.firebaseio.com/");
        dbFunc = new FirebaseDB(database, myRef);
        count++;
       // setUpDone = true;
    }


    @Test
    public void testSubmit() {
        Uri.Builder builder = new Uri.Builder();
        builder.path("TestURL");
        Uri uri = builder.build();
        builder.path("TestURL2");
        Uri uri2 = builder.build();
        builder.path("TestURL3");
        Uri uri3 = builder.build();
        builder.path("TestURL4");
        Uri uri4 = builder.build();

        dbFunc.submit("Tosh and I", "I-House", "Beautiful Pain", someTime - 100, uri);
        dbFunc.submit("Cory and Graham", "Off-campus", "Blood on your bootheels", someTime - 150, uri2);
        dbFunc.submit("Tong", "I-House", "America Religious", moreThanOneWeekAgo, uri3);
        dbFunc.submit("Wei", "Sixth", "Beautiful Pain", someTime - 10, uri4);

        // Querying database to check if values were populated appropriately
        Query queryRef = myRef.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> expectedAddressList = new ArrayList<String>();
            ArrayList<String> expectedUserList = new ArrayList<String>();
            ArrayList<String> expectedURLList = new ArrayList<String>();
            ArrayList<String> expectedSongList = new ArrayList<String>();
            ArrayList<Long> expectedLongs = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                }
                else {

                    // Looping through database records
                    for( DataSnapshot locationSnap: snapshot.getChildren() ) {
                        expectedAddressList.add(locationSnap.getKey());
                        // Looping through users in location
                        for( DataSnapshot userSnap: locationSnap.getChildren() ) {
                            expectedUserList.add(userSnap.getKey());
                            // Looping through songs for user
                            for( DataSnapshot songSnap: userSnap.getChildren() ) {
                                expectedSongList.add( songSnap.getKey());
                                for (DataSnapshot URLsnap : songSnap.getChildren()) {
                                    expectedURLList.add(URLsnap.getKey());
                                    expectedLongs.add((Long) URLsnap.getValue());
                                }
                            }
                        }

                    }

                    // Checking addresses
                    assertEquals(expectedAddressList.size(), 3);
                    assertEquals(expectedAddressList.get(0), "I-House");
                    assertEquals(expectedAddressList.get(1), "Off-campus");
                    assertEquals(expectedAddressList.get(2), "Sixth");

                    assertEquals(expectedUserList.size(), 4);
                    assertEquals(expectedUserList.get(0), "Tong");
                    assertEquals(expectedUserList.get(1), "Tosh and I");
                    assertEquals(expectedUserList.get(2), "Cory and Graham");
                    assertEquals(expectedUserList.get(3), "Wei");

                    assertEquals(expectedSongList.size(), 4);
                    assertEquals(expectedSongList.get(0), "America Religious");
                    assertEquals(expectedSongList.get(1), "Beautiful Pain");
                    assertEquals(expectedSongList.get(2), "Blood on your bootheels");
                    assertEquals(expectedSongList.get(3), "Beautiful Pain");

                    assertEquals(expectedURLList.size(), 4);
                    assertEquals(expectedURLList.get(0), "TestURL3");
                    assertEquals(expectedURLList.get(1), "TestURL");
                    assertEquals(expectedURLList.get(2), "TestURL2");
                    assertEquals(expectedURLList.get(3), "TestURL4");

                    assertEquals(expectedLongs.size(), 4);
                    assertEquals((long) expectedLongs.get(0), moreThanOneWeekAgo);
                    assertEquals((long )expectedLongs.get(1), someTime - 100);
                    assertEquals((long) expectedLongs.get(2), someTime - 150);
                    assertEquals((long )expectedLongs.get(3), someTime -10 );
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "failed to read value.", error.toException());
            }
        });
    }


    @Test
    public void testGetAllSongsForVibe() {
            long time = someTime / MILLISECODNS_IN_DAY;
            LocalDate songDate = LocalDate.ofEpochDay(time);
            LocalDate weekAgo = LocalDate.ofEpochDay(moreThanOneWeekAgo / MILLISECODNS_IN_DAY);
            Log.d("", "SONG YEAR IS " + songDate.getYear() + "DAY OF YEAR IS " + songDate.getDayOfYear());
            LocalDate currDate = LocalDate.ofEpochDay(someTime / MILLISECODNS_IN_DAY);
            Log.d("", "WEEK AGO YEAR " + currDate.getYear() + "DAY OF WEEK AGO" + currDate.getDayOfYear() );

            dbFunc.getAllSongsForVibe("I-House", songDate, "user", new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList, String adr, String usr, long time) {
                assertEquals(songNameList.size(), 12);
                assertEquals(songURLList.size(), 12);
                assertEquals(songNameList.contains("Beautiful Pain"), true);
                assertEquals(songNameList.contains("America Religious"), true);
                assertEquals(songNameList.contains("Blood on your bootheels"),true);

                // Removing all songs once
                songNameList.remove("Beautiful Pain");
                songNameList.remove("America Religious");
                songNameList.remove("Blood on your bootheels");
                assertEquals(songNameList.size(), 9);


                // Checking which ones are still there
                assertEquals(songNameList.contains("Beautiful Pain"), true);
                assertEquals(songNameList.contains("America Religious"), true);
                assertEquals(songNameList.contains("Blood on your bootheels"),true);


                // Remove again
                songNameList.remove("Beautiful Pain");
                songNameList.remove("America Religious");
                songNameList.remove("Blood on your bootheels");
              //  assertEquals(songNameList.size(), 6);

                // Now only locations based ones should be present
                assertEquals(songNameList.contains("Beautiful Pain"), true);
                assertEquals(songNameList.contains("America Religious"), true);
                assertEquals(songNameList.contains("Blood on your bootheels"),false);

                // Remove again twice
                songNameList.remove("Beautiful Pain");
                songNameList.remove("America Religious");
                songNameList.remove("Blood on your bootheels");
                songNameList.remove("Beautiful Pain");
                songNameList.remove("America Religious");
                songNameList.remove("Blood on your bootheels");
                assertEquals(songNameList.size(), 2);

                // Now only Beautiful Pain should be there
                assertEquals(songNameList.contains("Beautiful Pain"), true);
                assertEquals(songNameList.contains("America Religious"), false);
                assertEquals(songNameList.contains("Blood on your bootheels"),false);

                // Remove again twice
                songNameList.remove("Beautiful Pain");
                songNameList.remove("America Religious");
                songNameList.remove("Blood on your bootheels");
                songNameList.remove("Beautiful Pain");
                songNameList.remove("America Religious");
                songNameList.remove("Blood on your bootheels");

                // Should be empty
                assertEquals(songNameList.contains("Beautiful Pain"), false);
                assertEquals(songNameList.contains("America Religious"), false);
                assertEquals(songNameList.contains("Blood on your bootheels"),false);
                assertEquals(songNameList.size(), 0);

                assertEquals(songURLList.contains("TestURL3"), true);
                assertEquals(songURLList.contains("TestURL"), true);
                assertEquals(songURLList.contains("TestURL4"), false);
            }
        });

    }

    @Test
    public void testGetLastSongPlayer() {
       dbFunc.getLastSongPlayer("Beautiful Pain", someTime, new FirebaseQueryObserver() {
           @Override
           public void update(ArrayList<String> songNameList, ArrayList<String> songURLList, String adr, String usr, long time) {
               assertEquals(adr, "Sixth");
               assertEquals(usr, "Wei");
               assertEquals(time, someTime - 10);
           }
       });
    }



}

