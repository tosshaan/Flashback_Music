package comf.example.tydia.cse_110_team_project_team_15_1; /**
 * Created by Cory Liang on 2/7/2018.
 */
//get API key for official google maps server https://developers.google.com/maps/documentation/android-api/start

import android.app.Activity;
import android.location.Address;
import android.location.Location;

import java.io.IOException;
import java.util.*;
import java.sql.Timestamp;
import java.lang.*;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.location.Geocoder;
import android.util.Log;

/**
 * Class to manage all the data related to location and time
 * of where and when songs were played, respectively
 */
public class database {

    private String currSongName;
    private Timestamp currSongTime;
    private String currSongAddress;
    private HashMap<String, ArrayList<String>> SongsAtLocation;
    private HashMap<String, SongInfo> SongsInformation;
    private boolean finishCheck;
    ArrayList<String> morning, noon, evening, mon, tue, wed, thur, fri, sat, sun;

    /**
     * constructor
     */
    public database() {
        SongsAtLocation = new HashMap<String, ArrayList<String>>();
        SongsInformation = new HashMap<String, SongInfo>();
        morning = new ArrayList<String>();
        noon = new ArrayList<String>();
        evening = new ArrayList<String>();
        mon = new ArrayList<String>();
        tue = new ArrayList<String>();
        wed = new ArrayList<String>();
        thur = new ArrayList<String>();
        fri = new ArrayList<String>();
        sat = new ArrayList<String>();
        sun = new ArrayList<String>();
    }

    /**
     * manage data when a song is requested
     * @param SongName - name of song that is to be played
     * @param context - activity context
     */
    public void startSongInfoRequest(String SongName, Context context, Timestamp time) throws IOException {

        currSongName = SongName;

        //Getting time
        currSongTime = time;
        Log.d("database", "Current song time is " + currSongTime);

        Location myLoc = MainActivity.getCurrLoc();

        //might just throw all request permission into method here
        Log.d("database", myLoc.getLatitude() + " , " + myLoc.getLongitude());
        if(myLoc != null) {
            finishCheck = true;

            currSongAddress = getAddress(myLoc, context);
            Log.d("database", "StartSongInfoRequest is " + currSongAddress);
        }
        else {
            finishCheck = false;
        }
    }


    /**
     * manage data when a song finishes playing
     * @param finishedPlaying - true if song has finished playing
     */
 
