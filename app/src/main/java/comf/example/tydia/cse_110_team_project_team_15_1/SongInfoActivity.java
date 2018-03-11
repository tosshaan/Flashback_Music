package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Activity Class for the mediaPlayer functionality of the currently playing song.
 * Opened when a particular song name is clicked from SongsActivity or AlbumSongsActivity
 * Redirects to FlashBackActivity
 */
public class SongInfoActivity extends AppCompatActivity {

    //private MediaPlayer mediaPlayer;
    private myMusicPlayer musicPlayer;
    MetadataGetter metadataGetter;
    private static int x  = 0;
    private boolean playFlag = true;
    private int songIndex;
    private String songName;
    private int [] SongsIDs;
    private String[] songsUri;
    //private boolean albumMode = true;

    database myData;

    /**
     * This method runs when the activity is created
     * Contains all functionality for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myData = MainActivity.data;


        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // = getIntent().getIntExtra("songID", 0);

        //songName = getIntent().getStringExtra("songName");
        Bundle bundle = getIntent().getExtras();
        songsUri = bundle.getStringArray("list");

        //SongsIDs = bundle.getIntArray("SongsIDs");
        songIndex = getIntent().getIntExtra("songIndex", 0);
        musicPlayer = new myMusicPlayer();

        musicPlayer.setMusic(songsUri, songIndex);

        musicPlayer.play();


        updateLastPlayedInfo();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        // Storing info from song to database
        try {

            myData.startSongInfoRequest(songName, getApplicationContext(), new Timestamp(System.currentTimeMillis()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        //myData.finishSongInfoRequest(true, false);

        // Creating metadatagetter
        metadataGetter = new MetadataGetter(this);
        metadataGetter.setPath(songsUri[songIndex]);

        songName = metadataGetter.getName();

        TextView showMetadata = (TextView) findViewById(R.id.text_SongName);
        showMetadata.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());

        // play and pause music
        final Button playButton = (Button) findViewById(R.id.button_play2);
        final Button pauseButton = (Button) findViewById(R.id.button_pause2);
        playButton.setVisibility(View.GONE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Log.d("tag", "index = " + songIndex + " // arraysize = " + albumSongsIDs.length );
            //    Log.d("songid", "songid = " + albumSongsIDs[songIndex] );
                musicPlayer.play();
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }
            }
        });

        setFinishListener(true);

        /**
         * functionality for clicking previous button
         */
        Button prevButton = (Button) findViewById(R.id.button_prev2);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayer.prev();
                if (songIndex > 0) {
                    songIndex--;

                    metadataGetter.setPath(songsUri[songIndex]);

                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
                    songName = metadataGetter.getName();
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());

