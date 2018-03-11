package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity Class for the list of Albums.
 * Opened when 'albums' is clicked from MainActivity
 * Redirects to AlbumSongsActivity, and FlashBackActivity
 */
public class AlbumsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of Album names from the comf.example.tydia.cse_110_team_project_team_15_1.database

    String[] albumNames;
    int[] songIDs;
    MetadataGetter metadataGetter;

    /**
     * This method runs when the activity is created
     * Contains all functionality for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        MainActivity.PACKAGE_NAME = getPackageName();
        metadataGetter = new MetadataGetter(this);

        //songIDs = SongsActivity.getSongIDs();
        albumNames = getAlbumNames(songIDs);

        Button switchScreen = (Button) findViewById(R.id.btn_back);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        list = (ListView) findViewById(R.id.list_allalbums);

        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , albumNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_album);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlashback();
            }
        });
    }

    /**
     * Item click method
     * int i is the index of the item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        //Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();

        launchAlbumSongs(i);
    }

    /**
     * Method to get names of albums based on IDs
     *
     * @param IDs - array of song IDs
     * @return - String array of all albumNames
     */
    public String[] getAlbumNames( int[] IDs ) {

        ArrayList<String> albumSet = new ArrayList<>();

        for( int i = 0; i < IDs.length; i++ ) {
            String album = metadataGetter.getAlbum(IDs[i]);
            if(!albumSet.contains( album ))
                albumSet.add(album);
        }
        String[] albumNames = new String[albumSet.size()];
        for(int i = 0; i < albumSet.size(); i++){
            albumNames[i] = albumSet.get(i);
        }

        Arrays.sort(albumNames);

        return albumNames;
    }

    /**
     * Goes to AlbumSongsActivity for the particular album that was clicked in the list
     * @param i - list index for the album that was clicked
     */
    public void launchAlbumSongs(int i) {
        Intent intent = new Intent (this, AlbumSongsActivity.class);
        // Getting songs from selected album and placing on intent
        int[] albumSongIDs = getAlbumSongs(i);
        intent.putExtra("albumSongIDs", albumSongIDs);
        startActivity(intent);
    }

    /**
     * Method to retrieve songs from a given album
     * @param i - index of album that selected in the list view
     * @return albumSongsIDs: an int array containing all the ids of the songs of the album clicked
    */
    public int[] getAlbumSongs(int i) {
        ArrayList<Integer> albumSongIDsArr = new ArrayList<>();

        for(int j = 0; j < songIDs.length; j++){

            if(albumNames[i].equals(metadataGetter.getAlbum(songIDs[j]))){
                albumSongIDsArr.add(songIDs[j]);
            }
        }

        int[] albumSongIDs = new int[albumSongIDsArr.size()];

        for( int k = 0; k < albumSongIDsArr.size(); k++ ) {
            albumSongIDs[k] = albumSongIDsArr.get(k);
        }

        return albumSongIDs;
    }

    /**
     * Goes to FlashBackActivity
     */
    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }

}
