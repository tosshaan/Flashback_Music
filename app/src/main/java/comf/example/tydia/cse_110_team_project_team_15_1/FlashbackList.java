package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.test.mock.MockContext;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cadu on 15-Feb-
 * Class to generate Flashback lists given a location and time
 */

public class FlashbackList {
    database db;
    String currAddress;
    Timestamp currTime;
    Context context;
    int[] flashbackSongIDs;
    HashMap<String, Integer> allSongIDs;
    MetadataGetter metadataGetter;

    public FlashbackList(String address, Timestamp time, database db, Context context ) {
        this.db = db;
        currTime = time;
        currAddress = address;
        this.context = context;
        // Getting IDs of all songs in storage
        allSongIDs = new HashMap<String, Integer>();
        metadataGetter = new MetadataGetter(context);
        populateallSongs();
    }

    //Method to populate list
    public void generateList() {
        // Temp ArrayList to hold flashbackSongs
        ArrayList<Integer> tempList = new ArrayList<>();
        // Getting all songs in currentLocation
        ArrayList<String> songsAtLoc = db.getSongsPlayedAtLocation(currAddress);
        for( int i = 0; i < songsAtLoc.size(); i++ ) {
            tempList.add(allSongIDs.get(songsAtLoc.get(i)));
        }
        // TODO: time of day and day of week

        // Copying tempArrayList to final array
        flashbackSongIDs = new int[tempList.size()];
        for( int k = 0; k < flashbackSongIDs.length; k++ ) {
            flashbackSongIDs[k] = tempList.get(k);
        }
    }


    // Method to populate song's hashMap
    public void populateallSongs() {
        int[] songIDs = SongsActivity.getSongIDs();
        for( int i = 0; i < songIDs.length; i++ ) {
            // Pushing song's name and ID to HashMap
            allSongIDs.put(metadataGetter.getName(songIDs[i]), songIDs[i]);
        }
    }

    // Getter for list
    public int[] getFlashbackSongIDs() {
        return flashbackSongIDs;
    }

    // Setter for address
    public void setCurAddress(String newAddress) {
        currAddress = newAddress;
    }

    // Setter for time
    public void setCurrTime(Timestamp newTime ) {
        currTime = newTime;
    }


}