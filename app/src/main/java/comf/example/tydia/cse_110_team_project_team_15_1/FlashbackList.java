package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.test.mock.MockContext;
import android.util.Log;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by Cadu on 15-Feb-
 * Class to generate Flashback lists given a location and time
 */

public class FlashbackList {
    database db;
    Location currLocation;
    Timestamp currTime;
    int[] flashbackSongIDs;
    HashMap<String, Integer> allSongIDs;

    public FlashbackList(Location loc, Timestamp time, database db ) {
        this.db = db;
        currTime = time;
        currLocation = loc;
        // Getting IDs of all songs in storage
        populateallSongs();
    }

    //Method to populate list
    public void generateList() {

    }

    /*
    Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + MEDIA_RES_ID);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, path);

     */

    // Method to populate song's hashMap
    public void populateallSongs() {
        int[] songIDs = SongsActivity.getSongIDs();
        Uri path;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        for( int i = 0; i < songIDs.length; i++ ) {
            // Pushing song's name and ID to HashMap
            path = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + songIDs[i]);
            retriever.setDataSource(new MockContext(), path);
            //String songName = retriever.
            // Log.d("Name is: ", );
        }

    }

    // Getter for list
    public int[] getFlashbackSongIDs() {
        return flashbackSongIDs;
    }


}
