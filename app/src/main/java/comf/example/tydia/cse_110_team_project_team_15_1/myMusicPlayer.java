package comf.example.tydia.cse_110_team_project_team_15_1;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by tosshaan on 3/10/2018.
 */

public class myMusicPlayer implements playerSubject {

    MediaPlayer mp;
    int songIndex;
    String[] songList;
    boolean playFlag = true;
    songObserver observer;
    private String songName;



    public myMusicPlayer() {
        mp = new MediaPlayer();
    }

    public void setMusic(String[] list, int index) {
        songList = list;
        songIndex = index;
        try {
            mp.setDataSource("file://" + songList[index]);
            mp.prepare();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void play() {
        mp.start();
        if (playFlag == true) {
            playFlag = false;
        } else {

        }
    }

    public void pause() {
        mp.pause();
        playFlag = true;
        Log.d("songActivity", ""+playFlag);
        if (playFlag == true) {
            playFlag = false;
        } else {

        }

    }

    public void skip() {
        if (songIndex < (songList.length - 1)) {
            songIndex++;
            mp.reset();

            try {
                mp.setDataSource("file://" + songList[songIndex]);
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mp.start();

            finish();

        } else {
            mp.reset();
            try {
                mp.setDataSource("file://" + songList[songIndex]);
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    public void finish() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                skip();
                notifyObservers();
            }
        });
    }

    public void prev() {
        if (songIndex > 0) {
            songIndex--;
            mp.reset();

            try {
                mp.setDataSource("file://" + songList[songIndex]);
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mp.start();


            //updateLastPlayedInfo();
            //updateDislikedButton();
            //updateLikedButton();
            playFlag = true;
            finish();

        } else {
            mp.reset();
            try {
                mp.setDataSource("file://" + songList[songIndex]);
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playFlag = false;
            finish();

        }
    }


    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public void release() {
        mp.release();
    }

    @Override
    public void notifyObservers() {
        observer.update();
    }

    @Override
    public void regObserver(songObserver obs) {
        observer = obs;
    }
}
