package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;

public class SongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of song names from the database
    public static String PACKAGE_NAME;


    private String[] songNames;
    private int[] IDs;
    MetadataGetter metadataGetter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MainActivity.PACKAGE_NAME = getPackageName();
        metadataGetter = new MetadataGetter(this);

        Button switchScreen = (Button) findViewById(R.id.btn_back2);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Dynamically populating idList
        IDs = getSongIDs();
        songNames = getSongNames(IDs);

        list = (ListView) findViewById(R.id.list_allsongs);
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,songNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_song);
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

    public void launchSongInfoAct(int i) {
        Intent intent = new Intent (this, SongInfoActivity.class);
        intent.putExtra("songID", IDs[i]);
        intent.putExtra("songName", songNames[i]);
        //intent.putExtra("albumMode", false);
        intent.putExtra("songIndex", i);
        Bundle bundle = new Bundle();
        bundle.putIntArray("SongsIDs", IDs);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    // Method to get the IDs of all songs in raw folder
    public static int[] getSongIDs() {
        Field[] ID_Fields = R.raw.class.getFields();
        int[] songIDs = new int[ID_Fields.length];
        for(int i = 0; i < ID_Fields.length; i++) {
            try {
                songIDs[i] = ID_Fields[i].getInt(null);
               // Log.d("id number: " + i, "" + songIDs[i] );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return songIDs;
    }

    // Method to get names of songs based on IDs
    public String[] getSongNames( int[] IDs ) {

        String[] songNames = new String[IDs.length];
        for( int i = 0; i < songNames.length; i++ ) {
            songNames[i] = metadataGetter.getName(IDs[i]);
        }
        return songNames;
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }



}
