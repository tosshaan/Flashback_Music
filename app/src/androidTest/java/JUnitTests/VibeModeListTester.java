package JUnitTests;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import comf.example.tydia.cse_110_team_project_team_15_1.FirebaseDB;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.VibeModeList;
import comf.example.tydia.cse_110_team_project_team_15_1.database;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Cadu on 15-Mar-18.
 * This class unit tests VibeModeList
 */

public class VibeModeListTester {

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    // Setting up test with TestDatabase

    VibeModeList list;
    database myData;
    private static int count = 0;
    ArrayList<String> songList;
    ArrayList<String> urlList;
    /*
 options = new FirebaseOptions.Builder().setApplicationId("1:375042707626:android:56c571077330d7c7")
                .setDatabaseUrl("https://vibemodetestdatabase.firebaseio.com/")
                .build();
        myData = new database();
        firebaseDatabase = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(mainActivity.getActivity().getApplicationContext(), options, "secondary"+count));
        myRef = firebaseDatabase.getReferenceFromUrl("https://vibemodetestdatabase.firebaseio.com/");
        dbFunc = new FirebaseDB(firebaseDatabase, myRef);
*/
    @Before
    public void setup() {
        myData = new database();
        // timestamp doesn't matter anymore
        Timestamp time = new Timestamp(0);
        // Adding some songs to test database
        try {
            myData.startSongInfoRequest("Beautiful Pain", mainActivity.getActivity().getApplicationContext(), time);
            myData.finishSongInfoRequest(true,false);
            myData.startSongInfoRequest("Chop Suey", mainActivity.getActivity().getApplicationContext(), time);
            myData.finishSongInfoRequest(true,false);
            myData.startSongInfoRequest("Toxicity", mainActivity.getActivity().getApplicationContext(), time);
            myData.finishSongInfoRequest(true,false);
            myData.startSongInfoRequest("Sound of Silence", mainActivity.getActivity().getApplicationContext(), time);
            myData.finishSongInfoRequest(true,false);
            myData.startSongInfoRequest("Blood on your Bootheels", mainActivity.getActivity().getApplicationContext(), time);
            myData.finishSongInfoRequest(true,false);
            myData.startSongInfoRequest("Sneaky Beaky", mainActivity.getActivity().getApplicationContext(), time);
            myData.finishSongInfoRequest(true,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list = new VibeModeList(myData);
        // Creating sample lists for tests
        songList = new ArrayList<>();
        urlList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            songList.add("Beautiful Pain");
            songList.add("Toxicity");
            songList.add("Chop Suey");
            urlList.add("Beautiful Pain URL");
            urlList.add("Toxicity URL");
            urlList.add("Chop Suey URL");
        }
        for (int i = 0; i < 6; i++) {
            songList.add("Sound of Silence");
            urlList.add("Sound of Silence URL");
        }

        for (int i = 0; i < 5; i++) {
            songList.add("Blood on your Bootheels");
            urlList.add("Blood on your Bootheels URL");
        }

        for (int i = 0; i < 1; i++) {
            songList.add("Sneaky Beaky");
            urlList.add("Sneaky Beaky URL");
        }
    }

    @Test
    public void testGenerateListNormalCase() {
        list.generateList(songList, urlList);
        ArrayList<String> vibeModeSongs = list.getVibeModeSongs();
        ArrayList<String> vibeModeURLs = list.getVibeModeURLs();

        assertEquals(vibeModeSongs.get(0), "Chop Suey");
        assertEquals(vibeModeSongs.get(1), "Toxicity");
        assertEquals(vibeModeSongs.get(2), "Beautiful Pain");
        assertEquals(vibeModeSongs.get(3), "Sound of Silence");
        assertEquals(vibeModeSongs.get(4), "Blood on your Bootheels");
        assertEquals(vibeModeSongs.get(5), "Sneaky Beaky");
        assertEquals(vibeModeURLs.get(0), "Chop Suey URL");
        assertEquals(vibeModeURLs.get(1), "Toxicity URL");
        assertEquals(vibeModeURLs.get(2), "Beautiful Pain URL");
        assertEquals(vibeModeURLs.get(3), "Sound of Silence URL");
        assertEquals(vibeModeURLs.get(4), "Blood on your Bootheels URL");
        assertEquals(vibeModeURLs.get(5), "Sneaky Beaky URL");

    }

    @Test
    public void testGenerateListWithDislike() {
        // Disliking sneaky beaky and making sure it's gone
        myData.setDislikedStatus("Sneaky Beaky", true);
        Log.d("", "IS SONG BEAKY DISLIKED? " + myData.getSongDislikedStatus("Sneaky Beaky"));
        list.generateList(songList, urlList);
        ArrayList<String> vibeModeSongs = list.getVibeModeSongs();
        ArrayList<String> vibeModeURLs= list.getVibeModeURLs();

        // Should remain unchanged except for Sneaky Beaky
        assertEquals(vibeModeSongs.get(0), "Chop Suey");
        assertEquals(vibeModeSongs.get(1), "Toxicity");
        assertEquals(vibeModeSongs.get(2), "Beautiful Pain");
        assertEquals(vibeModeSongs.get(3), "Sound of Silence");
        assertEquals(vibeModeSongs.get(4), "Blood on your Bootheels");
        assertEquals(false, vibeModeSongs.contains("Sneaky Beaky"));
        assertEquals(vibeModeURLs.size(), 5);
        assertEquals(vibeModeURLs.get(0), "Chop Suey URL");
        assertEquals(vibeModeURLs.get(1), "Toxicity URL");
        assertEquals(vibeModeURLs.get(2), "Beautiful Pain URL");
        assertEquals(vibeModeURLs.get(3), "Sound of Silence URL");
        assertEquals(vibeModeURLs.get(4), "Blood on your Bootheels URL");
        assertEquals(false, vibeModeURLs.contains("Sneaky Beaky URL"));
        assertEquals(vibeModeURLs.size(), 5);

        // Disliking first song. rest of list should remain the same

        myData.setDislikedStatus("Chop Suey", true);
        list.generateList(songList, urlList);
        vibeModeSongs = list.getVibeModeSongs();
        vibeModeURLs= list.getVibeModeURLs();
        assertEquals(vibeModeSongs.get(0), "Toxicity");
        assertEquals(vibeModeSongs.get(1), "Beautiful Pain");
        assertEquals(vibeModeSongs.get(2), "Sound of Silence");
        assertEquals(vibeModeSongs.get(3), "Blood on your Bootheels");
        assertEquals(false, vibeModeSongs.contains("Chop Suey"));
        assertEquals(vibeModeURLs.size(), 4);
        assertEquals(vibeModeURLs.get(0), "Toxicity URL");
        assertEquals(vibeModeURLs.get(1), "Beautiful Pain URL");
        assertEquals(vibeModeURLs.get(2), "Sound of Silence URL");
        assertEquals(vibeModeURLs.get(3), "Blood on your Bootheels URL");
        assertEquals(false, vibeModeURLs.contains("Chop Suey URL"));
        assertEquals(vibeModeURLs.size(), 4);
    }

    @Test
    public void testGenerateListWithLike() {
        // Repeat dislike
        // Like Sound of Silence and Beautiful Pain. Beautiful Pain
        // should go first, Sound of Silence should remain unchanged
        myData.setDislikedStatus("Chop Suey", true);
        myData.setDislikedStatus("Sneaky Beaky", true);
        myData.setLikedStatus("Beautiful Pain", true);
        myData.setLikedStatus("Sound of Silence", true);
        list.generateList(songList, urlList);

        ArrayList<String> vibeModeSongs = list.getVibeModeSongs();
        ArrayList<String> vibeModeURLs= list.getVibeModeURLs();
        assertEquals(vibeModeSongs.get(0), "Beautiful Pain");
        assertEquals(vibeModeSongs.get(1), "Toxicity");
        assertEquals(vibeModeSongs.get(2), "Sound of Silence");
        assertEquals(vibeModeSongs.get(3), "Blood on your Bootheels");;
        assertEquals(vibeModeURLs.size(), 4);
        assertEquals(vibeModeURLs.get(0), "Beautiful Pain URL");
        assertEquals(vibeModeURLs.get(1), "Toxicity URL");
        assertEquals(vibeModeURLs.get(2), "Sound of Silence URL");
        assertEquals(vibeModeURLs.get(3), "Blood on your Bootheels URL");
        assertEquals(vibeModeURLs.size(), 4);

    }



}
