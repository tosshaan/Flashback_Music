package comf.example.tydia.cse_110_team_project_team_15_1;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by tosshaan on 3/5/2018.
 */

public class FirebaseDB {

    public static final int DAYS_IN_A_WEEK = 7;
    public static final int MILLISECODNS_IN_DAY = 1000 * 3600 * 24;
    public static final int HIGHEST_PRIORITY = 4;
    public static final int SECOND_PRIORITY = 2;

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
    public void submit(String userName, String address, String songName, long date, Uri url ) {
        //songDatabaseRecord song = new songDatabaseRecord(userName, songName, time);
        myFirebaseRef.child(address).child(convertEmail(userName)).child(songName).child(convertURL(url)).setValue(date);
    }

    /** Method to find all songs played at a particular location
     * @param address the address corresponding to the song's location
     * @return the arrayList with the song names
     */
    /*
    public void getSongNamesAtLocation(String address, FirebaseQueryObserver callBack) {
        //ArrayList<String> songList = new ArrayList<>();
        ArrayList<String> songURLList = new ArrayList<>();
        ArrayList<String> songNameList = new ArrayList<>();
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
                                    if( !songNameList.contains(currSong)) {
                                        // Looping through all urls
                                        for( DataSnapshot lastSnap: snapYetAgain.getChildren() ) {
                                            String currSongURL = lastSnap.getKey();
                                            for(int i = 0; i < HIGHEST_PRIORITY; i++){
                                                songNameList.add(currSong);
                                                songURLList.add(currSongURL);
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                    callBack.update(songNameList, songURLList);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });
    }
    */

    /**
     * Method to get all of the songs played by friends of the user
     * @param userName the user
     * @return the list of the songs
     */
    /*
    public void getSongsByFriends(String userName, FirebaseQueryObserver callBack) {
        ArrayList<String> songURLList = new ArrayList<>();
        ArrayList<String> songNameList = new ArrayList<>();
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
                                if(!songNameList.contains(currSong)) {
                                    // Looping through all urls
                                    for( DataSnapshot lastSnap: snapYetAgain.getChildren() ) {
                                        String currSongURL = lastSnap.getKey();
                                        songNameList.add(currSong);
                                        songURLList.add(currSongURL);
                                    }
                                }

                            }
                        }

                    }
                    callBack.update(songNameList, songURLList);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Faile to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }
        });


    }
    */

    /**
     * Method to get all of the songs played last week
     * @param currDate the current date;
     * @return the list of the songs
     */
    /*
    public void getSongsLastWeek(LocalDate currDate, FirebaseQueryObserver callBack ) {
        ArrayList<String> songURLList = new ArrayList<>();
        ArrayList<String> songNameList = new ArrayList<>();
        //get all songs played at a particular location/address
        Query queryRef = myFirebaseRef.orderByKey();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                } else {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        for (DataSnapshot snapAgain : snap.getChildren()) {
                            for (DataSnapshot snapYetAgain : snapAgain.getChildren()) {
                                // At this level, get key has the songName whose child is url
                                String currSong = snapYetAgain.getKey();
                                if (!songNameList.contains(currSong)) {
                                    for (DataSnapshot lastSnap : snapYetAgain.getChildren()) {
                                        long songDays = (long) lastSnap.getValue() / MILLISECODNS_IN_DAY;
                                        LocalDate songDate = LocalDate.ofEpochDay(songDays);
                                        // Checking if songs were played within one week of each other
                                        if ((Math.abs(songDate.getDayOfYear() - currDate.getDayOfYear()) <= DAYS_IN_A_WEEK) ) {
                                            {
                                                String currSongURL = lastSnap.getKey();
                                                for(int i = 0; i < SECOND_PRIORITY; i++){
                                                    songNameList.add(currSong);
                                                    songURLList.add(currSongURL);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    callBack.update(songNameList, songURLList);
                }
            }

            @Override
            public void onCancelled (DatabaseError error){
                // Failed to read value
                Log.w("TAG1", "Failed to read value.", error.toException());
            }

        });

    }
*/

