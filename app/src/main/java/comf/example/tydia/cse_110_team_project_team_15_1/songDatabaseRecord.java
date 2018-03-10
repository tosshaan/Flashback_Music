package comf.example.tydia.cse_110_team_project_team_15_1;

import com.google.firebase.database.Exclude;

import java.time.LocalDate;

/**
 * Created by Cadu on 08-Mar-18.
 */

public class songDatabaseRecord {
    private String userName;
    private String songName;
    private long time;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public long getDate() {
        return time;
    }

    public void setDate(long date) {
        this.time = date;
    }

    /**
     * Constructor for songDatabaseRecord
     * @param UN the name of the user that listened to the song
     * @param SN the name of the song that was played
     * @param dt the date
     */
    public songDatabaseRecord(String UN, String SN, long dt ) {
        userName = UN;
        songName = SN;
        time = dt;
    }

    public songDatabaseRecord() {
        userName = "NOT A USER";
        songName = "NOT A REAL SONG";
        time = 0000000;

    }



}
