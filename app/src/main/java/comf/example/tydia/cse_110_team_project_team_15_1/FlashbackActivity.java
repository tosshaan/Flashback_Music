package comf.example.tydia.cse_110_team_project_team_15_1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class FlashbackActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    database myData;
    String songName;
    FlashbackList flashbackList;
    MetadataGetter metadataGetter;
    //currently playing song
    // Need to get list of song names from the database

    String[] songNames;
    int[] flashBackSongIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Timestamp currTime = new Timestamp( System.currentTimeMillis() );
        metadataGetter = new MetadataGetter(this);

        myData = MainActivity.data;
        //need to get currently playing song

        flashbackList = new FlashbackList("TODO", currTime, myData, this);
        flashbackList.generateList();
        flashBackSongIDs = flashbackList.getFlashbackSongIDs();
        songNames = getFlasbackSongNames(flashBackSongIDs);

        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button switchScreen = (Button) findViewById(R.id.normal_mode);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                finish();
            }
        });

        updateLastPlayedInfo();


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
                if(myData.getSongDislikedStatus(songName)){
                    myData.setDislikedStatus(songName, false);
                    // set button back to unhighlighted version
                    dislikeButton.setChecked(false);
                }
                else{
                    myData.setDislikedStatus(songName, true);
                    myData.setLikedStatus(songName, false);

                    dislikeButton.setChecked(true);
                    likeButton.setChecked(false);

                    // TODO: Skip song
                }
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
        TextView lastTime = (TextView) findViewById(R.id.text_timeAndDateFlashback);
        TextView lastLoc = (TextView) findViewById((R.id.text_locationFlashback));
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

    // Method to get names of songs based on IDs
    public String[] getFlasbackSongNames( int[] IDs ) {

        String[] songNames = new String[IDs.length];
        for( int i = 0; i < songNames.length; i++ ) {
            songNames[i] = metadataGetter.getName(IDs[i]);
        }
        return songNames;
    }

    /*
     * Update methods to change the look of the like and dislike buttons whenever the song changes, as we are not starting a new activity
     * Call only after the new song has started playing and the songName field has been updated
     */
    private void updateDislikedButton(){
        Button dislikeButton = (Button) findViewById(R.id.button_dislike);
        if(myData.getSongDislikedStatus(songName)){
            //TODO: set dislike button to be in highlighted state (because the song was already disliked at a previous time)
        }
    }
    private void updateLikedButton(){
        Button likeButton = (Button) findViewById(R.id.button_like);
        if(myData.getSongLikedStatus(songName)){
            //TODO: set like button to be in highlighted state because the song was already liked previously
        }
    }

}
