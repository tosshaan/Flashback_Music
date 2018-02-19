package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.test.mock.MockContext;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Class to generate Flashback lists given the location and time
 */
public class FlashbackList {
    database db;
    String currAddress;
    Timestamp currTime;
    Context context;
    int[] flashbackSongIDs;
    HashMap<String, Integer> allSongIDs;
    MetadataGetter metadataGetter;

    /**
     * constructor
     */
    public FlashbackList(String address, Timestamp time, database db, Context context ) {
        this.db = db;
        currTime = time;
        currAddress = address;
        this.context = context;
        // Getting IDs of all songs in storage
        allSongIDs = new HashMap<String, Integer>();
        metadataGetter = new MetadataGetter(context);
        populateAllSongs();
    }

    /**
     * populates flashback songs list
     */
    public void generateList() {
        // Temp ArrayList to hold flashbackSongs
        ArrayList<Integer> tempList = new ArrayList<>();

        // Getting all songs in currentLocation
        ArrayList<String> songsAtLoc = db.getSongsPlayedAtLocation(currAddress);
        if( songsAtLoc != null ) {
            for (int i = 0; i < songsAtLoc.size(); i++) {
                if (!db.getSongDislikedStatus(songsAtLoc.get(i))) {
                    tempList.add(allSongIDs.get(songsAtLoc.get(i)));
                }
            }
        }

        // Getting all songs at this time of day
        ArrayList<String> songsAtTime = db.getSongsAtTime(currTime.getHours());
        if( songsAtTime != null ) {
            for (int i = 0; i < songsAtTime.size(); i++) {
                if (!db.getSongDislikedStatus(songsAtTime.get(i))) {
                    tempList.add(allSongIDs.get(songsAtTime.get(i)));
                }
            }
        }

        // Getting all songs on this day of the week
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(currTime);
        ArrayList<String> songsOnDay = db.getSongsByDay(cal.get(GregorianCalendar.DAY_OF_WEEK));
        if( songsOnDay != null ) {
            for (int i = 0; i < songsOnDay.size(); i++) {
                if (!db.getSongDislikedStatus(songsOnDay.get(i))) {
                    tempList.add(allSongIDs.get(songsOnDay.get(i)));
                }
            }
        }

        //key is the id of the song; value is the points
        HashMap<Integer, Integer> duplicateCount = getDuplicateCount(tempList);

        //convert hashtable to arraylist, sorted in descending order by points (or number of duplicates)
        ArrayList<Map.Entry<Integer, Integer>> flashbackList = new ArrayList<>(duplicateCount.entrySet());
        flashbackList.sort( Collections.reverseOrder(Comparator.comparingInt(Map.Entry::getValue)));

        ArrayList<Integer> finalList = breakTies( flashbackList );

        // Copying tempArrayList to final array
        flashbackSongIDs = new int[finalList.size()];
        for( int k = 0; k < flashbackSongIDs.length; k++ ) {
            flashbackSongIDs[k] = finalList.get(k);
        }
    }

