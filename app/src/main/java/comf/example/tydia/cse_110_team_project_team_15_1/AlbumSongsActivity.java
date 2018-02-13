
package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AlbumSongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of Album names from the database
    String[] songNames;
    int[] songIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songIDs = getIntent().getIntArrayExtra("albumSongIDs");
        songNames = getSongNames(songIDs);

        Button switchScreen = (Button) findViewById(R.id.btn_backAlbumSongs);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = (ListView) findViewById(R.id.list_AlbumSongs);
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , songNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_album_songs);
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
        launchSongInfoAct(i);
    }

    // Method to get names of songs based on IDs
    private String[] getSongNames( int[] IDs ) {

        String[] songNames = new String[IDs.length];
        for( int i = 0; i < songNames.length; i++ ) {
            Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + IDs[i]);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, path);
            songNames[i] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }

        return songNames;
    }

    public void launchSongInfoAct(int i) {
        Intent intent = new Intent (this, SongInfoActivity.class);
        intent.putExtra("songID", songIDs[i]);
        intent.putExtra("songName", songNames[i]);
        startActivity(intent);
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }


}