    public void finishSongInfoRequest(boolean finishedPlaying, boolean wasDisliked){


        Log.d("database", "Song Finish request initiated");

        if(!finishedPlaying){
            if(SongsInformation.containsKey(currSongName) && wasDisliked){
                SongsInformation.get(currSongName).dislikeSong(true);
            }
            else if(!SongsInformation.containsKey(currSongName) && wasDisliked){
                SongInfo song = new SongInfo(null, null, currSongName);
                song.dislikeSong(true);
                SongsInformation.put(currSongName, song);
            }
            else if(SongsInformation.containsKey(currSongName) && !wasDisliked){
                SongsInformation.get(currSongName).likeSong(true);
            }
            else{
                SongInfo song = new SongInfo(null, null, currSongName);
                song.likeSong(true);
                SongsInformation.put(currSongName, song);
            }
            return;
        }

        if (finishCheck != false) {

            SongInfo song = new SongInfo(currSongTime, currSongAddress, currSongName);
            Log.d("database", currSongAddress);

            //update location song list
            if (SongsAtLocation.containsKey(currSongAddress)) {
                if ((SongsAtLocation.get(currSongAddress)).contains(currSongName)) {
                    (SongsAtLocation.get(currSongAddress)).remove(currSongName);
                    (SongsAtLocation.get(currSongAddress)).add(currSongName);
                } else {
                    (SongsAtLocation.get(currSongAddress)).add(currSongName);
                }

            } else {
                ArrayList<String> songs = new ArrayList<String>();
                songs.add(currSongName);
                SongsAtLocation.put(currSongAddress, songs);
            }
            //update information on the song
            if (SongsInformation.containsKey(currSongName)) {
                SongsInformation.get(currSongName).LocationSetter(currSongAddress);
                SongsInformation.get(currSongName).timeSetter(currSongTime);
            } else {
                SongsInformation.put(currSongName, song);
            }
            //update day song list
            Calendar cal = Calendar.getInstance();
            Log.d("database", currSongTime.toString());
            cal.setTime(currSongTime);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day == GregorianCalendar.MONDAY) {
                if (!mon.contains(currSongName)) {
                    mon.add(currSongName);
                }
            } else if (day == GregorianCalendar.TUESDAY) {
                if (!tue.contains(currSongName)) {
                    tue.add(currSongName);
                }
            } else if (day == GregorianCalendar.WEDNESDAY) {
                if (!wed.contains(currSongName)) {
                    wed.add(currSongName);
                }
            } else if (day == GregorianCalendar.THURSDAY) {
                if (!thur.contains(currSongName)) {
                    thur.add(currSongName);
                }
            } else if (day == GregorianCalendar.FRIDAY) {
                if (!fri.contains(currSongName)) {
                    fri.add(currSongName);
                }
            } else if (day == GregorianCalendar.SATURDAY) {
                if (!sat.contains(currSongName)) {
                    sat.add(currSongName);
                }
            } else if (day == GregorianCalendar.SUNDAY) {
                if (!sun.contains(currSongName)) {
                    sun.add(currSongName);
                }
            } else {
                Log.d("database", "Error trying to add to day of week list");
            }

            //add to time of day lists
            if (currSongTime.getHours() < 11 && currSongTime.getHours() >= 5) {
                if (!morning.contains(currSongName)) {
                    morning.add(currSongName);
                }
            } else if (currSongTime.getHours() >= 11 && currSongTime.getHours() < 17) {
                if (!noon.contains(currSongName)) {
                    noon.add(currSongName);
                }
            } else {
                if (!evening.contains(currSongName)) {
                    noon.add(currSongName);
                    Log.d("database","added right time");
                }
            }
        }
    }

    /**
     * Method that returns last played time of the song passed
     * @param SongName - name of song whose timestamp needs to be returned
     * @return timestamp of SongName
     */
    public Timestamp getCurrentSongTimestamp ( String SongName){
        if (SongsInformation.containsKey(SongName) == false || SongsInformation.get(SongName).timeGetter() == null || SongsInformation.get(SongName).timeGetter().equals(new Timestamp(0))) {
            Log.d("database", "Song hasn't finished playing before!");
            return null;
        }
        else {
            return SongsInformation.get(SongName).timeGetter();
        }
    }

    /**
     * Method that returns the last played location of the song that's passed
     * @param SongName - name of song whose last played location needs to be returned
     * @param context - context
     * @return String location of where SongName song was last played
     */
    public String getCurrentSongLastLocation ( String SongName, Context context) throws IOException {
        if (SongsInformation.containsKey(SongName) == false || SongsInformation.get(SongName).locGetter() == null) {
            Log.d("database", "Song hasn't finished playing before!");
            return null;
        }
        else {
            return SongsInformation.get(SongName).locGetter();
        }
    }

    /**
     * Method returns an array of all song names played at a location whose address is passed
     * @param address - address of location
     * @return array of song names played at that location
     */
    public ArrayList <String> getSongsPlayedAtLocation ( String address){
        Log.d("database","Checking for " + address);
        if (SongsAtLocation.containsKey(address) == false) {
            Log.d("database", "No songs have been played at this location!");
            return null;
        }
        else {
            ArrayList<String> songs = new ArrayList<String>(SongsAtLocation.get(address));
            return songs;
        }
    }

    /**
     * Method returns an arraylist of all songs played at a particular time
     * @param time - time for obtaining list
     * @return list of songs played at the time passed
     */
    public ArrayList<String> getSongsAtTime(int time){
        if(time < 11 && time >= 5){
            return morning;
        }
        else if(time >= 11 && time < 17){
            return noon;
        }
        else if((time >= 17 && time < 24) || time < 5){
            return evening;
        }
        Log.d("database", "Incorrect time");
        return new ArrayList<String>();
    }

    /**
     * returns arraylist of songs corresponding to a day of the week passed
     * @param day - int for day of the week
     * @return arraylist of songs
     */
    public ArrayList<String> getSongsByDay(int day){
        if(day == GregorianCalendar.MONDAY){
            return mon;
        }
        else if( day == GregorianCalendar.TUESDAY){
            return tue;
        }
        else if(day == GregorianCalendar.WEDNESDAY){
            return wed;
        }
        else if(day == GregorianCalendar.THURSDAY){
            return thur;
        }
        else if(day == GregorianCalendar.FRIDAY){
            return fri;
        }
        else if(day == GregorianCalendar.SATURDAY){
            return sat;
        }
        else if(day == GregorianCalendar.SUNDAY){
            return sun;
        }
        Log.d("database","Incorrect day");
        return new ArrayList<String>();
    }

    /**
     * Method to check if a song is disliked or not
     * @param SongName - name of song
     * @return true if song is disliked
     */
    public boolean getSongDislikedStatus ( String SongName){
        if(!SongsInformation.containsKey(SongName)){
            return false;
        }
        return SongsInformation.get(SongName).isDisliked();
    }

    /**
     * Method to check id a ong is liked
     * @param SongName - name of song
     * @return true if song is liked
     */
    public boolean getSongLikedStatus ( String SongName){
        if(!SongsInformation.containsKey(SongName)){
            return false;
        }
        return SongsInformation.get(SongName).isLiked();
    }

    /**
     * setter for disliking a song
     * @param SongName - song to dislike
     * @param isDisliked - false if not already disliked
     */
    public void setDislikedStatus ( String SongName, boolean isDisliked) {
        if (SongsInformation.containsKey(SongName) == false) {
            Log.d("database", "Song doesn't exist");
            SongInfo curr = new SongInfo(null, null, SongName);
            curr.dislikeSong(isDisliked);
            SongsInformation.put(SongName, curr);
        }
        else {
            SongsInformation.get(SongName).dislikeSong(isDisliked);
        }
    }

    /**
     * setter for liking a song
     * @param SongName - song to like
     * @param isLiked - true if already liked
     */
    public void setLikedStatus (String SongName, boolean isLiked) {
        if (SongsInformation.containsKey(SongName) == false) {
            Log.d("database", "Song doesn't exist");
            SongInfo curr = new SongInfo(null, null, SongName);
            curr.likeSong(isLiked);
            SongsInformation.put(SongName, curr);
        }
        else {
            SongsInformation.get(SongName).likeSong(isLiked);
        }
    }

    /**
     * method to check if a particular song has been played at current location
     * @param SongName - name of song
     * @return true if song has been played at
     */
    public boolean isSongHere (String SongName) {

        return SongsInformation.containsKey(SongName);
    }

    /**
     * method to check if a given address is a valid location
     * @param address - address
     * @return true if it's a location
     */
    public boolean isLocation (String address) {

        return SongsAtLocation.containsKey(address);
    }

    /**
     * method to get address of current location
     * @param loc - Location object of current location
     * @param context - context
     * @return a string for the address
     */
    public String getAddress(Location loc, Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        ArrayList<Address> currLocation = (ArrayList<Address>) geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
        Address addressLine = currLocation.get(0);
        String actualAddress = addressLine.getAddressLine(0);
        return actualAddress;
    }
    //TESTER METHOD
    public SongInfo getSongInfo(String songName) {
        return SongsInformation.get(songName);
    }

}