    /**
     * Sorts songs and then breaks ties first based on like status and then based on most recently played
     * @param flashbackList - arraylist of unsorted kay-value entries with song ids as keys and points as value
     * @return final arraylist of sorted song ids
     */
    private ArrayList<Integer> breakTies(ArrayList<Map.Entry<Integer, Integer>> flashbackList) {
        ArrayList<Integer> finalList = new ArrayList<>();
        boolean done = false;

        //print original arraylist
        //for(int i = 0; i < flashbackList.size(); i++){
        //    Log.d("FlashbackList[" + i + "]", metadataGetter.getName(flashbackList.get(i).getKey()));
        //}

        for( int i = 0; i < flashbackList.size()-1; i++ ) {
            done = true;
            for( int j = 0; j < flashbackList.size()-i-1; j++ ) {
                String songNamej = metadataGetter.getName(flashbackList.get(j).getKey());
                String songNamejPLUS1 = metadataGetter.getName(flashbackList.get(j+1).getKey());

                /*
                Log.d("SONG J IS:", songNamej);
                Log.d("SONG J's POINTS ARE", flashbackList.get(j).getValue().toString());
                Log.d("SONG J+1 IS:", songNamejPLUS1);
                Log.d("SONG J+1's POINTS ARE", flashbackList.get(j+1).getValue().toString());
                */
                if( flashbackList.get(j).getValue() == flashbackList.get(j+1).getValue() ) {
                    // Switching songs if liking breaks a tie
                    boolean result = !db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1);
                    if( !db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1 ) ) {
                        HashMap.Entry<Integer,Integer> temp = new HashMap.SimpleEntry<>(flashbackList.get(j).getKey(),
                                flashbackList.get(j).getValue());
                        //Log.d("TEMP NOW IS", metadataGetter.getName(temp.getKey()));
                        flashbackList.set(j, flashbackList.get(j+1));
                        //Log.d("THE NEW J IS", metadataGetter.getName(flashbackList.get(j).getKey()));
                        flashbackList.set(j+1, temp);
                        //Log.d("THE NEW J+1 IS", metadataGetter.getName(flashbackList.get(j+1).getKey()));
                        done = false;
                    }
                    // If songs do not break tie, look at timestamp
                    else if( (db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1) ) ||
                            (!db.getSongLikedStatus(songNamejPLUS1) &&
                                    !db.getSongLikedStatus(songNamej ))) {
                        if( db.getCurrentSongTimestamp(songNamej).compareTo(db.getCurrentSongTimestamp(songNamejPLUS1)) < 0) {
                            HashMap.Entry<Integer,Integer> temp = new HashMap.SimpleEntry<>(flashbackList.get(j).getKey(),
                                    flashbackList.get(j).getValue());
                            //Log.d("TEMP NOW IS", metadataGetter.getName(temp.getKey()));
                            flashbackList.set(j, flashbackList.get(j+1));
                            //Log.d("THE NEW J IS", metadataGetter.getName(flashbackList.get(j).getKey()));
                            flashbackList.set(j+1, temp);
                            //Log.d("THE NEW J+1 IS", metadataGetter.getName(flashbackList.get(j+1).getKey()));
                            done = false;
                        }
                    }

                    //TODO: Remove this
//                    else {
//                        Log.d("NO SWAP","");
//                    }

                }

            }
            /*
                // Printing at this iteration
            for(int k = 0; k < flashbackList.size(); k++){
                Log.d("FlashbackListAtIteration" + i + "[" + k + "]", metadataGetter.getName(flashbackList.get(k).getKey()));
            }
            */
            if( done) {
                break;
            }

        }

        for( int i = 0; i < flashbackList.size(); i++ ) {
            finalList.add(flashbackList.get(i).getKey());
            Log.d("finalList[" + i + "]", metadataGetter.getName(finalList.get(i)));
        }

        return finalList;
    }

    /**
     * populates get populate hashMap with song names
     */
    private void populateAllSongs() {
        int[] songIDs = SongsActivity.getSongIDs();
        for( int i = 0; i < songIDs.length; i++ ) {
            // Pushing song's name and ID to HashMap
            allSongIDs.put(metadataGetter.getName(songIDs[i]), songIDs[i]);
        }
    }

    /**
     * maanages counting of duplicates in hashMap
     * @param list - list of unsorted songIDs, with number of occurrences as the points
     * @return hashMap containing unique songIDs mapped to their points
     */
    public HashMap<Integer, Integer> getDuplicateCount(ArrayList<Integer> list) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for( int i: list ) {
            if( hashMap.get(i) != null ) {
                hashMap.put(i, hashMap.get(i) + 1);
            }
            else {
                hashMap.put(i, 1);
            }
        }
        return hashMap;
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

    // Setter for database
    public void setDatabase(database db) {
        this.db = db;
    }

}