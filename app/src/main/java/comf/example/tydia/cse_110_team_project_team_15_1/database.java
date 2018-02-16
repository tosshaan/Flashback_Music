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

public class database {

    private String currSongName;
    private Timestamp currSongTime;
    private Location currSongLocation;
    private HashMap<Location, HashMap<String, SongInfo>> SongsAtLocation;
    private HashMap<String, SongInfo> SongsInformation;
    ArrayList<String> morning, noon, evening, mon, tue, wed, thur, fri, sat, sun;

    public database() {
        SongsAtLocation = new HashMap<Location, HashMap<String, SongInfo>>();
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

    public void testInsert(){
        Location testLoc = new Location("testing");
        testLoc.setLatitude(1.123d);
        testLoc.setLongitude(1.123d);
        Timestamp testTime = new Timestamp(System.currentTimeMillis());
        SongInfo testInfo = new SongInfo(testTime, testLoc,"tester");
        HashMap<String, SongInfo> songs = new HashMap<String, SongInfo>();
        songs.put("tester", testInfo);
        SongsAtLocation.put(testLoc, null);
        SongsInformation.put("tester", testInfo);
        System.out.println("Finished inserting");
    }

    public void testPrint(){
        if(SongsInformation.containsKey("tester")){
            System.out.println("tester was found, Timestamp: " + SongsInformation.get("tester").timeGetter());
            System.out.println("Trying to find location now");
            Location findLoc = new Location("hi");
            findLoc.setLongitude(1.123d);
            findLoc.setLatitude(1.123d);
            if(SongsAtLocation.containsKey(findLoc)){
                System.out.println("Found location, printing song information:");
                ArrayList<String> songsAtLoc = getSongsPlayedAtLocation(findLoc);
                for(String s : songsAtLoc){
                    System.out.println("In loop" + s);
                }
            }
        }
        else{
            System.out.println("tester not found");
        }
    }

    public void changeTime(){
        if(SongsInformation.containsKey("tester")){
            System.out.println("Should be changing now");
            Timestamp testTime = new Timestamp(System.currentTimeMillis());
            SongsInformation.get("tester").timeSetter(testTime);
            System.out.println("time should have been set");
        }
        else{
            System.out.println("failed to change");
        }
    }

    public void startSongInfoRequest(String SongName, Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        currSongName = SongName;
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        currSongTime = new Timestamp(System.currentTimeMillis());
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }
        currSongLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Address currLocation = (Address) geocoder.getFromLocation(currSongLocation.getLatitude(),currSongLocation.getLongitude(),1);
        String addressLine = currLocation.getAddressLine(0);
    }

    public void finishSongInfoRequest(){
        SongInfo song = new SongInfo(currSongTime, currSongLocation, currSongName);
        //update location song list
        if(SongsAtLocation.containsKey(currSongLocation)){
            if(((HashMap)SongsAtLocation.get(currSongLocation)).containsKey(currSongName)){
                ((HashMap)SongsAtLocation.get(currSongLocation)).remove(currSongName);
                ((HashMap)SongsAtLocation.get(currSongLocation)).put(currSongName, null);
            }
            else {
                ((HashMap)SongsAtLocation.get(currSongLocation)).put(currSongName, null);
            }

        }
        else {
            HashMap<String, SongInfo> songs = new HashMap<String, SongInfo>();
            songs.put(currSongName, song);
            SongsAtLocation.put(currSongLocation,null);
        }
        //update information on the song
        if (SongsInformation.containsKey(currSongName)) {
            SongsInformation.remove(currSongName);
            SongsInformation.put(currSongName, song);
        }
        else {
            SongsInformation.put(currSongName, song);
        }
        //update day song list
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(currSongTime);
        int day = cal.get(GregorianCalendar.DAY_OF_WEEK);
        if(day == GregorianCalendar.MONDAY){
            if(!mon.contains(currSongName)){
                mon.add(currSongName);
            }
        }
        else if( day == GregorianCalendar.TUESDAY){
            if(!tue.contains(currSongName)){
                tue.add(currSongName);
            }
        }
        else if(day == GregorianCalendar.WEDNESDAY){
            if(!wed.contains(currSongName)){
                wed.add(currSongName);
            }
        }
        else if(day == GregorianCalendar.THURSDAY){
            if(!thur.contains(currSongName)){
                thur.add(currSongName);
            }
        }
        else if(day == GregorianCalendar.FRIDAY){
            if(!fri.contains(currSongName)){
                fri.add(currSongName);
            }
        }
        else if(day == GregorianCalendar.SATURDAY){
            if(!sat.contains(currSongName)){
                sat.add(currSongName);
            }
        }
        else if(day == GregorianCalendar.SUNDAY){
            if(!sun.contains(currSongName)){
                sun.add(currSongName);
            }
        }
        else{
            System.out.println("Error trying to add to day of week list");
        }

        //add to time of day lists
        if(currSongTime.getHours() < 8){
            if(!morning.contains(currSongName)){
                morning.add(currSongName);
            }
        }
        else if(currSongTime.getHours() <= 16){
            if(!noon.contains(currSongName)){
                noon.add(currSongName);
            }
        }
        else{
            if(!evening.contains(currSongName)){
                noon.add(currSongName);
            }
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
            return currLocation.getAddressLine(0);
        }
    }

    public ArrayList <String> getSongsPlayedAtLocation ( Location loc){
        if (SongsAtLocation.containsKey(loc) == false) {
            System.out.println("No songs have been played at this location!");
            return null;
        }
        else {
            ArrayList<String> songs = new ArrayList<String>(((HashMap) SongsAtLocation).keySet());
            return songs;
        }
    }

    public ArrayList<String> getSongsAtTime(int time){
        if(time == 0){
            return morning;
        }
        else if(time == 1){
            return noon;
        }
        else if(time == 2){
            return evening;
        }
        System.out.println("Incorrect time");
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
        System.out.println("Incorrect day");
        return new ArrayList<String>();
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

}