                    updateLastPlayedInfo();
                    updateDislikedButton();
                    updateLikedButton();
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    //get current information to update song if needed
                    try {

                        myData.startSongInfoRequest(songName, getApplicationContext(), new Timestamp(System.currentTimeMillis()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //myData.finishSongInfoRequest(true, false);
                    playFlag = true;
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                    setFinishListener(true);

                } else {
                    Toast.makeText(getApplicationContext(), "No more previous songs", Toast.LENGTH_SHORT).show();
                    playFlag = false;
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    setFinishListener(false);

                }

            }

        });

        /**
         * functionality for clicking next button
         */
        Button nextButton = (Button) findViewById(R.id.button_next2);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipSong();
                //musicPlayer.skip();
                if (songIndex < (songsUri.length - 1)) {
                    songIndex++;

                    //reset the visibility of pause button
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);


                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
                    songName = metadataGetter.getName();
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());

                    updateLastPlayedInfo();
                    updateDislikedButton();
                    updateLikedButton();


                    Timestamp time = new Timestamp(System.currentTimeMillis());

                    //get current information to update song if needed

                    try {

                        myData.startSongInfoRequest(songName, getApplicationContext(), new Timestamp(System.currentTimeMillis()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //myData.finishSongInfoRequest(true, false);
                    setFinishListener(true);
                    playFlag = true;

                } else {
                    Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();

                    setFinishListener(false);
                }
            }
        });

        /**
         * functionality for clicking pause
         */
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicPlayer.pause();
                playFlag = true;
                playButton.setVisibility(View.VISIBLE);
                Log.d("songActivity", ""+playFlag);
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }

            }
        });

        // like and dislike functionality
        final ToggleButton dislikeButton = (ToggleButton) findViewById(R.id.button_dislike2);
        final ToggleButton likeButton = (ToggleButton) findViewById(R.id.button_like2);
        likeButton.setText(null);
        likeButton.setTextOn(null);
        likeButton.setTextOff(null);
        dislikeButton.setText(null);
        dislikeButton.setTextOn(null);
        dislikeButton.setTextOff(null);

        // DISLIKE
        if(myData.getSongDislikedStatus(songName)){
            //set dislike button to be in highlighted state (because the song was already disliked at a previous time). Please do the same in the methods at the bottom of file
            dislikeButton.setChecked(true);
            likeButton.setChecked(false);
        }
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myData.getSongDislikedStatus(songName)){
                    myData.setDislikedStatus(songName, false);
                    // set button back to unhighlighted version
                    dislikeButton.setChecked(false);
                }
                else{
                    myData.setDislikedStatus(songName, true);
                    myData.setLikedStatus(songName, false);
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    //dislikeButton.setChecked(true);
                    //likeButton.setChecked(false);
                    //get current information to update song if needed
                    try {

                        myData.startSongInfoRequest(songName, getApplicationContext(),new Timestamp(System.currentTimeMillis()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myData.finishSongInfoRequest(false, true);



                    skipSong();
                    //musicPlayer.skip();

                    updateLastPlayedInfo();
                    updateDislikedButton();
                    updateLikedButton();

                }
                //setFinishListener();
            }
        });

        // LIKE
        if(myData.getSongLikedStatus(songName)){
            //set like button to be in highlighted state because the song was already liked previously. Please do the same in the methods at the bottom of the file
            likeButton.setChecked(true);
            dislikeButton.setChecked(false);
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myData.getSongLikedStatus(songName)){
                    myData.setLikedStatus(songName, false);
                    // set button back to unhighlighted state
                    likeButton.setChecked(false);
                }
                else{
                    myData.setLikedStatus(songName, true);
                    // change to highlighted state
                    likeButton.setChecked(true);
                    dislikeButton.setChecked(false);
                }
                Timestamp time = new Timestamp(System.currentTimeMillis());
                //get current information to update song if needed
                try {

                    myData.startSongInfoRequest(songName, getApplicationContext(), new Timestamp(System.currentTimeMillis()));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                myData.finishSongInfoRequest(false, false);
            }
        });

        /**
         * functionality for going back to last screen
         */
        Button switchScreen = (Button) findViewById(R.id.button_songInfoBack);
        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());

                //Set up to restore to previous screen when back button is hit
                SharedPreferences lastScreen = getApplicationContext().getSharedPreferences("Screen", MODE_PRIVATE);
                SharedPreferences.Editor edit = lastScreen.edit();
                edit.putBoolean("currentState", false);
                edit.apply();

                finish();
            }
        });

        /**
         * goes to FlashbackActivity
         */
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_songinfo);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                if (playFlag) {
                    musicPlayer.pause();
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    playFlag = false;
                }
                launchFlashback();
            }
        });
    }

    /**
     * Helper for launching FlashbackActivity
     */
    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(isChangingConfigurations() && musicPlayer.isPlaying()) {

        }
    }

    /**
     * calls super class' onDestroy() method
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayer.release();
    }

    /*
     * Call whenever a song changes within the same activity
     * Updates the Last Play Location and Last Played Time
     */
    private void updateLastPlayedInfo(){
        TextView lastTime = (TextView) findViewById(R.id.text_timeAndDate);
        TextView lastLoc = (TextView) findViewById((R.id.textView4));
        try {
            if(myData.getCurrentSongLastLocation(songName, this)!= null){
                lastLoc.setText(myData.getCurrentSongLastLocation(songName, this));
            }
            else{
                lastLoc.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(myData.getCurrentSongTimestamp(songName) != null){
            lastTime.setText(myData.getCurrentSongTimestamp(songName).toString());
        }
        else{
            lastTime.setText("Song has not been played before");
        }
    }

    /**
     * Update methods to change the look of the like and dislike buttons whenever the song changes, as we are not starting a new activity
     * Call only after the new song has started playing and the songName field has been updated
     */
    private void updateDislikedButton(){
        ToggleButton dislikeButton = (ToggleButton) findViewById(R.id.button_dislike2);
        if(myData.getSongDislikedStatus(songName)){
            dislikeButton.setChecked(true);
        }
        else {
            dislikeButton.setChecked(false);
        }
    }

    /**
     * Method for like button functionality
     */
    private void updateLikedButton(){
        ToggleButton likeButton = (ToggleButton) findViewById(R.id.button_like2);
        if(myData.getSongLikedStatus(songName)){
            likeButton.setChecked(true);
        }
        else {
            likeButton.setChecked(false);
        }
    }

    /**
     * Method to give a toast message when song finishes
     */
    private void setFinishListener(boolean request) {
        /*
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "FINISHED PLAYING A SONG", Toast.LENGTH_SHORT).show();
                if (request) {
                    myData.finishSongInfoRequest(true, false);
                }
                skipSong();
                //setFinishListener(true);
                updateLastPlayedInfo();
                updateDislikedButton();
                updateLikedButton();
            }
        }); */

    }

    /**
     * Method to skip a song
     */
    public void skipSong() {
        musicPlayer.skip();
        if (songIndex < (songsUri.length - 1)) {
            songIndex++;


            //loadMedia(albumSongsIDs[songIndex]);

            metadataGetter.setPath(songsUri[songIndex]);
            TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
            songName = metadataGetter.getName();
            showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());
            setFinishListener(true);

        } else {
            Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();
            setFinishListener(false);

        }
    }
}
