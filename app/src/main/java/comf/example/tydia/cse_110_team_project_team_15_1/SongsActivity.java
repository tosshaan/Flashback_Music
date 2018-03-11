package comf.example.tydia.cse_110_team_project_team_15_1;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.RemoteController;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;
import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * Activity Class for the list of all songs in memory.
 * Opened when "all songs" is clicked from MainActivity
 * Redirects to SongsInfoActivity, and FlashBackActivity
 */
public class SongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of song names from the database
    public static String PACKAGE_NAME;


    private String[] songNames;
    private int[] IDs;
    MetadataGetter metadataGetter;

    //https://www.youtube.com/watch?v=atZRWb6_QRs
    // yt tutorial to download songs
    long queueid;
    DownloadManager dm;


    /**
     * This method runs when the activity is created
     * Contains all functionality for the activity
     */

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

        Button viewDL = (Button) findViewById(R.id.showDL_songs);
        viewDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDLSong();
            }
        });

        Button b_dl = (Button) findViewById(R.id.b_download);
        b_dl.setOnClickListener(view -> {
            EditText editText = (EditText) findViewById(R.id.URLeditText);
            String input = editText.getText().toString();
            //"http://www.sakisgouzonis.com/files/mp3s/Sakis_Gouzonis_-_Quest_For_Peace_And_Progress.mp3";
            Download(input);
        });

        Button viewdl = (Button) findViewById(R.id.b_viewDownload);
        viewdl.setOnClickListener(view -> {
            View_Click(viewdl);
        });


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    DownloadManager.Query req_query = new DownloadManager.Query();
                    req_query.setFilterById(queueid);
                    Cursor c = dm.query(req_query);

                    if(c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            Toast.makeText(getApplicationContext(), "Download Sucessful", Toast.LENGTH_SHORT);
                            /*
                            VideoView videoView = (VideoView) findViewById(R.id.video);
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            MediaController mediaController = new MediaController(getApplicationContext());
                            mediaController.setAnchorView(videoView);
                            videoView.requestFocus();
                            videoView.setVideoURI(Uri.parse(uriString));
                            videoView.start();
                            */
                        }
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public void Download(String input) {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(input));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, input);
        //Log.d("DOWNLOADINGx", "Download path " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        haveStoragePermission();

        queueid = dm.enqueue(request);

    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

    public void View_Click(View view) {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);
    }

    /**
     * Item click method
     * @param i - index of the item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        //Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();
        launchSongInfoAct(i);
    }

    /**
     * Goes to SongInfoActivity for the particular song clicked in the list
     * @param i - index of the song in the song list
     */
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

    /**
     * Method to get an array of song ids for all the songs in the list
     * @return array of song ids of all songs in the list
     */
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

    /**
     * Method to get song names from songIDs
     * @param IDs - array of all songs' ids
     * @return array of all songs' names
     */
    public String[] getSongNames( int[] IDs ) {

        String[] songNames = new String[IDs.length];
        for( int i = 0; i < songNames.length; i++ ) {
            songNames[i] = metadataGetter.getName(IDs[i]);
        }
        return songNames;
    }

    /**
     * Goes to FlashbackActivity
     */
    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);

    }

    public void launchDLSong() {
        Intent intent = new Intent (this, ViewDLSongsActivity.class);
        startActivity(intent);
    }


}
