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

public class SongInfoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    MetadataGetter metadataGetter;
    private static int MEDIA_RES_ID = 0;
    private boolean playFlag = true;
    private int songIndex;
    private String songName;
    private int [] SongsIDs;
    //private boolean albumMode = true;

    database myData;

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

        MEDIA_RES_ID = getIntent().getIntExtra("songID", 0);
        songName = getIntent().getStringExtra("songName");
        Bundle bundle = getIntent().getExtras();
        //albumMode = getIntent().getBooleanExtra("albumMode", false);
        //if (albumMode) {
            SongsIDs = bundle.getIntArray("SongsIDs");
            songIndex = getIntent().getIntExtra("songIndex", 0);
        //}

        mediaPlayer = MediaPlayer.create(this, MEDIA_RES_ID);
        mediaPlayer.start();

        updateLastPlayedInfo();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        // Storing info from song to database
        try {
            myData.startSongInfoRequest(songName, getApplicationContext(),time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //myData.finishSongInfoRequest(true, false);

        // Creating metadatagetter
        metadataGetter = new MetadataGetter(this);

        TextView showMetadata = (TextView) findViewById(R.id.text_SongName);
        showMetadata.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist(MEDIA_RES_ID) + "\nAlbum: " + metadataGetter.getAlbum(MEDIA_RES_ID));

        // play and pause music

        final Button playButton = (Button) findViewById(R.id.button_play2);
        final Button pauseButton = (Button) findViewById(R.id.button_pause2);
        playButton.setVisibility(View.GONE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Log.d("tag", "index = " + songIndex + " // arraysize = " + albumSongsIDs.length );
            //    Log.d("songid", "songid = " + albumSongsIDs[songIndex] );
                mediaPlayer.start();
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }
            }
        });

        // finish playing a song
        setFinishListener();

        Button prevButton = (Button) findViewById(R.id.button_prev2);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songIndex > 0) {
                    songIndex--;
                    mediaPlayer.reset();
                    MEDIA_RES_ID = SongsIDs[songIndex];

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);

                    mediaPlayer.start();

                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
                    songName = metadataGetter.getName(MEDIA_RES_ID);
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist(MEDIA_RES_ID) + "\nAlbum: " + metadataGetter.getAlbum(MEDIA_RES_ID));

                    updateLastPlayedInfo();
                    updateDislikedButton();
                    updateLikedButton();
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    //get current information to update song if needed
                    try {
                        myData.startSongInfoRequest(songName, getApplicationContext(), time);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //myData.finishSongInfoRequest(true, false);

                } else {
                    Toast.makeText(getApplicationContext(), "No more previous songs", Toast.LENGTH_SHORT).show();
                    mediaPlayer.reset();
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                }

            }
        });

        Button nextButton = (Button) findViewById(R.id.button_next2);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songIndex < (SongsIDs.length - 1)) {
                    songIndex++;
                    mediaPlayer.reset();
                    MEDIA_RES_ID = SongsIDs[songIndex];

                    //reset the visibility of pause button
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                    mediaPlayer.start();

                    //loadMedia(albumSongsIDs[songIndex]);

                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
                    songName = metadataGetter.getName(MEDIA_RES_ID);
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist(MEDIA_RES_ID) + "\nAlbum: " + metadataGetter.getAlbum(MEDIA_RES_ID));


                    updateLastPlayedInfo();
                    updateDislikedButton();
                    updateLikedButton();

                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    //get current information to update song if needed
                    try {
                        myData.startSongInfoRequest(songName, getApplicationContext(), time);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //myData.finishSongInfoRequest(true, false);


                } else {
                    Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();
                    mediaPlayer.reset();
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                }
            }
        });



        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
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
                        myData.startSongInfoRequest(songName, getApplicationContext(),time);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myData.finishSongInfoRequest(false, true);

                    skipSong();
                    updateDislikedButton();
                    updateLikedButton();
                }
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
                    myData.startSongInfoRequest(songName, getApplicationContext(), time);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myData.finishSongInfoRequest(false, false);
            }
        });

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

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_songinfo);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                mediaPlayer.pause();
                pauseButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
                playFlag = false;
                launchFlashback();
            }
        });
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isChangingConfigurations() && mediaPlayer.isPlaying()) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
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

    /*
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
    private void updateLikedButton(){
        ToggleButton likeButton = (ToggleButton) findViewById(R.id.button_like2);
        if(myData.getSongLikedStatus(songName)){
            likeButton.setChecked(true);
        }
        else {
            likeButton.setChecked(false);
        }
    }
    private void setFinishListener() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "FINISHED PLAYING A SONG", Toast.LENGTH_SHORT).show();
                myData.finishSongInfoRequest(true, false);
                skipSong();
                setFinishListener();
                updateLastPlayedInfo();
                updateDislikedButton();
                updateLikedButton();
            }
        });

    }

    public void skipSong() {
        if (songIndex < (SongsIDs.length - 1)) {
            songIndex++;
            mediaPlayer.reset();
            MEDIA_RES_ID = SongsIDs[songIndex];

            mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
            mediaPlayer.start();

            //loadMedia(albumSongsIDs[songIndex]);


            TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
            songName = metadataGetter.getName(MEDIA_RES_ID);
            showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist(MEDIA_RES_ID) + "\nAlbum: " + metadataGetter.getAlbum(MEDIA_RES_ID));

        } else {
            Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
        }
    }
}
