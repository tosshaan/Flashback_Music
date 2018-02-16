package comf.example.tydia.cse_110_team_project_team_15_1;

import android.location.Location;

import java.sql.Timestamp;

/**
 * Created by Cadu on 15-Feb-
 * Class to generate Flashback lists given a location and time
 */

public class FlashbackList {
    database db;
    Location currLocation;
    Timestamp currTime;
    int[] flashbackSongIDs;
    int[] allSongIDs;

    public FlashbackList(Location loc, Timestamp time, database db ) {
        this.db = db;
        currTime = time;
        currLocation = loc;
        // Getting IDs of all songs in storage
        allSongIDs = SongsActivity.getSongIDs();
    }

    //Method to populate list
    public void generateList() {

    }

    // Getter for list
    public int[] getFlashbackSongIDs() {
        return flashbackSongIDs;
    }


}
