package comf.example.tydia.cse_110_team_project_team_15_1;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by tosshaan on 3/5/2018.
 */

public class FirebaseDB {

    public static final int DAYS_IN_A_WEEK = 7;
    public static final int MILLISECODNS_IN_DAY = 1000 * 3600 * 24;
    public static ArrayList<String> songList;

    FirebaseDatabase database;
    DatabaseReference myFirebaseRef;

    /**
     * Constrcutor for FirebaseDB class.
     */
    public FirebaseDB() {
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
    }

    /**
     * Method to submit a new record to the database
     * @param userName the name of the user that played the song
     * @param address the address where the song was played
     * @param songName the name of the song played
     * @param date the date when the song is played (or a different value when testing)
     */
    public void submit(String userName, String address, String songName, long date ) {
        //songDatabaseRecord song = new songDatabaseRecord(userName, songName, time);
        myFirebaseRef.child(address).child(userName).child(songName).setValue(date);
    }

    /** Method to find all songs played at a particular location
     * @param address the address corresponding to the song's location
     * @return the arrayList with the song names
     */
    public ArrayList<String> getSongNamesAtLocation(String address, MyCallback callBack) {
        //ArrayList<String> songList = new ArrayList<>();
        songList = new ArrayList<>();
        //get all songs played at a particular location/address
        Query queryRef = myFirebaseRef.orderByChild(address);

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                } else {
                    Log.d("RESULT OF QUERY IS: ", snapshot.getValue().toString());
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        //songDatabaseRecord song = snapshot.getValue(songDatabaseRecord.class);
                        if (snap.getKey().equals(address)) {
                            for (DataSnapshot snapAgain : snap.getChildren()) {
                                for (DataSnapshot snapYetAgain : snapAgain.getChildren()) {
                                    // At this level, get key has the songName whose value is time
                                    // Addings song 3 times to ensure higher prority in algorithm
                                    // based on repetittions
                                    // Log.d("WHAT IS GOING ON??????", snapYetAgain.getKey());
                                    String currSong = snapYetAgain.getKey();
                                    if( !songList.contains(currSong) ) {
                                        songList.add(currSong);
                                        songList.add(currSong);
                                        songList.add(currSong);
                                        songList.add(currSong);
                                    }

                                }
                            }

                        }
                    }
                    callBack.onCallback(songList);

                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
        Log.d("WHERE IS THIS SIZE: ", songList.size() + "");
        return songList;
    }

    /**
     * Method to get all of the songs played by friends of the user
     * @param userName the user
     * @return the list of the songs
     */
    public ArrayList<String> getSongsByFriends(String userName ) {
        ArrayList<String> songList = new ArrayList<>();
        //get all songs played at a particular location/address
        Query queryRef = myFirebaseRef.orderByKey();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                }
                else {
                    Log.d("HUNH", snapshot.getValue().toString());
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        //songDatabaseRecord song = snapshot.getValue(songDatabaseRecord.class);
                        for(DataSnapshot snapAgain : snap.getChildren()){
                            Log.d("HUNHSHOULDBE2BUTIS3", snapAgain.getKey());
                            // TODO: Check if this user is a friend, somehow, and then enter the for loop if it is
                            for(DataSnapshot snapYetAgain : snapAgain.getChildren()){
                                // At this level, get key has the songName whose value is time
                                String currSong = snapYetAgain.getKey();
                                if( !songList.contains(currSong) ) {
                                    songList.add(currSong);
                                }

                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
        return songList;

    }

    /**
     * Method to get all of the songs played last week
     * @param currDate the current date;
     * @return the list of the songs
     */
    public ArrayList<String> getSongsLastWeek(LocalDate currDate, MyCallback callBack ) {
        ArrayList<String> songList = new ArrayList<>();
        //get all songs played at a particular location/address
        Query queryRef = myFirebaseRef.orderByKey();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                }
                else {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        //songDatabaseRecord song = snapshot.getValue(songDatabaseRecord.class);
                        for(DataSnapshot snapAgain : snap.getChildren()){
                            for(DataSnapshot snapYetAgain : snapAgain.getChildren()){
                                // At this level, get key has the songName whose value is time
                                String currSong = snapYetAgain.getKey();
                                long songDays = (long) snapYetAgain.getValue() / MILLISECODNS_IN_DAY;
                                LocalDate songDate = LocalDate.ofEpochDay(songDays);
                                // Checking if songs were played within one week of each other
                                if( (Math.abs(songDate.getDayOfYear() - currDate.getDayOfYear()) <= DAYS_IN_A_WEEK) &&
                                (songDate.getYear() == currDate.getYear() ) && !songList.contains(currSong)){
                                    // Adding twice for proper priority ordering
                                    songList.add(currSong);
                                    songList.add(currSong);
                                }
                            }
                        }

                    }
                    callBack.onCallback(songList);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
        Log.d("WHEN IS THIS SIZE: ", songList.size() + "");
        return songList;

    }

    private static void actuallyChangeList(DataSnapshot snapshot, ArrayList<String> songList) {

        if (snapshot == null || snapshot.getValue() == null) {
            Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
        } else {
            for (DataSnapshot snap : snapshot.getChildren()) {
                //songDatabaseRecord song = snapshot.getValue(songDatabaseRecord.class);
                for (DataSnapshot snapAgain : snap.getChildren()) {
                    for (DataSnapshot snapYetAgain : snapAgain.getChildren()) {
                        // At this level, get key has the songName whose value is time
                        // Addings song 3 times to ensure higher prority in algorithm
                        // based on repetittions
                        Log.d("WHAT IS GOING ON??????", snapYetAgain.getKey());

                        songList.add(snapYetAgain.getKey());
                        songList.add(snapYetAgain.getKey());
                        songList.add(snapYetAgain.getKey());
                        songList.add(snapYetAgain.getKey());
                        songList.add("YOYOYO");
                        Log.d("TEMP SIZE ", songList.size() + "");
                    }
                }

            }
        }
    }

}
