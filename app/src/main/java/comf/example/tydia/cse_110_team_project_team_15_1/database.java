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

public class database {

    private String currSongName;
    private Timestamp currSongTime;
    private String currSongAddress;
    private HashMap<String, ArrayList<String>> SongsAtLocation;
    private HashMap<String, SongInfo> SongsInformation;
    private boolean finishCheck;
    ArrayList<String> morning, noon, evening, mon, tue, wed, thur, fri, sat, sun;


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

    public void startSongInfoRequest(String SongName, Context context) throws IOException {
        currSongName = SongName;

        //Getting time
        currSongTime = new Timestamp(System.currentTimeMillis());
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
            if (currSongTime.getHours() < 8) {
                if (!morning.contains(currSongName)) {
                    morning.add(currSongName);
                }
            } else if (currSongTime.getHours() <= 16) {
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

    public Timestamp getCurrentSongTimestamp ( String SongName){
        if (SongsInformation.containsKey(SongName) == false || SongsInformation.get(SongName).timeGetter() == null || SongsInformation.get(SongName).timeGetter().equals(new Timestamp(0))) {
            Log.d("database", "Song hasn't finished playing before!");
            return null;
        }
        else {
            return SongsInformation.get(SongName).timeGetter();
        }
    }

    public String getCurrentSongLastLocation ( String SongName, Context context) throws IOException {
        if (SongsInformation.containsKey(SongName) == false || SongsInformation.get(SongName).locGetter() == null) {
            Log.d("database", "Song hasn't finished playing before!");
            return null;
        }
        else {
            return SongsInformation.get(SongName).locGetter();
        }
    }

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

    public ArrayList<String> getSongsAtTime(int time){
        if(time < 8){
            return morning;
        }
        else if(time <= 16){
            return noon;
        }
        else if(time <= 24){
            return evening;
        }
        Log.d("database", "Incorrect time");
        return new ArrayList<String>();
    }

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

    public boolean getSongDislikedStatus ( String SongName){
        if(!SongsInformation.containsKey(SongName)){
            return false;
        }
        return SongsInformation.get(SongName).isDisliked();
    }

    public boolean getSongLikedStatus ( String SongName){
        if(!SongsInformation.containsKey(SongName)){
            return false;
        }
        return SongsInformation.get(SongName).isLiked();
    }

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

    public boolean isSongHere (String SongName) {
        return SongsInformation.containsKey(SongName);
    }
    public boolean isLocation (String address) {
        return SongsAtLocation.containsKey(address);
    }
    public String getAddress(Location loc, Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        ArrayList<Address> currLocation = (ArrayList<Address>) geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
        Address addressLine = currLocation.get(0);
        String actualAddress = addressLine.getAddressLine(0);
        return actualAddress;
    }

}
