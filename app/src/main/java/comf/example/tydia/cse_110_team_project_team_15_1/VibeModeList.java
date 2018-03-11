package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.util.Log;

import java.sql.Array;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cadu on 10-Mar-18.
 */

public class VibeModeList {

    // Context context;
    ArrayList<String> vibeModeSongs;
    ArrayList<String> vibeModeURLs;
    database db;
    //MetadataGetter metadataGetter;


    public ArrayList<String> getVibeModeSongs() {
        return vibeModeSongs;
    }

    public ArrayList<String> getVibeModeURLs() {
        return vibeModeURLs;
    }

    /**
     * constructor
     */
    public VibeModeList(database db) {
        vibeModeSongs = new ArrayList<>();
        vibeModeURLs = new ArrayList<>();
        this.db = db;
        // do something
    }

    /**
     * populates flashback songs list
     */
    public void generateList(ArrayList<String> songNameList, ArrayList<String> URLlist) {

        //returns a hashmap with unique songs as keys and their counts as values
        HashMap<String, Integer> uniqueSongsCount = getDuplicateCount(songNameList);
        HashMap<String, Integer> uniqueURLsCount = getDuplicateCount(URLlist);

        //convert hashtable to arraylist, sorted in descending order by points (or number of duplicates)
        ArrayList<Map.Entry<String, Integer>> vibeSongsList = new ArrayList<>(uniqueSongsCount.entrySet());
        vibeSongsList.sort( Collections.reverseOrder(Comparator.comparingInt(Map.Entry::getValue)));

        ArrayList<Map.Entry<String, Integer>> vibeURLsList = new ArrayList<>(uniqueSongsCount.entrySet());
        vibeSongsList.sort( Collections.reverseOrder(Comparator.comparingInt(Map.Entry::getValue)));

        breakTies( vibeSongsList, vibeURLsList );

    }


    /**
     * Sorts songs and then breaks ties first based on like status and then based on most recently played
     * @param vibeModeSongList - arraylist of unsorted kay-value entries with song names as keys and points as value
     * @param vibeModeSongList - arraylist of unsorted kay-value entries with song URLs as keys and points as value
     * @return final arraylist of sorted song ids
     */

    private void breakTies(ArrayList<Map.Entry<String, Integer>> vibeModeSongList, ArrayList<Map.Entry<String, Integer>> vibeModeURLList) {

        boolean done = false;


        for( int i = 0; i < vibeModeSongList.size()-1; i++ ) {
            done = true;
            for( int j = 0; j < vibeModeSongList.size()-i-1; j++ ) {
                String songNamej = vibeModeSongList.get(j).getKey();
                String songNamejPLUS1 = vibeModeSongList.get(j+1).getKey();

                if( vibeModeSongList.get(j).getValue() == vibeModeSongList.get(j+1).getValue() ) {
                    // Switching songs if liking breaks a tie
                    boolean result = !db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1);
                    if( !db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1 ) ) {
                        // Switching order of both songs and URLs
                        HashMap.Entry<String,Integer> temp = new HashMap.SimpleEntry<>(vibeModeSongList.get(j).getKey(),
                                vibeModeSongList.get(j).getValue());
                        HashMap.Entry<String,Integer> tempURL = new HashMap.SimpleEntry<>(vibeModeURLList.get(j).getKey(),
                                vibeModeURLList.get(j).getValue());
                        vibeModeSongList.set(j, vibeModeSongList.get(j+1));
                        vibeModeSongList.set(j+1, temp);
                        vibeModeURLList.set(j, vibeModeURLList.get(j+1));
                        vibeModeURLList.set(j+1, tempURL);
                        done = false;
                    }
                    // If songs do not break tie, look at timestamp
                    else if( (db.getSongLikedStatus(songNamej) &&
                            db.getSongLikedStatus(songNamejPLUS1) ) ||
                            (!db.getSongLikedStatus(songNamejPLUS1) &&
                                    !db.getSongLikedStatus(songNamej ))) {
                        if( db.getCurrentSongTimestamp(songNamej).compareTo(db.getCurrentSongTimestamp(songNamejPLUS1)) < 0) {
                            HashMap.Entry<String,Integer> temp = new HashMap.SimpleEntry<>(vibeModeSongList.get(j).getKey(),
                                    vibeModeSongList.get(j).getValue());
                            HashMap.Entry<String,Integer> tempURL = new HashMap.SimpleEntry<>(vibeModeURLList.get(j).getKey(),
                                    vibeModeURLList.get(j).getValue());
                            vibeModeSongList.set(j, vibeModeSongList.get(j+1));
                            vibeModeSongList.set(j+1, temp);
                            vibeModeURLList.set(j, vibeModeURLList.get(j+1));
                            vibeModeURLList.set(j+1, tempURL);
                            done = false;
                        }
                    }

                }

            }

            if( done) {
                break;
            }

        }


        for( int i = 0; i < vibeModeSongList.size(); i++ ) {
            vibeModeSongs.add(vibeModeSongList.get(i).getKey());
            vibeModeURLs.add(vibeModeURLList.get(i).getKey());
        }

    }

    /**
     * maanages counting of duplicates in hashMap
     * @param list - list of unsorted songIDs, with number of occurrences as the points
     * @return hashMap containing unique songIDs mapped to their points
     */

    public HashMap<String, Integer> getDuplicateCount(ArrayList<String> list) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for( String s: list ) {
            if( hashMap.get(s) != null ) {
                hashMap.put(s, hashMap.get(s) + 1);
            }
            else {
                hashMap.put(s, 1);
            }
        }
        return hashMap;
    }

}