    /**
     * Method to get list of all songs needed for vibe mode
     * @param address the address of the user
     * @param currDate the current date
     * @param userName the name of the user
     * @param callBack the Firebase observer object
     */
    public void getAllSongsForVibe(String address, LocalDate currDate, String userName,  FirebaseQueryObserver callBack) {
        ArrayList<String> songNames = new ArrayList<>();
        ArrayList<String> songURLs = new ArrayList<>();
        ArrayList<String> songNamesAdr = new ArrayList<>();
        ArrayList<String> songURLsAdr = new ArrayList<>();
        ArrayList<String> songNamesDate = new ArrayList<>();
        ArrayList<String> songURLsDate = new ArrayList<>();
        ArrayList<String> songNamesFriend = new ArrayList<>();
        ArrayList<String> songURLsFriend = new ArrayList<>();

        // Getting database records
        Query queryRef = myFirebaseRef.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                }
                else {
                    // Looping through database records
                    for( DataSnapshot locationSnap: snapshot.getChildren() ) {
                        String currAddress = locationSnap.getKey();
                        // Looping through users in location
                        for( DataSnapshot userSnap: locationSnap.getChildren() ) {
                            String currUser = unconvertEmail(userSnap.getKey());
                            if( !currUser.equals(userName) ){
                                    // Looping through songs for user
                                for( DataSnapshot songSnap: userSnap.getChildren() ) {
                                    String currSong = songSnap.getKey();
                                    // Looping through urls for user
                                    for (DataSnapshot URLsnap : songSnap.getChildren()) {
                                        String currURL = unconvertURL(URLsnap.getKey());
                                        long songDays = (long) URLsnap.getValue() / MILLISECODNS_IN_DAY;
                                        LocalDate songDate = LocalDate.ofEpochDay(songDays);
                                        // Checking for same address
                                        if (currAddress.equals(address) && !songNamesAdr.contains(currSong)) {
                                            // Adding 4 times for priority
                                            for (int i = 0; i < HIGHEST_PRIORITY; i++) {
                                                songNamesAdr.add(currSong);
                                                songURLsAdr.add(currURL);
                                            }
                                        }
                                        // Checking for same week
                                        if ((Math.abs(songDate.getDayOfYear() - currDate.getDayOfYear()) <= DAYS_IN_A_WEEK) &&
                                                !songNamesDate.contains(currSong)) {
                                            for (int i = 0; i < SECOND_PRIORITY; i++) {
                                                songNamesDate.add(currSong);
                                                songURLsDate.add(currURL);
                                            }
                                        }

                                        // Checking if user is a friend
                                        if( GoogleHelper.getFriend(GoogleHelper.parseForEmail(currUser)) != null
                                                && !songNamesFriend.contains(currSong)) {
                                            songNames.add(currSong);
                                            songURLs.add(currURL);
                                        }

                                    }
                                }
                            }

                        }

                    }
                    // Appending all lists to final list
                    songNames.addAll( songNamesAdr );
                    songNames.addAll( songNamesDate );
                    songNames.addAll( songNamesFriend );
                    songURLs.addAll( songURLsAdr );
                    songURLs.addAll( songURLsDate );
                    songURLs.addAll( songURLsFriend );
                    callBack.update(songNames, songURLs, null , null, 0);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "failed to read value.", error.toException());
            }
        });


    }


    public void getLastSongPlayer(String userName, String songName, long time, FirebaseQueryObserver callBack ) {
        long[] latestTime = new long[1];
       // String latestAddress = null;
       // String latestUser = null;
        StringBuilder latestAddress = new StringBuilder();
        StringBuilder latestUser = new StringBuilder();
        Query queryRef = myFirebaseRef.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null || snapshot.getValue() == null) {
                    Log.d("RESULT OF QUERY IS", "NO RECORD FOUND");
                }
                else {
                    // Looping through database records
                    for( DataSnapshot locationSnap: snapshot.getChildren() ) {
                        String currAddress = locationSnap.getKey();
                        // Looping through users in location
                        for( DataSnapshot userSnap: locationSnap.getChildren() ) {
                            String currUser = unconvertEmail(userSnap.getKey());
                            // Looping through songs for user
                            for( DataSnapshot songSnap: userSnap.getChildren() ) {
                                String currSong = songSnap.getKey();
                                // Looking at who played this song if it is the appropriate song
                                if( currSong.equals(songName)) {
                                    // Looping through urls for user
                                    for (DataSnapshot URLsnap : songSnap.getChildren()) {
                                        long currTime = (long) URLsnap.getValue();
                                        // Assigning current values to return values if song was played
                                        // more recently or if this is the first time
                                        if( latestTime[0] == 0 ) {
                                            latestAddress.replace(0, latestAddress.length() -1, currAddress);
                                            latestUser.replace(0, latestUser.length() -1, currUser);
                                            latestTime[0] = currTime;
                                        }
                                        else if( Math.abs(time - currTime) < latestTime[0] ) {
                                            latestAddress.replace(0, latestAddress.length() -1, currAddress);
                                            latestUser.replace(0, latestUser.length() -1, currUser);
                                            latestTime[0] = currTime;
                                        }
                                    }
                                }
                            }

                        }

                    }

                    callBack.update(null, null, latestAddress.toString(), latestUser.toString(),
                                                latestTime[0]);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG1", "failed to read value.", error.toException());
            }
        });

    }

    /**
     * Method to remove forbidden symbols from the URL and remove path
     * @param uri the uri to be converted
     * @return the converted url
     */
    private static String convertURL(Uri uri ) {
        Log.d("URL IS: ", uri.toString());
        String newURI = uri.toString();
        newURI = newURI.replace(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(),"");
        newURI = newURI.replace(".","@");
        newURI = newURI.replace("/", "^");
        Log.d("NOW URL IS: ", newURI);
        return newURI;
    }

    /**
     * Method to convert URL back to original version
     * @param url the url that was converted
     * @return the original url back
     */
    private static String unconvertURL( String url) {
        url = url.replace("@",".");
        url = url.replace("^","/");
        return url;
    }

    /**
     * Method to convert email addresses to remove forbidden characters
     * @param email the email to be converted
     * @return the email with forbidden characters replaced
     */
    private static String convertEmail(String email) {
        email = email.replace(".","*");
        return email;
    }

    /**
     * Unconvert email back to original
     * @param email the converted email
     * @return the original email
     */
    private static String unconvertEmail(String email) {
        email = email.replace("*",".");
        return email;
    }

}
