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
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.location.Geocoder;

public class database {

    private String currSongName;
    private Timestamp currSongTime;
    private Location currSongLocation;
    private HashMap<Location, HashMap<String, SongInfo>> SongsAtLocation;
    private HashMap<String, SongInfo> SongsInformation;

    public database() {
        SongsAtLocation = new HashMap<Location, HashMap<String, SongInfo>>();
        SongsInformation = new HashMap<String, SongInfo>();
    }

    public void startSongInfoRequest(String SongName, Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        currSongName = SongName;
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        currSongTime = new Timestamp(System.currentTimeMillis());
        System.out.println("Current song time is " + currSongTime);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            System.out.println("Don't have permission");
            return;
        }
        System.out.println("Now retrieving location");
        currSongLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currSongLocation == null) {
            System.out.println("Null reached");
            return;
        }
        System.out.println(currSongLocation.getLatitude() + " , " + currSongLocation.getLongitude());
        ArrayList<Address> currLocation = (ArrayList<Address>) geocoder.getFromLocation(currSongLocation.getLatitude(),currSongLocation.getLongitude(),1);
        Address addressLine = currLocation.get(0);
        String actualAddress = addressLine.getAddressLine(0);
        System.out.println("StartSongInfoRequest is " + actualAddress);
    }
    public void finishSongInfoRequest(){
        SongInfo song = new SongInfo(currSongTime, currSongLocation, currSongName);
        if(SongsAtLocation.containsKey(currSongLocation)){
            if(((HashMap)SongsAtLocation.get(currSongLocation)).containsKey(currSongName)){
                ((HashMap)SongsAtLocation.get(currSongLocation)).remove(currSongName);
                ((HashMap)SongsAtLocation.get(currSongLocation)).put(currSongName, song);
            }
            else {
                ((HashMap)SongsAtLocation.get(currSongLocation)).put(currSongName, song);
            }

        }
        else {
            HashMap<String, SongInfo> songs = new HashMap<String, SongInfo>();
            songs.put(currSongName, song);
            SongsAtLocation.put(currSongLocation,songs);
        }
        if (SongsInformation.containsKey(currSongName)) {
            SongsInformation.remove(currSongName);
            SongsInformation.put(currSongName, song);
        }
        else {
            SongsInformation.put(currSongName, song);
        }

    }

    public Timestamp getCurrentSongTimestamp ( String SongName){
        if (SongsInformation.containsKey(SongName) == false) {
            System.out.println("Song hasn't been player before!");
            return null;
        }
        else {
            return SongsInformation.get(SongName).timeGetter();
        }
    }
    public String getCurrentSongLastLocation ( String SongName, Context context) throws IOException {
        if (SongsInformation.containsKey(SongName) == false) {
            System.out.println("Song hasn't been played before!");
            return null;
        }
        else {
            Location retLoc = SongsInformation.get(SongName).locGetter();
            Geocoder geocoder = new Geocoder(context);
            Address currLocation = (Address) geocoder.getFromLocation(retLoc.getLatitude(),retLoc.getLongitude(),1);
            System.out.println("getting location is " + currLocation.getAddressLine(0));
            return currLocation.getAddressLine(0);
        }
    }
    public ArrayList <String> getSongsPlayedAtLocation ( Location loc){
        if (SongsAtLocation.containsKey(loc) == false) {
            System.out.println("No songs have been played at this location!");
            return null;
        }
        else {
            System.out.println("Location does exist");
            ArrayList<String> songs = new ArrayList<String>(((HashMap) SongsAtLocation).keySet());
            return songs;
        }
    }
    public boolean getSongDislikedStatus ( String SongName){
        return SongsInformation.get(SongName).isDisliked();
    }
    public boolean getSongLikedStatus ( String SongName){
        return SongsInformation.get(SongName).isLiked();
    }
    public void setDislikedStatus ( String SongName, boolean isDisliked) {
        if (SongsInformation.containsKey(SongName) == false) {
            System.out.println("Song doesn't exist");
        }
        else {
            SongsInformation.get(SongName).dislikeSong(isDisliked);
        }
    }
    public void setLikedStatus (String SongName, boolean isLiked) {
        if (SongsInformation.containsKey(SongName) == false) {
            System.out.println("Song doesn't exist");
        }
        else {
            SongsInformation.get(SongName).likeSong(isLiked);
        }
    }
    public boolean isSongHere (String SongName) {
        return SongsInformation.containsKey(SongName);
    }
    public boolean isLocation (Location location) {
        return SongsAtLocation.containsKey(location);
    }


}

