package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import static comf.example.tydia.cse_110_team_project_team_15_1.FirebaseDB.MILLISECODNS_IN_DAY;

/**
 * Activity Class for the mediaPlayer functionality of the currently playing song.
 * Opened when a particular song name is clicked from SongsActivity or AlbumSongsActivity
 * Redirects to FlashBackActivity
 */
public class SongInfoActivity extends AppCompatActivity implements Observer {

    //private MediaPlayer mediaPlayer;
    private myMusicPlayer musicPlayer;
    MetadataGetter metadataGetter;
    private static int x  = 0;
    private boolean playFlag = true;
    private int songIndex;
    private String songName;
    private String[] songsUri;
    private FirebaseDB firebaseDB;
    //private playerSubject subject;
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFireBaseRef = database.getReference();
        firebaseDB = new FirebaseDB(database, myFireBaseRef);
        myData = MainActivity.data;


        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // = getIntent().getIntExtra("songID", 0);

        //songName = getIntent().getStringExtra("songName");
        Bundle bundle = getIntent().getExtras();
        songsUri = bundle.getStringArray("list");
        songIndex = getIntent().getIntExtra("songIndex", 0);
        // Creating metadatagetter
        metadataGetter = new MetadataGetter(this);
        metadataGetter.setPath(songsUri[songIndex]);

        songName = metadataGetter.getName();

        //SongsIDs = bundle.getIntArray("SongsIDs");

        musicPlayer = new myMusicPlayer();
        musicPlayer.regObserver(this);

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
        final Button launchVibe = (Button) findViewById(R.id.b_vibe_songinfo);
        launchVibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                if (playFlag) {
                    musicPlayer.pause();
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    playFlag = false;
                }
                launchVibe();
            }
        });
    }

    /**
     * Helper for launching FlashbackActivity
     */
    public void launchVibe() {
        Intent intent = new Intent (this, VibeModeActivity.class);
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
     * Updates the Last Play Location, Last Played Time, and username
     */
    private void updateLastPlayedInfo(){
        TextView lastTime = (TextView) findViewById(R.id.text_timeAndDate);
        TextView lastLoc = (TextView) findViewById(R.id.textView4);
        TextView lastUsername = (TextView) findViewById(R.id.usernameField);

        /*TODO: change to get remote last played location and time instead of local

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

        //end TODO*/

        firebaseDB.getLastSongPlayer(songName, System.currentTimeMillis(),new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList, String latestAddress, String latestUser, long latestTime) {
                if(latestTime == 0){
                    lastLoc.setText("");
                    lastTime.setText("Song has not been played before!");
                    lastUsername.setText("");
                }
                else {
                    long time = latestTime / MILLISECODNS_IN_DAY;
                    LocalDate songDate = LocalDate.ofEpochDay(time);
                    lastTime.setText(songDate.toString());

                    lastLoc.setText(latestAddress);

                    String userToPrint = GoogleHelper.getDisplayName(latestUser);
                    lastUsername.setText(userToPrint);
                }
            }
        });
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
        });
        */


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

    @Override
    public void update() {
        //TODO: Figure some stuff out
        updateLastPlayedInfo();
        updateDislikedButton();
        updateLikedButton();
        myData.finishSongInfoRequest(true, false);
        Location myLoc = MainActivity.getCurrLoc();
        String currSongAddress = "";
        try {
            currSongAddress = myData.getAddress(myLoc, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("THIS HAS HAPPeNED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!","THIS HAS HAPPeNED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        firebaseDB.submit(MainActivity.myPersonalID, currSongAddress, songName, System.currentTimeMillis(), Uri.parse(songsUri[songIndex]));

    }
}
