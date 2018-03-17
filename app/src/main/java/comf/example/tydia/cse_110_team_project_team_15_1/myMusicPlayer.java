package comf.example.tydia.cse_110_team_project_team_15_1;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tosshaan on 3/10/2018.
 */

public class myMusicPlayer implements playerSubject, Observer {

    MediaPlayer mp;
    int songIndex;
    String[] songList;
    boolean playFlag = true;
    Observer observer;
    private String songName;
    boolean firstSongPlayable = true;

    public myMusicPlayer() {
        mp = new MediaPlayer();
    }

    public void setMusic(String[] list, int index) {
        songList = list;
        songIndex = index;

        if (list == null) {
            Log.d("CAN'T PLAY IN VIBE", "setMusic: nothing in list, let other users play some songs first");
            return;
        }
        File file = new File("" +list[0]);
        Log.d("", "THE FILE IS: " + file.toString() );
        if (!file.exists()) {
            Log.d("CAN'T PLAY IN VIBE", "none of the songs are downloaded, wait a bit for first song to download");
            firstSongPlayable = false;
            return;
        }

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
            Log.d("index", "Inner musicplayer skip songIndex" + songIndex);
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
        Log.d("index", "inner musicplayer skip songIndex finished" + songIndex);
    }

    public void finish() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                notifyObservers();
                skip();


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

    public int indexGetter() {
        return this.songIndex;
    }
    @Override
    public void notifyObservers() {
        observer.update();
    }

    @Override
    public void regObserver(Observer obs) {
        observer = obs;
    }

    @Override
    public void delObserver(Observer obs) {
        observer = null;
    }

    @Override
    public void update() {
        if (!firstSongPlayable) {
            firstSongPlayable = true;
            try {
                mp.setDataSource("file://" + songList[0]);
                mp.prepare();
                finish();
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
