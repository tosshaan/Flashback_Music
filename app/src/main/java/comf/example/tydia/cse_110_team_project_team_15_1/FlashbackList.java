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
        populateAllSongs();
    }

    //Method to populate list
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

    // Sorting songs, breaking ties first by like then by most recently played
    private ArrayList<Integer> breakTies(ArrayList<Map.Entry<Integer, Integer>> flashbackList) {
        ArrayList<Integer> finalList = new ArrayList<>();
        boolean done = false;

        //print original arraylist
        for(int i = 0; i < flashbackList.size(); i++){
            Log.d("FlashbackList[" + i + "]", metadataGetter.getName(flashbackList.get(i).getValue()));
        }


        for( int i = 0; i < flashbackList.size()-1; i++ ) {
            done = true;
            for( int j = 0; j < flashbackList.size()-i-1; j++ ) {
                String songNamej = metadataGetter.getName(flashbackList.get(j).getKey());
                String songNamejPLUS1 = metadataGetter.getName(flashbackList.get(j+1).getKey());

                Log.d("SONG I IS:", songNamej);
                Log.d("SONG I's POINTS ARE", flashbackList.get(i).getValue().toString());
                Log.d("SONG J IS:", songNamejPLUS1);
                Log.d("SONG J's POINTS ARE", flashbackList.get(j).getValue().toString());
                if( flashbackList.get(i).getValue() == flashbackList.get(j).getValue() ) {
                    // Switching songs if liking breaks a tie
                    boolean result = !db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1);
                    if( result )
                        Log.d("RESULT OF IF STATEMENT IS", "True");
                    else
                        Log.d("RESULT OF IF STATEMENT IS", "False");
                    if( !db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1 ) ) {
                        HashMap.Entry<Integer,Integer> temp = new HashMap.SimpleEntry<>(flashbackList.get(j).getKey(),
                                flashbackList.get(j).getValue());
                        Log.d("TEMP NOW IS", temp.toString());
                        flashbackList.set(j, flashbackList.get(j+1));
                        Log.d("THE NEW J IS", flashbackList.get(j).toString());
                        flashbackList.set(j+1, temp);
                        Log.d("THE NEW J+1 IS", flashbackList.get(j+1).toString());
                        done = false;
                    }
                    // If songs do not break tie, look at timestamp
                    else if( (db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamej) ) ||
                            (!db.getSongLikedStatus(songNamejPLUS1) &&
                                    !db.getSongLikedStatus(songNamej ))) {
                        if( db.getCurrentSongTimestamp(songNamej).compareTo(db.getCurrentSongTimestamp(songNamejPLUS1)) < 0) {
                            HashMap.Entry<Integer,Integer> temp = new HashMap.SimpleEntry<>(flashbackList.get(j).getKey(),
                                    flashbackList.get(j).getValue());
                            flashbackList.set(j, flashbackList.get(j+1));
                            flashbackList.set(j+1, temp);
                            done = false;
                        }
                    }

                }
                if( done) {
                    break;
                }
            }
        }

        for( int i = 0; i < flashbackList.size(); i++ ) {
            finalList.add(flashbackList.get(i).getKey());
            Log.d("finalList[" + i + "]", metadataGetter.getName(finalList.get(i)));
        }

        return finalList;
    }

    // Method to populate song's hashMap
    public void populateAllSongs() {
        int[] songIDs = SongsActivity.getSongIDs();
        for( int i = 0; i < songIDs.length; i++ ) {
            // Pushing song's name and ID to HashMap
            allSongIDs.put(metadataGetter.getName(songIDs[i]), songIDs[i]);
        }
    }

    // Method to count duplicates in ArrayList
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

    /*comparator for flashbackList elements based on points
    @Override
    public int compare(Integer val1, Integer val2) {
        return val1 > val2 ? -1 : (val1 == val2 ? 0 : 1);
    }

    public ArrayList<Integer> getFlashbackList (HashMap<Integer, Integer> hMap) {
        //ArrayList<Integer> fbList = new ArrayList<>();
        //Transfer as List and sort it
        ArrayList<Map.Entry<Integer, Integer>> fbList = new ArrayList(hMap.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){

            public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }});

        System.out.println(l);
        return fbList;

    }*/

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