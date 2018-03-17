package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.widget.ToggleButton;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Activity Class for the list of flashback songs
 * Opened when a flashback mode is selected from any normal mode activity
 * Redirects to normal mode, specifically the activity that called it in the first place
 */
public class FlashbackActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    database myData;
    String songName;
    FlashbackList flashbackList;
    MetadataGetter metadataGetter;
    MediaPlayer mp;
    int songIndex = 0;
    private int i  = 0;
    private boolean playFlag = true;


    //currently playing song
    // Need to get list of song names from the database

    String[] songNames;
    int[] flashBackSongIDs;

    /**
     * This method runs when the activity is created
     * Contains all functionality for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        SharedPreferences lastScreen = getApplicationContext().getSharedPreferences("Screen", MODE_PRIVATE);
        SharedPreferences.Editor edit = lastScreen.edit();
        edit.putString("Activity", "Flashback");
        edit.apply();

        setSupportActionBar(toolbar);
        Timestamp currTime = new Timestamp( System.currentTimeMillis() );
        metadataGetter = new MetadataGetter(this);

        myData = MainActivity.data;
        //need to get currently playing song

        try {
            flashbackList = new FlashbackList(myData.getAddress(MainActivity.getCurrLoc(),this), currTime, myData, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        flashbackList.generateList();

        Button switchScreen = (Button) findViewById(R.id.normal_mode);

        flashBackSongIDs = flashbackList.getFlashbackSongIDs();
        if (flashBackSongIDs.length == 0) {
            Toast.makeText(getApplicationContext(), "Play some songs to use flashback mode.", Toast.LENGTH_SHORT).show();
        }


        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashBackSongIDs.length != 0) {
                    DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                    mp.release();
                }
                SharedPreferences.Editor edit = lastScreen.edit();
                edit.putString("Activity", "Main");
                edit.apply();
                finish();
            }
        });

        if (flashBackSongIDs.length == 0) {
            Button play = (Button) findViewById(R.id.button_play);
            play.setVisibility(View.GONE);
            Button pause = (Button) findViewById(R.id.button_pause);
            pause.setVisibility(View.GONE);
            Button skip = (Button) findViewById(R.id.button_next);
            skip.setVisibility(View.GONE);
            Button down = (Button) findViewById(R.id.button_dislike);
            down.setVisibility(View.GONE);
            Button up = (Button) findViewById(R.id.button_like);
            up.setVisibility(View.GONE);
            return;
        }
        songNames = getFlasbackSongNames(flashBackSongIDs);

         int x = flashBackSongIDs[songIndex];
        songName = songNames[songIndex];

        mp = MediaPlayer.create(this, 3);
        mp.start();

        TextView showMetadata2 = (TextView) findViewById(R.id.text_SongNameFlashback);
        songName = metadataGetter.getName();
        showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());


        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        final Button playButton = (Button) findViewById(R.id.button_play);
        final Button pauseButton = (Button) findViewById(R.id.button_pause);
        playButton.setVisibility(View.GONE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Log.d("tag", "index = " + songIndex + " // arraysize = " + albumSongsIDs.length );
                //    Log.d("songid", "songid = " + albumSongsIDs[songIndex] );
                mp.start();
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.pause();
                playFlag = true;
                playButton.setVisibility(View.VISIBLE);
                System.out.println(playFlag);
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }

            }
        });

        updateLastPlayedInfo();
        setFinishListener();


        // like and dislike functionality

        // DISLIKE
        final ToggleButton dislikeButton = (ToggleButton) findViewById(R.id.button_dislike);
        final ToggleButton likeButton = (ToggleButton) findViewById(R.id.button_like);
        likeButton.setText(null);
        likeButton.setTextOn(null);
        likeButton.setTextOff(null);
        dislikeButton.setText(null);
        dislikeButton.setTextOn(null);
        dislikeButton.setTextOff(null);

        if(myData.getSongDislikedStatus(songName)){
            //set dislike button to be in highlighted state (because the song was already disliked at a previous time). Please do the same in the methods at the bottom of file
            dislikeButton.setChecked(true);
            likeButton.setChecked(false);
        }
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(getApplicationContext(), "FINISHED PLAYING A SONG", Toast.LENGTH_SHORT).show();
                if (songIndex < (flashBackSongIDs.length - 1)) {
                    songIndex++;
                    mp.reset();
                     int x= flashBackSongIDs[songIndex];

                    mp = MediaPlayer.create(getApplicationContext(), 3);
                    mp.start();
                    myData.setDislikedStatus(songName, true);
                    myData.setLikedStatus(songName, false);


                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongNameFlashback);
                    songName = metadataGetter.getName();
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());

                    //loadMedia(albumSongsIDs[songIndex]);

                    updateLastPlayedInfo();
                    updateLikedButton();
                    updateDislikedButton();

                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                    playFlag = true;


                } else {
                    Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();
                    myData.setDislikedStatus(songName, true);
                    myData.setLikedStatus(songName, false);
                    mp.reset();
                    mp = MediaPlayer.create(getApplicationContext(), 3);
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    playFlag = false;
                }
                setFinishListener();

            }

        });

        // LIKE
        if(myData.getSongLikedStatus(songName)){
            // set like button to be in highlighted state because the song was already liked previously. Please do the same in the methods at the bottom of the file
            likeButton.setChecked(true);
            dislikeButton.setChecked(false);
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myData.getSongLikedStatus(songName)){
                    myData.setLikedStatus(songName, false);
                    //  set button back to unhighlighted state
                    likeButton.setChecked(false);

                }
                else{
                    myData.setLikedStatus(songName, true);
                    // change to highlighted state
                    likeButton.setChecked(true);
                    dislikeButton.setChecked(false);

                }
            }
        });

        Button nextButton = (Button) findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songIndex < (flashBackSongIDs.length - 1)) {
                    songIndex++;
                    mp.reset();
                     int x= flashBackSongIDs[songIndex];

                    //reset the visibility of pause button
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);

                    mp = MediaPlayer.create(getApplicationContext(), 3);
                    mp.start();

                    //loadMedia(albumSongsIDs[songIndex]);

                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongNameFlashback);
                    songName = metadataGetter.getName();
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());


                    updateLastPlayedInfo();
                    updateDislikedButton();
                    updateLikedButton();


                    //get current information to update song if needed

                    /*
                    try {
                        myData.startSongInfoRequest(songName, getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myData.finishSongInfoRequest();
                    */



                } else {
                    Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();
                    mp.reset();
                    mp = MediaPlayer.create(getApplicationContext(), 3);
                }
                setFinishListener();
            }
        });

        list = (ListView) findViewById(R.id.list_listofsongs);
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,songNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    // Item click method
    // int i is the index of the item clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();

    }

    /*
     * Call whenever a song changes within the same activity
     * Updates the Last Play Location and Last Played Time
     */
    private void updateLastPlayedInfo(){
        TextView lastTime = (TextView) findViewById(R.id.text_timeAndDateVibe);
        TextView lastLoc = (TextView) findViewById((R.id.text_locationVibe));
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
     * Method to get names of songs based on IDs
     * @param IDs - array of song ids
     * @return array of flashback song names
     */
    public String[] getFlasbackSongNames( int[] IDs ) {

        String[] songNames = new String[IDs.length];
        for( int i = 0; i < songNames.length; i++ ) {
            songNames[i] = metadataGetter.getName();
        }
        return songNames;
    }

    /*
     * Update methods to change the look of the like and dislike buttons whenever the song changes, as we are not starting a new activity
     * Call only after the new song has started playing and the songName field has been updated
     */
    private void updateDislikedButton(){
        ToggleButton dislikeButton = (ToggleButton) findViewById(R.id.button_dislike);
        if(myData.getSongDislikedStatus(songName)){
            dislikeButton.setChecked(true);
        }
        else {
            dislikeButton.setChecked(false);
        }
    }

    /**
     * like button functionality
     */
    private void updateLikedButton(){
        ToggleButton likeButton = (ToggleButton) findViewById(R.id.button_like);
        if(myData.getSongLikedStatus(songName)){
            likeButton.setChecked(true);
        }
        else {
            likeButton.setChecked(false);
        }
    }

    /**
     * Method to give toast message when a song finishes playing, and goes to next song
     */
    private void setFinishListener() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp2) {
                Toast.makeText(getApplicationContext(), "FINISHED PLAYING A SONG", Toast.LENGTH_SHORT).show();
                if (songIndex < (flashBackSongIDs.length - 1)) {
                    songIndex++;
                    mp.reset();
                    int f = flashBackSongIDs[songIndex];

                    TextView showMetadata2 = (TextView) findViewById(R.id.text_SongNameFlashback);
                    songName = metadataGetter.getName();
                    showMetadata2.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist() + "\nAlbum: " + metadataGetter.getAlbum());

                    mp = MediaPlayer.create(getApplicationContext(), 3);
                    mp.start();

                    //loadMedia(albumSongsIDs[songIndex]);


                    updateLastPlayedInfo();
                    updateLikedButton();
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    //get current information to update song if needed
                    try {

                        myData.startSongInfoRequest(songName, getApplicationContext(), new Timestamp(System.currentTimeMillis()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "End of song list", Toast.LENGTH_SHORT).show();
                    mp.reset();
                    mp = MediaPlayer.create(getApplicationContext(), 3);
                }
                setFinishListener();
            }
        });

    }


}
