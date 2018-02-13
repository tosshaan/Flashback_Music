package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
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

public class AlbumsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of Album names from the comf.example.tydia.cse_110_team_project_team_15_1.database

    String[] albumNames;
    int[] songIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        songIDs = getSongIDs();
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

    // Item click method
    // int i is the index of the item clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        //Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();

        launchAlbumSongs(i);
    }

    // Method to get the IDs of all songs in raw folder
    private int[] getSongIDs() {
        Field[] ID_Fields = R.raw.class.getFields();
        int[] songIDs = new int[ID_Fields.length];
        for(int i = 0; i < ID_Fields.length; i++) {
            try {
                songIDs[i] = ID_Fields[i].getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return songIDs;
    }

    // Method to get names of albums based on IDs
    private String[] getAlbumNames( int[] IDs ) {

        ArrayList<String> albumSet = new ArrayList<>();

        for( int i = 0; i < IDs.length; i++ ) {
            Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + IDs[i]);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, path);
            if(!albumSet.contains(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)))
                albumSet.add(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        }
        String[] albumNames = new String[albumSet.size()];
        for(int i = 0; i < albumSet.size(); i++){
            albumNames[i] = albumSet.get(i);
        }

        Arrays.sort(albumNames);

        return albumNames;
    }

    public void launchAlbumSongs(int i) {
        Intent intent = new Intent (this, AlbumSongsActivity.class);
        ArrayList<Integer> albumSongIDsArr = new ArrayList<>();

        for(int j = 0; j < songIDs.length; j++){

            Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + songIDs[j]);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, path);
            if(albumNames[i].equals(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM))){
                albumSongIDsArr.add(songIDs[j]);
            }
        }

        int[] albumSongIDs = new int[albumSongIDsArr.size()];
        for( int k = 0; k < albumSongIDsArr.size(); k++ ) {
            albumSongIDs[k] = albumSongIDsArr.get(k);
        }

        intent.putExtra("albumSongIDs", albumSongIDs);
        startActivity(intent);
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }

}
