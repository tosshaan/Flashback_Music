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

import java.io.IOException;

public class FlashbackActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    database myData;
    String songName; //currently playing song
    // Need to get list of song names from the database

    String[] songNames = {"song1", "song2", "song3", "song4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        myData = MainActivity.data;
        //need to get currently playing song

        Button switchScreen = (Button) findViewById(R.id.normal_mode);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                finish();
            }
        });

        updateLastPlayedInfo();

        //Setting up initial Dislike Button
        Button dislikeButton = (Button) findViewById(R.id.button_dislike);
        if(myData.getSongDislikedStatus(songName)){
            //TODO: set dislike button to be in highlighted state (because the song was already disliked at a previous time). Please do the same in the methods at the bottom of file
        }
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myData.getSongDislikedStatus(songName)){
                    myData.setDislikedStatus(songName, false);
                    // TODO: set button back to unhighlighted version
                }
                else{
                    myData.setDislikedStatus(songName, true);
                    myData.setLikedStatus(songName, false);
                    // TODO: Skip song
                }
            }
        });

        //Setting up initial Like Button
        Button likeButton = (Button) findViewById(R.id.button_like);
        if(myData.getSongLikedStatus(songName)){
            //TODO: set like button to be in highlighted state because the song was already liked previously. Please do the same in the methods at the bottom of the file
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myData.getSongLikedStatus(songName)){
                    myData.setLikedStatus(songName, false);
                    // TODO: set button back to unhighlighted state
                }
                else{
                    myData.setLikedStatus(songName, true);
                    // TODO: change to highlighted state
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
