package comf.example.tydia.cse_110_team_project_team_15_1; /**
 * Created by Cory Liang on 2/7/2018.
 */
import android.location.Address;
import android.location.Location;

import java.io.Serializable;
import java.util.*;
import java.sql.Timestamp;
import java.lang.*;

/**
 * Objects of this class correspond to each song
 */
public class SongInfo implements Serializable{
    private Timestamp time;
    private String songName;
    private String location;
    private boolean liked;
    private boolean disliked;

    //constructor
    public SongInfo(Timestamp t, String l, String songName){
        this.time = t;
        if( l != null) {
            this.location = l;
        }
        else{
            this.location = "";
        }
        this.songName = songName;
    }

    //parameterized constructor
    public SongInfo(Timestamp t, String s, String l, boolean li, boolean di){
        time = t;
        songName = s;
        if( l != null) {
            this.location = l;
        }
        else{
            this.location = "";
        }
        liked = li;
        disliked = di;
    }

    //gets time
    public Timestamp timeGetter() {
        if(this.time == null){
            return new Timestamp(0);
        }
        return this.time;
    }

    //sets time
    public void timeSetter(Timestamp time) {
        this.time = time;
    }

    //gets location
    public String locGetter() {
        return this.location;
    }

    //sets location
    public void LocationSetter(String location) {
        if( location != null) {
            this.location = location;
        }
        else{
            this.location = "";
        }
    }

    //getter for songName
    public String songGetter() {
        return this.songName;
    }

    //setter for songName
    public void songSetter( String songName) {
        this.songName = songName;
    }

    //setter for disliking a song
    public void likeSong(boolean l) {
        if (l == true) {
            this.liked = true;
            this.disliked = false;
        }
        else {
            this.liked = false;
        }
    }

    //setter for disliking a song
    public void dislikeSong(boolean isDisliked) {
        if (isDisliked == true) {
            this.disliked = true;
            this.liked = false;
        }
        else {
            this.disliked = false;
        }
    }
    //checks is song is liked
    public boolean isLiked() {
        return this.liked;
    }

    //checks if song is disliked
    public boolean isDisliked() {
        return this.disliked;
    }


}
