package comf.example.tydia.cse_110_team_project_team_15_1; /**
 * Created by Cory Liang on 2/7/2018.
 */
import android.location.Address;
import android.location.Location;

import java.io.Serializable;
import java.util.*;
import java.sql.Timestamp;
import java.lang.*;

public class SongInfo implements Serializable{
    private Timestamp time;
    private String songName;
    private String location;
    private boolean liked;
    private boolean disliked;
    public SongInfo(Timestamp t, String l, String songName){
        this.time = t;
        this.location = l;
        this.songName = songName;
    }
    public SongInfo(Timestamp t, String s, String l, boolean li, boolean di){
        time = t;
        songName = s;
        location = l;
        liked = li;
        disliked = di;
    }
    public Timestamp timeGetter() {
        return this.time;
    }
    public void timeSetter(Timestamp time) {
        this.time = time;
    }
    public String locGetter() {
        return this.location;
    }
    public void LocationSetter(String location) {
        this.location = location;
    }
    public String songGetter() {
        return this.songName;
    }
    public void songSetter( String songName) {
        this.songName = songName;
    }
    public void likeSong(boolean l) {
        if (l == true) {
            this.liked = true;
            this.disliked = false;
        }
        else {
            this.liked = false;
        }
    }
    public void dislikeSong(boolean l) {
        if (l == true) {
            this.disliked = true;
            this.liked = false;
        }
        else {
            this.disliked = false;
        }
    }
    public boolean isLiked() {
        return this.liked;
    }
    public boolean isDisliked() {
        return this.disliked;
    }


}
